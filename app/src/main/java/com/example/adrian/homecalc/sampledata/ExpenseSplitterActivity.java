package com.example.adrian.homecalc.sampledata;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
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
import com.example.adrian.homecalc.CategoryDialogFragment;
import com.example.adrian.homecalc.MyApplication;
import com.example.adrian.homecalc.NumbersFragment;
import com.example.adrian.homecalc.OperationActivity;
import com.example.adrian.homecalc.PersonDialogFragment;
import com.example.adrian.homecalc.R;
import com.example.adrian.homecalc.adapter.UserAdapterR;
import com.example.adrian.homecalc.database.DBCallback;
import com.example.adrian.homecalc.database.ParticipantDBUtils;
import com.example.adrian.homecalc.database.PaymentDBUtils;
import com.example.adrian.homecalc.database.UserDBUtils;
import com.example.adrian.homecalc.model.Category;
import com.example.adrian.homecalc.model.Participant;
import com.example.adrian.homecalc.model.Payment;
import com.example.adrian.homecalc.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.adrian.homecalc.OperationActivity.replaceDoubleToString;
import static com.example.adrian.homecalc.OperationActivity.replaceStringToDouble;

public class ExpenseSplitterActivity extends AppCompatActivity implements NumbersFragment.ValueListener,
        CategoryDialogFragment.CategoryListener, PersonDialogFragment.PersonListener {

    @BindView(R.id.expense_value)
    EditText valueText;
    @BindView(R.id.expense_category)
    EditText categoryText;
    @BindView(R.id.expense_title)
    EditText titleText;
    @BindView(R.id.expense_date)
    EditText dateText;
    @BindView(R.id.expense_balance)
    TextView balance;
    @BindView(R.id.expense_value_user)
    ImageView imageUser;
    @BindView(R.id.expense_operation_button)
    Button button;
    @BindView(R.id.expense_participant)
    RecyclerView participant;
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
    private Unbinder unbinder;
    private UserAdapterR userAdapter;
    private FragmentManager manager;
    private double value;
    private SparseArray<String> sparseArray = new SparseArray<>();
    DBCallback<User> userDBCallback = new DBCallback<User>() {
        @Override
        public void onCallback(List<User> array) {
            userAdapter.setUsers((ArrayList<User>) array, sparseArray);
        }
    };
    private int idCategory, idPlace, idPaying;
    private int idEdit = -1;
    private int place = -1;
    private Toast toast;
    private Cursor cursorParticipant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        unbinder = ButterKnife.bind(this);
        idEdit = getIntent().getIntExtra(OperationActivity.EDIT, -1);
        toast = Toast.makeText(this, R.string.database_error, Toast.LENGTH_SHORT);
        manager = getSupportFragmentManager();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        participant.setLayoutManager(new LinearLayoutManager(this));

        if (idEdit != -1) {
            //editFields();
        } else {
            setPaying(1);
            updateDate();
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Category category = MyApplication.getHomeRoomDatabase().categoryDao().getDefaultCategory();
                        ExpenseSplitterActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                idCategory = category.getId();
                                categoryText.setText(category.getName());
                                titleText.setHint(category.getName());
                            }
                        });
                    }
                }).start();
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
                new DatePickerDialog(ExpenseSplitterActivity.this, d, year, month, day).show();
            }
        });

        try {
            userAdapter = new UserAdapterR();
            UserDBUtils.getAll(userDBCallback);
            participant.setAdapter(userAdapter);
            userAdapter.setUserListener(new UserAdapterR.UserListener() {
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
                    userAdapter.notifyItemChanged(position);
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
                                    Toast.makeText(ExpenseSplitterActivity.this, "Wartości nie mogą być ujemne", Toast.LENGTH_SHORT).show();
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                createOperations();
                            }
                        } else {
                            Toast.makeText(ExpenseSplitterActivity.this, "Bilans musi być równy 0.0", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ExpenseSplitterActivity.this, "Wartość musi być większa od zera", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ExpenseSplitterActivity.this, "Operacja musi zawierać wartość", Toast.LENGTH_SHORT).show();
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
            userAdapter.notifyItemChanged(place);
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

    private void setPaying(final int id) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    User user = MyApplication.getHomeRoomDatabase().userDao().getUser(id);
                    setPerson(user.getName(), user.getColor(), user.getId());
                }
            }).start();
        } catch (SQLiteException w) {
            toast.show();
        }
    }

    private void createOperations() {
        String title = titleText.getText().toString();
        int id = getId() + 1;
        if (title.length() == 0) {
            title = titleText.getHint().toString();
        }
        if (idEdit != -1) {
            //db.delete(ApplicationDatabase.PAYMENT, "ID_PAY = ?", new String[]{Integer.toString(idEdit)});
            id = idEdit;
        }
        try {
            PaymentDBUtils.insert(new Payment(title, value, dateTime.getTimeInMillis(), idCategory, idPaying));
            for (int i = 0; i < sparseArray.size(); i++) {
                int key = sparseArray.keyAt(i);
                double number = replaceStringToDouble(sparseArray.get(key));
                if (number == 0.0) {
                    continue;
                } else {
                    number *= -1;
                }
                ParticipantDBUtils.insert(new Participant(id, key, number));
            }
        } catch (SQLiteException w) {
            toast.show();
        }
        finish();
        setResult(RESULT_OK, getSupportParentActivityIntent());
    }

    private int getId() {
        return MyApplication.getHomeRoomDatabase().paymentDao().getLastId();
    }

//    private void editFields() {
//        setTitle(R.string.editing_operations);
//        try {
//            Cursor cursor = db.rawQuery("SELECT P.TITLE, P.VALUE, strftime('%Y-%m-%d', datetime(DATE/1000, 'unixepoch', 'localtime')), " +
//                    "P.CATEGORY_ID, P.PERSON_ID, C.NAME, P.PAYING_ID FROM PAYMENT AS P, CATEGORY AS C " +
//                    "WHERE P.CATEGORY_ID = C._id AND P.ID_PAY = " + idEdit, null);
//            cursor.moveToFirst();
//            titleText.setText(cursor.getString(0));
//            categoryText.setText(cursor.getString(5));
//            idCategory = cursor.getInt(3);
//            dateText.setText(cursor.getString(2));
//            setPaying(cursor.getInt(6));
//            double cursorValue = 0;
//            do {
//                cursorValue += Math.abs(cursor.getDouble(1));
//                sparseArray.put(cursor.getInt(4), replaceDoubleToString(Math.abs(cursor.getDouble(1))));
//            } while (cursor.moveToNext());
//            valueText.setText(replaceDoubleToString(cursorValue));
//            cursor.close();
//        } catch (SQLiteException w) {
//            toast.show();
//        }
//    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (idEdit != -1) {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseSplitterActivity.this);
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
        //db.delete(ApplicationDatabase.PAYMENT, "ID_PAY = ?", new String[]{Integer.toString(idEdit)});
        finish();
        setResult(RESULT_OK, getSupportParentActivityIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursorParticipant.close();
        unbinder.unbind();
    }
}
