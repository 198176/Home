package com.example.adrian.homecalc.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import com.example.adrian.homecalc.model.Payment;

import java.util.List;

@Dao
public interface PaymentDao {

    String DATE_BY_BILLING = "(CASE WHEN cast(strftime('%d', date(DATE/1000, 'unixepoch', 'localtime')) as integer) " +
            "< strftime(:dayBilling) THEN strftime('%Y-%m', date(DATE/1000, 'unixepoch', 'localtime', '-1 month')) " +
            "ELSE strftime('%Y-%m', date(DATE/1000, 'unixepoch', 'localtime')) END)";

    @Query("SELECT * FROM Payments")
    List<Payment> getAll();

    @Query("SELECT id FROM Payments WHERE id = ( SELECT MAX(id) FROM Payments)")
    int getLastId();

    @Query("SELECT title, value , strftime('%Y-%m-%d', date(DATE/1000, 'unixepoch', 'localtime')) date, name, color, icon, Payments.id " +
            "FROM Payments, Categories WHERE Payments.category_id = Categories.id AND PAYING_ID = :payingId AND " +
            DATE_BY_BILLING + " = :date AND DATE/1000 <= cast(strftime('%s', 'now') as integer) " +
            "ORDER BY Payments.DATE DESC, Payments.id DESC")
    Cursor getAllPaymentsWhereDateAndUser(int dayBilling, String date, int payingId);

    @Query("SELECT title, value , strftime('%Y-%m-%d', date(DATE/1000, 'unixepoch', 'localtime')) date, name, color, icon, Payments.id " +
            "FROM Payments, Categories WHERE Payments.category_id = Categories.id AND " + DATE_BY_BILLING +
            " = :date AND DATE/1000 <= cast(strftime('%s', 'now') as integer) ORDER BY Payments.DATE DESC, Payments.id DESC")
    Cursor getAllPaymentsWhereDateForAllUsers (int dayBilling, String date);

    @Query("SELECT title, value, strftime('%Y-%m-%d', date(DATE/1000, 'unixepoch', 'localtime')) date, name, color, icon, Payments.id " +
            "FROM Payments, Categories WHERE Payments.category_id = Categories.id AND DATE/1000 > " +
            "cast(strftime('%s', 'now') as integer) ORDER BY date ASC, Payments.id DESC")
    Cursor getAllPlannedPayments();

    @Query("SELECT id as _id, " + DATE_BY_BILLING + " MONTH FROM Payments WHERE DATE/1000 <= cast(strftime('%s', 'now') as integer) " +
            "GROUP BY " + DATE_BY_BILLING + " ORDER BY " + DATE_BY_BILLING + " DESC")
    Cursor getSpinnerDate(int dayBilling);

    @Insert
    long[] insert(Payment... payments);

    @Update
    void update(Payment... payments);

    @Delete
    void delete(Payment... payments);
}
