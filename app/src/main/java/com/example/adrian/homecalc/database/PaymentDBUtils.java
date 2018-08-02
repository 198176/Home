package com.example.adrian.homecalc.database;

import com.example.adrian.homecalc.MyApplication;
import com.example.adrian.homecalc.model.Participant;
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

    public static void getAllPaymentsWhereDateAndUser(final DBCallbackCursor callback, final int dayBilling, final String date, final int payingId) {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                callback.onCallback(MyApplication.getHomeRoomDatabase().paymentDao().getAllPaymentsWhereDateAndUser(dayBilling, date, payingId));
            }
        }).start();
    }

    public static void getAllPaymentsWhereDateForAllUsers(final DBCallbackCursor callback, final int dayBilling, final String date) {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                callback.onCallback(MyApplication.getHomeRoomDatabase().paymentDao().getAllPaymentsWhereDateForAllUsers(dayBilling, date));
            }
        }).start();
    }

    public static void getAllPlannedPayments(final DBCallbackCursor callback) {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                callback.onCallback(MyApplication.getHomeRoomDatabase().paymentDao().getAllPlannedPayments());
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
                long[] id = MyApplication.getHomeRoomDatabase().paymentDao().insert(payment);
                for (Participant participant : payment.getParticipants()) {
                    participant.setPayment_id(id[0]);
                    MyApplication.getHomeRoomDatabase().participantDao().insert(participant);
                }
            }
        }).start();
    }
}
