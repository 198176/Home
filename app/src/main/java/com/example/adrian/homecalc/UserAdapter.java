package com.example.adrian.homecalc;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Adrian on 2018-03-14.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private UserListener listener;
    private Context context;
    private SparseArray<String> sparseArray;
    private Cursor cursor;

    public UserAdapter(Context context, Cursor cursor, SparseArray<String> sparseArray) {
        this.context = context;
        this.cursor = cursor;
        this.sparseArray = sparseArray;
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_user, parent, false);
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
        TextView categoryTitle = (TextView) cardView.findViewById(R.id.category_title);
        categoryTitle.setText(cursor.getString(0));
        final TextView value = (TextView) cardView.findViewById(R.id.category_value);
        if (sparseArray.size() != 0) {
            value.setText(sparseArray.get(cursor.getInt(3)));
        }
        ImageButton imageButton = (ImageButton) cardView.findViewById(R.id.rest_cost);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    cursor.moveToPosition(position);
                    listener.setBalance(position, cursor.getInt(3));
                }
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumbers();
                if (listener != null) {
                    cursor.moveToPosition(position);
                    listener.setValue(position, cursor.getInt(3));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void setListener(UserListener listener) {
        this.listener = listener;
    }

    public void showNumbers() {
        FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
        NumbersFragment fragment = new NumbersFragment();
        fragment.show(manager, "Dialog");
    }

    interface UserListener {
        void setValue(int position, int id);

        void setBalance(int position, int id);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }
}
