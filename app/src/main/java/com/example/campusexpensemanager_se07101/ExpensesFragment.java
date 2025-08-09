package com.example.campusexpensemanager_se07101;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.campusexpensemanager_se07101.adapter.ExpenseAdapter;
import com.example.campusexpensemanager_se07101.budget.AddExpenseActivity;
import com.example.campusexpensemanager_se07101.budget.EditExpenseActivity;
import com.example.campusexpensemanager_se07101.database.ExpenseModel;
import com.example.campusexpensemanager_se07101.database.ExpenseRepository;

import java.util.List;

public class ExpensesFragment extends Fragment {
    private RecyclerView rvExpense;
    private ExpenseAdapter expenseAdapter;
    private ExpenseRepository expenseRepository;
    private List<ExpenseModel> expenseList;
    private int userId;

    public ExpensesFragment() {
        // Required empty public constructor
    }

    public static ExpensesFragment newInstance(String param1, String param2) {
        ExpensesFragment fragment = new ExpensesFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ✅ 1. Lấy user_id từ SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        // ✅ 2. Ánh xạ RecyclerView
        rvExpense = view.findViewById(R.id.rvExpense);
        rvExpense.setLayoutManager(new LinearLayoutManager(getContext()));

        // ✅ 3. Khởi tạo repository và danh sách
        expenseRepository = new ExpenseRepository(getContext());
        expenseList = expenseRepository.getAllExpensesByUser(userId); // lấy dữ liệu ban đầu

        // Gán adapter
        expenseAdapter = new ExpenseAdapter(requireContext(), expenseList, new ExpenseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ExpenseModel expense) {
                if (expense != null && expense.getId() > 0) {
                    Intent intent = new Intent(requireContext(), EditExpenseActivity.class);
                    intent.putExtra("expense_id", expense.getId());
                    startActivity(intent);
                } else {
                    Toast.makeText(requireContext(), "Không thể mở chi tiêu này", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rvExpense.setAdapter(expenseAdapter);

        // ✅ 5. Nút chuyển sang AddExpenseActivity
        Button btnCreateExpense = view.findViewById(R.id.btnCreateExpense);
        btnCreateExpense.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddExpenseActivity.class);
            startActivity(intent);
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        loadExpenses();
    }

    private void loadExpenses() {
        if (expenseRepository != null && userId != -1) {
            expenseList.clear();
            List<ExpenseModel> updatedList = expenseRepository.getAllExpensesByUser(userId);
            if (updatedList != null) {
                expenseList.addAll(updatedList);
            }
            expenseAdapter.notifyDataSetChanged();
        }
    }

}
