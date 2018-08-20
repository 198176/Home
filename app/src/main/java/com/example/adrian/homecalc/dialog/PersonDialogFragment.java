package com.example.adrian.homecalc.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.example.adrian.homecalc.R;
import com.example.adrian.homecalc.adapter.UserAdapter;
import com.example.adrian.homecalc.database.DBCallback;
import com.example.adrian.homecalc.database.UserDBUtils;
import com.example.adrian.homecalc.model.User;

import java.util.ArrayList;
import java.util.List;

public class PersonDialogFragment extends DialogFragment {

    public static final String ALL = "all";
    private PersonListener listener;
    private RecyclerView view;
    private boolean flag = false;
    private UserAdapter userAdapter;
    DBCallback<User> userDBCallback = new DBCallback<User>() {
        @Override
        public void onCallback(final List<User> array) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    userAdapter.setUsers((ArrayList<User>) array);
                    userAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = (RecyclerView) inflater.inflate(R.layout.dialog_category, null);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(ALL)) {
            flag = bundle.getBoolean(ALL, true);
        }
        userAdapter = new UserAdapter();
        userAdapter.setFlagAllUsers(flag);
        UserDBUtils.getAll(userDBCallback);
        view.setAdapter(userAdapter);
        view.setLayoutManager(new LinearLayoutManager(getActivity()));
        userAdapter.setPersonListener(new UserAdapter.PersonListener() {
            @Override
            public void setPerson(User user) {
                if (listener != null) {
                    listener.setPerson(user);
                    dismiss();
                }
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("Osoby");
        return builder.create();
    }

    public void setListener(PersonListener listener) {
        this.listener = listener;
    }

    public interface PersonListener {
        void setPerson(User user);
    }

}
