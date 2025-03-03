package com.macromaster.app.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.macromaster.app.R;
import com.macromaster.app.models.Condition;

/**
 * Dialog for creating and editing conditions.
 */
public class ConditionDialog extends Dialog {

    public interface ConditionDialogListener {
        void onConditionCreated(Condition condition);
    }

    private ConditionDialogListener listener;
    private RadioGroup conditionTypeRadioGroup;
    private View imageMatchLayout;
    private View textMatchLayout;
    private View timeoutLayout;
    private ImageView targetImageView;
    private Button captureImageButton;
    private SeekBar thresholdSeekBar;
    private TextView thresholdValueTextView;
    private CheckBox stopOnMatchCheckBox;
    private EditText targetTextEditText;
    private CheckBox textStopOnMatchCheckBox;
    private EditText timeoutEditText;
    private Button saveButton;
    private Button cancelButton;
    
    private Bitmap capturedImage;

    public ConditionDialog(@NonNull Context context, ConditionDialogListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_condition);

        // Initialize views
        conditionTypeRadioGroup = findViewById(R.id.conditionTypeRadioGroup);
        imageMatchLayout = findViewById(R.id.imageMatchLayout);
        textMatchLayout = findViewById(R.id.textMatchLayout);
        timeoutLayout = findViewById(R.id.timeoutLayout);
        targetImageView = findViewById(R.id.targetImageView);
        captureImageButton = findViewById(R.id.captureImageButton);
        thresholdSeekBar = findViewById(R.id.thresholdSeekBar);
        thresholdValueTextView = findViewById(R.id.thresholdValueTextView);
        stopOnMatchCheckBox = findViewById(R.id.stopOnMatchCheckBox);
        targetTextEditText = findViewById(R.id.targetTextEditText);
        textStopOnMatchCheckBox = findViewById(R.id.textStopOnMatchCheckBox);
        timeoutEditText = findViewById(R.id.timeoutEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Set up radio group listener
        conditionTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.imageMatchRadioButton) {
                showImageMatchLayout();
            } else if (checkedId == R.id.textMatchRadioButton) {
                showTextMatchLayout();
            } else if (checkedId == R.id.timeoutRadioButton) {
                showTimeoutLayout();
            }
        });

        // Set up threshold seek bar
        thresholdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float threshold = progress / 100.0f;
                thresholdValueTextView.setText(String.format("%.2f", threshold));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Set up capture image button
        captureImageButton.setOnClickListener(v -> {
            // TODO: Implement screen capture functionality
            // For now, we'll just show a placeholder
            targetImageView.setImageResource(android.R.drawable.ic_menu_camera);
            targetImageView.setVisibility(View.VISIBLE);
        });

        // Set default selection
        ((RadioButton) conditionTypeRadioGroup.getChildAt(0)).setChecked(true);
        showImageMatchLayout();

        // Set up button listeners
        saveButton.setOnClickListener(v -> saveCondition());
        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void showImageMatchLayout() {
        imageMatchLayout.setVisibility(View.VISIBLE);
        textMatchLayout.setVisibility(View.GONE);
        timeoutLayout.setVisibility(View.GONE);
    }

    private void showTextMatchLayout() {
        imageMatchLayout.setVisibility(View.GONE);
        textMatchLayout.setVisibility(View.VISIBLE);
        timeoutLayout.setVisibility(View.GONE);
    }

    private void showTimeoutLayout() {
        imageMatchLayout.setVisibility(View.GONE);
        textMatchLayout.setVisibility(View.GONE);
        timeoutLayout.setVisibility(View.VISIBLE);
    }

    private void saveCondition() {
        int selectedId = conditionTypeRadioGroup.getCheckedRadioButtonId();
        Condition condition = null;

        try {
            if (selectedId == R.id.imageMatchRadioButton) {
                if (capturedImage != null) {
                    float threshold = thresholdSeekBar.getProgress() / 100.0f;
                    boolean stopOnMatch = stopOnMatchCheckBox.isChecked();
                    condition = new Condition(Condition.Type.IMAGE_MATCH, capturedImage, threshold, stopOnMatch);
                } else {
                    // Show error message
                    TextView errorTextView = findViewById(R.id.errorTextView);
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Please capture an image first");
                    return;
                }
            } else if (selectedId == R.id.textMatchRadioButton) {
                String targetText = targetTextEditText.getText().toString();
                if (!targetText.isEmpty()) {
                    boolean stopOnMatch = textStopOnMatchCheckBox.isChecked();
                    condition = new Condition(Condition.Type.TEXT_MATCH, targetText, stopOnMatch);
                } else {
                    // Show error message
                    TextView errorTextView = findViewById(R.id.errorTextView);
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Please enter target text");
                    return;
                }
            } else if (selectedId == R.id.timeoutRadioButton) {
                String timeoutStr = timeoutEditText.getText().toString();
                if (!timeoutStr.isEmpty()) {
                    long timeout = Long.parseLong(timeoutStr);
                    condition = new Condition(Condition.Type.TIMEOUT, timeout);
                } else {
                    // Show error message
                    TextView errorTextView = findViewById(R.id.errorTextView);
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Please enter timeout value");
                    return;
                }
            }

            if (condition != null && listener != null) {
                listener.onConditionCreated(condition);
                dismiss();
            }
        } catch (NumberFormatException e) {
            // Show error message
            TextView errorTextView = findViewById(R.id.errorTextView);
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText("Please enter valid numbers");
        }
    }
}