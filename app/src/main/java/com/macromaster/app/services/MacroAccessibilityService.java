package com.macromaster.app.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Bundle;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.macromaster.app.models.Action;
import com.macromaster.app.models.Macro;

/**
 * Accessibility service for performing actions like clicks, swipes, and text input.
 */
public class MacroAccessibilityService extends AccessibilityService {

    private static MacroAccessibilityService instance;
    private Macro currentMacro;
    private boolean isRunning = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // We don't need to process events here as we'll be performing actions programmatically
    }

    @Override
    public void onInterrupt() {
        // Stop any running macros when the service is interrupted
        stopCurrentMacro();
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    /**
     * Get the singleton instance of the service.
     */
    public static MacroAccessibilityService getInstance() {
        return instance;
    }

    /**
     * Execute a macro.
     *
     * @param macro The macro to execute
     */
    public void executeMacro(Macro macro) {
        if (isRunning) {
            stopCurrentMacro();
        }

        currentMacro = macro;
        isRunning = true;
        currentMacro.setRunning(true);

        // Start executing the macro in a background thread
        new Thread(() -> {
            try {
                for (Action action : currentMacro.getActions()) {
                    if (!isRunning) {
                        break;
                    }
                    executeAction(action);
                }
            } finally {
                isRunning = false;
                currentMacro.setRunning(false);
                currentMacro = null;
            }
        }).start();
    }

    /**
     * Stop the currently running macro.
     */
    public void stopCurrentMacro() {
        isRunning = false;
        if (currentMacro != null) {
            currentMacro.setRunning(false);
            currentMacro = null;
        }
    }

    /**
     * Execute a single action.
     *
     * @param action The action to execute
     */
    private void executeAction(Action action) {
        switch (action.getType()) {
            case CLICK:
                performClick(action.getX(), action.getY());
                break;
            case SWIPE:
                performSwipe(action.getX(), action.getY(), action.getEndX(), action.getEndY(), action.getDuration());
                break;
            case TEXT_INPUT:
                performTextInput(action.getText());
                break;
            case WAIT:
                try {
                    Thread.sleep(action.getDuration());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                break;
        }
    }

    /**
     * Perform a click at the specified coordinates.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    private void performClick(int x, int y) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path path = new Path();
        path.moveTo(x, y);
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 1));
        dispatchGesture(builder.build(), null, null);
    }

    /**
     * Perform a swipe from one point to another.
     *
     * @param startX   Starting X coordinate
     * @param startY   Starting Y coordinate
     * @param endX     Ending X coordinate
     * @param endY     Ending Y coordinate
     * @param duration Duration of the swipe in milliseconds
     */
    private void performSwipe(int startX, int startY, int endX, int endY, long duration) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path path = new Path();
        path.moveTo(startX, startY);
        path.lineTo(endX, endY);
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, duration));
        dispatchGesture(builder.build(), null, null);
    }

    /**
     * Input text into the currently focused field.
     *
     * @param text The text to input
     */
    private void performTextInput(String text) {
        AccessibilityNodeInfo focusedNode = findFocusedNode(getRootInActiveWindow());
        if (focusedNode != null) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            focusedNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            focusedNode.recycle();
        }
    }

    /**
     * Find the currently focused input field.
     *
     * @param root The root node
     * @return The focused node, or null if none is found
     */
    private AccessibilityNodeInfo findFocusedNode(AccessibilityNodeInfo root) {
        if (root == null) {
            return null;
        }

        if (root.isFocused() && root.isEditable()) {
            return root;
        }

        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo child = root.getChild(i);
            if (child != null) {
                AccessibilityNodeInfo focused = findFocusedNode(child);
                if (focused != null) {
                    return focused;
                }
                child.recycle();
            }
        }

        return null;
    }
}