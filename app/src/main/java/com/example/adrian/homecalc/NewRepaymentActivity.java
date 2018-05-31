package com.example.adrian.homecalc;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

public class NewRepaymentActivity extends AppCompatActivity implements PersonDialogFragment.PersonListener,
        NumbersFragment.ValueListener{

    private View view;
    private ImageView userFrom;
    private ImageView userTo;
    private FragmentManager manager;
    private EditText value;
    private TextView nameFrom;
    private TextView nameTo;
    private int idPaying = 0;
    private int idPerson = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_repayment);
        manager = getSupportFragmentManager();
        userFrom = (ImageView) findViewById(R.id.image_user_from);
        userTo = (ImageView) findViewById(R.id.image_user_to);
        nameFrom = (TextView) findViewById(R.id.text_user_from);
        nameTo = (TextView) findViewById(R.id.text_user_to);
        value = (EditText) findViewById(R.id.value_text);
        TextDrawable drawable = TextDrawable.builder()
                .buildRound("+", R.color.cardview_dark_background);
        userFrom.setImageDrawable(drawable);
        userTo.setImageDrawable(drawable);
        Button button = (Button) findViewById(R.id.operation_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idPaying != 0 && idPerson != 0) {
                    if (idPaying != idPerson) {
                        if (value.getText().length() != 0) {
                            if (OperationActivity.replaceStringToDouble(value.getText().toString()) != 0) {
                                createRepayment();
                            } else {
                                Toast.makeText(NewRepaymentActivity.this, "Wartość musi być większa od zera", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(NewRepaymentActivity.this, "Operacja musi zawierać wartość", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(NewRepaymentActivity.this, "Musisz wybrać różnych użytkowników", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NewRepaymentActivity.this, "Musisz wybrać użytkowników", Toast.LENGTH_SHORT).show();
                }
            }
        });
        value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNumbers();
            }
        });
    }

    public void showPerson(View view) {
        this.view = view;
        new PersonDialogFragment().show(manager, "Person");
    }

    private void showNumbers() {
        new NumbersFragment().show(manager, "Dialog");
    }

    private void createRepayment() {
        ContentValues values = new ContentValues();
        try {
            SQLiteOpenHelper helper = new ApplicationDatabase(this);
            SQLiteDatabase db = helper.getWritableDatabase();
            values.put("ID_PAY", 0);
            values.put("VALUE", OperationActivity.replaceStringToDouble(value.getText().toString())*(-1));
            //values.put("DATE", dateText.getText().toString());
            values.put("PERSON_ID", idPerson);
            values.put("PAYING_ID", idPaying);
            db.insert("PAYMENT", null, values);

        } catch (SQLiteException w) {
            Toast.makeText(this, R.string.database_error, Toast.LENGTH_SHORT).show();
        }
        finish();
        setResult(RESULT_OK, getSupportParentActivityIntent());
    }

    @Override
    public void setPerson(String text, int color, int ids) {
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(Character.toString(text.charAt(0)), color);
        switch (view.getId()){
            case R.id.image_user_from:
                userFrom.setImageDrawable(drawable);
                nameFrom.setText(text);
                idPaying = ids;
                break;
            case R.id.image_user_to:
                userTo.setImageDrawable(drawable);
                nameTo.setText(text);
                idPerson = ids;
                break;
        }
    }

    @Override
    public void setValue(String text) {
        value.setText(text);
    }
}
