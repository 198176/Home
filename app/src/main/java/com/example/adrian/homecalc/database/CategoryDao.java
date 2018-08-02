package com.example.adrian.homecalc.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.adrian.homecalc.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM Categories")
    List<Category> getAll();

    @Query("SELECT * FROM Categories WHERE id = :id")
    Category getCategoryById(int id);

    @Query("SELECT * FROM CATEGORIES WHERE DEFAULTS = 1")
    Category getDefaultCategory();

    @Insert
    void insert(Category... payments);

    @Update
    void update(Category... payments);

    @Delete
    void delete(Category... payments);
}
