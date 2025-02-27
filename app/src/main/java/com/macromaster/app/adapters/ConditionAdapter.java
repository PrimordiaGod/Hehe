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
import com.macromaster.app.models.Condition;

import java.util.List;

/**
 * Adapter for displaying and managing conditions in a RecyclerView.
 */
public class ConditionAdapter extends RecyclerView.Adapter<ConditionAdapter.ConditionViewHolder> {

    private List<Condition> conditions;
    private Context context;

    public ConditionAdapter(List<Condition> conditions, Context context) {
        this.conditions = conditions;
        this.context = context;
    }

    @NonNull
    @Override
    public ConditionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_condition, parent, false);
        return new ConditionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConditionViewHolder holder, int position) {
        Condition condition = conditions.get(position);
        holder.conditionTextView.setText(condition.toString());
        
        holder.deleteButton.setOnClickListener(v -> {
            conditions.remove(position);
            notifyDataSetChanged();
        });
        
        holder.editButton.setOnClickListener(v -> {
            // TODO: Implement edit condition functionality
        });
    }

    @Override
    public int getItemCount() {
        return conditions.size();
    }

    static class ConditionViewHolder extends RecyclerView.ViewHolder {
        TextView conditionTextView;
        ImageButton editButton;
        ImageButton deleteButton;

        ConditionViewHolder(View itemView) {
            super(itemView);
            conditionTextView = itemView.findViewById(R.id.conditionTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}