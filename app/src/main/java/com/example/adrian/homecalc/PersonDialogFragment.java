package com.example.adrian.homecalc;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.Toast;

/**
 * Created by Adrian on 2018-03-20.
 */

public class PersonDialogFragment extends DialogFragment {

    public static final String ALL = "all";
    private PersonDialogFragment.PersonListener listener;
    private RecyclerView view;
    private SQLiteDatabase db;
    private Cursor cursor;
    private boolean flag = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = (RecyclerView) inflater.inflate(R.layout.dialog_category, null);
        try {
            SQLiteOpenHelper helper = new ApplicationDatabase(getActivity());
            db = helper.getReadableDatabase();
            cursor = db.query("PERSON", new String[]{"NAME", "COLOR", "_id"}, null, null, null, null, null);
        } catch (SQLiteException w) {
            Toast toast = Toast.makeText(getActivity(), "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
        }
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(ALL)) {
            flag = bundle.getBoolean(ALL, true);
        }
        UserAdapter adapter = new UserAdapter(cursor, flag);
        view.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        view.setLayoutManager(layoutManager);
        adapter.setPersonListener(new UserAdapter.PersonListener() {
            @Override
            public void setPerson(String text, int color, int ids) {
                listener.setPerson(text, color, ids);
                dismiss();
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("Osoby");
        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (PersonDialogFragment.PersonListener) activity;
    }

    interface PersonListener {
        void setPerson(String text, int color, int ids);
    }

}
