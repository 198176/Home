package com.example.adrian.homecalc;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OperationActivity extends AppCompatActivity implements NumbersFragment.ValueListener, CategoryFragment.CategoryListener {

    public static final String INT_EXTRA = "extra";
    private static final String DIALOG = "Dialog";

    private SimpleDateFormat dateFormat;
    private Calendar dateTime = Calendar.getInstance();
    private EditText dateText, valueText, categoryText, titleText;
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, month);
            dateTime.set(Calendar.DAY_OF_MONTH, day);
            updateDate();
        }
    };
    private int day, month, year;
    private SQLiteDatabase db;
    private RadioGroup radioGroup;
    private int ids, impact;
    private double value;
    private UserAdapter adapter;
    private int place = -1;
    private int idPlace;
    private TextView balance;
    private boolean plus;
    private SparseArray<String> sparseArray = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_operation);
        Intent intent = getIntent();
        impact = intent.getIntExtra(INT_EXTRA, 0);
        SQLiteOpenHelper helper = new ApplicationDatabase(this);
        db = helper.getWritableDatabase();

        if (impact == 0) {
            setContentView(R.layout.activity_expense);
            RecyclerView participant = (RecyclerView) findViewById(R.id.participant);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            participant.setLayoutManager(layoutManager);
            balance = (TextView) findViewById(R.id.balance);
            try {
                Cursor cur = db.rawQuery("SELECT NAME, COLOR, ICON_ID, _id FROM PERSON", null);
                adapter = new UserAdapter(this, cur, sparseArray);
                participant.setAdapter(adapter);
                adapter.setListener(new UserAdapter.UserListener() {
                    @Override
                    public void setValue(int position, int id) {
                        place = position;
                        idPlace = id;
                    }

                    @Override
                    public void setBalance(int position, int id) {
                        double value = 0.0;
                        if (sparseArray.get(id) != null) {
                            value = replaceStringToDouble(sparseArray.get(id));
                        }
                        double balanceValue = replaceStringToDouble(balance.getText().toString());
                        value += balanceValue;
                        balanceValue = 0.0;
                        sparseArray.put(id, replaceDoubleToString(value));
                        balance.setText(replaceDoubleToString(balanceValue));
                        adapter.notifyItemChanged(position);
                    }
                });
            } catch (SQLiteException w) {
                Toast toast = Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            setContentView(R.layout.activity_operation);
            radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        }

        valueText = (EditText) findViewById(R.id.value);
        valueText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumbers();
            }
        });

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        categoryText = (EditText) findViewById(R.id.category);
        categoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategory();
            }
        });

        titleText = (EditText) findViewById(R.id.title);

        try {
            Cursor cursor = db.query("CATEGORY", new String[]{"NAME", "_id"}, "DEFAULTS = 1", null, null, null, null);
            if (cursor.moveToFirst()) {
                ids = cursor.getInt(1);
                categoryText.setText(cursor.getString(0));
                titleText.setHint(cursor.getString(0));
            }
        } catch (SQLiteException w) {
            Toast toast = Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
        }

        dateText = (EditText) findViewById(R.id.date);
        updateDate();
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = dateTime.get(Calendar.DAY_OF_MONTH);
                month = dateTime.get(Calendar.MONTH);
                year = dateTime.get(Calendar.YEAR);
                new DatePickerDialog(OperationActivity.this, d, year, month, day).show();
            }
        });

        final Button button = (Button) findViewById(R.id.operation_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroup != null) {
                    int radioId = radioGroup.getCheckedRadioButtonId();
                    plus = radioId == R.id.radio_profit;
                }
                if (valueText.getText().length() != 0) {
                    value = replaceStringToDouble(valueText.getText().toString());
                    if (value != 0) {
                        if (impact == 1) {
                            createOperation();
                        } else {
                            if (replaceStringToDouble(balance.getText().toString()) == 0.0) {
                                boolean flag = false;
                                for (int i = 0; i < sparseArray.size(); i++) {
                                    int key = sparseArray.keyAt(i);
                                    if (replaceStringToDouble(sparseArray.get(key)) >= 0.0) {
                                        flag = true;
                                    } else {
                                        Toast.makeText(OperationActivity.this, "Wartości nie mogą być ujemne", Toast.LENGTH_SHORT).show();
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    createOperations();
                                }
                            } else {
                                Toast.makeText(OperationActivity.this, "Bilans musi być równy 0.0", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(OperationActivity.this, "Wartość musi być większa od zera", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OperationActivity.this, "Operacja musi zawierać wartość", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateDate() {
        dateText.setText(dateFormat.format(dateTime.getTime()));
    }

    @Override
    public void setValue(String text) {
        if (place == -1) {
            valueText.setText(text);
        } else {
            sparseArray.put(idPlace, text);
            adapter.notifyItemChanged(place);
            place = -1;
        }
        if (impact == 0) {
            setBalance();
        }
    }

    private void setBalance() {
        Double val = replaceStringToDouble(valueText.getText().toString());
        for (int i = 0; i < sparseArray.size(); i++) {
            int key = sparseArray.keyAt(i);
            val -= replaceStringToDouble(sparseArray.get(key));
        }
        balance.setText(replaceDoubleToString(val));
    }

    @Override
    public void setCategory(String text, int ids) {
        this.ids = ids;
        categoryText.setText(text);
        titleText.setHint(text);
    }

    private void showNumbers() {
        FragmentManager manager = getSupportFragmentManager();
        NumbersFragment fragment = new NumbersFragment();
        fragment.show(manager, DIALOG);
    }

    private void showCategory() {
        FragmentManager manager = getSupportFragmentManager();
        CategoryFragment fragment = new CategoryFragment();
        fragment.show(manager, "Category");
    }

    private void createOperation() {
        ContentValues values = new ContentValues();
        String title = titleText.getText().toString();
        if (!plus) {
            value *= -1;
        }
        if (title.length() == 0) {
            title = titleText.getHint().toString();
        }
        try {
            values.put("ID_PAY", getId() + 1);
            values.put("TITLE", title);
            values.put("VALUE", value);
            values.put("DATE", dateText.getText().toString());
            values.put("CATEGORY_ID", ids);
            values.put("PERSON_ID", 1);
            db.insert("PAYMENT", null, values);
        } catch (SQLiteException w) {
            Toast toast = Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
        }
        finish();
        startActivity(getSupportParentActivityIntent());
    }

    private void createOperations() {
        ContentValues values = new ContentValues();
        String title = titleText.getText().toString();
        int id = getId() + 1;
        if (title.length() == 0) {
            title = titleText.getHint().toString();
        }
        for (int i = 0; i < sparseArray.size(); i++) {
            int key = sparseArray.keyAt(i);
            double number = replaceStringToDouble(sparseArray.get(key));
            if (number == 0.0) {
                continue;
            }
            else {
                number *= -1;
            }
            try {
                values.put("ID_PAY", id);
                values.put("TITLE", title);
                values.put("VALUE", number);
                values.put("DATE", dateText.getText().toString());
                values.put("CATEGORY_ID", ids);
                values.put("PERSON_ID", key);
                db.insert("PAYMENT", null, values);
            } catch (SQLiteException w) {
                Toast toast = Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        finish();
        setResult(RESULT_OK, getSupportParentActivityIntent());
        //startActivity(getSupportParentActivityIntent());
    }

    public double replaceStringToDouble(String text) {
        return Double.parseDouble(text.replace(
                ",", ".").substring(0, text.length() - 3));
    }

    public static String replaceDoubleToString(double value) {
        String text = String.format("%.2f", value);
        return (text.replace(".", ",") + " zł");
    }

    private int getId() {
        Cursor cursor = db.rawQuery("SELECT _id FROM PAYMENT WHERE _id = ( SELECT MAX(_id) FROM PAYMENT)", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return cursor.getInt(0);
        } else {
            return 0;
        }
    }
}
