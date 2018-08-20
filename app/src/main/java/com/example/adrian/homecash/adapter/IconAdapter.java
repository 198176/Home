package com.example.adrian.homecash.adapter;

import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.adrian.homecash.R;

public class IconAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private IconListener listener;
    private TypedArray icons;

    public IconAdapter(TypedArray icons) {
        this.icons = icons;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IconViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_icon, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ((IconViewHolder) holder).bind(position);
    }

    @Override
    public int getItemCount() {
        return (icons == null) ? 0 : icons.length();
    }

    public void setListener(IconListener listener) {
        this.listener = listener;
    }

    public interface IconListener {
        void setIcon(int ic);
    }

    class IconViewHolder extends RecyclerView.ViewHolder {
        View parent;
        ImageButton imageButton;

        IconViewHolder(View v) {
            super(v);
            parent = v;
            imageButton = v.findViewById(R.id.icon_button);
        }

        void bind(final int position) {
            imageButton.setImageDrawable(icons.getDrawable(position));
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.setIcon(position);
                    }
                }
            });
        }
    }

}
