package com.example.adrian.homecalc;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by Adrian on 2017-08-20.
 */

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {

    interface IconListener{
        void setIcon(int ic);
    }

    private IconListener listener;

    private TypedArray icons;

    public IconAdapter(TypedArray icons) {
        this.icons = icons;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    @Override
    public IconAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_icon, parent, false);
        return new IconAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(IconAdapter.ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        ImageButton imageButton = (ImageButton) cardView.findViewById(R.id.icon_button);
       // Drawable drawable = cardView.getResources().getDrawable(icons[position]);
        imageButton.setImageDrawable(icons.getDrawable(position));
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.setIcon(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return icons.length();
    }

    public void setListener(IconListener listener) {
        this.listener = listener;
    }

}
