package com.example.adrian.homecash.database;

import com.example.adrian.homecash.MyApplication;
import com.example.adrian.homecash.model.Participant;

public abstract class ParticipantDBUtils {

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
