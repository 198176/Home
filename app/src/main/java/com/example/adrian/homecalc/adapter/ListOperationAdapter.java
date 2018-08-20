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

import com.example.adrian.homecalc.R;

public class ListOperationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Cursor cursor;
    private OperationListener listener;

    public ListOperationAdapter() {
    }

    public ListOperationAdapter(OperationListener listener) {
        this.listener = listener;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OperationViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_operation, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ((OperationViewHolder) holder).bind(position);
    }

    @Override
    public int getItemCount() {
        return (cursor == null) ? 0 : cursor.getCount();
    }

    public interface OperationListener {
        void editOperation(int id, boolean isPlus);
    }

    public class OperationViewHolder extends RecyclerView.ViewHolder {

        View parent;
        TextView title;
        TextView value;
        TextView date;
        TextView category;
        FloatingActionButton floating;

        OperationViewHolder(View v) {
            super(v);
            parent = v;
            title = v.findViewById(R.id.title_operation);
            value = v.findViewById(R.id.value_operation);
            date = v.findViewById(R.id.date_operation);
            category = v.findViewById(R.id.category_operation);
            floating = v.findViewById(R.id.floatingCategory);
        }

        void bind(final int position) {
            String mark = "";
            cursor.moveToPosition(position);
            title.setText(cursor.getString(cursor.getColumnIndex("title")));
            if (cursor.getDouble(cursor.getColumnIndex("value")) < 0) {
                value.setTextColor(0xFFD50000);
            } else {
                mark = "+";
                value.setTextColor(0xFF2E7D32);
            }
            value.setText(mark + String.format("%.2f zł", cursor.getDouble(cursor.getColumnIndex("value"))));
            date.setText(cursor.getString(cursor.getColumnIndex("date")));
            category.setText(cursor.getString(cursor.getColumnIndex("name")));
            Drawable drawable = parent.getResources().getDrawable(cursor.getInt(cursor.getColumnIndex("icon")));
            floating.setBackgroundTintList(ColorStateList.valueOf(cursor.getInt(cursor.getColumnIndex("color"))));
            floating.setImageDrawable(drawable);
            parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null) {
                        cursor.moveToPosition(position);
                        listener.editOperation(cursor.getInt(cursor.getColumnIndex("id")), (cursor.getDouble(cursor.getColumnIndex("value")) > 0));
                    }
                    return false;
                }
            });
        }
    }

}
