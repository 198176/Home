package com.example.adrian.homecalc.fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adrian.homecalc.adapter.ArrearAdapter;
import com.example.adrian.homecalc.R;
import com.example.adrian.homecalc.database.DBCallbackCursor;
import com.example.adrian.homecalc.database.PaymentDBUtils;

import java.util.ArrayList;

public class ArrearFragment extends Fragment {

    private ArrayList<String[]> listArrear = new ArrayList<>();
    private ArrayList<String[]> listRepayment = new ArrayList<>();
    private RecyclerView recyclerArrear;
    private ArrearAdapter arrearAdapter;
    DBCallbackCursor callbackArrears = new DBCallbackCursor() {
        @Override
        public void onCallback(Cursor cursor) {
            addToList(listArrear, cursor);
            recyclerArrear.post(new Runnable() {
                @Override
                public void run() {
                    arrearAdapter.setList(getListSettledArrears(listArrear));
                    arrearAdapter.notifyDataSetChanged();
                }
            });
        }
    };
    private ArrearAdapter repaymentAdapter;
    private RecyclerView recyclerRepayment;
    DBCallbackCursor callbackRepayments = new DBCallbackCursor() {
        @Override
        public void onCallback(Cursor cursor) {
            addToList(listRepayment, cursor);
            recyclerRepayment.post(new Runnable() {
                @Override
                public void run() {
                    repaymentAdapter.setList(listRepayment);
                    repaymentAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrearAdapter = new ArrearAdapter();
        repaymentAdapter = new ArrearAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arrear, container, false);
        recyclerArrear = view.findViewById(R.id.arrear_recycler);
        recyclerRepayment = view.findViewById(R.id.repayment_recycler);
        recyclerArrear.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerRepayment.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerArrear.setAdapter(arrearAdapter);
        recyclerRepayment.setAdapter(repaymentAdapter);
        PaymentDBUtils.getArrearsUsers(callbackArrears);
        PaymentDBUtils.getRepaymentsUsers(callbackRepayments);
        getActivity().setTitle("Zaległości");
        FloatingActionButton fab = getActivity().findViewById(R.id.drawer_fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.drawer_fragment, new NewRepaymentFragment()).commit();
            }
        });

//            Cursor cursorPayment = db.rawQuery("SELECT SUM(PAYMENT.VALUE), PAYMENT.PAYING_ID, PAYMENT.PERSON_ID, " +
//                    "PAYING.NAME, PAYING.COLOR, P.NAME, P.COLOR FROM PAYMENT, PERSON AS P, PERSON AS PAYING " +
//                    "WHERE PAYMENT.PERSON_ID=P._id AND PAYMENT.PAYING_ID=PAYING._id GROUP BY PAYMENT.PAYING_ID, " +
//                    "PAYMENT.PERSON_ID HAVING PAYMENT.PAYING_ID!=PAYMENT.PERSON_ID", null);
//            addToList(listPayment, cursorPayment);
//            Cursor cursorRepayment = db.rawQuery("SELECT SUM(PAYMENT.VALUE), PAYMENT.PERSON_ID, PAYMENT.PAYING_ID, " +
//                    "P.NAME, P.COLOR, PAYING.NAME, PAYING.COLOR FROM PAYMENT, PERSON AS P, PERSON AS PAYING " +
//                    "WHERE PAYMENT.PERSON_ID=P._id AND PAYMENT.PAYING_ID=PAYING._id AND PAYMENT.ID_PAY=0 " +
//                    "GROUP BY PAYMENT.PAYING_ID, PAYMENT.PERSON_ID", null);
//            addToList(listRepayment, cursorRepayment);
        return view;
    }

    private void addToList(ArrayList<String[]> list, Cursor cursor) {
        list.clear();
        if (cursor.moveToFirst()) {
            do {
                list.add(new String[]{Double.toString(Math.abs(cursor.getDouble(0))),
                        Integer.toString(cursor.getInt(1)), Integer.toString(cursor.getInt(2)),
                        cursor.getString(3), Integer.toString(cursor.getInt(4)),
                        cursor.getString(5), Integer.toString(cursor.getInt(6))});
            } while (cursor.moveToNext());
        }
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

}
