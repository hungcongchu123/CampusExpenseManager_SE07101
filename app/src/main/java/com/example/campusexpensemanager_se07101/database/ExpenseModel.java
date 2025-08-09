package com.example.campusexpensemanager_se07101.database;

public class ExpenseModel {
    private int id;
    private int userId;
    private int budgetId;
    private String expenseName;
    private String category;
    private int amount;
    private String description;
    private String date;

    public ExpenseModel(int id, int userId, int budgetId,String expenseName, String category, int amount, String description, String date)
    {
        this.id = id;
        this.userId = userId;
        this.budgetId = budgetId;
        this.expenseName = expenseName;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }
    public String getExpenseName() {
        return expenseName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
