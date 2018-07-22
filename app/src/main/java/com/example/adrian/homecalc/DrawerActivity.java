package com.example.adrian.homecalc;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class DrawerActivity extends AppCompatActivity {

    private Intent fabIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.drawer_fab);
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        setTitle(intent.getStringExtra("title"));
        FragmentManager manager = getFragmentManager();
        switch (id) {
            case R.id.nav_planned:
                ListOperationFragment operationFragment = new ListOperationFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("planned", true);
                operationFragment.setArguments(bundle);
                fab.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().add(R.id.drawer_fragment, operationFragment).commit();
                break;
            case R.id.nav_users:
                Fragment fragment = new PersonFragment();
                fabIntent = new Intent(this, NewUserActivity.class);
                manager.beginTransaction().add(R.id.drawer_fragment, fragment).commit();
                break;
            case R.id.nav_category:
                fragment = new CategoryFragment();
                fabIntent = new Intent(this, NewCategoryActivity.class);
                manager.beginTransaction().add(R.id.drawer_fragment, fragment).commit();
                break;
            case R.id.nav_arrears:
                fragment = new ArrearFragment();
                fabIntent = new Intent(this, NewRepaymentActivity.class);
                manager.beginTransaction().add(R.id.drawer_fragment, fragment).commit();
                break;
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(fabIntent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
        startActivity(getIntent());
    }
}
