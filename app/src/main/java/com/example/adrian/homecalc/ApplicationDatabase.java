package com.example.adrian.homecalc;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Adrian on 2017-08-10.
 */

public class ApplicationDatabase extends SQLiteOpenHelper {

    public static final String CATEGORY = "CATEGORY";
    public static final String PERSON = "PERSON";
    public static final String PAYMENT = "PAYMENT";
    public static final String NAME = "NAME";
    public static final String COLOR = "COLOR";
    public static final String ICON_ID = "ICON_ID";
    public static final String DEFAULTS = "DEFAULTS";
    private static final String DB_NAME = "Application";
    private static final int DB_VERSION = 1;

    public ApplicationDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private static void insertCategory(SQLiteDatabase db, String name, int color, int icon, int def) {
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(COLOR, color);
        values.put(ICON_ID, icon);
        values.put(DEFAULTS, def);
        db.insert(CATEGORY, null, values);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE CATEGORY (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "NAME TEXT, " + "COLOR INTEGER, " + "ICON_ID INTEGER, " + "DEFAULTS INTEGER);");
        insertCategory(db, "Inne", 0xFF000000, R.drawable.ic_category1, 1);
        insertCategory(db, "Jedzenie", 0xFFDD2C00, R.drawable.ic_category2, 0);
        insertCategory(db, "Samochód", 0xFF795548, R.drawable.ic_category3, 0);
        insertCategory(db, "Praca", 0xFF0D47A1, R.drawable.ic_category4, 0);
        insertCategory(db, "Dom", 0xFF33691E, R.drawable.ic_category5, 0);
        insertCategory(db, "Edukacja", 0xFF757575, R.drawable.ic_category7, 0);
        insertCategory(db, "Zdrowie", 0xFFB71C1C, R.drawable.ic_category8, 0);
        insertCategory(db, "Wakacje", 0xFFFF6D00, R.drawable.ic_category9, 0);
        insertCategory(db, "Dzieci", 0xFFC51162, R.drawable.ic_category11, 0);
        insertCategory(db, "Sport", 0xFFE65100, R.drawable.ic_category14, 0);
        insertCategory(db, "Zwierzęta", 0xFF6200EA, R.drawable.ic_category16, 0);
        insertCategory(db, "Kosmetyczne", 0xFF00BFA5, R.drawable.ic_category17, 0);
        insertCategory(db, "Rachunki", 0xFF9E9D24, R.drawable.ic_category18, 0);
        insertCategory(db, "Prezenty", 0xFFFFAB00, R.drawable.ic_category19, 0);
        insertCategory(db, "Ubrania", 0xFF00838F, R.drawable.ic_category23, 0);
        insertCategory(db, "Naprawy", 0xFF546E7A, R.drawable.ic_category26, 0);
        insertCategory(db, "Rozrywka", 0xFF00C853, R.drawable.ic_category28, 0);
        insertCategory(db, "Paliwo", 0xFF5D4037, R.drawable.ic_category33, 0);
        insertCategory(db, "Fast Food", 0xFF7B1FA2, R.drawable.ic_category34, 0);

        db.execSQL("CREATE TABLE PERSON (_id INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, COLOR INTEGER);");
        ContentValues values = new ContentValues();
        values.put(NAME, "Ja");
        values.put(COLOR, 0xFF03A9F4);
        db.insert(PERSON, null, values);

        db.execSQL("CREATE TABLE PAYMENT (_id INTEGER PRIMARY KEY AUTOINCREMENT, ID_PAY INTEGER, "
                + "TITLE TEXT, VALUE REAL, DATE INTEGER, CATEGORY_ID INTEGER, PERSON_ID INTEGER, "
                + "PAYING_ID INTEGER, FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORY (_id));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
