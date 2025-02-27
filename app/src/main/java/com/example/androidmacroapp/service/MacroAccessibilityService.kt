package com.example.androidmacroapp.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.util.*

class MacroAccessibilityService : AccessibilityService() {

    private val TAG = "MacroAccessibilityService"
    private var isRecording = false
    private var isRunning = false
    private val recordedActions = mutableListOf<MacroAction>()
    private val handler = Handler(Looper.getMainLooper())
    
    companion object {
        const val ACTION_START_RECORDING = "com.example.androidmacroapp.START_RECORDING"
        const val ACTION_STOP_RECORDING = "com.example.androidmacroapp.STOP_RECORDING"
        const val ACTION_START_MACRO = "com.example.androidmacroapp.START_MACRO"
        const val ACTION_STOP_MACRO = "com.example.androidmacroapp.STOP_MACRO"
        const val ACTION_SAVE_MACRO = "com.example.androidmacroapp.SAVE_MACRO"
        const val EXTRA_MACRO_NAME = "macro_name"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Accessibility service connected")
        showToast("Macro Accessibility Service Connected")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_RECORDING -> {
                    isRecording = true
                    recordedActions.clear()
                    showToast("Started recording macro")
                    Log.d(TAG, "Started recording macro")
                }
                ACTION_STOP_RECORDING -> {
                    isRecording = false
                    showToast("Stopped recording macro. ${recordedActions.size} actions recorded.")
                    Log.d(TAG, "Stopped recording macro. ${recordedActions.size} actions recorded.")
                }
                ACTION_START_MACRO -> {
                    if (recordedActions.isNotEmpty()) {
                        isRunning = true
                        showToast("Starting macro execution")
                        Log.d(TAG, "Starting macro execution")
                        executeMacro()
                    } else {
                        showToast("No macro actions to execute")
                        Log.d(TAG, "No macro actions to execute")
                    }
                }
                ACTION_STOP_MACRO -> {
                    isRunning = false
                    showToast("Stopped macro execution")
                    Log.d(TAG, "Stopped macro execution")
                }
                ACTION_SAVE_MACRO -> {
                    val macroName = it.getStringExtra(EXTRA_MACRO_NAME) ?: "unnamed_macro"
                    saveMacro(macroName)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (isRecording) {
            when (event.eventType) {
                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                    val nodeInfo = event.source ?: return
                    val bounds = android.graphics.Rect()
                    nodeInfo.getBoundsInScreen(bounds)
                    
                    recordedActions.add(
                        MacroAction.Click(
                            bounds.centerX().toFloat(),
                            bounds.centerY().toFloat(),
                            System.currentTimeMillis()
                        )
                    )
                    
                    Log.d(TAG, "Recorded click at (${bounds.centerX()}, ${bounds.centerY()})")
                    nodeInfo.recycle()
                }
                // You can add more event types here as needed
            }
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted")
    }

    private fun executeMacro() {
        if (!isRunning || recordedActions.isEmpty()) return
        
        var lastTimestamp = recordedActions.first().timestamp
        
        recordedActions.forEachIndexed { index, action ->
            val delay = if (index == 0) 0 else action.timestamp - lastTimestamp
            lastTimestamp = action.timestamp
            
            handler.postDelayed({
                if (!isRunning) return@postDelayed
                
                when (action) {
                    is MacroAction.Click -> {
                        performClick(action.x, action.y)
                        Log.d(TAG, "Executing click at (${action.x}, ${action.y})")
                    }
                    // Handle other action types here
                }
                
                // If this is the last action, we're done
                if (index == recordedActions.size - 1) {
                    showToast("Macro execution completed")
                    isRunning = false
                }
            }, delay)
        }
    }

    private fun performClick(x: Float, y: Float) {
        val path = Path()
        path.moveTo(x, y)
        
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 100))
        
        dispatchGesture(gestureBuilder.build(), null, null)
    }

    private fun saveMacro(macroName: String) {
        try {
            val dir = File(filesDir, "macros")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            
            val file = File(dir, "$macroName.macro")
            val fos = FileOutputStream(file)
            val oos = ObjectOutputStream(fos)
            
            oos.writeObject(recordedActions)
            oos.close()
            fos.close()
            
            showToast("Macro saved as $macroName")
            Log.d(TAG, "Macro saved as $macroName")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving macro", e)
            showToast("Error saving macro: ${e.message}")
        }
    }

    private fun showToast(message: String) {
        handler.post {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }
}

sealed class MacroAction(val timestamp: Long) : java.io.Serializable {
    class Click(val x: Float, val y: Float, timestamp: Long) : MacroAction(timestamp)
    // You can add more action types here as needed, such as:
    // class Swipe(val startX: Float, val startY: Float, val endX: Float, val endY: Float, timestamp: Long) : MacroAction(timestamp)
    // class TextInput(val text: String, timestamp: Long) : MacroAction(timestamp)
}