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

    public static void getAllPaymentsByDateAndUser(final DBCallbackCursor callback, final int dayBilling, final String date, final int payingId) {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                callback.onCallback(MyApplication.getHomeRoomDatabase().paymentDao().getAllPaymentsByDateAndUser(dayBilling, date, payingId));
            }
        }).start();
    }

    public static void getAllPaymentsByDateForAllUsers(final DBCallbackCursor callback, final int dayBilling, final String date) {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                callback.onCallback(MyApplication.getHomeRoomDatabase().paymentDao().getAllPaymentsByDateForAllUsers(dayBilling, date));
            }
        }).start();
    }

    public static void getAllParticipantPaymentByUser(final DBCallbackCursor callback, final int dayBilling, final String date, final int payingId) {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                callback.onCallback(MyApplication.getHomeRoomDatabase().paymentDao().getAllParticipantPaymentByUser(dayBilling, date, payingId));
            }
        }).start();
    }

    public static void getAllParticipantPaymentForAllUsers(final DBCallbackCursor callback, final int dayBilling, final String date) {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                callback.onCallback(MyApplication.getHomeRoomDatabase().paymentDao().getAllParticipantPaymentForAllUsers(dayBilling, date));
            }
        }).start();
    }

    public static void getPositiveSummaryPaymentsCategories(final DBCallbackCursor callback, final int dayBilling, final String date, final int payingId) {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                if (payingId == -1) {
                    callback.onCallback(MyApplication.getHomeRoomDatabase().paymentDao()
                            .getPositiveSummaryPaymentsCategoriesForAllUsers(dayBilling, date));
                } else {
                    callback.onCallback(MyApplication.getHomeRoomDatabase().paymentDao()
                            .getPositiveSummaryPaymentsCategoriesByUser(dayBilling, date, payingId));
                }
            }
        }).start();
    }

    public static void getNegativeSummaryPaymentsCategories(final DBCallbackCursor callback, final int dayBilling, final String date, final int payingId) {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                if (payingId == -1) {
                    callback.onCallback(MyApplication.getHomeRoomDatabase().paymentDao()
                            .getNegativeSummaryPaymentsCategoriesForAllUsers(dayBilling, date));
                } else {
                    callback.onCallback(MyApplication.getHomeRoomDatabase().paymentDao()
                            .getNegativeSummaryPaymentsCategoriesByUser(dayBilling, date, payingId));
                }
            }
        }).start();
    }

    public static void getArrearsUsers(final DBCallbackCursor callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                callback.onCallback(MyApplication.getHomeRoomDatabase().paymentDao().getArrearsUsers());
            }
        }).start();
    }

    public static void getRepaymentsUsers(final DBCallbackCursor callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                callback.onCallback(MyApplication.getHomeRoomDatabase().paymentDao().getRepaymentsUsers());
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

    public static void updatePaymentsCategoryIdByCategory(final int id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int default_id = MyApplication.getHomeRoomDatabase().categoryDao().getDefaultCategory().getId();
                MyApplication.getHomeRoomDatabase().paymentDao().updatePaymentsCategoryIdByCategory(default_id, id);
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
