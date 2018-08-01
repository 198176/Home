package com.example.adrian.homecalc;

import android.app.Application;

import com.example.adrian.homecalc.database.HomeRoomDatabase;
import com.facebook.stetho.Stetho;

public class MyApplication extends Application {

    private static HomeRoomDatabase homeRoomDatabase;

    public static HomeRoomDatabase getHomeRoomDatabase(){
        return homeRoomDatabase;
    }

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        homeRoomDatabase = HomeRoomDatabase.getInstance(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                homeRoomDatabase.categoryDao().getAll();
            }
        }).start();
    }
}
