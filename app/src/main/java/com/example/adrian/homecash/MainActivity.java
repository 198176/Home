package com.example.adrian.homecash;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
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
import com.example.adrian.homecash.activity.DrawerActivity;
import com.example.adrian.homecash.activity.ExpenseSplitterActivity;
import com.example.adrian.homecash.activity.OperationActivity;
import com.example.adrian.homecash.dialog.PersonDialogFragment;
import com.example.adrian.homecash.fragment.ListOperationFragment;
import com.example.adrian.homecash.fragment.ListPersonOperationFragment;
import com.example.adrian.homecash.fragment.ListSummaryFragment;
import com.example.adrian.homecash.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements PersonDialogFragment.PersonListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static int person_id = 1;
    private static int dayBilling = 1;
    private static String spinner_date = "";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.expense_container)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.fab_person)
    ImageView person;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.fab_plus)
    FloatingActionButton fab_plus;
    @BindView(R.id.fab_minus)
    FloatingActionButton fab_minus;
    @BindView(R.id.plus_text)
    TextView textPlus;
    @BindView(R.id.minus_text)
    TextView textMinus;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.monthly_balance)
    TextView textCost;
    @BindView(R.id.total_balance)
    TextView textTotal;
    private boolean isOpen = false;
    private Unbinder unbinder;
    private SharedPreferences preferences;
    private Animation fabOpen, fabClose, fabRClockwise, fabRAnticlockwise;

    public static String getSpinnerDate() {
        return spinner_date;
    }

    public static int getPersonId() {
        return person_id;
    }

    public static int getDayBilling() {
        return dayBilling;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_drawer_main);
        unbinder = ButterKnife.bind(this);
        preferences = getSharedPreferences("prefer", 0);
        dayBilling = preferences.getInt("day", 1);

        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

        tabLayout.setupWithViewPager(mViewPager);

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabRClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        fabRAnticlockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);
        new Thread(new Runnable() {
            @Override
            public void run() {
                setPerson(MyApplication.getHomeRoomDatabase().userDao().getUser(1));
            }
        }).start();
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
                Intent intent = new Intent(MainActivity.this, ExpenseSplitterActivity.class);
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

        setSpinnerDate();
    }

    void setBillingPeriod() {
        View view = getLayoutInflater().inflate(R.layout.dialog_billing_period, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Pierwszy dzień miesiąca");
        dialog.setView(view);
        NumberPicker picker = view.findViewById(R.id.number_picker);
        picker.setMinValue(1);
        picker.setMaxValue(28);
        picker.setValue(dayBilling);
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int o, int n) {
                preferences.edit().putInt("day", n).apply();
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Cursor cursorDate = MyApplication.getHomeRoomDatabase().paymentDao().getSpinnerDate(dayBilling);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CursorAdapter listAdapter = new SimpleCursorAdapter(MainActivity.this, R.layout.spinner_date,
                                    cursorDate, new String[]{"MONTH"}, new int[]{R.id.spinner_text}, 0);
                            spinner.setAdapter(listAdapter);
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
                        }
                    });
                }
            }).start();
        } catch (SQLiteException w) {
            Toast.makeText(this, R.string.database_error, Toast.LENGTH_SHORT).show();
        }
    }

    void setAmountCost() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final double monthly, total;
                if (person_id == -1) {
                    monthly = MyApplication.getHomeRoomDatabase().paymentDao().getMonthlyBalancePaymentsForAllUsers(dayBilling, spinner_date);
                } else {
                    monthly = MyApplication.getHomeRoomDatabase().paymentDao().getMonthlyBalancePaymentsByUser(dayBilling, person_id, spinner_date);
                }
                total = MyApplication.getHomeRoomDatabase().paymentDao().getTotalBalancePayments();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textCost.setText(OperationActivity.replaceDoubleToString(monthly));
                        textTotal.setText(OperationActivity.replaceDoubleToString(total));
                    }
                });
            }
        }).start();
    }

    @Override
    public void setPerson(User user) {
        if (user == null) {
            person.setImageResource(R.drawable.ic_user);
            person_id = -1;
        } else {
            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRound(Character.toString(user.getName().charAt(0)), user.getColor());
            person.setImageDrawable(textDrawable);
            person_id = user.getId();
        }
        refreshFragments();
    }

    private void showPerson() {
        FragmentManager manager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putBoolean(PersonDialogFragment.ALL, true);
        PersonDialogFragment fragment = new PersonDialogFragment();
        fragment.setListener(this);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_billing_period:
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
        String title = item.getTitle().toString();
        Intent intent = new Intent(this, DrawerActivity.class);
        intent.putExtra("id", item.getItemId());
        intent.putExtra("title", title);
        startActivity(intent);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
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
