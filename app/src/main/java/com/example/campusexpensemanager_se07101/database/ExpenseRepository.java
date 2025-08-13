package com.example.campusexpensemanager_se07101.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseRepository extends DbHelper {

    public ExpenseRepository(@Nullable Context context) {
        super(context);
    }

    // ✅ 1. Thêm expense mới
    public long addExpense(int userId, int budgetId,String expenseName, String type, int amount, String note, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EXPENSE_USER_ID, userId);
        values.put(COL_EXPENSE_BUDGET_ID, budgetId);
        values.put(COL_EXPENSE_NAME,expenseName);
        values.put(COL_EXPENSE_TYPE, type);              // "Spending", "Income"
        values.put(COL_EXPENSE_AMOUNT, amount);
        values.put(COL_EXPENSE_NOTE, note);              // ghi chú
        values.put(COL_EXPENSE_DATE, date);
        long result = db.insert(DB_TABLE_EXPENSE, null, values);
        db.close();
        return result;
    }

    // ✅ 2. Lấy tất cả expense của 1 user
    public List<ExpenseModel> getAllExpensesByUser(int userId) {
        List<ExpenseModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DB_TABLE_EXPENSE, null,
                COL_EXPENSE_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null,
                COL_EXPENSE_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                ExpenseModel expense = new ExpenseModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_USER_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_BUDGET_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPENSE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPENSE_TYPE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPENSE_NOTE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPENSE_DATE))
                );
                list.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    // ✅ 3. Xóa expense theo ID
    public int deleteExpenseById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleted = db.delete(DB_TABLE_EXPENSE, COL_EXPENSE_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return deleted;
    }

    // ✅ 4. Trừ tiền ngân sách khi thêm expense
    public void subtractFromBudget(int budgetId, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Trừ vào ngân sách còn lại
        db.execSQL("UPDATE " + DB_TABLE_BUDGET + " SET " +
                COL_BUDGET_MONEY_REMAINING + " = " + COL_BUDGET_MONEY_REMAINING + " - ? WHERE " +
                COL_BUDGET_ID + " = ?", new Object[]{amount, budgetId});
        db.close();
    }

    // ✅ 5. Cộng tiền lại vào ngân sách khi xóa expense
    public void addBackToBudget(int budgetId, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + DB_TABLE_BUDGET + " SET " +
                COL_BUDGET_MONEY_REMAINING + " = " + COL_BUDGET_MONEY_REMAINING + " + ? WHERE " +
                COL_BUDGET_ID + " = ?", new Object[]{amount, budgetId});
        db.close();
    }
    // Lấy expense theo ID
    public ExpenseModel getExpenseById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE_EXPENSE, null,
                COL_EXPENSE_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor.moveToFirst()) {
            ExpenseModel expense = new ExpenseModel(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_USER_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_BUDGET_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPENSE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPENSE_TYPE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_AMOUNT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPENSE_NOTE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPENSE_DATE))
            );
            cursor.close();
            db.close();
            return expense;
        }
        cursor.close();
        db.close();
        return null;
    }
    // Cập nhật expense
    public int updateExpense(int id, String name, int newAmount, String note, String date, String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 1. Lấy thông tin chi tiêu cũ để biết số tiền đã chi trước đó và budgetId
        Cursor cursor = db.query(DB_TABLE_EXPENSE, new String[]{COL_EXPENSE_AMOUNT, COL_EXPENSE_BUDGET_ID},
                COL_EXPENSE_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);

        int oldAmount = 0;
        int budgetId = -1;

        if (cursor.moveToFirst()) {
            oldAmount = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_AMOUNT));
            budgetId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_BUDGET_ID));
        }
        cursor.close();

        // 2. Tính chênh lệch
        int delta = oldAmount - newAmount;

        // 3. Cập nhật lại ngân sách nếu có sự thay đổi
        if (delta != 0 && budgetId != -1) {
            if (delta > 0) {
                // hoàn lại tiền nếu chi tiêu giảm
                db.execSQL("UPDATE " + DB_TABLE_BUDGET + " SET " +
                        COL_BUDGET_MONEY_REMAINING + " = " + COL_BUDGET_MONEY_REMAINING + " + ? WHERE " +
                        COL_BUDGET_ID + " = ?", new Object[]{delta, budgetId});
            } else {
                // trừ thêm nếu chi tiêu tăng
                db.execSQL("UPDATE " + DB_TABLE_BUDGET + " SET " +
                        COL_BUDGET_MONEY_REMAINING + " = " + COL_BUDGET_MONEY_REMAINING + " - ? WHERE " +
                        COL_BUDGET_ID + " = ?", new Object[]{Math.abs(delta), budgetId});
            }
        }

        // 4. Cập nhật lại expense
        ContentValues values = new ContentValues();
        values.put(COL_EXPENSE_NAME, name);
        values.put(COL_EXPENSE_AMOUNT, newAmount);
        values.put(COL_EXPENSE_NOTE, note);
        values.put(COL_EXPENSE_DATE, date);
        values.put(COL_EXPENSE_TYPE, type);

        int updated = db.update(DB_TABLE_EXPENSE, values,
                COL_EXPENSE_ID + " = ?", new String[]{String.valueOf(id)});

        db.close();
        return updated;
    }

    // Lấy danh sách danh mục từ bảng budget
    public List<String> getAllBudgetCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COL_BUDGET_CATEGORY + " FROM " + DB_TABLE_BUDGET + " WHERE " + COL_BUDGET_CATEGORY + " IS NOT NULL", null);

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_BUDGET_CATEGORY));
                categories.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return categories;

    }
    // bieu do
    public Map<String, Float> getTotalExpenseByCategory(int userId) {
        Map<String, Float> result = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT type, SUM(amount) AS total FROM " + DB_TABLE_EXPENSE +
                        " WHERE user_id = ? GROUP BY type",
                new String[]{String.valueOf(userId)}
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String category = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                float total = cursor.getFloat(cursor.getColumnIndexOrThrow("total"));
                result.put(category, total);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close(); // thêm dòng này để đóng DB

        return result;
    }
    
    // ✅ THÊM MỚI: Lấy tổng chi tiêu theo danh mục cụ thể
    public double getTotalExpensesForCategory(int userId, String category) {
        double total = 0.0;
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT SUM(" + COL_EXPENSE_AMOUNT + ") FROM " + DB_TABLE_EXPENSE +
                " WHERE " + COL_EXPENSE_USER_ID + " = ? AND " + COL_EXPENSE_TYPE + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), category});
        
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        
        cursor.close();
        db.close();
        return total;
    }
    
    // ✅ THÊM MỚI: Kiểm tra trùng lặp expense theo tên
    public boolean isExpenseDuplicate(int userId, String expenseName, int excludeExpenseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT COUNT(*) FROM " + DB_TABLE_EXPENSE + 
                " WHERE " + COL_EXPENSE_USER_ID + " = ? AND " + 
                COL_EXPENSE_NAME + " = ? AND " + 
                COL_EXPENSE_ID + " != ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId), expenseName, String.valueOf(excludeExpenseId)
        });
        
        boolean isDuplicate = false;
        if (cursor.moveToFirst()) {
            isDuplicate = cursor.getInt(0) > 0;
        }
        
        cursor.close();
        db.close();
        return isDuplicate;
    }
    
    // ✅ THÊM MỚI: Kiểm tra trùng lặp expense khi thêm mới
    public boolean isExpenseDuplicateNew(int userId, String expenseName) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT COUNT(*) FROM " + DB_TABLE_EXPENSE + 
                " WHERE " + COL_EXPENSE_USER_ID + " = ? AND " + 
                COL_EXPENSE_NAME + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId), expenseName
        });
        
        boolean isDuplicate = false;
        if (cursor.moveToFirst()) {
            isDuplicate = cursor.getInt(0) > 0;
        }
        
        cursor.close();
        db.close();
        return isDuplicate;
    }
}
