package com.macromaster.app.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.macromaster.app.models.Action;
import com.macromaster.app.models.Condition;
import com.macromaster.app.models.Macro;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * Service that provides AI functionality for macro creation and optimization.
 */
public class AIService {
    private static final String TAG = "AIService";
    private static AIService instance;
    
    private final Context context;
    private final TextRecognizer textRecognizer;
    private final Executor executor;
    private final Handler mainHandler;
    
    // Interface for callbacks
    public interface AICallback {
        void onSuccess(String result);
        void onError(String error);
    }
    
    // Interface for text recognition callbacks
    public interface TextRecognitionCallback {
        void onTextRecognized(String text);
        void onError(Exception e);
    }
    
    // Interface for macro suggestion callbacks
    public interface MacroSuggestionCallback {
        void onSuggestionReady(Macro suggestedMacro);
        void onError(String error);
    }
    
    private AIService(Context context) {
        this.context = context.getApplicationContext();
        this.textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public static synchronized AIService getInstance(Context context) {
        if (instance == null) {
            instance = new AIService(context);
        }
        return instance;
    }
    
    /**
     * Recognize text from a bitmap image
     */
    public void recognizeText(Bitmap bitmap, TextRecognitionCallback callback) {
        executor.execute(() -> {
            InputImage image = InputImage.fromBitmap(bitmap, 0);
            textRecognizer.process(image)
                .addOnSuccessListener(text -> {
                    String recognizedText = text.getText();
                    mainHandler.post(() -> callback.onTextRecognized(recognizedText));
                })
                .addOnFailureListener(e -> {
                    mainHandler.post(() -> callback.onError(e));
                });
        });
    }
    
    /**
     * Analyze screen content and suggest actions
     */
    public void suggestActionsFromScreen(Bitmap screenBitmap, AICallback callback) {
        recognizeText(screenBitmap, new TextRecognitionCallback() {
            @Override
            public void onTextRecognized(String text) {
                List<String> actionablePhrases = findActionablePhrases(text);
                if (!actionablePhrases.isEmpty()) {
                    StringBuilder suggestions = new StringBuilder("Suggested actions:\n");
                    for (String phrase : actionablePhrases) {
                        suggestions.append("- ").append(phrase).append("\n");
                    }
                    callback.onSuccess(suggestions.toString());
                } else {
                    callback.onSuccess("No actionable elements detected on screen.");
                }
            }
            
            @Override
            public void onError(Exception e) {
                callback.onError("Failed to analyze screen: " + e.getMessage());
            }
        });
    }
    
    /**
     * Find actionable phrases in text
     */
    private List<String> findActionablePhrases(String text) {
        List<String> actionablePhrases = new ArrayList<>();
        
        // Common UI element patterns
        Pattern buttonPattern = Pattern.compile("\\b(OK|CANCEL|SUBMIT|NEXT|BACK|CONTINUE|SIGN IN|LOG IN|REGISTER)\\b", 
                Pattern.CASE_INSENSITIVE);
        Pattern inputPattern = Pattern.compile("\\b(username|email|password|search)\\b", 
                Pattern.CASE_INSENSITIVE);
        Pattern menuPattern = Pattern.compile("\\b(menu|settings|options|profile|account)\\b", 
                Pattern.CASE_INSENSITIVE);
        
        // Find matches
        java.util.regex.Matcher buttonMatcher = buttonPattern.matcher(text);
        java.util.regex.Matcher inputMatcher = inputPattern.matcher(text);
        java.util.regex.Matcher menuMatcher = menuPattern.matcher(text);
        
        while (buttonMatcher.find()) {
            actionablePhrases.add("Click on '" + buttonMatcher.group() + "' button");
        }
        
        while (inputMatcher.find()) {
            actionablePhrases.add("Input text in '" + inputMatcher.group() + "' field");
        }
        
        while (menuMatcher.find()) {
            actionablePhrases.add("Open '" + menuMatcher.group() + "'");
        }
        
        return actionablePhrases;
    }
    
    /**
     * Generate a macro based on a natural language description
     */
    public void generateMacroFromDescription(String description, MacroSuggestionCallback callback) {
        executor.execute(() -> {
            try {
                // Simple NLP to extract actions from description
                List<Action> actions = new ArrayList<>();
                List<Condition> conditions = new ArrayList<>();
                
                // Parse for click actions
                Pattern clickPattern = Pattern.compile("click\\s+(?:on\\s+)?['\"]?([\\w\\s]+)['\"]?", 
                        Pattern.CASE_INSENSITIVE);
                java.util.regex.Matcher clickMatcher = clickPattern.matcher(description);
                
                while (clickMatcher.find()) {
                    // For demo purposes, use dummy coordinates
                    // In a real implementation, this would use image recognition to find the element
                    actions.add(new Action(Action.Type.CLICK, 500, 500));
                }
                
                // Parse for text input actions
                Pattern typePattern = Pattern.compile("(?:type|input|enter)\\s+['\"]([^'\"]+)['\"]", 
                        Pattern.CASE_INSENSITIVE);
                java.util.regex.Matcher typeMatcher = typePattern.matcher(description);
                
                while (typeMatcher.find()) {
                    String textToType = typeMatcher.group(1);
                    actions.add(new Action(Action.Type.TEXT_INPUT, textToType));
                }
                
                // Parse for wait actions
                Pattern waitPattern = Pattern.compile("wait\\s+for\\s+(\\d+)\\s*(?:seconds|s)", 
                        Pattern.CASE_INSENSITIVE);
                java.util.regex.Matcher waitMatcher = waitPattern.matcher(description);
                
                while (waitMatcher.find()) {
                    int seconds = Integer.parseInt(waitMatcher.group(1));
                    actions.add(new Action(Action.Type.WAIT, seconds * 1000L));
                }
                
                // If we found any actions, create a macro
                if (!actions.isEmpty()) {
                    String macroName = "AI Generated Macro";
                    Macro suggestedMacro = new Macro(macroName, actions, conditions);
                    mainHandler.post(() -> callback.onSuggestionReady(suggestedMacro));
                } else {
                    mainHandler.post(() -> callback.onError("Could not generate actions from description"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error generating macro", e);
                mainHandler.post(() -> callback.onError("Error generating macro: " + e.getMessage()));
            }
        });
    }
    
    /**
     * Optimize a macro by analyzing its actions and conditions
     */
    public void optimizeMacro(Macro macro, AICallback callback) {
        executor.execute(() -> {
            try {
                // Simple optimization: combine consecutive waits
                List<Action> optimizedActions = new ArrayList<>();
                List<Action> originalActions = macro.getActions();
                
                for (int i = 0; i < originalActions.size(); i++) {
                    Action currentAction = originalActions.get(i);
                    
                    // If this is a wait action and the next one is also a wait, combine them
                    if (currentAction.getType() == Action.Type.WAIT && 
                            i < originalActions.size() - 1 && 
                            originalActions.get(i + 1).getType() == Action.Type.WAIT) {
                        
                        long combinedDuration = currentAction.getDuration() + 
                                originalActions.get(i + 1).getDuration();
                        optimizedActions.add(new Action(Action.Type.WAIT, combinedDuration));
                        i++; // Skip the next action since we combined it
                    } else {
                        optimizedActions.add(currentAction);
                    }
                }
                
                // Calculate optimization metrics
                int originalCount = originalActions.size();
                int optimizedCount = optimizedActions.size();
                int reduction = originalCount - optimizedCount;
                
                String result = String.format(
                        "Macro optimized: Reduced from %d to %d actions (%d%% improvement)",
                        originalCount, optimizedCount, 
                        originalCount > 0 ? (reduction * 100 / originalCount) : 0);
                
                mainHandler.post(() -> callback.onSuccess(result));
            } catch (Exception e) {
                Log.e(TAG, "Error optimizing macro", e);
                mainHandler.post(() -> callback.onError("Error optimizing macro: " + e.getMessage()));
            }
        });
    }
    
    /**
     * Clean up resources
     */
    public void shutdown() {
        textRecognizer.close();
        instance = null;
    }
}