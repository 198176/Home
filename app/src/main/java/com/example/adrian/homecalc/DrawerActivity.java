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
    private Fragment fragment;
    private FragmentManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.drawer_fab);
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        setTitle(intent.getStringExtra("title"));
        manager = getFragmentManager();
        switch (id) {
            case R.id.nav_home:
                break;
            case R.id.nav_users:
                fragment = new PersonFragment();
                fabIntent = new Intent(this, NewUserActivity.class);
                manager.beginTransaction().add(R.id.drawer_fragment, fragment).commit();
                break;
            case R.id.nav_category:
                fragment = new CategoryFragment();
                fabIntent = new Intent(this, NewCategoryActivity.class);
                manager.beginTransaction().add(R.id.drawer_fragment, fragment).commit();
                break;
            case R.id.nav_arrears:
                break;
            default:
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
