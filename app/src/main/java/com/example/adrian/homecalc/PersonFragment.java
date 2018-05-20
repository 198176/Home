package com.example.adrian.homecalc;


import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
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
    
    private RecyclerView view;
    private SQLiteDatabase db;
    private Cursor cursor;
    public PersonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (RecyclerView) inflater.inflate(R.layout.dialog_category, null);
        try {
            SQLiteOpenHelper helper = new ApplicationDatabase(getActivity());
            db = helper.getReadableDatabase();
            cursor = db.query("PERSON", new String[]{"NAME", "COLOR", "_id"}, null, null, null, null, null);
        } catch (SQLiteException w) {
            Toast toast = Toast.makeText(getActivity(), "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
        }
        UserAdapter adapter = new UserAdapter(cursor);
        view.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        view.setLayoutManager(layoutManager);
        adapter.setPersonListener(new UserAdapter.PersonListener() {
            @Override
            public void setPerson(String text, int color, int id) {
                Intent intent = new Intent(getActivity(), NewUserActivity.class);
                intent.putExtra(NewUserActivity.EDIT, id);
                startActivityForResult(intent, 0);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }

}
