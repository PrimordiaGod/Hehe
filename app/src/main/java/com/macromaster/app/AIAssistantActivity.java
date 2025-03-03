package com.macromaster.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.macromaster.app.models.Macro;
import com.macromaster.app.services.AIService;
import com.macromaster.app.services.ScreenCaptureService;

public class AIAssistantActivity extends AppCompatActivity {

    private EditText descriptionEditText;
    private Button generateMacroButton;
    private Button analyzeScreenButton;
    private Button optimizeMacroButton;
    private TextView resultTextView;
    private ProgressBar progressBar;
    private ImageView screenPreviewImageView;
    
    private AIService aiService;
    private Bitmap currentScreenshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);
        
        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("AI Assistant");
        }
        
        // Initialize AI service
        aiService = AIService.getInstance(this);
        
        // Initialize UI components
        descriptionEditText = findViewById(R.id.descriptionEditText);
        generateMacroButton = findViewById(R.id.generateMacroButton);
        analyzeScreenButton = findViewById(R.id.analyzeScreenButton);
        optimizeMacroButton = findViewById(R.id.optimizeMacroButton);
        resultTextView = findViewById(R.id.resultTextView);
        progressBar = findViewById(R.id.progressBar);
        screenPreviewImageView = findViewById(R.id.screenPreviewImageView);
        
        // Set click listeners
        generateMacroButton.setOnClickListener(v -> generateMacroFromDescription());
        analyzeScreenButton.setOnClickListener(v -> analyzeCurrentScreen());
        optimizeMacroButton.setOnClickListener(v -> optimizeCurrentMacro());
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources
        if (currentScreenshot != null && !currentScreenshot.isRecycled()) {
            currentScreenshot.recycle();
            currentScreenshot = null;
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void generateMacroFromDescription() {
        String description = descriptionEditText.getText().toString().trim();
        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showProgress(true);
        resultTextView.setText("");
        
        aiService.generateMacroFromDescription(description, new AIService.MacroSuggestionCallback() {
            @Override
            public void onSuggestionReady(Macro suggestedMacro) {
                showProgress(false);
                
                // Display the generated macro details
                StringBuilder result = new StringBuilder("Generated Macro:\n");
                result.append("Name: ").append(suggestedMacro.getName()).append("\n\n");
                
                result.append("Actions:\n");
                for (int i = 0; i < suggestedMacro.getActions().size(); i++) {
                    result.append(i + 1).append(". ")
                          .append(suggestedMacro.getActions().get(i).toString())
                          .append("\n");
                }
                
                resultTextView.setText(result.toString());
                
                // Offer to save or edit the macro
                Toast.makeText(AIAssistantActivity.this, 
                        "Macro generated! You can now edit or save it.", 
                        Toast.LENGTH_LONG).show();
                
                // TODO: Add functionality to save or edit the generated macro
            }
            
            @Override
            public void onError(String error) {
                showProgress(false);
                resultTextView.setText("Error: " + error);
            }
        });
    }
    
    private void analyzeCurrentScreen() {
        showProgress(true);
        resultTextView.setText("");
        
        // Capture the current screen
        captureScreen(bitmap -> {
            currentScreenshot = bitmap;
            
            // Display the screenshot
            runOnUiThread(() -> {
                screenPreviewImageView.setImageBitmap(bitmap);
                screenPreviewImageView.setVisibility(View.VISIBLE);
            });
            
            // Analyze the screen
            aiService.suggestActionsFromScreen(bitmap, new AIService.AICallback() {
                @Override
                public void onSuccess(String result) {
                    showProgress(false);
                    resultTextView.setText(result);
                }
                
                @Override
                public void onError(String error) {
                    showProgress(false);
                    resultTextView.setText("Error: " + error);
                }
            });
        });
    }
    
    private void optimizeCurrentMacro() {
        // For demo purposes, we'll create a sample macro with redundant actions
        // In a real app, this would come from a selected macro
        
        showProgress(true);
        resultTextView.setText("");
        
        // Create a sample macro with redundant wait actions
        Macro sampleMacro = createSampleMacro();
        
        // Optimize the macro
        aiService.optimizeMacro(sampleMacro, new AIService.AICallback() {
            @Override
            public void onSuccess(String result) {
                showProgress(false);
                resultTextView.setText(result);
            }
            
            @Override
            public void onError(String error) {
                showProgress(false);
                resultTextView.setText("Error: " + error);
            }
        });
    }
    
    private Macro createSampleMacro() {
        // Create a sample macro with some redundant actions for optimization demo
        java.util.List<com.macromaster.app.models.Action> actions = new java.util.ArrayList<>();
        java.util.List<com.macromaster.app.models.Condition> conditions = new java.util.ArrayList<>();
        
        // Add some actions with redundant waits
        actions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.CLICK, 100, 200));
        actions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.WAIT, 1000));
        actions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.WAIT, 2000));
        actions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.CLICK, 300, 400));
        actions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.TEXT_INPUT, "Sample text"));
        actions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.WAIT, 500));
        actions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.WAIT, 1500));
        
        return new com.macromaster.app.models.Macro("Sample Macro", actions, conditions);
    }
    
    private void captureScreen(ScreenCaptureCallback callback) {
        // In a real app, this would use MediaProjection API to capture the screen
        // For this demo, we'll create a placeholder bitmap
        
        // Create a placeholder bitmap (in a real app, this would be the actual screenshot)
        Bitmap placeholderBitmap = Bitmap.createBitmap(800, 1600, Bitmap.Config.ARGB_8888);
        placeholderBitmap.eraseColor(android.graphics.Color.LTGRAY);
        
        // Draw some UI elements on the bitmap to simulate a screen
        android.graphics.Canvas canvas = new android.graphics.Canvas(placeholderBitmap);
        android.graphics.Paint paint = new android.graphics.Paint();
        
        // Draw a button
        paint.setColor(android.graphics.Color.BLUE);
        canvas.drawRect(300, 500, 500, 600, paint);
        
        // Draw button text
        paint.setColor(android.graphics.Color.WHITE);
        paint.setTextSize(40);
        canvas.drawText("LOGIN", 350, 565, paint);
        
        // Draw an input field
        paint.setColor(android.graphics.Color.WHITE);
        canvas.drawRect(200, 300, 600, 400, paint);
        
        // Draw input field label
        paint.setColor(android.graphics.Color.BLACK);
        paint.setTextSize(30);
        canvas.drawText("Username", 220, 280, paint);
        
        // Simulate delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        callback.onScreenCaptured(placeholderBitmap);
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    // Interface for screen capture callback
    interface ScreenCaptureCallback {
        void onScreenCaptured(Bitmap bitmap);
    }
}