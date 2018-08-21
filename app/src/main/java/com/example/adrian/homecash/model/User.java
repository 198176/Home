package com.example.adrian.homecash.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Users")
public class User implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int color;
    private String mail;

    public User(String name, String mail, int color) {
        this.name = name;
        this.mail = mail;
        this.color = color;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
