package com.example.adrian.homecash.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adrian.homecash.activity.OperationActivity;
import com.example.adrian.homecash.R;
import com.example.adrian.homecash.adapter.UserAdapter;
import com.example.adrian.homecash.database.DBCallback;
import com.example.adrian.homecash.database.UserDBUtils;
import com.example.adrian.homecash.model.User;

import java.util.ArrayList;
import java.util.List;

public class PersonFragment extends Fragment implements NewUserFragment.Listener {

    private RecyclerView view;
    private UserAdapter userAdapter;
    private ArrayList<User> users;

    DBCallback<User> userDBCallback = new DBCallback<User>() {
        @Override
        public void onCallback(List<User> array) {
            users = (ArrayList<User>) array;
            view.post(new Runnable() {
                @Override
                public void run() {
                    userAdapter.setUsers(users);
                    userAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAdapter = new UserAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (RecyclerView) inflater.inflate(R.layout.dialog_category, null);
        view.setLayoutManager(new LinearLayoutManager(getActivity()));
        view.setAdapter(userAdapter);
        UserDBUtils.getAll(userDBCallback);
        getActivity().setTitle("Użytkownicy");
        FloatingActionButton fab = getActivity().findViewById(R.id.drawer_fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewUserFragment userFragment = new NewUserFragment();
                userFragment.setListener(PersonFragment.this);
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.drawer_fragment, userFragment).commit();
            }
        });
        userAdapter.setPersonListener(new UserAdapter.PersonListener() {
            @Override
            public void setPerson(User user) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(OperationActivity.EDIT, user);
                NewUserFragment userFragment = new NewUserFragment();
                userFragment.setArguments(bundle);
                userFragment.setListener(PersonFragment.this);
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.drawer_fragment, userFragment).commit();
            }
        });
        return view;
    }

    @Override
    public void onClick(User user, boolean isDelete) {
        if (isDelete) {
            int position = users.indexOf(user);
            users.remove(position);
            userAdapter.notifyItemRemoved(position);
        } else {
            users.add(user);
            userAdapter.notifyItemInserted(users.size());
            view.scrollToPosition(users.size());
        }
    }
}
