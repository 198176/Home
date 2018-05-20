package com.example.adrian.homecalc;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

/**
 * Created by Adrian on 2018-03-14.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private UserListener userListener;
    private PersonListener personListener;
    private Context context;
    private SparseArray<String> sparseArray;
    private Cursor cursor;

    public UserAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

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
        TextView categoryTitle = (TextView) cardView.findViewById(R.id.category_title);
        categoryTitle.setText(cursor.getString(0));
        TextDrawable textDrawable = TextDrawable.builder()
                .buildRound(Character.toString(cursor.getString(0).charAt(0)), cursor.getInt(1));
        ImageView image = (ImageView) cardView.findViewById(R.id.image_user);
        image.setImageDrawable(textDrawable);
        ImageButton imageButton = (ImageButton) cardView.findViewById(R.id.rest_cost);
        if(sparseArray != null) {
            final TextView value = (TextView) cardView.findViewById(R.id.category_value);
            if (sparseArray.size() != 0) {
                value.setText(sparseArray.get(cursor.getInt(2)));
            }
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userListener != null) {
                        cursor.moveToPosition(position);
                        userListener.setBalance(position, cursor.getInt(2));
                    }
                }
            });
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNumbers();
                    if (userListener != null) {
                        cursor.moveToPosition(position);
                        userListener.setValue(position, cursor.getInt(2));
                    }
                }
            });
        }
        else {
            imageButton.setVisibility(View.INVISIBLE);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (personListener != null) {
                        cursor.moveToPosition(position);
                        personListener.setPerson(cursor.getString(0), cursor.getInt(1), cursor.getInt(2));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void setUserListener(UserListener userListener) {
        this.userListener = userListener;
    }

    public void setPersonListener(PersonListener personListener) {
        this.personListener = personListener;
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

    interface PersonListener {
        void setPerson(String text, int color, int id);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }
}
