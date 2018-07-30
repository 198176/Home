package com.example.adrian.homecalc;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OperationActivity extends AppCompatActivity implements NumbersFragment.ValueListener,
        CategoryDialogFragment.CategoryListener {

    public static final String EDIT = "edit";
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
    private SQLiteDatabase db;
    private RadioGroup radioGroup;
    private int ids;
    private double value;
    private int idEdit = -1;
    private boolean plus;
    private Toast toast;
    private FragmentManager manager;

    public static String replaceDoubleToString(double value) {
        String text = String.format("%.2f", value);
        return (text.replace(".", ",") + " zł");
    }

    public static double replaceStringToDouble(String text) {
        return Double.parseDouble(text.replace(
                ",", ".").substring(0, text.length() - 3));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);
        Intent intent = getIntent();
        idEdit = intent.getIntExtra(EDIT, -1);
        toast = Toast.makeText(this, R.string.database_error, Toast.LENGTH_SHORT);
        manager = getSupportFragmentManager();
        SQLiteOpenHelper helper = new ApplicationDatabase(this);
        db = helper.getWritableDatabase();
        valueText = (EditText) findViewById(R.id.operation_value);
        categoryText = (EditText) findViewById(R.id.operation_category);
        titleText = (EditText) findViewById(R.id.operation_title);
        dateText = (EditText) findViewById(R.id.operation_date);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        Button button = (Button) findViewById(R.id.operation_button);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (idEdit != -1) {
                editFields();
            } else {
                updateDate();
                Cursor cursor = db.rawQuery("SELECT NAME, _id FROM CATEGORY WHERE DEFAULTS = 1", null);
                if (cursor.moveToFirst()) {
                    ids = cursor.getInt(1);
                    categoryText.setText(cursor.getString(0));
                    titleText.setHint(cursor.getString(0));
                }
                cursor.close();
            }
        } catch (SQLiteException w) {
            toast.show();
        }

        valueText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumbers();
            }
        });

        categoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategory();
            }
        });

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = dateTime.get(Calendar.DAY_OF_MONTH);
                int month = dateTime.get(Calendar.MONTH);
                int year = dateTime.get(Calendar.YEAR);
                new DatePickerDialog(OperationActivity.this, d, year, month, day).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioId = radioGroup.getCheckedRadioButtonId();
                plus = radioId == R.id.radio_profit;
                if (valueText.getText().length() != 0) {
                    value = replaceStringToDouble(valueText.getText().toString());
                    if (value != 0) {
                        createOperation();
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
        valueText.setText(text);
    }

    @Override
    public void setCategory(String text, int ids) {
        this.ids = ids;
        categoryText.setText(text);
        titleText.setHint(text);
    }

    private void showNumbers() {
        new NumbersFragment().show(manager, "Dialog");
    }

    private void showCategory() {
        new CategoryDialogFragment().show(manager, "Category");
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
            values.put("TITLE", title);
            values.put("VALUE", value);
            values.put("DATE", dateTime.getTimeInMillis());
            values.put("CATEGORY_ID", ids);
            if (idEdit == -1) {
                values.put("ID_PAY", getId() + 1);
                values.put("PAYING_ID", 1);
                values.put("PERSON_ID", 1);
                db.insert(ApplicationDatabase.PAYMENT, null, values);
            } else {
                db.update(ApplicationDatabase.PAYMENT, values, "ID_PAY = ?",
                        new String[]{Integer.toString(idEdit)});
            }
        } catch (SQLiteException w) {
            toast.show();
        }
        finish();
        startActivity(getSupportParentActivityIntent());
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

    private void editFields() {
        setTitle(R.string.editing_operations);
        Cursor cursor = db.rawQuery("SELECT P.TITLE, P.VALUE, strftime('%Y-%m-%d', datetime(DATE/1000, 'unixepoch', 'localtime')), " +
                "P.CATEGORY_ID, C.NAME FROM PAYMENT AS P, CATEGORY AS C WHERE P.CATEGORY_ID = C._id " +
                "AND P.ID_PAY = " + idEdit, null);
        cursor.moveToFirst();
        titleText.setText(cursor.getString(0));
        valueText.setText(replaceDoubleToString(cursor.getDouble(1)));
        dateText.setText(cursor.getString(2));
        ids = cursor.getInt(3);
        categoryText.setText(cursor.getString(4));
        cursor.close();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (idEdit != -1) {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OperationActivity.this);
                    builder.setTitle(R.string.confirmation)
                            .setMessage(R.string.ask_delete_operation)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteOperation();
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
        return true;
    }

    private void deleteOperation() {
        db.delete(ApplicationDatabase.PAYMENT, "ID_PAY = ?", new String[]{Integer.toString(idEdit)});
        finish();
        setResult(RESULT_OK, getSupportParentActivityIntent());
    }
}
