package com.example.campusexpensemanager_se07101.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class UserRepository extends DbHelper{
    public UserRepository(@Nullable Context context) { super(context); }
    public long saveUserAccount(String username, String password, String email, String phone)
    {
        @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter dft = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        @SuppressLint({"NewApi", "LocalSuppress"}) ZonedDateTime zone = ZonedDateTime.now();
        @SuppressLint({"NewApi", "LocalSuppress"}) String CurrentDate = dft.format(zone);
        // lay ra ngay thang hien tai
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_USER_USERNAME, username); // do du luu vao cot username
        values.put(DbHelper.COL_USER_PASSWORD, password);
        values.put(DbHelper.COL_USER_EMAIL, email);
        values.put(DbHelper.COL_USER_PHONE, phone);
        values.put(DbHelper.COL_USER_ROLE, 0); //admin
        values.put(DbHelper.COL_CREATED_AT, CurrentDate);
        SQLiteDatabase db = this.getWritableDatabase();// viet du liue vao database
        long insert = db.insert(DbHelper.DB_TABLE_USER, null, values);
        db.close();
        return insert;
    }
    public void insertTestAccountIfNotExists() {
        SQLiteDatabase db = this.getWritableDatabase();
        String checkQuery = "SELECT * FROM " + DB_TABLE_USER + " WHERE " + COL_USER_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(checkQuery, new String[]{"testuser"});
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(COL_USER_USERNAME, "testuser");
            values.put(COL_USER_PASSWORD, "123456"); // mật khẩu test
            values.put(COL_USER_EMAIL, "test@example.com");
            values.put(COL_USER_PHONE, "0123456789");
            values.put(COL_USER_ROLE, 0);
            values.put(COL_CREATED_AT, "2025-07-31 10:00:00");
            db.insert(DB_TABLE_USER, null, values);
        }
        cursor.close();
        db.close();
    }
    @SuppressLint("Range")
    public UserModel getInfoUserByUserName(String username, String password) {
        UserModel user = new UserModel();
        try {
            SQLiteDatabase db = this.getReadableDatabase();//doc du lieu(cau hinh select)
            // toa mot mang chua cac cot du lieun can truy van
            String[] columns = {
                    DbHelper.COL_USER_ID,
                    DbHelper.COL_USER_USERNAME,
                    DbHelper.COL_USER_EMAIL,
                    DbHelper.COL_USER_PHONE,
                    DbHelper.COL_USER_ROLE};
            // select id,username,email,phone,role from user whree username =? and password = ?
            String condition = DbHelper.COL_USER_USERNAME + " =? AND " + DbHelper.COL_USER_PASSWORD + " =? ";
            String[] params = {username, password};
            Cursor cursor = db.query(DbHelper.DB_TABLE_USER, columns, condition, params, null, null, null);
            // thuc thi cau lenh sql
            if (cursor.getCount() > 0) {
                //co du lieu
                cursor.moveToFirst();
                // do du lieu vao model
                user.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.COL_USER_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(DbHelper.COL_USER_USERNAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(DbHelper.COL_USER_EMAIL)));
                user.setPhone(cursor.getString(cursor.getColumnIndex(DbHelper.COL_USER_PHONE)));
                user.setRole(cursor.getInt(cursor.getColumnIndex(DbHelper.COL_USER_ROLE)));

            }
        } catch (RuntimeException e) {

        }
        return user;
    }

}
