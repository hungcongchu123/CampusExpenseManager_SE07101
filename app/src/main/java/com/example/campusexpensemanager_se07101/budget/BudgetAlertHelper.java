package com.example.campusexpensemanager_se07101.budget;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import com.example.campusexpensemanager_se07101.database.BudgetRepository;
import com.example.campusexpensemanager_se07101.database.ExpenseRepository;
import com.example.campusexpensemanager_se07101.budget.BudgetSettingsHelper;

public class BudgetAlertHelper {
    private Context context;
    private BudgetRepository budgetRepository;
    private ExpenseRepository expenseRepository;
    private BudgetSettingsHelper settingsHelper;

    public BudgetAlertHelper(Context context) {
        this.context = context;
        this.budgetRepository = new BudgetRepository(context);
        this.expenseRepository = new ExpenseRepository(context);
        this.settingsHelper = new BudgetSettingsHelper(context);
    }

    public void checkBudget(int userId, String category, double amount) {
        // Kiểm tra xem thông báo có được bật không
        if (!settingsHelper.areNotificationsEnabled()) {
            return;
        }

        double budget = budgetRepository.getBudgetForCategory(userId, category);
        if (budget <= 0) return;

        double totalExpenses = expenseRepository.getTotalExpensesForCategory(userId, category);
        double totalAfterExpense = totalExpenses + amount;
        double percentage = totalAfterExpense / budget;

        // Lấy ngưỡng cảnh báo từ cài đặt
        double warningThreshold = settingsHelper.getWarningThreshold();

        if (percentage >= 1.0) {
            // Vượt quá ngân sách
            showAlert("🚨 Budget Exceeded!",
                "Category " + category + " has exceeded the budget of " +
                String.format("%.0f", budget) + "đ", null);
        } else if (percentage >= warningThreshold) {
            // Gần ngưỡng ngân sách
            showAlert("⚠️ Budget Warning",
                "Category " + category + " has used " +
                String.format("%.1f", percentage * 100) + "% of budget", null);
        }
    }

    public void checkBudgetWithCallback(int userId, String category, double amount, Runnable onDialogDismissed) {
        // Kiểm tra xem thông báo có được bật không
        if (!settingsHelper.areNotificationsEnabled()) {
            if (onDialogDismissed != null) onDialogDismissed.run();
            return;
        }

        double budget = budgetRepository.getBudgetForCategory(userId, category);
        if (budget <= 0) {
            if (onDialogDismissed != null) onDialogDismissed.run();
            return;
        }

        double totalExpenses = expenseRepository.getTotalExpensesForCategory(userId, category);
        double totalAfterExpense = totalExpenses + amount;
        double percentage = totalAfterExpense / budget;

        // Lấy ngưỡng cảnh báo từ cài đặt
        double warningThreshold = settingsHelper.getWarningThreshold();

        if (percentage >= 1.0) {
            // Vượt quá ngân sách
            showAlert("🚨 Budget Exceeded!",
                "Category " + category + " has exceeded the budget of " +
                String.format("%.0f", budget) + "đ", onDialogDismissed);
        } else if (percentage >= warningThreshold) {
            // Gần ngưỡng ngân sách
            showAlert("⚠️ Budget Warning",
                "Category " + category + " has used " +
                String.format("%.1f", percentage * 100) + "% of budget", onDialogDismissed);
        } else {
            if (onDialogDismissed != null) onDialogDismissed.run();
        }
    }

    private void showAlert(String title, String message, Runnable onDialogDismissed) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
               .setMessage(message)
               .setPositiveButton("OK", (dialog, which) -> {
                   if (onDialogDismissed != null) {
                       onDialogDismissed.run();
                   }
               })
               .setCancelable(false); // Không cho phép đóng dialog bằng cách chạm bên ngoài
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void checkAllBudgets(int userId) {
        // Kiểm tra xem thông báo có được bật không
        if (!settingsHelper.areNotificationsEnabled()) {
            return;
        }

        try {
            for (String category : budgetRepository.getAllCategoriesByUser(userId)) {
                double budget = budgetRepository.getBudgetForCategory(userId, category);
                if (budget <= 0) continue;

                double expenses = expenseRepository.getTotalExpensesForCategory(userId, category);
                double percentage = expenses / budget;

                // Lấy ngưỡng cảnh báo từ cài đặt
                double warningThreshold = settingsHelper.getWarningThreshold();

                if (percentage >= 1.0) {
                    Toast.makeText(context,
                        "🚨 " + category + " budget exceeded!",
                        Toast.LENGTH_LONG).show();
                } else if (percentage >= warningThreshold) {
                    Toast.makeText(context,
                        "⚠️ " + category + " budget nearly exhausted",
                        Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            // Bỏ qua lỗi
        }
    }
}
