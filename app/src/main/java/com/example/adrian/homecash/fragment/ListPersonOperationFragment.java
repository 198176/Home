package com.example.adrian.homecash.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.adrian.homecash.MainActivity;
import com.example.adrian.homecash.R;
import com.example.adrian.homecash.adapter.ListOperationAdapter;
import com.example.adrian.homecash.database.DBCallbackCursor;
import com.example.adrian.homecash.database.PaymentDBUtils;

import static com.example.adrian.homecash.MainActivity.getDayBilling;
import static com.example.adrian.homecash.MainActivity.getPersonId;

public class ListPersonOperationFragment extends Fragment {

    private Cursor cursor;
    private RecyclerView view;
    private ListOperationAdapter adapter;

    DBCallbackCursor dbCallbackCursor = new DBCallbackCursor() {
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ListOperationAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (RecyclerView) inflater.inflate(R.layout.dialog_category, container, false);
        view.setLayoutManager(new LinearLayoutManager(getActivity()));
        view.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (getPersonId() == null) {
                PaymentDBUtils.getAllParticipantPaymentForAllUsers(dbCallbackCursor, getDayBilling(), MainActivity.getSpinnerDate());
            } else {
                PaymentDBUtils.getAllParticipantPaymentByUser(dbCallbackCursor, getDayBilling(), MainActivity.getSpinnerDate(), getPersonId());
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

}
