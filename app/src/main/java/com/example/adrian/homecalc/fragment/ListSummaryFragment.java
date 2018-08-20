package com.example.adrian.homecalc.fragment;


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
import android.widget.ToggleButton;

import com.example.adrian.homecalc.MainActivity;
import com.example.adrian.homecalc.activity.OperationActivity;
import com.example.adrian.homecalc.R;
import com.example.adrian.homecalc.adapter.SummaryAdapter;
import com.example.adrian.homecalc.database.DBCallbackCursor;
import com.example.adrian.homecalc.database.PaymentDBUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;

import static com.example.adrian.homecalc.MainActivity.getDayBilling;
import static com.example.adrian.homecalc.MainActivity.getPersonId;

public class ListSummaryFragment extends Fragment implements SummaryAdapter.SummaryListener {

    private PieChart pieChart;
    private Cursor cursor;
    private RecyclerView recyclerView;
    private ToggleButton toggleButton;
    private SummaryAdapter adapter;

    DBCallbackCursor dbCallbackCursor = new DBCallbackCursor() {
        @Override
        public void onCallback(Cursor callCursor) {
            cursor = callCursor;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    adapter.setCursor(cursor);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_summary, container, false);
        recyclerView = view.findViewById(R.id.summary_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new SummaryAdapter(this);
        recyclerView.setAdapter(adapter);
        toggleButton = view.findViewById(R.id.summary_toggle);
        pieChart = view.findViewById(R.id.chart1);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStart();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (toggleButton.isChecked()) {
                PaymentDBUtils.getPositiveSummaryPaymentsCategories(dbCallbackCursor, getDayBilling(), MainActivity.getSpinnerDate(), getPersonId());
            } else {
                PaymentDBUtils.getNegativeSummaryPaymentsCategories(dbCallbackCursor, getDayBilling(), MainActivity.getSpinnerDate(), getPersonId());
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
    public void setSummary(ArrayList<Entry> entry, ArrayList<String> labels, ArrayList<Integer> colors, double sum) {
        PieDataSet pieDataSet = new PieDataSet(entry, "");
        PieData pieData = new PieData(labels, pieDataSet);
        pieDataSet.setColors(colors);
        pieChart.setData(pieData);
        pieChart.animateY(1000);
        pieDataSet.setValueTextColor(0xFFFFFFFF);
        pieDataSet.setValueTextSize(10f);
        pieChart.setCenterText(OperationActivity.replaceDoubleToString(sum));
    }
}
