package com.example.adrian.homecalc;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

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

    public static String spinner_date="";
    private FloatingActionButton fab, fab_plus, fab_minus;
    private Animation fabOpen, fabClose, fabRClockwise, fabRAnticlockwise;
    private TextView textPlus, textMinus;
    private SQLiteOpenHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor;
    boolean isOpen = false;

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

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_plus = (FloatingActionButton) findViewById(R.id.fab_plus);
        fab_minus = (FloatingActionButton) findViewById(R.id.fab_minus);
        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabRClockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
        fabRAnticlockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);
        textPlus = (TextView) findViewById(R.id.plus_text);
        textMinus = (TextView) findViewById(R.id.minus_text);

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                clickFab();
            }
        });

        fab_minus.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                clickFab();
                Intent intent = new Intent(MainActivity.this, OperationActivity.class);
                intent.putExtra(OperationActivity.INT_EXTRA,0);
                startActivity(intent);
            }
        });

        fab_plus.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                clickFab();
                Intent intent = new Intent(MainActivity.this, OperationActivity.class);
                intent.putExtra(OperationActivity.INT_EXTRA,1);
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

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        setSpinnerDate();
//    }

    public void setSpinnerDate(){
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        try{
            helper = new ApplicationDatabase(this);
            db = helper.getReadableDatabase();
            cursor = db.rawQuery("SELECT _id, SUBSTR(DATE, 1, 7) MONTH FROM PAYMENT GROUP BY MONTH ORDER BY MONTH DESC", null);
        } catch(SQLiteException w){
            Toast toast = Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
        }
        CursorAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.spinner_date,
                cursor, new String[]{"MONTH"}, new int[]{R.id.spinner_text}, 0);
        spinner.setAdapter(listAdapter);
        //spinner_date = String.valueOf(spinner.getContentDescription());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Cursor c =(Cursor) parent.getItemAtPosition(pos);
                spinner_date = c.getString(c.getColumnIndex("MONTH"));
                List<Fragment> fragment = getSupportFragmentManager().getFragments();
                for(int i=0;i<fragment.size();i++) {
                    getSupportFragmentManager().beginTransaction().detach(fragment.get(i)).attach(fragment.get(i)).commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void clickFab(){
        if(isOpen){
            fab_minus.startAnimation(fabClose);
            fab_plus.startAnimation(fabClose);
            textPlus.startAnimation(fabClose);
            textMinus.startAnimation(fabClose);
            fab.startAnimation(fabRAnticlockwise);
            fab_minus.setClickable(false);
            fab_plus.setClickable(false);
            isOpen = false;
        }
        else{
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
                    ListOperationFragment list = new ListOperationFragment();
                    return list;
                case 1:
                    ListSummaryFragment summary = new ListSummaryFragment();
                    return summary;
                case 2:
                    ListOperationFragment list2 = new ListOperationFragment();
                    return list2;
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
                    return "Podsumowanie";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
