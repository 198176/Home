package com.example.adrian.homecalc.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Participants")
public class Participant {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int payment_id;
    private int user_id;
    private double user_value;

    public Participant(int payment_id, int user_id, double user_value) {
        this.payment_id = payment_id;
        this.user_id = user_id;
        this.user_value = user_value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public double getUser_value() {
        return user_value;
    }

    public void setUser_value(double user_value) {
        this.user_value = user_value;
    }

    public int getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(int payment_id) {
        this.payment_id = payment_id;
    }
}
