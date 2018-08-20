package com.example.adrian.homecash.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.adrian.homecash.fragment.PersonFragment;
import com.example.adrian.homecash.R;
import com.example.adrian.homecash.fragment.ArrearFragment;
import com.example.adrian.homecash.fragment.CategoryFragment;
import com.example.adrian.homecash.fragment.ListOperationFragment;
import com.example.adrian.homecash.fragment.NewCategoryFragment;

import java.util.Objects;

public class DrawerActivity extends AppCompatActivity {

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        FloatingActionButton fab = findViewById(R.id.drawer_fab);
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        setTitle(intent.getStringExtra("title"));
        switch (id) {
            case R.id.nav_planned:
                fragment = new ListOperationFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("planned", true);
                fragment.setArguments(bundle);
                fab.setVisibility(View.GONE);
                break;
            case R.id.nav_users:
                fragment = new PersonFragment();
                break;
            case R.id.nav_category:
                fragment = new CategoryFragment();
                break;
            case R.id.nav_arrears:
                fragment = new ArrearFragment();
                break;
            case R.layout.fragment_new_category:
                fragment = new NewCategoryFragment();
                Objects.requireNonNull(getActionBar()).setDisplayHomeAsUpEnabled(false);
                break;
        }
        getSupportFragmentManager().beginTransaction().add(R.id.drawer_fragment, fragment).commit();
    }
}
