package com.example.adrian.homecash.adapter;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adrian.homecash.R;
import com.example.adrian.homecash.model.Category;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private CategoryListener listener;
    private ArrayList<Category> categories;

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ((CategoryViewHolder) holder).bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return (categories == null) ? 0 : categories.size();
    }

    public void setListener(CategoryListener listener) {
        this.listener = listener;
    }

    public interface CategoryListener {
        void setCategory(Category category);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        View parent;
        FloatingActionButton floating;
        TextView textView;

        CategoryViewHolder(View v) {
            super(v);
            parent = v;
            floating = v.findViewById(R.id.floatingButton);
            textView = v.findViewById(R.id.category_title);
        }

        void bind(final Category category) {
            Drawable drawable = parent.getResources().getDrawable(category.getIcon());
            floating.setBackgroundTintList(ColorStateList.valueOf(category.getColor()));
            floating.setImageDrawable(drawable);
            textView.setText(category.getName());
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.setCategory(category);
                    }
                }
            });
        }
    }
}
