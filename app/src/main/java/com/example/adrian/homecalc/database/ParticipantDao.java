package com.example.adrian.homecalc.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.adrian.homecalc.model.Participant;

import java.util.List;

@Dao
public interface ParticipantDao {

    @Query("SELECT * FROM Participants")
    List<Participant> getAll();

    @Insert
    void insert(Participant... payments);

    @Update
    void update(Participant... payments);

    @Delete
    void delete(Participant... payments);
}
