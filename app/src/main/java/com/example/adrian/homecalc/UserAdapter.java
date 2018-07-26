package com.example.adrian.homecalc;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
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

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private UserListener userListener;
    private PersonListener personListener;
    private Context context;
    private SparseArray<String> sparseArray;
    private Cursor cursor;
    private int flag = 0;

    public UserAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    public UserAdapter(Cursor cursor, boolean flag) {
        this.cursor = cursor;
        if(flag) this.flag = 1;
    }

    public UserAdapter(Context context, Cursor cursor, SparseArray<String> sparseArray) {
        this.context = context;
        this.cursor = cursor;
        this.sparseArray = sparseArray;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < cursor.getCount()) {
            return 0;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user, parent, false));
            case 1:
                return new AllUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ((UserViewHolder) holder).bind(position);
                break;
            case 1:
                ((AllUserViewHolder) holder).bind();
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(cursor != null) return cursor.getCount() + flag;
        return 0;
    }

    public void setUserListener(UserListener userListener) {
        this.userListener = userListener;
    }

    public void setPersonListener(PersonListener personListener) {
        this.personListener = personListener;
    }

    void showNumbers() {
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

    class UserViewHolder extends RecyclerView.ViewHolder {
        View parent;
        TextView categoryTitle;
        ImageView image;
        ImageButton imageButton;
        TextView value;

        UserViewHolder(View itemView) {
            super(itemView);
            parent = itemView;
            categoryTitle = (TextView) parent.findViewById(R.id.category_title);
            image = (ImageView) parent.findViewById(R.id.image_user);
            imageButton = (ImageButton) parent.findViewById(R.id.rest_cost);
            value = (TextView) parent.findViewById(R.id.category_value);
        }

        void bind(final int position) {
            cursor.moveToPosition(position);
            categoryTitle.setText(cursor.getString(0));
            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRound(Character.toString(cursor.getString(0).charAt(0)), cursor.getInt(1));
            image.setImageDrawable(textDrawable);
            if (sparseArray != null) {
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
                parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showNumbers();
                        if (userListener != null) {
                            cursor.moveToPosition(position);
                            userListener.setValue(position, cursor.getInt(2));
                        }
                    }
                });
            } else {
                imageButton.setVisibility(View.GONE);
                parent.setOnClickListener(new View.OnClickListener() {
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
    }

    class AllUserViewHolder extends RecyclerView.ViewHolder {
        View parent;
        TextView categoryTitle;
        ImageView image;
        ImageView imageButton;

        AllUserViewHolder(View itemView) {
            super(itemView);
            parent = itemView;
            categoryTitle = (TextView) parent.findViewById(R.id.category_title);
            image = (ImageView) parent.findViewById(R.id.image_user);
            imageButton = (ImageButton) parent.findViewById(R.id.rest_cost);
        }

        void bind() {
            categoryTitle.setText("Wszyscy");
            image.setImageResource(R.drawable.ic_user);
            imageButton.setVisibility(View.GONE);
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (personListener != null) {
                        personListener.setPerson("", -1, R.drawable.ic_user);
                    }
                }
            });
        }
    }
}
