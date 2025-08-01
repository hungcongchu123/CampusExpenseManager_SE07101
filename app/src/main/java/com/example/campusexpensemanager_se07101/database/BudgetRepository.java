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
        values.put(DbHelper.COL_BUDGET_DESCRIPTION, description);      // Mô tả
        values.put(DbHelper.COL_BUDGET_CATEGORY, category);            // Loại
        values.put(DbHelper.COL_BUDGET_START_DATE, startDate);         // Ngày bắt đầu
        values.put(DbHelper.COL_BUDGET_END_DATE, endDate);             // Ngày kết thúc
        values.put(DbHelper.COL_UPDATED_AT, CurrentDate);       // Ngày cập nhật
        return db.update(DbHelper.DB_TABLE_BUDGET, values, "id = ?", new String[]{String.valueOf(id)});

    }
    public long AddNewBudget (int userId,String budgetName, int budgetMoney, String description,
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
}
