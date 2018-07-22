package com.example.adrian.homecalc;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Adrian on 2018-03-20.
 */

public class ListPersonOperationFragment extends Fragment {

    private SQLiteDatabase db;
    private Cursor cursor;
    private RecyclerView view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            SQLiteOpenHelper helper = new ApplicationDatabase(getActivity());
            db = helper.getReadableDatabase();
        } catch(SQLiteException w){
            Toast.makeText(getActivity(), R.string.database_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (RecyclerView) inflater.inflate(R.layout.dialog_category, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        view.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            cursor = db.rawQuery("SELECT PAYMENT.TITLE, PAYMENT.VALUE, strftime('%Y-%m-%d', " +
                    "date(DATE/1000, 'unixepoch', 'localtime')), CATEGORY.NAME, CATEGORY.COLOR, CATEGORY.ICON_ID, " +
                    "(CASE WHEN cast(strftime('%d', date(DATE/1000, 'unixepoch', 'localtime')) as integer) < strftime("+MainActivity.dayBilling+") " +
                    "THEN strftime('%Y-%m', date(DATE/1000, 'unixepoch', 'localtime', '-1 month')) ELSE " +
                    "strftime('%Y-%m', date(DATE/1000, 'unixepoch', 'localtime')) END) MONTH FROM PAYMENT, " +
                    "CATEGORY WHERE PAYMENT.CATEGORY_ID=CATEGORY._id AND MONTH = '" + MainActivity.getSpinnerDate() +
                    "' AND DATE/1000 <= cast(strftime('%s', 'now') as integer) AND PAYMENT.PERSON_ID='" +
                    MainActivity.getPersonId() + "' ORDER BY PAYMENT.DATE DESC, PAYMENT._id DESC", null);
        } catch (SQLiteException w) {
            Toast.makeText(getActivity(), R.string.database_error, Toast.LENGTH_SHORT).show();
        }

        ListOperationAdapter adapter = new ListOperationAdapter(cursor);
        view.setAdapter(adapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }

}
