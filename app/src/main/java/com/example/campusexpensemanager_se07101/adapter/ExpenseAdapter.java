package com.example.campusexpensemanager_se07101.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpensemanager_se07101.R;
import com.example.campusexpensemanager_se07101.database.ExpenseModel;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final List<ExpenseModel> expenseList;
    private final Context context;
    private OnItemClickListener listener;

    // Thêm interface này
    // Interface sửa để nhận ExpenseModel
    public interface OnItemClickListener {
        void onItemClick(ExpenseModel expense);
    }

    public ExpenseAdapter(Context context, List<ExpenseModel> expenseList, OnItemClickListener listener) {
        this.context = context;
        this.expenseList = expenseList;
        this.listener = listener; // Khởi tạo listener
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expense_item_view, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        ExpenseModel expense = expenseList.get(position);
        // hien thi du lieu len view
        holder.tvName.setText("Expense: " + expense.getExpenseName());
        holder.tvAmount.setText("Amount: " + expense.getAmount() + "đ");
        holder.tvDesc.setText("Description: " + expense.getDescription());
        holder.tvCategory.setText("Category: " + expense.getCategory());
        holder.tvDate.setText("Date: " + expense.getDate());

        // Tạm thời ẩn budget name nếu không dùng
        holder.tvBudgetName.setVisibility(View.GONE);
        // Thêm sự kiện click cho item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(expense);
            }
        });

        // Nếu cần hiển thị ID ngân sách:
        // holder.tvBudgetName.setText("Budget ID: " + expense.getBudgetId());
        // holder.tvBudgetName.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvDesc, tvDate, tvBudgetName,tvName,tvCategory;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvExpenseAmount);
            tvDesc = itemView.findViewById(R.id.tvExpenseDesc);
            tvName = itemView.findViewById(R.id.tvExpenseName);
            tvDate = itemView.findViewById(R.id.tvExpenseDate);
            tvBudgetName = itemView.findViewById(R.id.tvExpenseBudgetName);
            tvCategory = itemView.findViewById(R.id.tvExpenseCategory);

        }
    }
}
