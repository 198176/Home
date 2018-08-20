package com.example.adrian.homecalc.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "Payments")
public class Payment {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private double value;
    private long date;
    private int category_id;
    private int paying_id;
    @Ignore
    private ArrayList<Participant> participants;

    @Ignore
    public Payment(double value, int paying_id, ArrayList<Participant> participants) {
        this.value = value;
        this.paying_id = paying_id;
        this.participants = participants;
    }

    public Payment(String title, double value, long date, int category_id, int paying_id) {
        this.title = title;
        this.value = value;
        this.date = date;
        this.category_id = category_id;
        this.paying_id = paying_id;
    }

    @Ignore
    public Payment(String title, Double value, long date, int category_id, int paying_id, ArrayList<Participant> participants) {
        this(title, value, date, category_id, paying_id);
        this.participants = participants;
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
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

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }
}
