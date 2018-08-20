package com.example.adrian.homecalc.adapter;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adrian.homecalc.activity.OperationActivity;
import com.example.adrian.homecalc.R;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class SummaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SummaryListener listener;
    private Cursor cursor;
    private ArrayList<Entry> BarEntry;
    private ArrayList<String> BarEntryLabels;
    private ArrayList<Integer> BarColors;

    public SummaryAdapter(SummaryListener listener) {
        this.listener = listener;
        BarEntry = new ArrayList<>();
        BarEntryLabels = new ArrayList<>();
        BarColors = new ArrayList<>();
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        setDataChart();
    }

    private void setDataChart() {
        BarEntry.clear();
        BarEntryLabels.clear();
        BarColors.clear();
        double sum = 0;
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            float value = Math.abs(cursor.getFloat(cursor.getColumnIndex("value")));
            sum += value;
            BarEntry.add(new BarEntry(value, i));
            BarEntryLabels.add(cursor.getString(cursor.getColumnIndex("name")));
            BarColors.add(cursor.getInt(cursor.getColumnIndex("color")));
        }
        listener.setSummary(BarEntry, BarEntryLabels, BarColors, sum);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SummaryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ((SummaryViewHolder) holder).bind(position);
    }

    @Override
    public int getItemCount() {
        return (cursor == null) ? 0 : cursor.getCount();
    }

    public interface SummaryListener {
        void setSummary(ArrayList<Entry> entry, ArrayList<String> labels, ArrayList<Integer> colors, double sum);
    }

    public class SummaryViewHolder extends RecyclerView.ViewHolder {
        View parent;
        FloatingActionButton floating;
        TextView title;
        TextView value;

        SummaryViewHolder(View v) {
            super(v);
            parent = v;
            floating = v.findViewById(R.id.floatingButton);
            title = v.findViewById(R.id.category_title);
            value = v.findViewById(R.id.category_value);
        }

        void bind(int position) {
            cursor.moveToPosition(position);
            Drawable drawable = parent.getResources().getDrawable(cursor.getInt(cursor.getColumnIndex("icon")));
            floating.setBackgroundTintList(ColorStateList.valueOf(cursor.getInt(cursor.getColumnIndex("color"))));
            floating.setImageDrawable(drawable);
            title.setText(cursor.getString(cursor.getColumnIndex("name")));
            String mark = "";
            double val = cursor.getDouble(cursor.getColumnIndex("value"));
            if (val < 0) {
                value.setTextColor(0xFFD50000);
            } else {
                value.setTextColor(0xFF2E7D32);
                mark = "+";
            }
            value.setText(mark + OperationActivity.replaceDoubleToString(val));
        }
    }
}

