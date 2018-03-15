package com.example.adrian.homecalc;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends DialogFragment {

    interface CategoryListener{
        void setCategory(String text, int ids);
    }

    private CategoryListener listener;

    private RecyclerView view;
    private ListView list;
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = (RecyclerView) inflater.inflate(R.layout.dialog_category, null);
        try{
            SQLiteOpenHelper helper = new ApplicationDatabase(getActivity());
            db = helper.getReadableDatabase();
            cursor = db.query("CATEGORY", new String[]{"NAME", "COLOR", "ICON_ID", "_id"}, null, null, null, null, null);
        } catch(SQLiteException w){
            Toast toast = Toast.makeText(getActivity(), "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
        }
        CategoryAdapter adapter = new CategoryAdapter(cursor);
        view.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        view.setLayoutManager(layoutManager);
        adapter.setListener(new CategoryAdapter.CategoryListener() {
            @Override
            public void setCategory(String text, int ids) {
                listener.setCategory(text, ids);
                dismiss();
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("Kategorie");
        builder.setNeutralButton("Dodaj kategorię", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), NewCategoryActivity.class);
                startActivity(intent);
            }
        });
        return builder.create();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (CategoryListener) activity;
    }
}
