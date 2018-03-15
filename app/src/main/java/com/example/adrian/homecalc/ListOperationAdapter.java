package com.example.adrian.homecalc;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Adrian on 2017-08-26.
 */

public class ListOperationAdapter extends RecyclerView.Adapter<ListOperationAdapter.ViewHolder> {

    private Cursor cursor;

    public ListOperationAdapter(Cursor cursor){
        this.cursor = cursor;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    @Override
    public ListOperationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_operation, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ListOperationAdapter.ViewHolder holder, int position) {
        String mark = "";
        CardView cardView = holder.cardView;
        cursor.moveToPosition(position);
        TextView title = (TextView) cardView.findViewById(R.id.title_operation);
        title.setText(cursor.getString(0));
        TextView value = (TextView) cardView.findViewById(R.id.value_operation);
        if(cursor.getDouble(1)<0) {
            value.setTextColor(0xFFD50000);
        }
        else{
            mark = "+";
            value.setTextColor(0xFF2E7D32);
        }
        value.setText(mark+String.format("%.2f zł", cursor.getDouble(1)));
        TextView date = (TextView) cardView.findViewById(R.id.date_operation);
        date.setText(cursor.getString(2));
        TextView category = (TextView) cardView.findViewById(R.id.category_operation);
        category.setText(cursor.getString(3));
        FloatingActionButton floating = (FloatingActionButton) cardView.findViewById(R.id.floatingCategory);
        Drawable drawable = cardView.getResources().getDrawable(cursor.getInt(5));
        floating.setBackgroundTintList(ColorStateList.valueOf(cursor.getInt(4)));
        floating.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

}
