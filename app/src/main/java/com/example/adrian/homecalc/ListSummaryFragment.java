package com.example.adrian.homecalc;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListSummaryFragment extends Fragment {


    public ListSummaryFragment() {
        // Required empty public constructor
    }


    private SQLiteDatabase db;
    private Cursor cursor;
    private View view;
    private RecyclerView rview;
    private SQLiteOpenHelper helper;
    private SummaryAdapter adapter;

    BarChart chart ;
    //ArrayList<BarEntry> BARENTRY ;
    //ArrayList<String> BarEntryLabels ;
    BarDataSet Bardataset ;
    BarData BARDATA ;

    PieChart pieChart;
    PieDataSet pieDataSet ;
    PieData pieData ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            helper = new ApplicationDatabase(getActivity());
            db = helper.getReadableDatabase();
        } catch(SQLiteException w){
            Toast toast = Toast.makeText(getActivity(), "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
        }

        //BARENTRY = new ArrayList<>();

        //BarEntryLabels = new ArrayList<String>();

        //AddValuesToBARENTRY();

        //AddValuesToBarEntryLabels();

        //Bardataset = new BarDataSet(BARENTRY, "Projects");

        //BARDATA = new BarData(BarEntryLabels, Bardataset);

        //Bardataset.setColors();
    }

//    public void AddValuesToBARENTRY(){
//
//        BARENTRY.add(new BarEntry(2f, 0));
//        BARENTRY.add(new BarEntry(4f, 1));
//        BARENTRY.add(new BarEntry(6f, 2));
//        BARENTRY.add(new BarEntry(8f, 3));
//        BARENTRY.add(new BarEntry(7f, 4));
//        BARENTRY.add(new BarEntry(3f, 5));
//    }
//
//    public void AddValuesToBarEntryLabels(){
//
//        BarEntryLabels.add("January");
//        BarEntryLabels.add("February");
//        BarEntryLabels.add("March");
//        BarEntryLabels.add("April");
//        BarEntryLabels.add("May");
//        BarEntryLabels.add("June");
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_summary, container, false);
        rview = (RecyclerView) view.findViewById(R.id.summary_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rview.setLayoutManager(layoutManager);

        //chart = (BarChart) view.findViewById(R.id.chart1);
        pieChart = (PieChart) view.findViewById(R.id.chart1);

//        chart.setData(BARDATA);
//
//        chart.animateY(3000);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        try{
            cursor = db.rawQuery("SELECT CATEGORY.NAME, CATEGORY.COLOR, CATEGORY.ICON_ID, CATEGORY._id, SUM(PAYMENT.VALUE) "+
                    "FROM PAYMENT, CATEGORY WHERE PAYMENT.CATEGORY_ID=CATEGORY._id AND SUBSTR(PAYMENT.DATE, 1, 7)='" +
                    MainActivity.getSpinnerDate() + "' AND PAYMENT.PERSON_ID='" +
                    MainActivity.getPersonId() + "' GROUP BY CATEGORY._id",null);
        } catch(SQLiteException w){
            Toast toast = Toast.makeText(getActivity(), "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
        }
        adapter = new SummaryAdapter(cursor);
        rview.setAdapter(adapter);
        adapter.setListener(new SummaryAdapter.SummaryListener() {
            @Override
            public void setSummary(ArrayList<Entry> entry, ArrayList<String> labels, ArrayList<Integer> colors) {
                //Bardataset = new BarDataSet(entry, "Projects");
                pieDataSet = new PieDataSet(entry, "");
                //BARDATA = new BarData(labels, Bardataset);
                pieData = new PieData(labels, pieDataSet);
                //Bardataset.setColors(colors);
                pieDataSet.setColors(colors);
                //chart.setData(BARDATA);
                pieChart.setData(pieData);
                //chart.animateY(3000);
                pieChart.animateY(3000);
                pieDataSet.setValueTextColor(0xFFFFFFFF);
                pieDataSet.setValueTextSize(10f);
                pieChart.setCenterText("1000 zł");
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }
}