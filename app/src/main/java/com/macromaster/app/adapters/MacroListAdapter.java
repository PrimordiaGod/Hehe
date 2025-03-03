package com.macromaster.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.macromaster.app.R;
import com.macromaster.app.models.Macro;

import java.util.List;

/**
 * Adapter for displaying and managing macros in a RecyclerView.
 */
public class MacroListAdapter extends RecyclerView.Adapter<MacroListAdapter.MacroViewHolder> {

    private List<Macro> macros;
    private Context context;
    
    // Interfaces for click listeners
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    
    public interface OnRunClickListener {
        void onRunClick(int position);
    }
    
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
    
    private OnItemClickListener itemClickListener;
    private OnRunClickListener runClickListener;
    private OnDeleteClickListener deleteClickListener;

    public MacroListAdapter(List<Macro> macros, Context context) {
        this.macros = macros;
        this.context = context;
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
    
    public void setOnRunClickListener(OnRunClickListener listener) {
        this.runClickListener = listener;
    }
    
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    @NonNull
    @Override
    public MacroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_macro, parent, false);
        return new MacroViewHolder(view, itemClickListener, runClickListener, deleteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MacroViewHolder holder, int position) {
        Macro macro = macros.get(position);
        
        holder.macroNameTextView.setText(macro.getName());
        
        // Display action count
        int actionCount = macro.getActions().size();
        holder.actionCountTextView.setText(actionCount + " action" + (actionCount != 1 ? "s" : ""));
        
        // Display condition count
        int conditionCount = macro.getConditions().size();
        holder.conditionCountTextView.setText(conditionCount + " condition" + (conditionCount != 1 ? "s" : ""));
        
        // Display last modified date
        long lastModified = macro.getLastModifiedAt();
        String formattedDate = android.text.format.DateFormat.format("MMM dd, yyyy", lastModified).toString();
        holder.lastModifiedTextView.setText("Modified: " + formattedDate);
        
        // Set running status
        if (macro.isRunning()) {
            holder.statusTextView.setVisibility(View.VISIBLE);
            holder.statusTextView.setText("Running");
            holder.runButton.setEnabled(false);
        } else {
            holder.statusTextView.setVisibility(View.GONE);
            holder.runButton.setEnabled(true);
        }
    }

    @Override
    public int getItemCount() {
        return macros.size();
    }

    static class MacroViewHolder extends RecyclerView.ViewHolder {
        TextView macroNameTextView;
        TextView actionCountTextView;
        TextView conditionCountTextView;
        TextView lastModifiedTextView;
        TextView statusTextView;
        ImageButton runButton;
        ImageButton editButton;
        ImageButton deleteButton;

        MacroViewHolder(View itemView, 
                       final OnItemClickListener itemClickListener,
                       final OnRunClickListener runClickListener,
                       final OnDeleteClickListener deleteClickListener) {
            super(itemView);
            
            macroNameTextView = itemView.findViewById(R.id.macroNameTextView);
            actionCountTextView = itemView.findViewById(R.id.actionCountTextView);
            conditionCountTextView = itemView.findViewById(R.id.conditionCountTextView);
            lastModifiedTextView = itemView.findViewById(R.id.lastModifiedTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            runButton = itemView.findViewById(R.id.runButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            
            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(position);
                    }
                }
            });
            
            runButton.setOnClickListener(v -> {
                if (runClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        runClickListener.onRunClick(position);
                    }
                }
            });
            
            editButton.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(position);
                    }
                }
            });
            
            deleteButton.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        deleteClickListener.onDeleteClick(position);
                    }
                }
            });
        }
    }
}