package com.example.adrian.homecalc;


import android.content.Intent;
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
 * A simple {@link Fragment} subclass.
 */
public class ListOperationFragment extends Fragment {


    private SQLiteDatabase db;
    private Cursor cursor;
    private RecyclerView view;
    private Toast toast;

    public ListOperationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toast = Toast.makeText(getActivity(), R.string.database_error, Toast.LENGTH_SHORT);
        try {
            SQLiteOpenHelper helper = new ApplicationDatabase(getActivity());
            db = helper.getReadableDatabase();
//            cursor = db.rawQuery("SELECT PAYMENT.TITLE, PAYMENT.VALUE, PAYMENT.DATE, "+
//                            "CATEGORY.NAME, CATEGORY.COLOR, CATEGORY.ICON_ID FROM PAYMENT, CATEGORY "+
//                            "WHERE PAYMENT.CATEGORY_ID=CATEGORY._id",null);
        } catch (SQLiteException w) {
            toast.show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (RecyclerView) inflater.inflate(R.layout.dialog_category, container, false);
//        adapter = new ListOperationAdapter(cursor);
//        view.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        view.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            cursor = db.rawQuery("SELECT PAYMENT.TITLE, SUM(PAYMENT.VALUE), PAYMENT.DATE, " +
                    "CATEGORY.NAME, CATEGORY.COLOR, CATEGORY.ICON_ID, PAYMENT.ID_PAY FROM PAYMENT, CATEGORY " +
                    "WHERE PAYMENT.CATEGORY_ID=CATEGORY._id AND SUBSTR(PAYMENT.DATE, 1, 7)='" +
                    MainActivity.getSpinnerDate() + "' AND PAYMENT.PAYING_ID='" + MainActivity.getPersonId() +
                    "' GROUP BY PAYMENT.ID_PAY ORDER BY PAYMENT.DATE DESC, PAYMENT._id DESC", null);
        } catch (SQLiteException w) {
            toast.show();
        }

        ListOperationAdapter adapter = new ListOperationAdapter(cursor);
        adapter.setOperationListener(new ListOperationAdapter.OperationListener() {
            @Override
            public void editOperation(int id, boolean isPlus) {
                Intent intent = new Intent();
                if (isPlus) {
                    intent.setClass(getActivity(), OperationActivity.class);
                } else {
                    intent.setClass(getActivity(), ExpenseActivity.class);
                }
                intent.putExtra(OperationActivity.EDIT, id);
                startActivityForResult(intent, 0);
            }
        });
        view.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }

}
