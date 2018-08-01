package com.example.adrian.homecalc.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Categories")
public class Category {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int color;
    private int icon;
    private boolean defaults;

    public Category(String name, int color, int icon, boolean defaults) {
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.defaults = defaults;
    }

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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isDefaults() {
        return defaults;
    }

    public void setDefaults(boolean defaults) {
        this.defaults = defaults;
    }
}
