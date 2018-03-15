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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    interface CategoryListener{
        void setCategory(String text, int ids);
    }

    private CategoryListener listener;

    private Cursor cursor;

    public CategoryAdapter(Cursor cursor){
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
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    cursor.moveToPosition(position);
                    listener.setCategory(cursor.getString(0), cursor.getInt(3));
                }
            }
        });
        if(!cursor.isNull(4)){
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
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void setListener(CategoryListener listener) {
        this.listener = listener;
    }
}
