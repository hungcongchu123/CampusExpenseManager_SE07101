package com.example.campusexpensemanager_se07101.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.example.campusexpensemanager_se07101.model.Expense;

public class ExpenseRepository {
    private final DbHelper dbHelper;

    public ExpenseRepository(Context context) {
        dbHelper = new DbHelper(context);
    }

    public long addExpense(Expense expense) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_EXPENSE_USER_ID, expense.getUserId());
        values.put(DbHelper.COL_EXPENSE_BUDGET_ID, expense.getBudgetId());
        values.put(DbHelper.COL_EXPENSE_NAME, expense.getName());
        values.put(DbHelper.COL_EXPENSE_AMOUNT, expense.getAmount());
        values.put(DbHelper.COL_EXPENSE_DATE, expense.getDate());

        long newRowId = db.insert(DbHelper.DB_TABLE_EXPENSE, null, values);
        db.close();
        return newRowId;
    }
}