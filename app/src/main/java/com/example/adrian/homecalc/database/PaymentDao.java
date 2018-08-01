package com.example.adrian.homecalc.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.adrian.homecalc.model.Payment;

import java.util.List;

@Dao
public interface PaymentDao {

    @Query("SELECT * FROM Payments")
    List<Payment> getAll();

    @Query("SELECT id FROM Payments WHERE id = ( SELECT MAX(id) FROM Payments)")
    int getLastId();

    @Insert
    void insert(Payment... payments);

    @Update
    void update(Payment... payments);

    @Delete
    void delete(Payment... payments);
}
