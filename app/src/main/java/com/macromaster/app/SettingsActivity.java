package com.macromaster.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    private Switch enableAISwitch;
    private Switch enableAutoOptimizeSwitch;
    private Switch enableDarkModeSwitch;
    private SeekBar aiConfidenceThresholdSeekBar;
    private TextView aiConfidenceThresholdValueTextView;
    private Button resetSettingsButton;
    private Button saveSettingsButton;
    
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }
        
        // Initialize shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        // Initialize UI components
        enableAISwitch = findViewById(R.id.enableAISwitch);
        enableAutoOptimizeSwitch = findViewById(R.id.enableAutoOptimizeSwitch);
        enableDarkModeSwitch = findViewById(R.id.enableDarkModeSwitch);
        aiConfidenceThresholdSeekBar = findViewById(R.id.aiConfidenceThresholdSeekBar);
        aiConfidenceThresholdValueTextView = findViewById(R.id.aiConfidenceThresholdValueTextView);
        resetSettingsButton = findViewById(R.id.resetSettingsButton);
        saveSettingsButton = findViewById(R.id.saveSettingsButton);
        
        // Load current settings
        loadSettings();
        
        // Set up seek bar listener
        aiConfidenceThresholdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float threshold = progress / 100.0f;
                aiConfidenceThresholdValueTextView.setText(String.format("%.2f", threshold));
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        
        // Set up button listeners
        resetSettingsButton.setOnClickListener(v -> resetSettings());
        saveSettingsButton.setOnClickListener(v -> saveSettings());
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void loadSettings() {
        boolean enableAI = sharedPreferences.getBoolean("enable_ai", true);
        boolean enableAutoOptimize = sharedPreferences.getBoolean("enable_auto_optimize", false);
        boolean enableDarkMode = sharedPreferences.getBoolean("enable_dark_mode", false);
        int aiConfidenceThreshold = sharedPreferences.getInt("ai_confidence_threshold", 70);
        
        enableAISwitch.setChecked(enableAI);
        enableAutoOptimizeSwitch.setChecked(enableAutoOptimize);
        enableDarkModeSwitch.setChecked(enableDarkMode);
        aiConfidenceThresholdSeekBar.setProgress(aiConfidenceThreshold);
        aiConfidenceThresholdValueTextView.setText(String.format("%.2f", aiConfidenceThreshold / 100.0f));
    }
    
    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        editor.putBoolean("enable_ai", enableAISwitch.isChecked());
        editor.putBoolean("enable_auto_optimize", enableAutoOptimizeSwitch.isChecked());
        editor.putBoolean("enable_dark_mode", enableDarkModeSwitch.isChecked());
        editor.putInt("ai_confidence_threshold", aiConfidenceThresholdSeekBar.getProgress());
        
        editor.apply();
        
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        
        // Apply dark mode if changed
        if (enableDarkModeSwitch.isChecked() != sharedPreferences.getBoolean("dark_mode_applied", false)) {
            editor.putBoolean("dark_mode_applied", enableDarkModeSwitch.isChecked());
            editor.apply();
            
            // Recreate the activity to apply theme changes
            recreate();
        }
    }
    
    private void resetSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        editor.putBoolean("enable_ai", true);
        editor.putBoolean("enable_auto_optimize", false);
        editor.putBoolean("enable_dark_mode", false);
        editor.putInt("ai_confidence_threshold", 70);
        
        editor.apply();
        
        loadSettings();
        
        Toast.makeText(this, "Settings reset to defaults", Toast.LENGTH_SHORT).show();
    }
}