package com.example.adrian.homecalc.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Payments")
public class Payment {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private Double value;
    private long date;
    private int category_id;
    private int paying_id;

    public Payment(String title, Double value, long date, int category_id, int paying_id) {
        this.title = title;
        this.value = value;
        this.date = date;
        this.category_id = category_id;
        this.paying_id = paying_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getPaying_id() {
        return paying_id;
    }

    public void setPaying_id(int paying_id) {
        this.paying_id = paying_id;
    }

}
