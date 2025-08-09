package com.example.campusexpensemanager_se07101.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpensemanager_se07101.R;
import com.example.campusexpensemanager_se07101.database.BudgetModel;

import java.util.ArrayList;

public class BudgetRVAdapter extends RecyclerView.Adapter<BudgetRVAdapter.BudgetItemViewHolder> {
    public ArrayList<BudgetModel> budgetModels;
    public Context context;
    public OnClickListener clickListener;
    public interface OnClickListener{
        void onClick(int postition);
    }
    public void setOnClickListener(OnClickListener clickListener){
        this.clickListener = clickListener;
    }
    public BudgetRVAdapter(ArrayList<BudgetModel > model, Context context){
        this.budgetModels = model;
        this.context = context;
    }
    @NonNull
    @Override
    public BudgetItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_item_view, parent, false);
        return new BudgetItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetItemViewHolder holder, int position) {
        BudgetModel model = budgetModels.get(position);
        // gán dữ liệu ở đây
        holder.tvNameBudget.setText(model.getBudgetName());
        //holder.tvButgetMoney.setText(String.valueOf(model.getBudgetMoney()));
        holder.tvButgetMoney.setText(String.valueOf(model.getMoneyRemaining()));
        holder.tvCategory.setText("Category: " + model.getCategory());
        holder.tvDateRange.setText("From: " + model.getStartDate() + " to " + model.getEndDate());
        holder.tvDescription.setText("Note: " + model.getBudgetDescription());
        holder.itemView.setOnClickListener(view ->{
            if (clickListener != null){
                clickListener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return budgetModels.size(); // so luong phan list view
    }

    public class BudgetItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameBudget, tvButgetMoney, tvCategory, tvDateRange, tvDescription;
        View itemView;
        public BudgetItemViewHolder(@NonNull View itemView) {
            super(itemView.getRootView());
            this.itemView = itemView;
            tvNameBudget = itemView.findViewById(R.id.tvNameBudget);
            tvButgetMoney = itemView.findViewById(R.id.tvMoneyBudget);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDateRange = itemView.findViewById(R.id.tvDateRange);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            itemView.setOnClickListener(view -> {
                if (clickListener != null){
                    clickListener.onClick(getAdapterPosition());
                }
            });

        }
    }
}
