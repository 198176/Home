package com.example.adrian.homecalc;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.ViewHolder> {

    private SummaryListener listener;
    private Cursor cursor;
    private ArrayList<Entry> BARENTRY;
    private ArrayList<String> BarEntryLabels;
    private ArrayList<Integer> BarColors;
    private boolean pointer;
    private double sum;
    public SummaryAdapter(Cursor cursor) {
        this.cursor = cursor;
        BARENTRY = new ArrayList<>();
        BarEntryLabels = new ArrayList<>();
        BarColors = new ArrayList<>();
        pointer = true;
        sum = 0;
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            float value = cursor.getFloat(4);
            if (value < 0) {
                value *= -1;
            }
            sum += value;
            BARENTRY.add(new BarEntry(value, i));
            BarEntryLabels.add(cursor.getString(0));
            BarColors.add(cursor.getInt(1));
        }
    }

    @Override
    public SummaryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_category, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        cursor.moveToPosition(position);
        FloatingActionButton floating = (FloatingActionButton) cardView.findViewById(R.id.floatingButton);
        Drawable drawable = cardView.getResources().getDrawable(cursor.getInt(2));
        floating.setBackgroundTintList(ColorStateList.valueOf(cursor.getInt(1)));
        floating.setImageDrawable(drawable);
        TextView textView = (TextView) cardView.findViewById(R.id.category_title);
        textView.setText(cursor.getString(0));
        TextView value = (TextView) cardView.findViewById(R.id.category_value);
        String mark = "";
        double val = cursor.getDouble(4);
        if (val < 0) {
            value.setTextColor(0xFFD50000);
        } else {
            value.setTextColor(0xFF2E7D32);
            mark = "+";
        }
        value.setText(mark + OperationActivity.replaceDoubleToString(val));

//        BARENTRY.add(new BarEntry(-1*cursor.getFloat(4), position));
//        BarEntryLabels.add(cursor.getString(0));
//        BarColors.add(cursor.getInt(1));
        if (pointer) {
            listener.setSummary(BARENTRY, BarEntryLabels, BarColors, sum);
            pointer = false;
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void setListener(SummaryListener listener) {
        this.listener = listener;
    }

    interface SummaryListener {
        void setSummary(ArrayList<Entry> entry, ArrayList<String> labels, ArrayList<Integer> colors, double sum);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }
}

