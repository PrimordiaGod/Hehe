package com.example.androidmacroapp

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidmacroapp.databinding.ActivityMainBinding
import com.example.androidmacroapp.service.MacroAccessibilityService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isRecording = false
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        checkAccessibilityServiceEnabled()
    }

    private fun setupClickListeners() {
        binding.btnEnableAccessibility.setOnClickListener {
            openAccessibilitySettings()
        }

        binding.btnRecordMacro.setOnClickListener {
            if (isAccessibilityServiceEnabled()) {
                toggleRecording()
            } else {
                showAccessibilityServiceNotEnabledMessage()
            }
        }

        binding.btnStartMacro.setOnClickListener {
            if (isAccessibilityServiceEnabled()) {
                toggleMacroExecution()
            } else {
                showAccessibilityServiceNotEnabledMessage()
            }
        }

        binding.btnSaveMacro.setOnClickListener {
            if (binding.etMacroName.text.toString().isNotEmpty()) {
                saveMacro(binding.etMacroName.text.toString())
            } else {
                Toast.makeText(this, "Please enter a macro name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleRecording() {
        isRecording = !isRecording
        binding.btnRecordMacro.text = if (isRecording) getString(R.string.stop_macro) else getString(R.string.record_macro)
        
        // Send broadcast to service to start/stop recording
        val intent = Intent(this, MacroAccessibilityService::class.java)
        intent.action = if (isRecording) MacroAccessibilityService.ACTION_START_RECORDING else MacroAccessibilityService.ACTION_STOP_RECORDING
        startService(intent)
    }

    private fun toggleMacroExecution() {
        isRunning = !isRunning
        binding.btnStartMacro.text = if (isRunning) getString(R.string.stop_macro) else getString(R.string.start_macro)
        
        // Send broadcast to service to start/stop macro execution
        val intent = Intent(this, MacroAccessibilityService::class.java)
        intent.action = if (isRunning) MacroAccessibilityService.ACTION_START_MACRO else MacroAccessibilityService.ACTION_STOP_MACRO
        startService(intent)
    }

    private fun saveMacro(macroName: String) {
        // Send broadcast to service to save the macro
        val intent = Intent(this, MacroAccessibilityService::class.java)
        intent.action = MacroAccessibilityService.ACTION_SAVE_MACRO
        intent.putExtra(MacroAccessibilityService.EXTRA_MACRO_NAME, macroName)
        startService(intent)
        
        Toast.makeText(this, "Macro saved: $macroName", Toast.LENGTH_SHORT).show()
        binding.etMacroName.text.clear()
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        // Check if our accessibility service is enabled
        val accessibilityEnabled = Settings.Secure.getInt(
            contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED, 0
        ) == 1

        if (accessibilityEnabled) {
            val serviceString = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            return serviceString?.contains("${packageName}/${MacroAccessibilityService::class.java.name}") == true
        }
        return false
    }

    private fun checkAccessibilityServiceEnabled() {
        if (isAccessibilityServiceEnabled()) {
            binding.btnEnableAccessibility.isEnabled = false
            binding.btnRecordMacro.isEnabled = true
            binding.btnStartMacro.isEnabled = true
            binding.btnSaveMacro.isEnabled = true
            binding.etMacroName.isEnabled = true
        } else {
            binding.btnEnableAccessibility.isEnabled = true
            binding.btnRecordMacro.isEnabled = false
            binding.btnStartMacro.isEnabled = false
            binding.btnSaveMacro.isEnabled = false
            binding.etMacroName.isEnabled = false
            showAccessibilityServiceNotEnabledMessage()
        }
    }

    private fun showAccessibilityServiceNotEnabledMessage() {
        Toast.makeText(
            this,
            getString(R.string.accessibility_service_not_enabled),
            Toast.LENGTH_LONG
        ).show()
    }
}