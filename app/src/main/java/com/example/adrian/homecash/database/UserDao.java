package com.example.adrian.homecash.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.adrian.homecash.model.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM Users")
    List<User> getAll();

    @Query("SELECT * FROM Users WHERE id = :id")
    User getUser(int id);

    @Query("SELECT COUNT(*) FROM (SELECT * FROM Users WHERE mail = :mail)")
    int getCountUserByEmail(String mail);

    @Query("SELECT COUNT(*) FROM (SELECT user_id, paying_id FROM Payments, Participants WHERE user_id = :id OR paying_id = :id)")
    int getCountOperationsUser(int id);

    @Insert
    void insert(User... payments);

    @Update
    void update(User... payments);

    @Delete
    void delete(User... payments);
}
