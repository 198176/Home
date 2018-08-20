package com.example.adrian.homecash.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.adrian.homecash.dialog.NumbersFragment;
import com.example.adrian.homecash.dialog.PersonDialogFragment;
import com.example.adrian.homecash.R;
import com.example.adrian.homecash.database.PaymentDBUtils;
import com.example.adrian.homecash.model.Participant;
import com.example.adrian.homecash.model.Payment;
import com.example.adrian.homecash.model.User;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.adrian.homecash.activity.OperationActivity.replaceStringToDouble;

public class NewRepaymentFragment extends Fragment implements PersonDialogFragment.PersonListener,
        NumbersFragment.ValueListener {

    @BindView(R.id.image_user_from)
    ImageView userFrom;
    @BindView(R.id.image_user_to)
    ImageView userTo;
    @BindView(R.id.expense_value_text)
    EditText value;
    @BindView(R.id.text_user_from)
    TextView nameFrom;
    @BindView(R.id.text_user_to)
    TextView nameTo;
    @BindView(R.id.expense_operation_button)
    Button button;
    private Unbinder unbinder;
    private FragmentManager manager;
    private int idPaying = 0;
    private int idParticipant = 0;
    private int imageId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_repayment, null);
        manager = getFragmentManager();
        unbinder = ButterKnife.bind(this, view);
        FloatingActionButton fab = getActivity().findViewById(R.id.drawer_fab);
        fab.setVisibility(View.GONE);
        getActivity().setTitle(R.string.title_new_repayment);
        TextDrawable drawable = TextDrawable.builder()
                .buildRound("+", R.color.cardview_dark_background);
        userFrom.setImageDrawable(drawable);
        userTo.setImageDrawable(drawable);
        userFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPerson(userFrom);
            }
        });
        userTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPerson(userTo);
            }
        });
        value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNumbers();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idPaying != 0 && idParticipant != 0) {
                    if (idPaying != idParticipant) {
                        if (value.getText().length() != 0) {
                            if (replaceStringToDouble(value.getText().toString()) != 0) {
                                createRepayment();
                            } else {
                                Toast.makeText(getActivity(), "Wartość musi być większa od zera", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Operacja musi zawierać wartość", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Musisz wybrać różnych użytkowników", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Musisz wybrać użytkowników", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    public void showPerson(ImageView image) {
        imageId = image.getId();
        PersonDialogFragment personDialogFragment = new PersonDialogFragment();
        personDialogFragment.setListener(this);
        personDialogFragment.show(manager, "Person");
    }

    private void showNumbers() {
        NumbersFragment numbersFragment = new NumbersFragment();
        numbersFragment.setListener(this);
        numbersFragment.show(manager, "Dialog");
    }

    private void createRepayment() {
        PaymentDBUtils.insert(new Payment(replaceStringToDouble(value.getText().toString()) * (-1), idPaying,
                new ArrayList<>(Collections.singletonList(new Participant(idParticipant, replaceStringToDouble(value.getText().toString()))))));
        manager.popBackStack();
    }

    @Override
    public void setPerson(User user) {
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(Character.toString(user.getName().charAt(0)), user.getColor());
        switch (imageId) {
            case R.id.image_user_from:
                userFrom.setImageDrawable(drawable);
                nameFrom.setText(user.getName());
                idPaying = user.getId();
                break;
            case R.id.image_user_to:
                userTo.setImageDrawable(drawable);
                nameTo.setText(user.getName());
                idParticipant = user.getId();
                break;
        }
    }

    @Override
    public void setValue(String text) {
        value.setText(text);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
