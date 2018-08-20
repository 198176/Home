package com.example.adrian.homecash.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
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

import com.example.adrian.homecash.MyApplication;
import com.example.adrian.homecash.dialog.NumbersFragment;
import com.example.adrian.homecash.R;
import com.example.adrian.homecash.database.ParticipantDBUtils;
import com.example.adrian.homecash.database.PaymentDBUtils;
import com.example.adrian.homecash.dialog.CategoryDialogFragment;
import com.example.adrian.homecash.model.Category;
import com.example.adrian.homecash.model.Participant;
import com.example.adrian.homecash.model.Payment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OperationActivity extends AppCompatActivity implements NumbersFragment.ValueListener,
        CategoryDialogFragment.CategoryListener {

    public static final String EDIT = "edit";
    @BindView(R.id.operation_date)
    EditText dateText;
    @BindView(R.id.operation_value)
    EditText valueText;
    @BindView(R.id.operation_category)
    EditText categoryText;
    @BindView(R.id.operation_title)
    EditText titleText;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.expense_operation_button)
    Button buttonOperation;
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
    private int idCategory;
    private double value;
    private int idEdit = -1;
    private boolean plus;
    private Toast toast;
    private FragmentManager manager;
    private Payment payment;
    private ArrayList<Participant> participants;

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
        unbinder = ButterKnife.bind(this);
        Intent intent = getIntent();
        idEdit = intent.getIntExtra(EDIT, -1);
        toast = Toast.makeText(this, R.string.database_error, Toast.LENGTH_SHORT);
        manager = getSupportFragmentManager();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (idEdit != -1) {
                editFields();
            } else {
                participants = new ArrayList<>();
                updateDate();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Category category = MyApplication.getHomeRoomDatabase().categoryDao().getDefaultCategory();
                        OperationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                idCategory = category.getId();
                                categoryText.setText(category.getName());
                                titleText.setHint(category.getName());
                            }
                        });
                    }
                }).start();
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

        buttonOperation.setOnClickListener(new View.OnClickListener() {
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
        idCategory = ids;
        categoryText.setText(text);
        titleText.setHint(text);
    }

    private void showNumbers() {
        NumbersFragment numbersFragment = new NumbersFragment();
        numbersFragment.setListener(this);
        numbersFragment.show(manager, "Dialog");
    }

    private void showCategory() {
        CategoryDialogFragment categoryDialogFragment = new CategoryDialogFragment();
        categoryDialogFragment.setListener(this);
        categoryDialogFragment.show(manager, "Category");
    }

    private void createOperation() {
        String title = titleText.getText().toString();
        if (!plus) {
            value *= -1;
        }
        if (title.length() == 0) {
            title = titleText.getHint().toString();
        }
        try {
            if (idEdit == -1) {
                participants.add(new Participant(1, value));
                PaymentDBUtils.insert(new Payment(title, value, dateTime.getTimeInMillis(), idCategory, 1, participants));
            } else {
                participants = payment.getParticipants();
                participants.get(0).setUser_value(value);
                payment.setTitle(title);
                payment.setValue(value);
                payment.setCategory_id(idCategory);
                payment.setParticipants(participants);
                payment.setDate(dateTime.getTimeInMillis());
                PaymentDBUtils.update(payment);
                ParticipantDBUtils.update(participants.get(0));
            }
        } catch (SQLiteException w) {
            toast.show();
        }
        finish();
        startActivity(getSupportParentActivityIntent());
    }

    private void editFields() {
        setTitle(R.string.editing_operations);
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    payment = MyApplication.getHomeRoomDatabase().paymentDao().getPaymentById(idEdit);
                    payment.setParticipants((ArrayList<Participant>) MyApplication.getHomeRoomDatabase().participantDao().getAllById(payment.getId()));
                    final Category category = MyApplication.getHomeRoomDatabase().categoryDao().getCategoryById(payment.getCategory_id());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            titleText.setText(payment.getTitle());
                            categoryText.setText(category.getName());
                            idCategory = payment.getCategory_id();
                            dateTime.setTimeInMillis(payment.getDate());
                            valueText.setText(replaceDoubleToString(Math.abs(payment.getValue())));
                            updateDate();
                        }
                    });
                }
            }).start();
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
        PaymentDBUtils.delete(payment);
        finish();
        setResult(RESULT_OK, getSupportParentActivityIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
