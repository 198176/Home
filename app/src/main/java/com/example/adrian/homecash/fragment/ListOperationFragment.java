package com.example.adrian.homecash.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.adrian.homecash.R;
import com.example.adrian.homecash.activity.ExpenseSplitterActivity;
import com.example.adrian.homecash.activity.OperationActivity;
import com.example.adrian.homecash.adapter.ListOperationAdapter;
import com.example.adrian.homecash.database.DBCallbackCursor;
import com.example.adrian.homecash.database.PaymentDBUtils;

import static com.example.adrian.homecash.MainActivity.getDayBilling;
import static com.example.adrian.homecash.MainActivity.getPersonId;
import static com.example.adrian.homecash.MainActivity.getSpinnerDate;

public class ListOperationFragment extends Fragment implements ListOperationAdapter.OperationListener {


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
    private Bundle bundle;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (RecyclerView) inflater.inflate(R.layout.dialog_category, container, false);
        view.setLayoutManager(new LinearLayoutManager(getActivity()));
        bundle = getArguments();
        adapter = new ListOperationAdapter(this);
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
                if (getPersonId() == null) {
                    PaymentDBUtils.getAllPaymentsByDateForAllUsers(callbackCursor, getDayBilling(), getSpinnerDate());
                } else {
                    PaymentDBUtils.getAllPaymentsByDateAndUser(callbackCursor, getDayBilling(), getSpinnerDate(), getPersonId());
                }
            }
        } catch (SQLiteException w) {
            Toast.makeText(getActivity(), R.string.database_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
    }

    @Override
    public void editOperation(int id, boolean isPlus) {
        Intent intent = new Intent();
        if (isPlus) {
            intent.setClass(view.getContext(), OperationActivity.class);
        } else {
            intent.setClass(view.getContext(), ExpenseSplitterActivity.class);
        }
        intent.putExtra(OperationActivity.EDIT, id);
        startActivityForResult(intent, 0);
    }
}
