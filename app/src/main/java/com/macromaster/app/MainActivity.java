package com.macromaster.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_OVERLAY_PERMISSION = 1001;
    private static final int REQUEST_ACCESSIBILITY_PERMISSION = 1002;

    private Button createMacroButton;
    private Button myMacrosButton;
    private Button settingsButton;
    private Button helpButton;
    private TextView permissionStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        createMacroButton = findViewById(R.id.createMacroButton);
        myMacrosButton = findViewById(R.id.myMacrosButton);
        settingsButton = findViewById(R.id.settingsButton);
        helpButton = findViewById(R.id.helpButton);
        permissionStatusTextView = findViewById(R.id.permissionStatusTextView);

        // Set click listeners
        createMacroButton.setOnClickListener(v -> openMacroEditor());
        myMacrosButton.setOnClickListener(v -> openMacroList());
        settingsButton.setOnClickListener(v -> openSettings());
        helpButton.setOnClickListener(v -> openHelp());

        // Check permissions on startup
        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update permission status when returning to the activity
        checkPermissions();
    }

    private void openMacroEditor() {
        if (hasRequiredPermissions()) {
            Intent intent = new Intent(this, MacroEditorActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please grant all required permissions first", Toast.LENGTH_SHORT).show();
            checkPermissions();
        }
    }

    private void openMacroList() {
        // TODO: Implement macro list activity
        Toast.makeText(this, "My Macros feature coming soon", Toast.LENGTH_SHORT).show();
    }

    private void openSettings() {
        // TODO: Implement settings activity
        checkPermissions();
    }

    private void openHelp() {
        // TODO: Implement help activity
        Toast.makeText(this, "Help feature coming soon", Toast.LENGTH_SHORT).show();
    }

    private boolean hasRequiredPermissions() {
        boolean hasOverlayPermission = Settings.canDrawOverlays(this);
        boolean hasAccessibilityPermission = isAccessibilityServiceEnabled();
        return hasOverlayPermission && hasAccessibilityPermission;
    }

    private void checkPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            permissionStatusTextView.setVisibility(View.VISIBLE);
            permissionStatusTextView.setText("Overlay permission is required. Tap here to grant.");
            permissionStatusTextView.setOnClickListener(v -> requestOverlayPermission());
        } else if (!isAccessibilityServiceEnabled()) {
            permissionStatusTextView.setVisibility(View.VISIBLE);
            permissionStatusTextView.setText("Accessibility service is required. Tap here to enable.");
            permissionStatusTextView.setOnClickListener(v -> requestAccessibilityPermission());
        } else {
            permissionStatusTextView.setVisibility(View.GONE);
        }
    }

    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
    }

    private void requestAccessibilityPermission() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, REQUEST_ACCESSIBILITY_PERMISSION);
    }

    private boolean isAccessibilityServiceEnabled() {
        // Check if our accessibility service is enabled
        String serviceName = getPackageName() + "/" + MacroAccessibilityService.class.getCanonicalName();
        String enabledServices = Settings.Secure.getString(
                getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return enabledServices != null && enabledServices.contains(serviceName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OVERLAY_PERMISSION || requestCode == REQUEST_ACCESSIBILITY_PERMISSION) {
            // Check permissions again after returning from permission request
            checkPermissions();
        }
    }
}