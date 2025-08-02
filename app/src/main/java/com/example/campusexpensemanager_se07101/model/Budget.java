package com.example.campusexpensemanager_se07101.model;

public class Budget {
    private int id;
    private int userId;
    private String name;
    private int money;
    private String description;
    private int categoryId;
    private String startDate;
    private String endDate;

    public Budget(int id, int userId, String name, int money, String description, int categoryId, String startDate, String endDate) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.money = money;
        this.description = description;
        this.categoryId = categoryId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Constructor mới để sử dụng trong EditBudgetActivity
    public Budget(int id, int money, String name, String description, int categoryId, String startDate, String endDate) {
        this.id = id;
        this.money = money;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return name;
    }
}