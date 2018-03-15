package com.example.adrian.homecalc;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.ViewHolder> {

    interface SummaryListener{
        void setSummary(ArrayList<Entry> entry, ArrayList<String> labels, ArrayList<Integer> colors);
    }

    private SummaryListener listener;

    private Cursor cursor;

    ArrayList<Entry> BARENTRY ;
    ArrayList<String> BarEntryLabels ;
    ArrayList<Integer> BarColors;
    boolean pointer;

    public SummaryAdapter(Cursor cursor){
        this.cursor = cursor;
        BARENTRY = new ArrayList<>();
        BarEntryLabels = new ArrayList<>();
        BarColors = new ArrayList<>();
        pointer = true;
        for(int i=0; i<cursor.getCount(); i++){
            cursor.moveToPosition(i);
            BARENTRY.add(new BarEntry(-1*cursor.getFloat(4), i));
            BarEntryLabels.add(cursor.getString(0));
            BarColors.add(cursor.getInt(1));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    @Override
    public SummaryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_category, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
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
            if(val<0){
                value.setTextColor(0xFFD50000);
            }
            else{
                value.setTextColor(0xFF2E7D32);
                mark = "+";
            }
            value.setText(mark+String.format("%.2f zł", val));

//        BARENTRY.add(new BarEntry(-1*cursor.getFloat(4), position));
//        BarEntryLabels.add(cursor.getString(0));
//        BarColors.add(cursor.getInt(1));
        if(pointer) {
            listener.setSummary(BARENTRY, BarEntryLabels, BarColors);
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
}

