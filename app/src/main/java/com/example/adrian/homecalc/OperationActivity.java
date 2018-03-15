package com.example.adrian.homecalc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class OperationActivity extends AppCompatActivity implements NumbersFragment.ValueListener, CategoryFragment.CategoryListener {

    public static final String INT_EXTRA = "extra";
    private static final String DIALOG = "Dialog";

    private SimpleDateFormat dateFormat;
    private Calendar dateTime = Calendar.getInstance();
    private EditText dateText, valueText, categoryText, titleText;
    private int day, month, year;
    private SQLiteOpenHelper helper;
    private SQLiteDatabase db;
    private RadioGroup radioGroup;
    private int ids, impact;
    private double value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_operation);
        Intent intent = getIntent();
        impact = intent.getIntExtra(INT_EXTRA, 0);
        helper = new ApplicationDatabase(this);
        db = helper.getWritableDatabase();

        if(impact==0){
            setContentView(R.layout.activity_expense);
            //RecyclerView paying = (RecyclerView) findViewById(R.id.paying);
            RecyclerView participant = (RecyclerView) findViewById(R.id.participant);
            //LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            //paying.setLayoutManager(layoutManager1);
            participant.setLayoutManager(layoutManager);
            try{
//                helper = new ApplicationDatabase(this);
//                db = helper.getWritableDatabase();
                Cursor cur = db.rawQuery("SELECT NAME, COLOR, ICON_ID, _id FROM PERSON",null);
                UserAdapter adapter = new UserAdapter(cur);
//                paying.setAdapter(adapter);
//                adapter.setListener(new CategoryAdapter.CategoryListener() {
//                    @Override
//                    public void setCategory(String text, int ids) {
//
//                    }
//                });
                participant.setAdapter(adapter);
            } catch(SQLiteException w){
                Toast toast = Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else{
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
//        valueText = (EditText) findViewById(R.id.value);
//        valueText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showNumbers();
//            }
//        });

        categoryText = (EditText) findViewById(R.id.category);
        categoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategory();
            }
        });

        titleText = (EditText) findViewById(R.id.title) ;

        try{
//            helper = new ApplicationDatabase(this);
//            db = helper.getWritableDatabase();
            Cursor cursor = db.query("CATEGORY", new String[]{"NAME", "_id"}, "DEFAULTS = 1", null, null, null, null);
            if(cursor.moveToFirst()) {
                ids = cursor.getInt(1);
                categoryText.setText(cursor.getString(0));
                titleText.setHint(cursor.getString(0));
            }
        } catch(SQLiteException w){
            Toast toast = Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
        }

        dateText = (EditText) findViewById(R.id.date);
        updateDate();
        dateText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                day = dateTime.get(Calendar.DAY_OF_MONTH);
                month = dateTime.get(Calendar.MONTH);
                year = dateTime.get(Calendar.YEAR);
                new DatePickerDialog(OperationActivity.this, d, year, month, day).show();
            }
        });

        Button button = (Button) findViewById(R.id.operation_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioGroup!=null) {
                    int radioId = radioGroup.getCheckedRadioButtonId();
                    if (radioId == R.id.radio_profit) {
                        impact = 1;
                    } else {
                        impact = 0;
                    }
                }
                if(valueText.getText().length()!=0) {
                    String val = valueText.getText().toString().replace(",",".").substring(0,valueText.length()-3);
                    value = Double.parseDouble(val);
                    if(value!=0) {
                        createOperation();
                    }
                    else{
                        Toast.makeText(OperationActivity.this, "Wartość musi być większa od zera", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(OperationActivity.this, "Operacja musi zawierać wartość", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateDate(){
        dateText.setText(dateFormat.format(dateTime.getTime()));
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, month);
            dateTime.set(Calendar.DAY_OF_MONTH, day);
            updateDate();
        }
    };

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

    private void showNumbers(){
        FragmentManager manager = getSupportFragmentManager();
        NumbersFragment fragment = new NumbersFragment();
        fragment.show(manager, DIALOG);
    }

    private void showCategory(){
        FragmentManager manager = getSupportFragmentManager();
        CategoryFragment fragment = new CategoryFragment();
        fragment.show(manager, "Category");
    }

    private void createOperation(){
        ContentValues values = new ContentValues();
        String title = titleText.getText().toString();
        if(impact==0){
            value = -1 * value;
        }
        if(title.length()==0){
            title = titleText.getHint().toString();
        }
        try {
            values.put("TITLE", title);
            values.put("VALUE", value);
            values.put("DATE", dateText.getText().toString());
            values.put("CATEGORY_ID", ids);
            db.insert("PAYMENT", null, values);
        }
        catch(SQLiteException w){
            Toast toast = Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
        }
        finish();
        startActivity(getSupportParentActivityIntent());
    }

}
