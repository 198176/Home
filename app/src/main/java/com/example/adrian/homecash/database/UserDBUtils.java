package com.example.adrian.homecash.database;

import com.example.adrian.homecash.MyApplication;
import com.example.adrian.homecash.model.User;

public abstract class UserDBUtils {

    public static void getAll(final DBCallback<User> callback) {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                callback.onCallback(MyApplication.getHomeRoomDatabase().userDao().getAll());
            }
        }).start();
    }

    public synchronized static void delete(final User user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getHomeRoomDatabase().userDao().delete(user);
            }
        }).start();
    }

    public synchronized static void update(final User user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getHomeRoomDatabase().userDao().update(user);
            }
        }).start();
    }

    public synchronized static void insert(final User user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getHomeRoomDatabase().userDao().insert(user);
            }
        }).start();
    }
}
