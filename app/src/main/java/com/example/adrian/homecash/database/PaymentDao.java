package com.example.adrian.homecash.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import com.example.adrian.homecash.model.Payment;

import java.util.List;

@Dao
public interface PaymentDao {

    String DATE_BY_BILLING = "(CASE WHEN cast(strftime('%d', date(DATE/1000, 'unixepoch', 'localtime')) as integer) " +
            "< strftime(:dayBilling) THEN strftime('%Y-%m', date(DATE/1000, 'unixepoch', 'localtime', '-1 month')) " +
            "ELSE strftime('%Y-%m', date(DATE/1000, 'unixepoch', 'localtime')) END)";

    @Query("SELECT * FROM Payments")
    List<Payment> getAll();

    @Query("SELECT * FROM Payments WHERE id = :idEdit")
    Payment getPaymentById(int idEdit);

    @Query("SELECT title, value , strftime('%Y-%m-%d', date(DATE/1000, 'unixepoch', 'localtime')) date, name, color, icon, Payments.id " +
            "FROM Payments, Categories WHERE Payments.category_id = Categories.id AND PAYING_ID = :payingId AND " +
            DATE_BY_BILLING + " = :date AND DATE/1000 <= cast(strftime('%s', 'now') as integer) " +
            "ORDER BY Payments.DATE DESC, Payments.id DESC")
    Cursor getAllPaymentsByDateAndUser(int dayBilling, String date, String payingId);

    @Query("SELECT title, value , strftime('%Y-%m-%d', date(DATE/1000, 'unixepoch', 'localtime')) date, name, color, icon, Payments.id " +
            "FROM Payments, Categories WHERE Payments.category_id = Categories.id AND " + DATE_BY_BILLING +
            " = :date AND DATE/1000 <= cast(strftime('%s', 'now') as integer) ORDER BY Payments.DATE DESC, Payments.id DESC")
    Cursor getAllPaymentsByDateForAllUsers(int dayBilling, String date);

    @Query("SELECT title, value, strftime('%Y-%m-%d', date(DATE/1000, 'unixepoch', 'localtime')) date, name, color, icon, Payments.id " +
            "FROM Payments, Categories WHERE Payments.category_id = Categories.id AND DATE/1000 > " +
            "cast(strftime('%s', 'now') as integer) ORDER BY date ASC, Payments.id DESC")
    Cursor getAllPlannedPayments();

    @Query("SELECT title, user_value value, strftime('%Y-%m-%d', date(DATE/1000, 'unixepoch', 'localtime')) date, " +
            "name, color, icon FROM Payments, Categories, Participants WHERE Payments.category_id = Categories.id " +
            "AND user_id = :personId AND Participants.payment_id = Payments.id AND " + DATE_BY_BILLING +
            " = :date AND DATE/1000 <= cast(strftime('%s', 'now') as integer) ORDER BY date DESC, Payments.id DESC")
    Cursor getAllParticipantPaymentByUser(int dayBilling, String date, String personId);

    @Query("SELECT title, user_value value, strftime('%Y-%m-%d', date(DATE/1000, 'unixepoch', 'localtime')) date, name, color, icon " +
            "FROM Payments, Categories, Participants WHERE Payments.category_id = Categories.id AND Participants.payment_id = Payments.id AND " +
            DATE_BY_BILLING + " = :date AND DATE/1000 <= cast(strftime('%s', 'now') as integer) ORDER BY date DESC, Payments.id DESC")
    Cursor getAllParticipantPaymentForAllUsers(int dayBilling, String date);

    @Query("SELECT name, color, icon, Categories.id, SUM(user_value) value FROM Payments, Categories, Participants WHERE " +
            "Participants.user_id = :personId AND Participants.payment_id = Payments.id AND Payments.category_id = Categories.id AND " +
            DATE_BY_BILLING + " = :date AND DATE/1000 <= cast(strftime('%s', 'now') as integer) AND value > 0 GROUP BY Categories.id")
    Cursor getPositiveSummaryPaymentsCategoriesByUser(int dayBilling, String date, String personId);

