package com.example.adrian.homecash.database;

import com.example.adrian.homecash.MyApplication;
import com.example.adrian.homecash.model.Category;

public abstract class CategoryDBUtils {

    public static void getAll(final DBCallback<Category> callback) {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                callback.onCallback(MyApplication.getHomeRoomDatabase().categoryDao().getAll());
            }
        }).start();
    }

    public static void updateDefaultOnFalse() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getHomeRoomDatabase().categoryDao().updateDefaultOnFalse();
            }
        }).start();
    }

    public static void delete(final Category category) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getHomeRoomDatabase().categoryDao().delete(category);
            }
        }).start();
    }

    public static void update(final Category category) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getHomeRoomDatabase().categoryDao().update(category);
            }
        }).start();
    }

    public static void insert(final Category category) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getHomeRoomDatabase().categoryDao().insert(category);
            }
        }).start();
    }
}
