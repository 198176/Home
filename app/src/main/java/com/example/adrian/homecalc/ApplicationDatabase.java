package com.example.adrian.homecalc;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Adrian on 2017-08-10.
 */

public class ApplicationDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "Application";
    private static final int DB_VERSION = 1;
    public static final String CATEGORY = "CATEGORY";
    public static final String NAME = "NAME";
    public static final String COLOR = "COLOR";
    public static final String ICON_ID = "ICON_ID";
    public static final String DEFAULTS = "DEFAULTS";

    public ApplicationDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE CATEGORY (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            +"NAME TEXT, "+"COLOR INTEGER, "+"ICON_ID INTEGER, "+"DEFAULTS INTEGER);");
        insertCategory(db, "Inne", 0xFF000000, R.drawable.ic_category1, 1);
        insertCategory(db, "Jedzenie", 0xFFF90509, R.drawable.ic_category2, 0);

        db.execSQL("CREATE TABLE PERSON (_id INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, COLOR INTEGER, ICON_ID INTEGER);");
        ContentValues values = new ContentValues();
        values.put(NAME, "Ja");
        values.put(COLOR, 0xFF03A9F4);
        values.put(ICON_ID, R.drawable.ic_category1);
        db.insert("PERSON", null, values);
        values = new ContentValues();
        values.put(NAME, "Kasia");
        values.put(COLOR, 0xFFE91E63);
        values.put(ICON_ID, R.drawable.ic_category1);
        db.insert("PERSON", null, values);

        db.execSQL("CREATE TABLE PAYMENT (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+"TITLE TEXT, "+"VALUE REAL, "
                +"DATE TEXT, "+"CATEGORY_ID INTEGER, "+"FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORY (_id));");
//        ContentValues values = new ContentValues();
//        values.put("TITLE", "Tytuł");
//        values.put("VALUE", 22.34);
//        values.put("DATE", "20 sie 1994");
//        values.put("CATEGORY_ID", 1);
//        db.insert("PAYMENT", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static void insertCategory(SQLiteDatabase db, String name, int color, int icon, int def){
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(COLOR, color);
        values.put(ICON_ID, icon);
        values.put(DEFAULTS, def);
        db.insert(CATEGORY, null, values);
    }

}
