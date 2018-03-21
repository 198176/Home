package com.example.adrian.homecalc;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PersonFragment.PersonListener {

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
    private FloatingActionButton fab, fab_plus, fab_minus, person;
    private Animation fabOpen, fabClose, fabRClockwise, fabRAnticlockwise;
    private TextView textPlus, textMinus;
    private SQLiteDatabase db;

    public static String getSpinnerDate() {
        return spinner_date;
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        setSpinnerDate();
//    }

    public static int getPersonId() {
        return person_id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

        person = (FloatingActionButton) findViewById(R.id.fab_person);
        try {
            Cursor cursor = db.rawQuery("SELECT COLOR, ICON_ID, _id FROM PERSON", null);
            cursor.moveToFirst();
            setPerson(cursor.getInt(2), cursor.getInt(1), cursor.getInt(0));
        } catch (SQLiteException w) {
            Toast toast = Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
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
                Intent intent = new Intent(MainActivity.this, OperationActivity.class);
                intent.putExtra(OperationActivity.INT_EXTRA, 0);
                startActivity(intent);
            }
        });

        fab_plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickFab();
                Intent intent = new Intent(MainActivity.this, OperationActivity.class);
                intent.putExtra(OperationActivity.INT_EXTRA, 1);
                startActivity(intent);
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

    public void setSpinnerDate() {
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        try {
            Cursor cur = db.rawQuery("SELECT _id, SUBSTR(DATE, 1, 7) MONTH FROM PAYMENT GROUP BY MONTH ORDER BY MONTH DESC", null);
            CursorAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.spinner_date,
                    cur, new String[]{"MONTH"}, new int[]{R.id.spinner_text}, 0);
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
            Toast toast = Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void setPerson(int ids, int icon, int color) {
        Drawable drawable = getResources().getDrawable(icon);
        person.setBackgroundTintList(ColorStateList.valueOf(color));
        person.setImageDrawable(drawable);
        person_id = ids;
        refreshFragments();
    }

    private void showPerson() {
        FragmentManager manager = getSupportFragmentManager();
        PersonFragment fragment = new PersonFragment();
        fragment.show(manager, "Person");
    }

    private void refreshFragments(){
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if(fragmentList!=null) {
            for (int i = 0; i < fragmentList.size(); i++) {
                Fragment fragment = fragmentList.get(i);
                if (fragment != null && !fragment.getClass().equals(PersonFragment.class)) {
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                    return "Osoby";
                case 2:
                    return "Podsumowanie";
            }
            return null;
        }
    }
}