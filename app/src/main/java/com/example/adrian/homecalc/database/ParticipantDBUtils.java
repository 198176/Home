package com.example.adrian.homecalc.database;

import com.example.adrian.homecalc.MyApplication;
import com.example.adrian.homecalc.model.Participant;

public abstract class ParticipantDBUtils {

    public static void getAll(final DBCallback<Participant> callback) {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                callback.onCallback(MyApplication.getHomeRoomDatabase().participantDao().getAll());
            }
        }).start();
    }

    public static void delete(final Participant participant) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getHomeRoomDatabase().participantDao().delete(participant);
            }
        }).start();
    }

    public static void update(final Participant participant) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getHomeRoomDatabase().participantDao().update(participant);
            }
        }).start();
    }

    public static void insert(final Participant participant) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getHomeRoomDatabase().participantDao().insert(participant);
            }
        }).start();
    }
}
