package com.example.adrian.homecash.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.adrian.homecash.R;
import com.example.adrian.homecash.model.Category;
import com.example.adrian.homecash.model.Participant;
import com.example.adrian.homecash.model.Payment;
import com.example.adrian.homecash.model.User;

import java.util.concurrent.Executors;

@Database(entities = {Payment.class, Category.class, User.class, Participant.class}, version = 1)
public abstract class HomeRoomDatabase extends RoomDatabase {

    private static HomeRoomDatabase instance;

    public synchronized static HomeRoomDatabase getInstance(Context context) {
        if (instance == null) {
            instance = buildDatabase(context);
        }
        return instance;
    }

    private static HomeRoomDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context, HomeRoomDatabase.class, "home-database").addCallback(new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        getInstance(context).categoryDao().insert(defaultsCategory());
                        //getInstance(context).userDao().insert(new User("Ja", 0xFF1976D2));
                    }
                });
            }
        }).build();
    }

    private static Category[] defaultsCategory() {
        return new Category[]{
                new Category("Inne", 0xFF000000, R.drawable.ic_category1, true),
                new Category("Jedzenie", 0xFFDD2C00, R.drawable.ic_category2, false),
                new Category("Samochód", 0xFF795548, R.drawable.ic_category3, false),
                new Category("Praca", 0xFF0D47A1, R.drawable.ic_category4, false),
                new Category("Dom", 0xFF33691E, R.drawable.ic_category5, false),
                new Category("Edukacja", 0xFF757575, R.drawable.ic_category7, false),
                new Category("Zdrowie", 0xFFB71C1C, R.drawable.ic_category8, false),
                new Category("Wakacje", 0xFFFF6D00, R.drawable.ic_category9, false),
                new Category("Dzieci", 0xFFC51162, R.drawable.ic_category11, false),
                new Category("Sport", 0xFFE65100, R.drawable.ic_category14, false),
                new Category("Zwierzęta", 0xFF6200EA, R.drawable.ic_category16, false),
                new Category("Kosmetyczne", 0xFF00BFA5, R.drawable.ic_category17, false),
                new Category("Rachunki", 0xFF9E9D24, R.drawable.ic_category18, false),
                new Category("Prezenty", 0xFFFFAB00, R.drawable.ic_category19, false),
                new Category("Ubrania", 0xFF00838F, R.drawable.ic_category23, false),
                new Category("Naprawy", 0xFF546E7A, R.drawable.ic_category26, false),
                new Category("Rozrywka", 0xFF00C853, R.drawable.ic_category28, false),
                new Category("Paliwo", 0xFF5D4037, R.drawable.ic_category33, false),
                new Category("Fast Food", 0xFF7B1FA2, R.drawable.ic_category34, false)
        };
    }

    public abstract PaymentDao paymentDao();

    public abstract CategoryDao categoryDao();

    public abstract UserDao userDao();

    public abstract ParticipantDao participantDao();
}
