package com.example.adrian.homecalc.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.example.adrian.homecalc.activity.DrawerActivity;
import com.example.adrian.homecalc.R;
import com.example.adrian.homecalc.adapter.CategoryAdapter;
import com.example.adrian.homecalc.database.CategoryDBUtils;
import com.example.adrian.homecalc.database.DBCallback;
import com.example.adrian.homecalc.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDialogFragment extends DialogFragment {

    private CategoryListener listener;
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

    public void setListener(CategoryListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = (RecyclerView) inflater.inflate(R.layout.dialog_category, null);
        adapter = new CategoryAdapter();
        CategoryDBUtils.getAll(categoryDBCallback);
        view.setAdapter(adapter);
        view.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.setListener(new CategoryAdapter.CategoryListener() {
            @Override
            public void setCategory(Category category) {
                listener.setCategory(category.getName(), category.getId());
                dismiss();
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("Kategorie");
        builder.setNeutralButton("Dodaj kategorię", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), DrawerActivity.class);
                intent.putExtra("id", R.layout.fragment_new_category);
                startActivity(intent);
            }
        });
        return builder.create();
    }

    public interface CategoryListener {
        void setCategory(String text, int ids);
    }

}
