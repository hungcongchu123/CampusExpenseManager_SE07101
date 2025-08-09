package com.example.campusexpensemanager_se07101.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "chuhungcong";//ten co so du lieu
    private static final int DB_VERSION = 7;// phien ban
    //Khai bao bang du liue va cac cot trong bang du lieu
    //dinh nghia bang user
    protected static final String DB_TABLE_USER = "user";
    protected static final String COL_USER_ID = "id";
    protected static final String COL_USER_USERNAME = "username";
    protected static final String COL_USER_PASSWORD = "password";
    protected  static final String COL_USER_EMAIL = "email";
    protected static final String COL_USER_PHONE = "phone";
    protected static final String COL_USER_ROLE = "role";
    //dhung chung cho cac bang-2 truong nay
    protected static final String COL_CREATED_AT = "created_at";
    protected static final String COL_UPDATED_AT = "updated_at";
    // dinh nghia va budget
    public static final String DB_TABLE_BUDGET = "budget";
    protected static final String COL_BUDGET_ID = "id";
    protected static final String COL_BUDGET_NAME = "budget_name";
    protected static final String COL_BUDGET_MONEY = "budget_money";
    protected static final String COL_BUDGET_MONEY_REMAINING = "money_remaining"; //  Cột mới
    protected static final String COL_BUDGET_DESCRIPTION = "description";
    public static final String COL_BUDGET_CATEGORY = "category";
    public static final String COL_BUDGET_START_DATE = "start_date";
    public static final String COL_BUDGET_END_DATE = "end_date";
    public static final String COL_BUDGET_USER_ID = "user_id"; // thêm vào để liên kết user
    // DINH NGHIA BANG EXPENSE
    public static final String DB_TABLE_EXPENSE = "expenses";
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

        // Tạo bảng budget (đã cập nhật đúng với BudgetModel)
        String createBudgetTable = "CREATE TABLE " + DB_TABLE_BUDGET + " ( "
                + COL_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_BUDGET_USER_ID + " INTEGER, "
                + COL_BUDGET_NAME + " VARCHAR(150) NOT NULL, "
                + COL_BUDGET_MONEY + " INTEGER NOT NULL, "
                + COL_BUDGET_MONEY_REMAINING + " INTEGER NOT NULL, " //  thêm dòng này
                + COL_BUDGET_DESCRIPTION + " TEXT, "
                + COL_BUDGET_CATEGORY + " TEXT, "
                + COL_BUDGET_START_DATE + " TEXT, "
                + COL_BUDGET_END_DATE + " TEXT, "
                + COL_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + COL_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP )";
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
        db.execSQL(createBudgetTable);
        db.execSQL(createExpenseTable);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_USER);// xoa bang neu co loi
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_BUDGET);// xoa bang neu co loi
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_EXPENSE);// xoa bang neu co loi
            onCreate(db);// tao lai bang
        }

    }
}
