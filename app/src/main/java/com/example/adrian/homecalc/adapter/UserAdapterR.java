package com.example.adrian.homecalc.adapter;

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
import com.example.adrian.homecalc.NumbersFragment;
import com.example.adrian.homecalc.R;
import com.example.adrian.homecalc.model.User;

import java.util.ArrayList;

public class UserAdapterR extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private UserListener userListener;
    private PersonListener personListener;
    private Context context;
    private SparseArray<String> sparseArray;
    private Cursor cursor;
    private int flag = 0;
    private ArrayList<User> users;

    public UserAdapterR() {}

    public UserAdapterR(Cursor cursor, boolean flag) {
        this.cursor = cursor;
        if(flag) this.flag = 1;
    }

    public UserAdapterR(Context context, Cursor cursor, SparseArray<String> sparseArray) {
        this.context = context;
        this.cursor = cursor;
        this.sparseArray = sparseArray;
    }

    public void setUsers(ArrayList<User> users, SparseArray<String> sparseArray){
        this.users = users;
        this.sparseArray = sparseArray;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < users.size()) {
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
                ((UserViewHolder) holder).bind(users.get(position), position);
                break;
            case 1:
                ((AllUserViewHolder) holder).bind();
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(users != null) return users.size() + flag;
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

    public interface UserListener {
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
            categoryTitle = parent.findViewById(R.id.category_title);
            image = parent.findViewById(R.id.image_user);
            imageButton = parent.findViewById(R.id.rest_cost);
            value = parent.findViewById(R.id.category_value);
        }

        void bind(final User user, final int position) {
            categoryTitle.setText(user.getName());
            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRound(Character.toString(user.getName().charAt(0)), user.getColor());
            image.setImageDrawable(textDrawable);
            if (sparseArray != null) {
                if (sparseArray.size() != 0) {
                    value.setText(sparseArray.get(cursor.getInt(2)));
                }
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userListener != null) {
                            userListener.setBalance(position, cursor.getInt(2));
                        }
                    }
                });
                parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showNumbers();
                        if (userListener != null) {
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
            categoryTitle = parent.findViewById(R.id.category_title);
            image = parent.findViewById(R.id.image_user);
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

