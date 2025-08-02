package com.example.campusexpensemanager_se07101.model;

public class Expense {
    private int id;
    private int userId;
    private int budgetId;
    private String name;
    private double amount;
    private String date;

    public Expense(int id, int userId, int budgetId, String name, double amount, String date) {
        this.id = id;
        this.userId = userId;
        this.budgetId = budgetId;
        this.name = name;
        this.amount = amount;
        this.date = date;
    }

    // Getters and Setters
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}