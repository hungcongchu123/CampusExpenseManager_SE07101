package com.example.campusexpensemanager_se07101.model;

public class Category {
    private int id;
    private String name;

    // Constructor
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Phương thức này rất quan trọng để Spinner hiển thị tên danh mục
    @Override
    public String toString() {
        return name;
    }
}