    @Query("SELECT name, color, icon, Categories.id, SUM(user_value) value FROM Payments, Categories, Participants WHERE " +
            "Participants.user_id = :personId AND Participants.payment_id = Payments.id AND Payments.category_id = Categories.id AND " +
            DATE_BY_BILLING + " = :date AND DATE/1000 <= cast(strftime('%s', 'now') as integer) AND value < 0 GROUP BY Categories.id")
    Cursor getNegativeSummaryPaymentsCategoriesByUser(int dayBilling, String date, String personId);

    @Query("SELECT name, color, icon, Categories.id, SUM(user_value) value FROM Payments, Categories, Participants WHERE " +
            "Payments.category_id = Categories.id AND Participants.payment_id = Payments.id AND " + DATE_BY_BILLING +
            " = :date AND DATE/1000 <= cast(strftime('%s', 'now') as integer) AND value > 0 GROUP BY Categories.id")
    Cursor getPositiveSummaryPaymentsCategoriesForAllUsers(int dayBilling, String date);

    @Query("SELECT name, color, icon, Categories.id, SUM(user_value) value FROM Payments, Categories, Participants WHERE " +
            "Payments.category_id = Categories.id AND Participants.payment_id = Payments.id AND " + DATE_BY_BILLING +
            " = :date AND DATE/1000 <= cast(strftime('%s', 'now') as integer) AND value < 0 GROUP BY Categories.id")
    Cursor getNegativeSummaryPaymentsCategoriesForAllUsers(int dayBilling, String date);

    @Query("SELECT SUM(ABS(user_value)), paying_id, user_id, U.NAME, U.COLOR, P.NAME, P.COLOR FROM " +
            "Payments, Participants, Users AS P, Users AS U WHERE user_id = P.id AND paying_id = U.id " +
            "AND Payments.id = payment_id GROUP BY paying_id, user_id HAVING paying_id != user_id")
    Cursor getArrearsUsers();

    @Query("SELECT SUM(user_value), user_id, paying_id, P.NAME, P.COLOR, U.NAME, U.COLOR FROM " +
            "Payments, Participants, Users AS P, Users AS U WHERE user_id = P.id AND paying_id = U.id " +
            "AND category_id = 0 AND Payments.id = payment_id GROUP BY paying_id, user_id")
    Cursor getRepaymentsUsers();

    @Query("SELECT id as _id, " + DATE_BY_BILLING + " MONTH FROM Payments WHERE DATE/1000 <= cast(strftime('%s', 'now') as integer) " +
            "GROUP BY " + DATE_BY_BILLING + " ORDER BY " + DATE_BY_BILLING + " DESC")
    Cursor getSpinnerDate(int dayBilling);

    @Query("SELECT SUM(user_value) FROM Participants, Payments WHERE user_id = :userId AND " + DATE_BY_BILLING +
            " = :date AND DATE/1000 <= cast(strftime('%s', 'now') as integer) AND category_id != 0 AND Payments.id = payment_id")
    double getMonthlyBalancePaymentsByUser(int dayBilling, String userId, String date);

    @Query("SELECT SUM(user_value) FROM Participants, Payments WHERE " + DATE_BY_BILLING + " = :date " +
            "AND DATE/1000 <= cast(strftime('%s', 'now') as integer) AND category_id != 0 AND Payments.id = payment_id")
    double getMonthlyBalancePaymentsForAllUsers(int dayBilling, String date);

    @Query("SELECT SUM(CASE WHEN category_id = 0 AND paying_id = :id THEN value ELSE user_value END) FROM " +
            "Participants, Payments WHERE (paying_id = :id OR (category_id = 0 AND user_id = :id)) AND Payments.id = payment_id")
    double getTotalBalancePayments(String id);

    @Query("UPDATE Payments SET category_id = :default_id WHERE category_id = :id")
    void updatePaymentsCategoryIdByCategory(int default_id, int id);

    @Insert
    long[] insert(Payment... payments);

    @Update
    void update(Payment... payments);

    @Delete
    void delete(Payment... payments);
}
