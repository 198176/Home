package com.example.adrian.homecalc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PersonDialogFragment.PersonListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static String spinner_date = "";
    private static int person_id = 1;
    boolean isOpen = false;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ImageView person;
    private FloatingActionButton fab, fab_plus, fab_minus;
    private Animation fabOpen, fabClose, fabRClockwise, fabRAnticlockwise;
    private TextView textPlus, textMinus;
    private SQLiteDatabase db;
    private Spinner spinner;
    private DrawerLayout drawer;
    private TextView textCost;
    private TextView textTotal;
    public static int dayBilling = 1;
    private SharedPreferences preferences;

    public static String getSpinnerDate() {
        return spinner_date;
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        setSpinnerDate();
//    }

    public static String getPersonId() {
        if(person_id == -1) {
            return "";
        } else {
            return "PERSON_ID = " + person_id + " AND";
        }
    }

    public static String getPayingId() {
        if(person_id == -1) {
            return "";
        } else {
            return "PAYING_ID = " + person_id + " AND";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_drawer_main);
        preferences = getSharedPreferences("prefer", 0);
        dayBilling = preferences.getInt("day", 1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        SQLiteOpenHelper helper = new ApplicationDatabase(this);
        db = helper.getReadableDatabase();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_plus = (FloatingActionButton) findViewById(R.id.fab_plus);
        fab_minus = (FloatingActionButton) findViewById(R.id.fab_minus);
        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabRClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        fabRAnticlockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);
        textPlus = (TextView) findViewById(R.id.plus_text);
        textMinus = (TextView) findViewById(R.id.minus_text);
        textCost = (TextView) findViewById(R.id.monthly_balance);
        textTotal = findViewById(R.id.total_balance);
        spinner = (Spinner) findViewById(R.id.spinner);
        person = (ImageView) findViewById(R.id.fab_person);
        try {
            Cursor cursor = db.rawQuery("SELECT NAME, COLOR, _id FROM PERSON", null);
            cursor.moveToFirst();
            setPerson(cursor.getString(0), cursor.getInt(1), cursor.getInt(2));
        } catch (SQLiteException w) {
            Toast.makeText(this, R.string.database_error, Toast.LENGTH_SHORT).show();
        }
        person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPerson();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFab();
            }
        });

        fab_minus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickFab();
                Intent intent = new Intent(MainActivity.this, ExpenseActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        fab_plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickFab();
                Intent intent = new Intent(MainActivity.this, OperationActivity.class);
                startActivityForResult(intent, 0);
            }
        });

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        setSpinnerDate();
    }

    void setBillingPeriod() {
        View view = getLayoutInflater().inflate(R.layout.dialog_billing_period, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Pierwszy dzień miesiąca");
        dialog.setView(view);
        NumberPicker picker = (NumberPicker) view.findViewById(R.id.number_picker);
        picker.setMinValue(1);
        picker.setMaxValue(28);
        picker.setValue(dayBilling);
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int o, int n) {
                preferences.edit().putInt("day", n).commit();
                dayBilling = n;
            }
        });
        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setSpinnerDate();
                dialogInterface.dismiss();
            }
        });
        dialog.create().show();
    }

    public void setSpinnerDate() {
        try {
            Cursor cursorDate = db.rawQuery("SELECT _id, (CASE WHEN cast(strftime('%d', date(DATE/1000, 'unixepoch', 'localtime')) as integer) " +
                    "< strftime(" + dayBilling + ") THEN strftime('%Y-%m', date(DATE/1000, 'unixepoch', 'localtime', '-1 month')) " +
                    "ELSE strftime('%Y-%m', date(DATE/1000, 'unixepoch', 'localtime')) END) MONTH FROM PAYMENT " +
                    "WHERE DATE/1000 <= cast(strftime('%s', 'now') as integer) GROUP BY MONTH ORDER BY MONTH DESC", null);
            CursorAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.spinner_date,
                    cursorDate, new String[]{"MONTH"}, new int[]{R.id.spinner_text}, 0);
            spinner.setAdapter(listAdapter);
            //spinner_date = String.valueOf(spinner.getContentDescription());
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    Cursor c = (Cursor) parent.getItemAtPosition(pos);
                    spinner_date = c.getString(c.getColumnIndex("MONTH"));
                    refreshFragments();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (SQLiteException w) {
            Toast.makeText(this, R.string.database_error, Toast.LENGTH_SHORT).show();
        }
    }

    void setAmountCost() {
        try {
            Cursor cursor = db.rawQuery("SELECT SUM(VALUE), (CASE WHEN cast(strftime('%d', " +
                    "date(DATE/1000, 'unixepoch', 'localtime')) as integer) < strftime("+MainActivity.dayBilling+") " +
                    "THEN strftime('%Y-%m', date(DATE/1000, 'unixepoch', 'localtime', '-1 month')) " +
                    "ELSE strftime('%Y-%m', date(DATE/1000, 'unixepoch', 'localtime')) END) MONTH " +
                    "FROM PAYMENT WHERE " + getPersonId() + " MONTH = '" + getSpinnerDate() + "' " +
                    "AND DATE/1000 <= cast(strftime('%s', 'now') as integer) AND ID_PAY != 0", null);
            cursor.moveToFirst();
            textCost.setText(OperationActivity.replaceDoubleToString(cursor.getDouble(0)));
            cursor = db.rawQuery("SELECT SUM(CASE WHEN ID_PAY == 0 THEN ABS(VALUE) ELSE VALUE END) " +
                    "FROM PAYMENT WHERE PAYING_ID = 1 OR (ID_PAY == 0 AND PERSON_ID == 1)", null);
            cursor.moveToFirst();
            textTotal.setText(OperationActivity.replaceDoubleToString(cursor.getDouble(0)));
            cursor.close();
        } catch (SQLiteException w) {
            Toast.makeText(this, R.string.database_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setPerson(String text, int color, int ids) {
        if (color == -1) {
            person.setImageResource(ids);
            person_id = color;
        } else {
            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRound(Character.toString(text.charAt(0)), color);
            person.setImageDrawable(textDrawable);
            person_id = ids;
        }
        refreshFragments();
    }

    private void showPerson() {
        FragmentManager manager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putBoolean(PersonDialogFragment.ALL, true);
        PersonDialogFragment fragment = new PersonDialogFragment();
        fragment.setArguments(bundle);
        fragment.show(manager, "Person");
    }

    private void refreshFragments() {
        setAmountCost();
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            for (int i = 0; i < fragmentList.size(); i++) {
                Fragment fragment = fragmentList.get(i);
                if (fragment != null && !fragment.getClass().equals(PersonDialogFragment.class)) {
                    getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                }
            }
        }
    }

    private void clickFab() {
        if (isOpen) {
            fab_minus.startAnimation(fabClose);
            fab_plus.startAnimation(fabClose);
            textPlus.startAnimation(fabClose);
            textMinus.startAnimation(fabClose);
            fab.startAnimation(fabRAnticlockwise);
            fab_minus.setClickable(false);
            fab_plus.setClickable(false);
            isOpen = false;
        } else {
            fab_minus.startAnimation(fabOpen);
            fab_plus.startAnimation(fabOpen);
            textPlus.startAnimation(fabOpen);
            textMinus.startAnimation(fabOpen);
            fab.startAnimation(fabRClockwise);
            fab_minus.setClickable(true);
            fab_plus.setClickable(true);
            isOpen = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_billing_period) {
            setBillingPeriod();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setSpinnerDate();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        String title = item.getTitle().toString();
        Intent intent = new Intent(this, DrawerActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        startActivity(intent);
//        switch (id) {
//            case R.id.nav_home:
//                break;
//            case R.id.nav_users:
//                showPerson();
//                break;
//            case R.id.nav_category:
//
//                break;
//            case R.id.nav_arrears:
//                break;
//            default:
//        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ListOperationFragment();
                case 1:
                    return new ListPersonOperationFragment();
                case 2:
                    return new ListSummaryFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Operacje";
                case 1:
                    return "Koszty";
                case 2:
                    return "Podsumowanie";
            }
            return null;
        }
    }
}
