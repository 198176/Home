package com.example.adrian.homecalc;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonFragment extends Fragment {


    public PersonFragment() {
        // Required empty public constructor
    }

    private RecyclerView view;
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (RecyclerView) inflater.inflate(R.layout.dialog_category, null);
        try{
            SQLiteOpenHelper helper = new ApplicationDatabase(getActivity());
            db = helper.getReadableDatabase();
            cursor = db.query("PERSON", new String[]{"NAME", "COLOR", "_id"}, null, null, null, null, null);
        } catch(SQLiteException w){
            Toast toast = Toast.makeText(getActivity(), "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
        }
        UserAdapter adapter = new UserAdapter(cursor);
        view.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        view.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }

}
