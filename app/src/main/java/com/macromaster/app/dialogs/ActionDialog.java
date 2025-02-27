package com.macromaster.app.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.macromaster.app.R;
import com.macromaster.app.models.Action;

/**
 * Dialog for creating and editing actions.
 */
public class ActionDialog extends Dialog {

    public interface ActionDialogListener {
        void onActionCreated(Action action);
    }

    private ActionDialogListener listener;
    private RadioGroup actionTypeRadioGroup;
    private View clickLayout;
    private View swipeLayout;
    private View textInputLayout;
    private View waitLayout;
    private EditText xEditText;
    private EditText yEditText;
    private EditText endXEditText;
    private EditText endYEditText;
    private EditText swipeDurationEditText;
    private EditText textInputEditText;
    private EditText waitDurationEditText;
    private Button saveButton;
    private Button cancelButton;

    public ActionDialog(@NonNull Context context, ActionDialogListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_action);

        // Initialize views
        actionTypeRadioGroup = findViewById(R.id.actionTypeRadioGroup);
        clickLayout = findViewById(R.id.clickLayout);
        swipeLayout = findViewById(R.id.swipeLayout);
        textInputLayout = findViewById(R.id.textInputLayout);
        waitLayout = findViewById(R.id.waitLayout);
        xEditText = findViewById(R.id.xEditText);
        yEditText = findViewById(R.id.yEditText);
        endXEditText = findViewById(R.id.endXEditText);
        endYEditText = findViewById(R.id.endYEditText);
        swipeDurationEditText = findViewById(R.id.swipeDurationEditText);
        textInputEditText = findViewById(R.id.textInputEditText);
        waitDurationEditText = findViewById(R.id.waitDurationEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Set up radio group listener
        actionTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.clickRadioButton) {
                showClickLayout();
            } else if (checkedId == R.id.swipeRadioButton) {
                showSwipeLayout();
            } else if (checkedId == R.id.textInputRadioButton) {
                showTextInputLayout();
            } else if (checkedId == R.id.waitRadioButton) {
                showWaitLayout();
            }
        });

        // Set default selection
        ((RadioButton) actionTypeRadioGroup.getChildAt(0)).setChecked(true);
        showClickLayout();

        // Set up button listeners
        saveButton.setOnClickListener(v -> saveAction());
        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void showClickLayout() {
        clickLayout.setVisibility(View.VISIBLE);
        swipeLayout.setVisibility(View.GONE);
        textInputLayout.setVisibility(View.GONE);
        waitLayout.setVisibility(View.GONE);
    }

    private void showSwipeLayout() {
        clickLayout.setVisibility(View.GONE);
        swipeLayout.setVisibility(View.VISIBLE);
        textInputLayout.setVisibility(View.GONE);
        waitLayout.setVisibility(View.GONE);
    }

    private void showTextInputLayout() {
        clickLayout.setVisibility(View.GONE);
        swipeLayout.setVisibility(View.GONE);
        textInputLayout.setVisibility(View.VISIBLE);
        waitLayout.setVisibility(View.GONE);
    }

    private void showWaitLayout() {
        clickLayout.setVisibility(View.GONE);
        swipeLayout.setVisibility(View.GONE);
        textInputLayout.setVisibility(View.GONE);
        waitLayout.setVisibility(View.VISIBLE);
    }

    private void saveAction() {
        int selectedId = actionTypeRadioGroup.getCheckedRadioButtonId();
        Action action = null;

        try {
            if (selectedId == R.id.clickRadioButton) {
                int x = Integer.parseInt(xEditText.getText().toString());
                int y = Integer.parseInt(yEditText.getText().toString());
                action = new Action(Action.Type.CLICK, x, y);
            } else if (selectedId == R.id.swipeRadioButton) {
                int x = Integer.parseInt(xEditText.getText().toString());
                int y = Integer.parseInt(yEditText.getText().toString());
                int endX = Integer.parseInt(endXEditText.getText().toString());
                int endY = Integer.parseInt(endYEditText.getText().toString());
                long duration = Long.parseLong(swipeDurationEditText.getText().toString());
                action = new Action(Action.Type.SWIPE, x, y, endX, endY, duration);
            } else if (selectedId == R.id.textInputRadioButton) {
                String text = textInputEditText.getText().toString();
                action = new Action(Action.Type.TEXT_INPUT, text);
            } else if (selectedId == R.id.waitRadioButton) {
                long duration = Long.parseLong(waitDurationEditText.getText().toString());
                action = new Action(Action.Type.WAIT, duration);
            }

            if (action != null && listener != null) {
                listener.onActionCreated(action);
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