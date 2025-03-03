package com.macromaster.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.macromaster.app.adapters.MacroListAdapter;
import com.macromaster.app.models.Macro;

import java.util.ArrayList;
import java.util.List;

public class MacroListActivity extends AppCompatActivity {

    private RecyclerView macrosRecyclerView;
    private TextView emptyStateTextView;
    private Button createMacroButton;
    
    private MacroListAdapter macroListAdapter;
    private List<Macro> macros = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macro_list);
        
        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Macros");
        }
        
        // Initialize UI components
        macrosRecyclerView = findViewById(R.id.macrosRecyclerView);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);
        createMacroButton = findViewById(R.id.createMacroButton);
        
        // Set up RecyclerView
        setupRecyclerView();
        
        // Set click listener for create macro button
        createMacroButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MacroEditorActivity.class);
            startActivity(intent);
        });
        
        // Load macros
        loadMacros();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload macros when returning to the activity
        loadMacros();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void setupRecyclerView() {
        macroListAdapter = new MacroListAdapter(macros, this);
        macrosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        macrosRecyclerView.setAdapter(macroListAdapter);
        
        // Set item click listener
        macroListAdapter.setOnItemClickListener(position -> {
            Macro selectedMacro = macros.get(position);
            openMacroEditor(selectedMacro);
        });
        
        // Set run button click listener
        macroListAdapter.setOnRunClickListener(position -> {
            Macro selectedMacro = macros.get(position);
            runMacro(selectedMacro);
        });
        
        // Set delete button click listener
        macroListAdapter.setOnDeleteClickListener(position -> {
            Macro selectedMacro = macros.get(position);
            deleteMacro(selectedMacro, position);
        });
    }
    
    private void loadMacros() {
        // TODO: In a real app, load macros from database
        // For now, create some sample macros
        macros.clear();
        
        // Add sample macros
        List<Macro> sampleMacros = createSampleMacros();
        macros.addAll(sampleMacros);
        
        // Update UI
        macroListAdapter.notifyDataSetChanged();
        updateEmptyState();
    }
    
    private void updateEmptyState() {
        if (macros.isEmpty()) {
            macrosRecyclerView.setVisibility(View.GONE);
            emptyStateTextView.setVisibility(View.VISIBLE);
        } else {
            macrosRecyclerView.setVisibility(View.VISIBLE);
            emptyStateTextView.setVisibility(View.GONE);
        }
    }
    
    private void openMacroEditor(Macro macro) {
        // Open MacroEditorActivity with the selected macro
        Intent intent = new Intent(this, MacroEditorActivity.class);
        // TODO: Pass macro data to editor
        startActivity(intent);
    }
    
    private void runMacro(Macro macro) {
        // Run the selected macro using the accessibility service
        if (macro != null) {
            com.macromaster.app.services.MacroAccessibilityService service = 
                    com.macromaster.app.services.MacroAccessibilityService.getInstance();
            
            if (service != null) {
                service.executeMacro(macro);
                // Show a toast or notification that the macro is running
                android.widget.Toast.makeText(this, 
                        "Running macro: " + macro.getName(), 
                        android.widget.Toast.LENGTH_SHORT).show();
            } else {
                // Show error if service is not available
                android.widget.Toast.makeText(this, 
                        "Accessibility service not available. Please enable it in settings.", 
                        android.widget.Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void deleteMacro(Macro macro, int position) {
        // TODO: In a real app, delete from database
        
        // Remove from list and update adapter
        macros.remove(position);
        macroListAdapter.notifyItemRemoved(position);
        
        // Update empty state
        updateEmptyState();
        
        // Show confirmation
        android.widget.Toast.makeText(this, 
                "Macro deleted: " + macro.getName(), 
                android.widget.Toast.LENGTH_SHORT).show();
    }
    
    private List<Macro> createSampleMacros() {
        List<Macro> sampleMacros = new ArrayList<>();
        
        // Create sample actions and conditions
        List<com.macromaster.app.models.Action> loginActions = new ArrayList<>();
        loginActions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.CLICK, 300, 400));
        loginActions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.TEXT_INPUT, "username"));
        loginActions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.CLICK, 300, 500));
        loginActions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.TEXT_INPUT, "password"));
        loginActions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.CLICK, 400, 600));
        
        List<com.macromaster.app.models.Action> scrollActions = new ArrayList<>();
        scrollActions.add(new com.macromaster.app.models.Action(
                com.macromaster.app.models.Action.Type.SWIPE, 500, 1000, 500, 200, 500));
        scrollActions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.WAIT, 1000));
        scrollActions.add(new com.macromaster.app.models.Action(
                com.macromaster.app.models.Action.Type.SWIPE, 500, 1000, 500, 200, 500));
        
        List<com.macromaster.app.models.Action> aiGeneratedActions = new ArrayList<>();
        aiGeneratedActions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.CLICK, 500, 500));
        aiGeneratedActions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.TEXT_INPUT, "Hello AI"));
        aiGeneratedActions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.WAIT, 2000));
        aiGeneratedActions.add(new com.macromaster.app.models.Action(com.macromaster.app.models.Action.Type.CLICK, 600, 700));
        
        // Create sample macros
        sampleMacros.add(new com.macromaster.app.models.Macro("Login Automation", loginActions, new ArrayList<>()));
        sampleMacros.add(new com.macromaster.app.models.Macro("Scroll Feed", scrollActions, new ArrayList<>()));
        sampleMacros.add(new com.macromaster.app.models.Macro("AI Generated Demo", aiGeneratedActions, new ArrayList<>()));
        
        return sampleMacros;
    }
}