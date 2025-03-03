package com.macromaster.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.macromaster.app.adapters.ActionAdapter;
import com.macromaster.app.adapters.ConditionAdapter;
import com.macromaster.app.dialogs.ActionDialog;
import com.macromaster.app.dialogs.ConditionDialog;
import com.macromaster.app.models.Action;
import com.macromaster.app.models.Condition;
import com.macromaster.app.models.Macro;

import java.util.ArrayList;
import java.util.List;

public class MacroEditorActivity extends AppCompatActivity {

    private EditText macroNameEditText;
    private RecyclerView actionsRecyclerView;
    private RecyclerView conditionsRecyclerView;
    private Button addActionButton;
    private Button addConditionButton;
    private Button saveMacroButton;
    private Button testMacroButton;

    private ActionAdapter actionAdapter;
    private ConditionAdapter conditionAdapter;

    private List<Action> actions = new ArrayList<>();
    private List<Condition> conditions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macro_editor);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Create Macro");
        }

        // Initialize UI components
        macroNameEditText = findViewById(R.id.macroNameEditText);
        actionsRecyclerView = findViewById(R.id.actionsRecyclerView);
        conditionsRecyclerView = findViewById(R.id.conditionsRecyclerView);
        addActionButton = findViewById(R.id.addActionButton);
        addConditionButton = findViewById(R.id.addConditionButton);
        saveMacroButton = findViewById(R.id.saveMacroButton);
        testMacroButton = findViewById(R.id.testMacroButton);

        // Set up RecyclerViews
        setupActionRecyclerView();
        setupConditionRecyclerView();

        // Set click listeners
        addActionButton.setOnClickListener(v -> showAddActionDialog());
        addConditionButton.setOnClickListener(v -> showAddConditionDialog());
        saveMacroButton.setOnClickListener(v -> saveMacro());
        testMacroButton.setOnClickListener(v -> testMacro());
    }

    private void setupActionRecyclerView() {
        actionAdapter = new ActionAdapter(actions, this);
        actionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        actionsRecyclerView.setAdapter(actionAdapter);
    }

    private void setupConditionRecyclerView() {
        conditionAdapter = new ConditionAdapter(conditions, this);
        conditionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        conditionsRecyclerView.setAdapter(conditionAdapter);
    }

    private void showAddActionDialog() {
        ActionDialog dialog = new ActionDialog(this, action -> {
            actions.add(action);
            actionAdapter.notifyDataSetChanged();
        });
        dialog.show();
    }

    private void showAddConditionDialog() {
        ConditionDialog dialog = new ConditionDialog(this, condition -> {
            conditions.add(condition);
            conditionAdapter.notifyDataSetChanged();
        });
        dialog.show();
    }

    private void saveMacro() {
        String macroName = macroNameEditText.getText().toString().trim();
        if (macroName.isEmpty()) {
            Toast.makeText(this, "Please enter a macro name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (actions.isEmpty()) {
            Toast.makeText(this, "Please add at least one action", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create and save the macro
        Macro macro = new Macro(macroName, actions, conditions);
        // TODO: Save macro to database or file
        
        Toast.makeText(this, "Macro saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void testMacro() {
        if (actions.isEmpty()) {
            Toast.makeText(this, "Please add at least one action", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Implement macro testing
        Toast.makeText(this, "Test feature coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}