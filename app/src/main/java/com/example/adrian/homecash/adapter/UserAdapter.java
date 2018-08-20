package com.example.adrian.homecash.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.adrian.homecash.R;
import com.example.adrian.homecash.model.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private UserListener userListener;
    private PersonListener personListener;
    private SparseArray<String> sparseArray;
    private int flag = 0;
    private ArrayList<User> users;

    public void setFlagAllUsers(boolean flag) {
        this.flag = (flag) ? 1 : 0;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public void setUsers(ArrayList<User> users, SparseArray<String> sparseArray) {
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
        if (users != null) return users.size() + flag;
        return 0;
    }

    public void setUserListener(UserListener userListener) {
        this.userListener = userListener;
    }

    public void setPersonListener(PersonListener personListener) {
        this.personListener = personListener;
    }

    public interface UserListener {
        void setValue(int position, int id);

        void setBalance(int position, int id);
    }

    public interface PersonListener {
        void setPerson(User user);
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
                    value.setText(sparseArray.get(user.getId()));
                }
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userListener != null) {
                            userListener.setBalance(position, user.getId());
                        }
                    }
                });
                parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userListener != null) {
                            userListener.setValue(position, user.getId());
                        }
                    }
                });
            } else {
                imageButton.setVisibility(View.GONE);
                parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (personListener != null) {
                            personListener.setPerson(user);
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
                        personListener.setPerson(null);
                    }
                }
            });
        }
    }
}

