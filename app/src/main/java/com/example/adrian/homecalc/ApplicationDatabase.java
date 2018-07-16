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
