package com.example.campusexpensemanager_se07101.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.campusexpensemanager_se07101.model.Budget;

import java.util.ArrayList;
import java.util.List;

public class BudgetRepository {
    private final DbHelper dbHelper;
    private static final String TAG = "BudgetRepository";

    public BudgetRepository(Context context) {
        dbHelper = new DbHelper(context);
    }

    public long AddNewBudget(int userId, String name, int money, String description,
                             int categoryId, String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_BUDGET_USER_ID, userId);
        values.put(DbHelper.COL_BUDGET_NAME, name);
        values.put(DbHelper.COL_BUDGET_MONEY, money);
        values.put(DbHelper.COL_BUDGET_DESCRIPTION, description);
        values.put(DbHelper.COL_BUDGET_CATEGORY_ID, categoryId);
        values.put(DbHelper.COL_BUDGET_START_DATE, startDate);
        values.put(DbHelper.COL_BUDGET_END_DATE, endDate);
        long newRowId = db.insert(DbHelper.DB_TABLE_BUDGET, null, values);
        db.close();
        return newRowId;
    }

    public List<Budget> getAllBudgetsByUserId(int userId) {
        List<Budget> budgets = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] projection = {
                    DbHelper.COL_BUDGET_ID,
                    DbHelper.COL_BUDGET_USER_ID,
                    DbHelper.COL_BUDGET_NAME,
                    DbHelper.COL_BUDGET_MONEY,
                    DbHelper.COL_BUDGET_DESCRIPTION,
                    DbHelper.COL_BUDGET_CATEGORY_ID,
                    DbHelper.COL_BUDGET_START_DATE,
                    DbHelper.COL_BUDGET_END_DATE
            };
            String selection = DbHelper.COL_BUDGET_USER_ID + " = ?";
            String[] selectionArgs = {String.valueOf(userId)};

            cursor = db.query(
                    DbHelper.DB_TABLE_BUDGET,
                    projection,
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_ID));
                    int budgetUserId = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_USER_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_NAME));
                    int money = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_MONEY));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_DESCRIPTION));
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_CATEGORY_ID));
                    String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_START_DATE));
                    String endDate = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_END_DATE));

                    budgets.add(new Budget(id, budgetUserId, name, money, description, categoryId, startDate, endDate));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy ngân sách từ database", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return budgets;
    }

    public Budget getBudgetById(int budgetId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        Budget budget = null;

        try {
            String[] projection = {
                    DbHelper.COL_BUDGET_ID,
                    DbHelper.COL_BUDGET_USER_ID,
                    DbHelper.COL_BUDGET_NAME,
                    DbHelper.COL_BUDGET_MONEY,
                    DbHelper.COL_BUDGET_DESCRIPTION,
                    DbHelper.COL_BUDGET_CATEGORY_ID,
                    DbHelper.COL_BUDGET_START_DATE,
                    DbHelper.COL_BUDGET_END_DATE
            };
            String selection = DbHelper.COL_BUDGET_ID + " = ?";
            String[] selectionArgs = {String.valueOf(budgetId)};

            cursor = db.query(
                    DbHelper.DB_TABLE_BUDGET,
                    projection,
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_USER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_NAME));
                int money = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_MONEY));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_DESCRIPTION));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_CATEGORY_ID));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_START_DATE));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_END_DATE));

                budget = new Budget(id, userId, name, money, description, categoryId, startDate, endDate);
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy ngân sách theo ID", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return budget;
    }

    public int updateBudget(Budget budget) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_BUDGET_NAME, budget.getName());
        values.put(DbHelper.COL_BUDGET_MONEY, budget.getMoney());
        values.put(DbHelper.COL_BUDGET_DESCRIPTION, budget.getDescription());
        values.put(DbHelper.COL_BUDGET_CATEGORY_ID, budget.getCategoryId());
        values.put(DbHelper.COL_BUDGET_START_DATE, budget.getStartDate());
        values.put(DbHelper.COL_BUDGET_END_DATE, budget.getEndDate());

        String whereClause = DbHelper.COL_BUDGET_ID + " = ?";
        String[] whereArgs = {String.valueOf(budget.getId())};

        int rowsAffected = db.update(DbHelper.DB_TABLE_BUDGET, values, whereClause, whereArgs);
        db.close();
        return rowsAffected;
    }
}