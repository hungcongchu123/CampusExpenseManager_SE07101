package com.example.campusexpensemanager_se07101.database;

public class BudgetModel {
    private int id;
    private int userId;
    private String budgetName;
    private int budgetMoney;
    private String budgetDescription;
    private String category;
    private String startDate;
    private String endDate;
    private String createdAt;
    private String updatedAt;
    // contructor
    public BudgetModel(int id, int userId, String budgetName, int budgetMoney,
                       String budgetDescription, String category,
                       String startDate, String endDate,
                       String createdAt, String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.budgetName = budgetName;
        this.budgetMoney = budgetMoney;
        this.budgetDescription = budgetDescription;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public int getBudgetMoney() {
        return budgetMoney;
    }

    public void setBudgetMoney(int budgetMoney) {
        this.budgetMoney = budgetMoney;
    }

    public String getBudgetDescription() {
        return budgetDescription;
    }

    public void setBudgetDescription(String budgetDescription) {
        this.budgetDescription = budgetDescription;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
