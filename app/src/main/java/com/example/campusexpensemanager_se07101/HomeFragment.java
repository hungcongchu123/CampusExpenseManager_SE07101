package com.example.campusexpensemanager_se07101;

import static com.example.campusexpensemanager_se07101.database.DbHelper.DB_TABLE_EXPENSE;
import com.example.campusexpensemanager_se07101.database.BudgetRepository;
import com.example.campusexpensemanager_se07101.budget.BudgetAlertHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.campusexpensemanager_se07101.database.ExpenseRepository;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ExpenseRepository expenseRepository;
    private BudgetRepository budgetRepository;
    private BudgetAlertHelper budgetAlertHelper;
    private TextView textTotalExpense, textRemaining;
    private int userId;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Lấy userId từ SharedPreferences
        SharedPreferences prefs = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        Log.d("HomeFragment", "userId: " + userId);
        // Gán TextView
        textTotalExpense = view.findViewById(R.id.textTotalExpense);
        textRemaining = view.findViewById(R.id.textRemaining);
        // Gọi PieChart
        expenseRepository = new ExpenseRepository(getContext());
        budgetRepository = new BudgetRepository(getContext());
        budgetAlertHelper = new BudgetAlertHelper(getContext());
        PieChart pieChart = view.findViewById(R.id.pieChart);
        setupPieChart(pieChart);

        // ✅ Kiểm tra cảnh báo ngân sách
        budgetAlertHelper.checkAllBudgets(userId);

        return view;

    }

    private void setupPieChart(PieChart pieChart) {
        Map<String, Float> expenseMap = expenseRepository.getTotalExpenseByCategory(userId);

        if (expenseMap.isEmpty()) {
            pieChart.setNoDataText("No expense data available.");
            textTotalExpense.setText("Total Expenses: 0đ");
            textRemaining.setText("Remaining: 0đ");
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        float totalExpense = 0f;

        for (Map.Entry<String, Float> entry : expenseMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            totalExpense += entry.getValue();
        }
        // Lấy ngân sách từ SharedPreferences
        float budget = (float) budgetRepository.getTotalBudget(userId);
        float remaining = budget - totalExpense;
        // Hiển thị lên TextView
        textTotalExpense.setText("Total Expenses: " + totalExpense + "đ");
        textRemaining.setText("Remaining: " + remaining + "đ");
        // thiet lap bieu do
        PieDataSet dataSet = new PieDataSet(entries, "Expenses by Category");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));

        // Tạo PieData và gán vào chart
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Expense Overview");
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(false);
        Description desc = new Description();
        desc.setText("Tổng quan chi tiêu");
        pieChart.setDescription(desc);

        pieChart.animateY(1000);
        pieChart.invalidate();
        // ✅ Xử lý khi click mảnh biểu đồ
        pieChart.setOnChartValueSelectedListener(new com.github.mikephil.charting.listener.OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, com.github.mikephil.charting.highlight.Highlight h) {
                if (e instanceof PieEntry) {
                    PieEntry pieEntry = (PieEntry) e;
                    String category = pieEntry.getLabel();
                    float expense = pieEntry.getValue();

                    float budgetForCategory = (float) budgetRepository.getBudgetForCategory(userId, category);
                    float remaining = budgetForCategory - expense;

                    showCategoryDetailDialog(category, expense, budgetForCategory, remaining);
                }
            }

            @Override
            public void onNothingSelected() {
                // Không làm gì cả
            }
        });
    }
    private void showCategoryDetailDialog(String category, float expense, float budget, float remaining) {
        String message = "Category Details: " + category + "\n"
                + "Expenses: " + String.format("%,.0f", expense) + "đ\n"
                + "Budget for this category: " + String.format("%,.0f", budget) + "đ\n"
                + "Remaining: " + String.format("%,.0f", remaining) + "đ";

        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Category Details")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
    @Override
    public void onResume() {
        super.onResume();
        // Gọi lại hàm vẽ biểu đồ mỗi khi quay lại màn hình Home
        PieChart pieChart = getView().findViewById(R.id.pieChart);
        setupPieChart(pieChart);
        // ✅ Kiểm tra lại cảnh báo ngân sách khi quay lại fragment
        budgetAlertHelper.checkAllBudgets(userId);
    }
    

}
