package com.example.campusexpensemanager_se07101.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpensemanager_se07101.R;
import com.example.campusexpensemanager_se07101.model.Budget;

import java.util.List;

public class BudgetRVAdapter extends RecyclerView.Adapter<BudgetRVAdapter.BudgetItemViewHolder> {
    public List<Budget> budgetList;
    public Context context;
    public OnClickListener clickListener;

    public interface OnClickListener {
        void onClick(int postition);
    }

    public void setOnClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public BudgetRVAdapter(List<Budget> budgetList, Context context) {
        this.budgetList = budgetList;
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
        Budget budget = budgetList.get(position);
        holder.tvNameBudget.setText(budget.getName());
        holder.tvButgetMoney.setText(String.valueOf(budget.getMoney()));
        // Note: Các thuộc tính Category và DateRange không tồn tại trong class Budget mới,
        // nếu muốn hiển thị, bạn cần chỉnh sửa lớp Budget hoặc truy vấn thêm từ CategoryRepository.
        // holder.tvCategory.setText("Category: " + budget.getCategory());
        // holder.tvDateRange.setText("From: " + budget.getStartDate() + " to " + budget.getEndDate());
        holder.tvDescription.setText("Note: " + budget.getDescription());

        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }

    public void updateData(List<Budget> newBudgetList) {
        this.budgetList = newBudgetList;
        notifyDataSetChanged();
    }

    public class BudgetItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameBudget, tvButgetMoney, tvCategory, tvDateRange, tvDescription;

        public BudgetItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameBudget = itemView.findViewById(R.id.tvNameBudget);
            tvButgetMoney = itemView.findViewById(R.id.tvMoneyBudget);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDateRange = itemView.findViewById(R.id.tvDateRange);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}