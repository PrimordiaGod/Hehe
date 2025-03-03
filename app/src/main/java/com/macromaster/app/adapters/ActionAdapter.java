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
import com.macromaster.app.models.Action;

import java.util.List;

/**
 * Adapter for displaying and managing actions in a RecyclerView.
 */
public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ActionViewHolder> {

    private List<Action> actions;
    private Context context;

    public ActionAdapter(List<Action> actions, Context context) {
        this.actions = actions;
        this.context = context;
    }

    @NonNull
    @Override
    public ActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_action, parent, false);
        return new ActionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActionViewHolder holder, int position) {
        Action action = actions.get(position);
        holder.actionTextView.setText(action.toString());
        
        holder.deleteButton.setOnClickListener(v -> {
            actions.remove(position);
            notifyDataSetChanged();
        });
        
        holder.editButton.setOnClickListener(v -> {
            // TODO: Implement edit action functionality
        });
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    static class ActionViewHolder extends RecyclerView.ViewHolder {
        TextView actionTextView;
        ImageButton editButton;
        ImageButton deleteButton;

        ActionViewHolder(View itemView) {
            super(itemView);
            actionTextView = itemView.findViewById(R.id.actionTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}