package com.example.adrian.homecash.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "Users")
public class User implements Serializable {

    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private int color;
    private String mail;

    public User(String id, String name, String mail, int color) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
