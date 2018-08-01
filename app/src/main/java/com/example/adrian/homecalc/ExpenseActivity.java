package com.example.adrian.homecalc;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.adrian.homecalc.OperationActivity.replaceDoubleToString;
import static com.example.adrian.homecalc.OperationActivity.replaceStringToDouble;

public class ExpenseActivity extends AppCompatActivity implements NumbersFragment.ValueListener,
        CategoryDialogFragment.CategoryListener, PersonDialogFragment.PersonListener {

    private SQLiteDatabase db;
    private SimpleDateFormat dateFormat;
    private Calendar dateTime = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, month);
            dateTime.set(Calendar.DAY_OF_MONTH, day);
            updateDate();
        }
    };
    private EditText valueText, categoryText, titleText, dateText;
    private TextView balance;
    private UserAdapter adapter;
    private FragmentManager manager;
    private double value;
    private SparseArray<String> sparseArray = new SparseArray<>();
    private int idCategory, idPlace, idPaying;
    private int idEdit = -1;
    private int place = -1;
    private ImageView imageUser;
    private Toast toast;
    private Cursor cursorParticipant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        idEdit = getIntent().getIntExtra(OperationActivity.EDIT, -1);
        toast = Toast.makeText(this, R.string.database_error, Toast.LENGTH_SHORT);
        manager = getSupportFragmentManager();
        SQLiteOpenHelper helper = new ApplicationDatabase(this);
        db = helper.getWritableDatabase();
        valueText = (EditText) findViewById(R.id.expense_value);
        titleText = (EditText) findViewById(R.id.expense_title);
        categoryText = (EditText) findViewById(R.id.expense_category);
        dateText = (EditText) findViewById(R.id.expense_date);
        imageUser = (ImageView) findViewById(R.id.expense_value_user);
        balance = (TextView) findViewById(R.id.expense_balance);
        Button button = (Button) findViewById(R.id.expense_operation_button);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        RecyclerView participant = (RecyclerView) findViewById(R.id.expense_participant);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        participant.setLayoutManager(layoutManager);

        if (idEdit != -1) {
            editFields();
        } else {
            setPaying(1);
            updateDate();
            try {
                Cursor cursor = db.rawQuery("SELECT NAME, _id FROM CATEGORY WHERE DEFAULTS = 1", null);
                if (cursor.moveToFirst()) {
                    idCategory = cursor.getInt(1);
                    categoryText.setText(cursor.getString(0));
                    titleText.setHint(cursor.getString(0));
                }
                cursor.close();
            } catch (SQLiteException w) {
                toast.show();
            }
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
        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPerson();
            }
        });
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = dateTime.get(Calendar.DAY_OF_MONTH);
                int month = dateTime.get(Calendar.MONTH);
                int year = dateTime.get(Calendar.YEAR);
                new DatePickerDialog(ExpenseActivity.this, d, year, month, day).show();
            }
        });

        try {
            cursorParticipant = db.rawQuery("SELECT NAME, COLOR, _id FROM PERSON", null);
            adapter = new UserAdapter(this, cursorParticipant, sparseArray);
            participant.setAdapter(adapter);
            adapter.setUserListener(new UserAdapter.UserListener() {
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
            toast.show();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valueText.getText().length() != 0) {
                    value = replaceStringToDouble(valueText.getText().toString());
                    if (value != 0) {
                        if (replaceStringToDouble(balance.getText().toString()) == 0.0) {
                            boolean flag = false;
                            for (int i = 0; i < sparseArray.size(); i++) {
                                int key = sparseArray.keyAt(i);
                                if (replaceStringToDouble(sparseArray.get(key)) >= 0.0) {
                                    flag = true;
                                } else {
                                    Toast.makeText(ExpenseActivity.this, "Wartości nie mogą być ujemne", Toast.LENGTH_SHORT).show();
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                createOperations();
                            }
                        } else {
                            Toast.makeText(ExpenseActivity.this, "Bilans musi być równy 0.0", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ExpenseActivity.this, "Wartość musi być większa od zera", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ExpenseActivity.this, "Operacja musi zawierać wartość", Toast.LENGTH_SHORT).show();
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
        setBalance();

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
    public void setPerson(String text, int color, int ids) {
        TextDrawable textDrawable = TextDrawable.builder()
                .buildRound(Character.toString(text.charAt(0)), color);
        imageUser.setImageDrawable(textDrawable);
        idPaying = ids;
    }

    @Override
    public void setCategory(String text, int ids) {
        this.idCategory = ids;
        categoryText.setText(text);
        titleText.setHint(text);
    }

    private void showNumbers() {
        new NumbersFragment().show(manager, "Dialog");
    }

    private void showCategory() {
        new CategoryDialogFragment().show(manager, "Category");
    }

    private void showPerson() {
        new PersonDialogFragment().show(manager, "Person");
    }

    private void setPaying(int id) {
        try {
            Cursor cursor = db.rawQuery("SELECT NAME, COLOR, _id FROM PERSON WHERE _id=" + id, null);
            if (cursor.moveToFirst()) {
                setPerson(cursor.getString(0), cursor.getInt(1), cursor.getInt(2));
            }
            cursor.close();
        } catch (SQLiteException w) {
            toast.show();
        }
    }

    private void createOperations() {
        ContentValues values = new ContentValues();
        String title = titleText.getText().toString();
        int id = getId() + 1;
        if (title.length() == 0) {
            title = titleText.getHint().toString();
        }
        if (idEdit != -1) {
            db.delete(ApplicationDatabase.PAYMENT, "ID_PAY = ?", new String[]{Integer.toString(idEdit)});
            id = idEdit;
        }
        for (int i = 0; i < sparseArray.size(); i++) {
            int key = sparseArray.keyAt(i);
            double number = replaceStringToDouble(sparseArray.get(key));
            if (number == 0.0) {
                continue;
            } else {
                number *= -1;
            }
            try {
                values.put("ID_PAY", id);
                values.put("TITLE", title);
                values.put("VALUE", number);
                values.put("DATE", dateTime.getTimeInMillis());
                values.put("CATEGORY_ID", idCategory);
                values.put("PERSON_ID", key);
                values.put("PAYING_ID", idPaying);
                db.insert(ApplicationDatabase.PAYMENT, null, values);

            } catch (SQLiteException w) {
                toast.show();
            }
        }
        finish();
        setResult(RESULT_OK, getSupportParentActivityIntent());
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
        try {
            Cursor cursor = db.rawQuery("SELECT P.TITLE, P.VALUE, strftime('%Y-%m-%d', datetime(DATE/1000, 'unixepoch', 'localtime')), " +
                    "P.CATEGORY_ID, P.PERSON_ID, C.NAME, P.PAYING_ID FROM PAYMENT AS P, CATEGORY AS C " +
                    "WHERE P.CATEGORY_ID = C._id AND P.ID_PAY = " + idEdit, null);
            cursor.moveToFirst();
            titleText.setText(cursor.getString(0));
            categoryText.setText(cursor.getString(5));
            idCategory = cursor.getInt(3);
            dateText.setText(cursor.getString(2));
            setPaying(cursor.getInt(6));
            double cursorValue = 0;
            do {
                cursorValue += Math.abs(cursor.getDouble(1));
                sparseArray.put(cursor.getInt(4), replaceDoubleToString(Math.abs(cursor.getDouble(1))));
            } while (cursor.moveToNext());
            valueText.setText(replaceDoubleToString(cursorValue));
            cursor.close();
        } catch (SQLiteException w) {
            toast.show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (idEdit != -1) {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseActivity.this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursorParticipant.close();
        db.close();
    }
}
