package com.example.adrian.homecalc;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.adrian.homecalc.database.DBCallbackCursor;
import com.example.adrian.homecalc.database.PaymentDBUtils;
import com.example.adrian.homecalc.sampledata.ExpenseSplitterActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListOperationFragment extends Fragment {


    ListOperationAdapter adapter;
    private Cursor cursor;
    private RecyclerView view;
    DBCallbackCursor callbackCursor = new DBCallbackCursor() {
        @Override
        public void onCallback(Cursor callCursor) {
            cursor = callCursor;
            view.post(new Runnable() {
                @Override
                public void run() {
                    adapter.setCursor(cursor);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };
    private Toast toast;
    private Bundle bundle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        toast = Toast.makeText(getActivity(), R.string.database_error, Toast.LENGTH_SHORT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (RecyclerView) inflater.inflate(R.layout.dialog_category, container, false);
        view.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ListOperationAdapter();
        adapter.setOperationListener(new ListOperationAdapter.OperationListener() {
            @Override
            public void editOperation(int id, boolean isPlus) {
                Intent intent = new Intent();
                if (isPlus) {
                    intent.setClass(getActivity(), OperationActivity.class);
                } else {
                    intent.setClass(getActivity(), ExpenseSplitterActivity.class);
                }
                intent.putExtra(OperationActivity.EDIT, id);
                startActivityForResult(intent, 0);
            }
        });
        view.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (bundle != null) {
                if (bundle.containsKey("planned") && bundle.getBoolean("planned")) {
                    PaymentDBUtils.getAllPlannedPayments(callbackCursor);
                }
            } else {
                if (MainActivity.person_id == -1) {
                    PaymentDBUtils.getAllPaymentsWhereDateForAllUsers(callbackCursor, MainActivity.dayBilling, MainActivity.getSpinnerDate());
                } else {
                    PaymentDBUtils.getAllPaymentsWhereDateAndUser(callbackCursor, MainActivity.dayBilling, MainActivity.getSpinnerDate(), MainActivity.person_id);
                }
            }
        } catch (SQLiteException w) {
            toast.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
    }

}
