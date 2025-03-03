package com.macromaster.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpActivity extends AppCompatActivity {

    private ExpandableListView expandableListView;
    
    // Constants for the adapter
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        
        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Help & FAQ");
        }
        
        // Initialize UI components
        expandableListView = findViewById(R.id.expandableListView);
        
        // Set up expandable list
        setupExpandableList();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void setupExpandableList() {
        // Create groups (questions)
        List<Map<String, String>> groupData = new ArrayList<>();
        List<List<Map<String, String>>> childData = new ArrayList<>();
        
        // Add FAQ items
        addFaqItem(groupData, childData, 
                "What is MacroMaster?", 
                "MacroMaster is an advanced Android macro application that enables users to automate repetitive tasks through image recognition, AI-powered suggestions, and multi-layered task management.");
        
        addFaqItem(groupData, childData, 
                "How do I create a macro?", 
                "To create a macro, tap the 'Create Macro' button on the home screen. Then, add actions (like clicks, swipes, or text input) and conditions (like image matching or timeouts) to build your macro sequence.");
        
        addFaqItem(groupData, childData, 
                "What permissions does the app need?", 
                "MacroMaster requires Accessibility Service permission to detect screen elements and perform actions, and Overlay permission to display controls while running macros. These permissions are necessary for the app to function properly.");
        
        addFaqItem(groupData, childData, 
                "How do the AI features work?", 
                "MacroMaster uses AI to analyze screen content, suggest actions, generate macros from natural language descriptions, and optimize existing macros. You can access these features from the AI Assistant section.");
        
        addFaqItem(groupData, childData, 
                "Can I run macros in the background?", 
                "Yes, macros can run in the background using the Accessibility Service. However, for optimal performance, it's recommended to keep the screen on while macros are running.");
        
        addFaqItem(groupData, childData, 
                "How do I use conditions in macros?", 
                "Conditions allow your macro to respond to changes on the screen. You can add conditions like 'wait for an image to appear' or 'stop if text is found' to make your macros more intelligent and adaptive.");
        
        addFaqItem(groupData, childData, 
                "Is there a limit to how many macros I can create?", 
                "There is no hard limit on the number of macros you can create. However, very complex macros with many actions and conditions may use more system resources.");
        
        addFaqItem(groupData, childData, 
                "How can I optimize my macros?", 
                "You can use the AI Assistant to analyze and optimize your macros. The AI can suggest improvements like combining consecutive wait actions or optimizing click sequences for better performance.");
        
        // Create adapter
        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                this,
                groupData,
                android.R.layout.simple_expandable_list_item_1,
                new String[] { TITLE },
                new int[] { android.R.id.text1 },
                childData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] { CONTENT },
                new int[] { android.R.id.text2 }
        );
        
        // Set adapter
        expandableListView.setAdapter(adapter);
    }
    
    private void addFaqItem(List<Map<String, String>> groupData, 
                           List<List<Map<String, String>>> childData,
                           String question, String answer) {
        // Add group item
        Map<String, String> groupMap = new HashMap<>();
        groupMap.put(TITLE, question);
        groupData.add(groupMap);
        
        // Add child item
        List<Map<String, String>> children = new ArrayList<>();
        Map<String, String> childMap = new HashMap<>();
        childMap.put(CONTENT, answer);
        children.add(childMap);
        childData.add(children);
    }
}