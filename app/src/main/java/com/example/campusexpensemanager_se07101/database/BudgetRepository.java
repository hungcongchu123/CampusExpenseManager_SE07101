package com.example.campusexpensemanager_se07101.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BudgetRepository extends DbHelper{
    public BudgetRepository(@Nullable Context context) { super(context); }
    public int updateBudgetById(String budgetName, int budgetMoney, String description,
                                String category, String startDate, String endDate, int id)
    {
        @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter dft = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        @SuppressLint({"NewApi", "LocalSuppress"}) ZonedDateTime zone = ZonedDateTime.now();
        @SuppressLint({"NewApi", "LocalSuppress"}) String CurrentDate = dft.format(zone);
        SQLiteDatabase db = getWritableDatabase(); // cap nhap lai du lieu
        ContentValues values = new ContentValues();// tao ra doi tuong de chua cac cot gia tri
        values.put(DbHelper.COL_BUDGET_NAME, budgetName);              // Tên ngân sách
        values.put(DbHelper.COL_BUDGET_MONEY, budgetMoney);            // Số tiền
        values.put(DbHelper.COL_BUDGET_MONEY_REMAINING, budgetMoney);  // ✅ Reset lại số dư khi sửa
        values.put(DbHelper.COL_BUDGET_DESCRIPTION, description);      // Mô tả
        values.put(DbHelper.COL_BUDGET_CATEGORY, category);            // Loại
        values.put(DbHelper.COL_BUDGET_START_DATE, startDate);         // Ngày bắt đầu
        values.put(DbHelper.COL_BUDGET_END_DATE, endDate);             // Ngày kết thúc
        values.put(DbHelper.COL_UPDATED_AT, CurrentDate);       // Ngày cập nhật
        return db.update(DbHelper.DB_TABLE_BUDGET, values, "id = ?", new String[]{String.valueOf(id)});

    }
    public long AddNewBudget (int userId,String budgetName, int budgetMoney,int budgetMoneyRemaining, String description,
                              String category, String startDate, String endDate)
    {
        @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter dft = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        @SuppressLint({"NewApi", "LocalSuppress"}) ZonedDateTime zone = ZonedDateTime.now();
        @SuppressLint({"NewApi", "LocalSuppress"}) String CurrentDate = dft.format(zone);
        //
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        values.put(DbHelper.COL_BUDGET_NAME, budgetName);
        values.put(DbHelper.COL_BUDGET_MONEY, budgetMoney);
        values.put(DbHelper.COL_BUDGET_MONEY_REMAINING, budgetMoney); // Khởi tạo bằng chính budget ban đầu
        values.put(DbHelper.COL_BUDGET_DESCRIPTION, description);
        values.put(DbHelper.COL_BUDGET_CATEGORY, category);
        values.put(DbHelper.COL_BUDGET_START_DATE, startDate);
        values.put(DbHelper.COL_BUDGET_END_DATE, endDate);
        values.put(DbHelper.COL_BUDGET_USER_ID, userId); //  Ghi user_id ở đây
        values.put(DbHelper.COL_CREATED_AT, CurrentDate);
        long insert = db.insert(DbHelper.DB_TABLE_BUDGET, null, values);
        //  THÊM LOG Ở ĐÂY
        Log.d("DEBUG_INSERT", "Insert result: " + insert);
        db.close();
        return insert;
    }
    // ✅ THÊM MỚI: Xóa ngân sách theo ID
    public int deleteBudgetById(int budgetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(DB_TABLE_BUDGET, COL_BUDGET_ID + " = ?", new String[]{String.valueOf(budgetId)});
        db.close();
        return deletedRows; // Trả về số dòng bị xóa (0 nếu không có dòng nào)
    }

    @SuppressLint("Range")
    public ArrayList<BudgetModel> getListBudget()
    {
        ArrayList<BudgetModel> budgetArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase(); //doc du liue
        Cursor cursor = db.rawQuery("SELECT * FROM " + DbHelper.DB_TABLE_BUDGET, null);
        if(cursor != null && cursor.getCount() > 0)
        {
            if (cursor.moveToFirst())
            {
                do{
                    // do du lieu vao model
                    budgetArrayList.add(
                            new BudgetModel(
                                    cursor.getInt(cursor.getColumnIndex(DbHelper.COL_BUDGET_ID)),
                                    cursor.getInt(cursor.getColumnIndex(DbHelper.COL_USER_ID)),
                                    cursor.getString(cursor.getColumnIndex(DbHelper.COL_BUDGET_NAME)),
                                    cursor.getInt(cursor.getColumnIndex(DbHelper.COL_BUDGET_MONEY)),
                                    cursor.getInt(cursor.getColumnIndex(DbHelper.COL_BUDGET_MONEY_REMAINING)),
                                    cursor.getString(cursor.getColumnIndex(DbHelper.COL_BUDGET_DESCRIPTION)),
                                    cursor.getString(cursor.getColumnIndex(DbHelper.COL_BUDGET_CATEGORY)),
                                    cursor.getString(cursor.getColumnIndex(DbHelper.COL_BUDGET_START_DATE)),
                                    cursor.getString(cursor.getColumnIndex(DbHelper.COL_BUDGET_END_DATE)),
                                    cursor.getString(cursor.getColumnIndex(DbHelper.COL_CREATED_AT)),
                                    cursor.getString(cursor.getColumnIndex(DbHelper.COL_UPDATED_AT))
                            )
                    );
                }while (cursor.moveToNext());

            }
            cursor.close();
        }
        db.close();
        return budgetArrayList;
    }
    // ✅ THÊM MỚI: Lấy tất cả các danh mục không trùng nhau theo user
    public List<String> getAllCategoriesByUser(int userId) {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT " + COL_BUDGET_CATEGORY + " FROM " + DB_TABLE_BUDGET +
                " WHERE " + COL_BUDGET_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                if (category != null && !category.trim().isEmpty()) {
                    categories.add(category);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }

    // ✅ THÊM MỚI: Lấy budget_id từ category + user
    public int getBudgetIdByCategoryAndUser(String category, int userId) {
        int budgetId = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                DB_TABLE_BUDGET,
                new String[]{COL_BUDGET_ID},
                COL_BUDGET_CATEGORY + " = ? AND " + COL_BUDGET_USER_ID + " = ?",
                new String[]{category, String.valueOf(userId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            budgetId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return budgetId;
    }
    // ham xu ly ngan sach con lai
    public double getTotalBudget(int userId) {
        double total = 0.0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_BUDGET_MONEY + ") FROM " + DB_TABLE_BUDGET + " WHERE " + COL_BUDGET_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }
    
    // ✅ THÊM MỚI: Kiểm tra trùng lặp budget theo tên
    public boolean isBudgetDuplicate(int userId, String budgetName, int excludeBudgetId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT COUNT(*) FROM " + DB_TABLE_BUDGET + 
                " WHERE " + COL_BUDGET_USER_ID + " = ? AND " + 
                COL_BUDGET_NAME + " = ? AND " + 
                COL_BUDGET_ID + " != ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId), budgetName, String.valueOf(excludeBudgetId)
        });
        
        boolean isDuplicate = false;
        if (cursor.moveToFirst()) {
            isDuplicate = cursor.getInt(0) > 0;
        }
        
        cursor.close();
        db.close();
        return isDuplicate;
    }
    
    // ✅ THÊM MỚI: Kiểm tra trùng lặp budget khi thêm mới
    public boolean isBudgetDuplicateNew(int userId, String budgetName) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT COUNT(*) FROM " + DB_TABLE_BUDGET + 
                " WHERE " + COL_BUDGET_USER_ID + " = ? AND " + 
                COL_BUDGET_NAME + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId), budgetName
        });
        
        boolean isDuplicate = false;
        if (cursor.moveToFirst()) {
            isDuplicate = cursor.getInt(0) > 0;
        }
        
        cursor.close();
        db.close();
        return isDuplicate;
    }
    

    public double getBudgetForCategory(int userId, String categoryName) {
        //tim hieu lan cap nhat gan nhat ngan sach cua nguoi dung
        double budget = 0.0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + DbHelper.COL_BUDGET_MONEY + " FROM " + DbHelper.DB_TABLE_BUDGET +
                        " WHERE " + DbHelper.COL_BUDGET_USER_ID + " = ? AND " +
                        DbHelper.COL_BUDGET_CATEGORY + " = ? " +
                        "ORDER BY " + DbHelper.COL_BUDGET_ID + " DESC LIMIT 1",  // Lấy dòng mới nhất
                new String[]{String.valueOf(userId), categoryName}
        );
        if (cursor.moveToFirst()) {
            budget = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return budget;
    }
}
