package com.example.adrian.homecash.fragment;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.adrian.homecash.MyApplication;
import com.example.adrian.homecash.R;
import com.example.adrian.homecash.activity.OperationActivity;
import com.example.adrian.homecash.database.UserDBUtils;
import com.example.adrian.homecash.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import yuku.ambilwarna.AmbilWarnaDialog;

public class NewUserFragment extends Fragment {

    @BindView(R.id.new_color)
    ImageView imageColor;
    @BindView(R.id.new_title)
    EditText title;
    @BindView(R.id.new_mail)
    EditText mail;
    @BindView(R.id.image_view)
    ImageView image;
    @BindView(R.id.button_user)
    Button button;
    @BindView(R.id.card_color)
    CardView cardColor;
    private Unbinder unbinder;
    private int colour;
    private String name;
    private String email;
    private User user;
    private Listener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_user, null);
        unbinder = ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(OperationActivity.EDIT)) {
            user = (User) bundle.getSerializable(OperationActivity.EDIT);
        }
        setHasOptionsMenu(true);
        FloatingActionButton fab = getActivity().findViewById(R.id.drawer_fab);
        fab.setVisibility(View.GONE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = title.getText().toString().trim();
                email = mail.getText().toString().trim();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final int count = MyApplication.getHomeRoomDatabase().userDao().getCountUserByEmail(email);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (count == 0) {
                                    if (name.length() != 0) {
                                        checkMail();
                                    } else {
                                        Toast.makeText(getActivity(), "Użytkownik musi mieć nazwę", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Posiadasz już użytkownika o podanym adresie email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).start();
            }
        });
        if (user != null) {
            getActivity().setTitle("Edycja użytkownika");
            title.setText(user.getName());
            mail.setText(user.getMail());
            imageColor.setBackgroundColor(user.getColor());
            colour = user.getColor();
            button.setText("Edytuj");
        } else {
            getActivity().setTitle(R.string.title_activity_new_user);
            colour = ((ColorDrawable) imageColor.getBackground()).getColor();
        }
        setImageColor();
        cardColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorpicker();
            }
        });
        return view;
    }

    public void colorpicker() {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(getActivity(), colour, new AmbilWarnaDialog.OnAmbilWarnaListener() {

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                colour = color;
                imageColor.setBackgroundColor(color);
                setImageColor();
            }
        });
        dialog.show();
    }

    public void setImageColor() {
        TextDrawable textdrawable = TextDrawable.builder().buildRound("A", colour);
        image.setImageDrawable(textdrawable);
    }

    public void createUser() {
        if (user == null) {
            user = new User(name, email, colour);
            UserDBUtils.insert(user);
            if (listener != null) {
                listener.onClick(user, false);
            }
        } else {
            user.setName(name);
            user.setColor(colour);
            UserDBUtils.update(user);
        }
        getFragmentManager().popBackStack();
    }

    private void checkMail() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("users").orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    createUser();
                } else {
                    Toast.makeText(getActivity(), "Użytkownik nie posiada konta lub email jest niepoprawny", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (user != null) {
            inflater.inflate(R.menu.menu_delete, menu);
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.confirmation)
                            .setMessage(R.string.ask_delete_user)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteUser();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    return true;
                }
            });
        }
    }

    public void deleteUser() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final int count = MyApplication.getHomeRoomDatabase().userDao().getCountOperationsUser(user.getId());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (count == 0 && user.getId() != 1) {
                                UserDBUtils.delete(user);
                                if (listener != null) {
                                    listener.onClick(user, true);
                                }
                                getFragmentManager().popBackStack();
                            } else {
                                Toast.makeText(getActivity(), "Nie można usunąć użytkownika posiadającego transakcje", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }).start();
        } catch (SQLiteException w) {
            Toast.makeText(getActivity(), R.string.database_error, Toast.LENGTH_SHORT).show();
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface Listener {
        void onClick(User user, boolean isDelete);
    }

}
