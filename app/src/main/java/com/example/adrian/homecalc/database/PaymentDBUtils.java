package com.example.adrian.homecalc.database;

import com.example.adrian.homecalc.MyApplication;
import com.example.adrian.homecalc.model.Payment;

public abstract class PaymentDBUtils {

    public static void getAll(final DBCallback<Payment> callback) {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                callback.onCallback(MyApplication.getHomeRoomDatabase().paymentDao().getAll());
            }
        }).start();
    }

    public static void delete(final Payment payment) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getHomeRoomDatabase().paymentDao().delete(payment);
            }
        }).start();
    }

    public static void update(final Payment payment) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getHomeRoomDatabase().paymentDao().update(payment);
            }
        }).start();
    }

    public static void insert(final Payment payment) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.getHomeRoomDatabase().paymentDao().insert(payment);
            }
        }).start();
    }
}
