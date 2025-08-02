package com.example.campusexpensemanager_se07101.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "cong hihi";
    private static final int DB_VERSION = 5; // Cập nhật phiên bản database

    // Khai bao bang du liue va cac cot trong bang du lieu
    // dinh nghia bang user
    protected static final String DB_TABLE_USER = "user";
    protected static final String COL_USER_ID = "id";
    protected static final String COL_USER_USERNAME = "username";
    protected static final String COL_USER_PASSWORD = "password";
    protected  static final String COL_USER_EMAIL = "email";
    protected static final String COL_USER_PHONE = "phone";
    protected static final String COL_USER_ROLE = "role";

    // dinh nghia bang categories
    protected static final String DB_TABLE_CATEGORIES = "categories";
    protected static final String COL_CATEGORY_ID = "id";
    protected static final String COL_CATEGORY_NAME = "name";

    // dhung chung cho cac bang-2 truong nay
    protected static final String COL_CREATED_AT = "created_at";
    protected static final String COL_UPDATED_AT = "updated_at";

    // dinh nghia va budget
    protected static final String DB_TABLE_BUDGET = "budget";
    protected static final String COL_BUDGET_ID = "id";
    protected static final String COL_BUDGET_NAME = "budget_name";
    protected static final String COL_BUDGET_MONEY = "budget_money";
    protected static final String COL_BUDGET_DESCRIPTION = "description";
    public static final String COL_BUDGET_CATEGORY_ID = "category_id"; // Thay thế bằng ID của danh mục
    public static final String COL_BUDGET_START_DATE = "start_date";
    public static final String COL_BUDGET_END_DATE = "end_date";
    public static final String COL_BUDGET_USER_ID = "user_id"; // thêm vào để liên kết user

    // DINH NGHIA BANG EXPENSE
    protected static final String DB_TABLE_EXPENSE = "expenses";
    protected static final String COL_EXPENSE_ID = "id";
    protected static final String COL_EXPENSE_NAME = "expense_name";
    protected static final String COL_EXPENSE_AMOUNT = "amount";
    protected static final String COL_EXPENSE_NOTE = "note";
    protected static final String COL_EXPENSE_DATE = "date";
    protected static final String COL_EXPENSE_TYPE = "type"; // optional: phân loại (ví dụ: ăn uống, đi lại)
    protected static final String COL_EXPENSE_BUDGET_ID = "budget_id"; // khóa ngoại liên kết budget
    protected static final String COL_EXPENSE_USER_ID = "user_id"; // khóa ngoại liên kết user

    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng user
        String createUserTable = "CREATE TABLE " + DB_TABLE_USER + " ( "
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_USERNAME + " VARCHAR(60) NOT NULL, "
                + COL_USER_PASSWORD + " VARCHAR(200) NOT NULL, "
                + COL_USER_EMAIL + " VARCHAR(100) NOT NULL, "
                + COL_USER_PHONE + " VARCHAR(20), "
                + COL_USER_ROLE + " INTEGER DEFAULT(0), "
                + COL_CREATED_AT + " DATETIME, "
                + COL_UPDATED_AT + " DATETIME )";

        // Tạo bảng categories
        String createCategoriesTable = "CREATE TABLE " + DB_TABLE_CATEGORIES + " ( "
                + COL_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CATEGORY_NAME + " TEXT UNIQUE NOT NULL )";

        // Tạo bảng budget (đã cập nhật để liên kết với bảng categories)
        String createBudgetTable = "CREATE TABLE " + DB_TABLE_BUDGET + " ( "
                + COL_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_BUDGET_USER_ID + " INTEGER, "
                + COL_BUDGET_NAME + " VARCHAR(150) NOT NULL, "
                + COL_BUDGET_MONEY + " INTEGER NOT NULL, "
                + COL_BUDGET_DESCRIPTION + " TEXT, "
                + COL_BUDGET_CATEGORY_ID + " INTEGER, " // Liên kết với categories
                + COL_BUDGET_START_DATE + " TEXT, "
                + COL_BUDGET_END_DATE + " TEXT, "
                + COL_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + COL_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + COL_BUDGET_USER_ID + ") REFERENCES " + DB_TABLE_USER + "(" + COL_USER_ID + "),"
                + "FOREIGN KEY(" + COL_BUDGET_CATEGORY_ID + ") REFERENCES " + DB_TABLE_CATEGORIES + "(" + COL_CATEGORY_ID + ")"
                + " )";

        // Tạo bảng expense (theo đúng ExpenseModel)
        String createExpenseTable = "CREATE TABLE " + DB_TABLE_EXPENSE + " ("
                + COL_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_EXPENSE_NAME + " TEXT, "
                + COL_EXPENSE_AMOUNT + " REAL, "
                + COL_EXPENSE_NOTE + " TEXT, "
                + COL_EXPENSE_DATE + " TEXT, "
                + COL_EXPENSE_TYPE + " TEXT, "
                + COL_EXPENSE_BUDGET_ID + " INTEGER, "
                + COL_EXPENSE_USER_ID + " INTEGER, "
                + "FOREIGN KEY(" + COL_EXPENSE_BUDGET_ID + ") REFERENCES " + DB_TABLE_BUDGET + "(" + COL_BUDGET_ID + "), "
                + "FOREIGN KEY(" + COL_EXPENSE_USER_ID + ") REFERENCES " + DB_TABLE_USER + "(" + COL_USER_ID + ")"
                + ");";

        // Thực thi lệnh tạo bảng
        db.execSQL(createUserTable);
        db.execSQL(createCategoriesTable); // Thực thi lệnh tạo bảng categories
        db.execSQL(createBudgetTable);
        db.execSQL(createExpenseTable);

        // Thêm các danh mục mặc định sau khi đã tạo bảng
        addDefaultCategories(db);
    }

    // Phương thức hỗ trợ để thêm các danh mục mặc định
    private void addDefaultCategories(SQLiteDatabase db) {
        List<String> defaultCategories = new ArrayList<>();
        defaultCategories.add("Ăn uống");
        defaultCategories.add("Đi lại");
        defaultCategories.add("Học phí");
        defaultCategories.add("Nhà ở");
        defaultCategories.add("Giải trí");
        defaultCategories.add("Tiền lương"); // Thêm một loại thu nhập
        defaultCategories.add("Quà tặng"); // Thêm một loại thu nhập

        for (String categoryName : defaultCategories) {
            ContentValues values = new ContentValues();
            values.put(COL_CATEGORY_NAME, categoryName);
            db.insert(DB_TABLE_CATEGORIES, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_CATEGORIES); // Xóa bảng categories
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_BUDGET);
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_EXPENSE);
            onCreate(db); // tạo lại các bảng
        }
    }

//    /**
//     * Thêm một danh mục mới vào database.
//     * @param categoryName Tên của danh mục.
//     */
//    public void addCategory(String categoryName) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COL_CATEGORY_NAME, categoryName);
//        db.insert(DB_TABLE_CATEGORIES, null, values);
//        db.close();
//    }

    /**
     * Lấy tất cả các danh mục có trong database.
     * @return Một danh sách các chuỗi chứa tên các danh mục.
     */
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DB_TABLE_CATEGORIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }
}