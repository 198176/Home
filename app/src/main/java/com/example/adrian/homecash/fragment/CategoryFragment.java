package com.example.adrian.homecash.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adrian.homecash.R;
import com.example.adrian.homecash.adapter.CategoryAdapter;
import com.example.adrian.homecash.database.CategoryDBUtils;
import com.example.adrian.homecash.database.DBCallback;
import com.example.adrian.homecash.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment implements NewCategoryFragment.Listener {

    private RecyclerView view;
    private CategoryAdapter adapter;
    private ArrayList<Category> categories;
    DBCallback<Category> categoryDBCallback = new DBCallback<Category>() {
        @Override
        public void onCallback(List<Category> array) {
            categories = (ArrayList<Category>) array;
            view.post(new Runnable() {
                @Override
                public void run() {
                    adapter.setCategories(categories);
                    adapter.notifyDataSetChanged();

                }
            });
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new CategoryAdapter();
        CategoryDBUtils.getAll(categoryDBCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (RecyclerView) inflater.inflate(R.layout.dialog_category, container, false);
        view.setLayoutManager(new LinearLayoutManager(getActivity()));
        view.setAdapter(adapter);
        getActivity().setTitle("Kategorie");
        FloatingActionButton fab = getActivity().findViewById(R.id.drawer_fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewCategoryFragment categoryFragment = new NewCategoryFragment();
                categoryFragment.setListener(CategoryFragment.this);
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.drawer_fragment, categoryFragment).commit();
            }
        });
        adapter.setListener(new CategoryAdapter.CategoryListener() {
            @Override
            public void setCategory(Category category) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(NewCategoryFragment.EDIT, category);
                NewCategoryFragment categoryFragment = new NewCategoryFragment();
                categoryFragment.setArguments(bundle);
                categoryFragment.setListener(CategoryFragment.this);
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.drawer_fragment, categoryFragment).commit();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void onClick(Category category, boolean isDelete) {
        if (isDelete) {
            int position = categories.indexOf(category);
            categories.remove(position);
            adapter.notifyItemRemoved(position);
        } else {
            categories.add(category);
            adapter.notifyItemInserted(categories.size());
            view.scrollToPosition(categories.size());
        }
    }
}
