package com.example.adrian.homecalc;


import android.app.Fragment;
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

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArrearFragment extends Fragment {

    private SQLiteDatabase db;

    public ArrearFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arrear, container, false);
        RecyclerView recyclerArrear = (RecyclerView) view.findViewById(R.id.arrear_recycler);
        RecyclerView recyclerRepayment = (RecyclerView) view.findViewById(R.id.repayment_recycler);
        ArrayList<String[]> listPayment = new ArrayList<>();
        ArrayList<String[]> listRepayment = new ArrayList<>();

        try {
            SQLiteOpenHelper helper = new ApplicationDatabase(getActivity());
            db = helper.getReadableDatabase();
            Cursor cursorPayment = db.rawQuery("SELECT SUM(PAYMENT.VALUE), PAYMENT.PAYING_ID, PAYMENT.PERSON_ID, " +
                    "PAYING.NAME, PAYING.COLOR, P.NAME, P.COLOR FROM PAYMENT, PERSON AS P, PERSON AS PAYING " +
                    "WHERE PAYMENT.PERSON_ID=P._id AND PAYMENT.PAYING_ID=PAYING._id GROUP BY PAYMENT.PAYING_ID, " +
                    "PAYMENT.PERSON_ID HAVING PAYMENT.PAYING_ID!=PAYMENT.PERSON_ID", null);
            addToList(listPayment, cursorPayment);
            Cursor cursorRepayment = db.rawQuery("SELECT SUM(PAYMENT.VALUE), PAYMENT.PERSON_ID, PAYMENT.PAYING_ID, " +
                    "P.NAME, P.COLOR, PAYING.NAME, PAYING.COLOR FROM PAYMENT, PERSON AS P, PERSON AS PAYING " +
                    "WHERE PAYMENT.PERSON_ID=P._id AND PAYMENT.PAYING_ID=PAYING._id AND PAYMENT.ID_PAY=0 " +
                    "GROUP BY PAYMENT.PAYING_ID, PAYMENT.PERSON_ID", null);
            addToList(listRepayment, cursorRepayment);
        } catch (SQLiteException w) {
            Toast.makeText(getActivity(), R.string.database_error, Toast.LENGTH_SHORT).show();
        }

        recyclerArrear.setAdapter(new ArrearAdapter(getListSettledArrears(listPayment)));
        recyclerArrear.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerRepayment.setAdapter(new ArrearAdapter(listRepayment));
        recyclerRepayment.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    private void addToList(ArrayList<String[]> list, Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                list.add(new String[]{Double.toString(Math.abs(cursor.getDouble(0))),
                        Integer.toString(cursor.getInt(1)), Integer.toString(cursor.getInt(2)),
                        cursor.getString(3), Integer.toString(cursor.getInt(4)),
                        cursor.getString(5), Integer.toString(cursor.getInt(6))});
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private ArrayList<String[]> getListSettledArrears(ArrayList<String[]> listPayment) {
        ArrayList<String[]> listAdapter = new ArrayList<>();
        String[] first, array;
        boolean flag;
        while (listPayment.size() > 0) {
            first = listPayment.get(0);
            flag = true;
            for (int i = 1; i < listPayment.size(); i++) {
                array = listPayment.get(i);
                if (first[1].equals(array[2]) && first[2].equals(array[1])) {
                    double valueArray = Double.parseDouble(array[0]);
                    double valueFirst = Double.parseDouble(first[0]);
                    if (valueArray > valueFirst) {
                        String valueText = Double.toString(valueArray - valueFirst);
                        array[0] = valueText;
                        first = array;
                    } else if (valueArray < valueFirst) {
                        String valueText = Double.toString(valueFirst - valueArray);
                        first[0] = valueText;
                    } else {
                        flag = false;
                    }
                    listPayment.remove(i);
                    break;
                }
            }
            if (flag) {
                listAdapter.add(first);
            }
            listPayment.remove(0);
        }
        return listAdapter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
