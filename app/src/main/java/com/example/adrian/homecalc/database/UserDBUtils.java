package com.example.adrian.homecalc.database;

import com.example.adrian.homecalc.MyApplication;
import com.example.adrian.homecalc.model.User;

public abstract class UserDBUtils {

    public static void getAll(final DBCallback<User> callback) {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                callback.onCallback(MyApplication.getHomeRoomDatabase().userDao().getAll());
            }
        }).start();
    }

    public static void delete(final User user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getHomeRoomDatabase().userDao().delete(user);
            }
        }).start();
    }

    public static void update(final User user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getHomeRoomDatabase().userDao().update(user);
            }
        }).start();
    }

    public static void insert(final User user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getHomeRoomDatabase().userDao().insert(user);
            }
        }).start();
    }
}
