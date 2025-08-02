package com.example.campusexpensemanager_se07101.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.campusexpensemanager_se07101.model.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private DbHelper dbHelper;
    private static final String TAG = "CategoryRepository";

    public CategoryRepository(Context context) {
        dbHelper = new DbHelper(context);
    }

    /**
     * Thêm một danh mục mới vào database.
     * @param name Tên của danh mục.
     * @return ID của dòng được thêm vào, hoặc -1 nếu có lỗi.
     */
    public long addCategory(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_CATEGORY_NAME, name);
        long newRowId = db.insert(DbHelper.DB_TABLE_CATEGORIES, null, values);
        db.close();
        return newRowId;
    }

    /**
     * Lấy tất cả các danh mục từ database.
     * @return Danh sách các đối tượng Category.
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] projection = {
                    DbHelper.COL_CATEGORY_ID,
                    DbHelper.COL_CATEGORY_NAME
            };
            cursor = db.query(
                    DbHelper.DB_TABLE_CATEGORIES,
                    projection,
                    null, null, null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_CATEGORY_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_CATEGORY_NAME));
                    categories.add(new Category(id, name));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy danh mục từ database", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return categories;
    }
}