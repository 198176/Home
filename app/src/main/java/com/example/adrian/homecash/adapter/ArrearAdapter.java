package com.example.adrian.homecash.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.adrian.homecash.activity.OperationActivity;
import com.example.adrian.homecash.R;

import java.util.ArrayList;

public class ArrearAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String[]> list;

    public void setList(ArrayList<String[]> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArrearViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_arrear, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ArrearViewHolder) holder).bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return (list == null) ? 0 : list.size();
    }

    class ArrearViewHolder extends RecyclerView.ViewHolder {
        View parent;
        ImageView userFrom;
        ImageView userTo;
        TextView nameFrom;
        TextView nameTo;
        TextView value;

        ArrearViewHolder(View v) {
            super(v);
            parent = v;
            userFrom = v.findViewById(R.id.image_user_from);
            userTo = v.findViewById(R.id.image_user_to);
            nameFrom = v.findViewById(R.id.name_user_from);
            nameTo = v.findViewById(R.id.name_user_to);
            value = v.findViewById(R.id.value_arrear);
        }

        void bind(String[] array) {
            TextDrawable drawableFrom = TextDrawable.builder()
                    .buildRound(Character.toString(array[5].charAt(0)), Integer.parseInt(array[6]));
            TextDrawable drawableTo = TextDrawable.builder()
                    .buildRound(Character.toString(array[3].charAt(0)), Integer.parseInt(array[4]));
            userFrom.setImageDrawable(drawableFrom);
            userTo.setImageDrawable(drawableTo);
            nameFrom.setText(array[5]);
            nameTo.setText(array[3]);
            value.setText(OperationActivity.replaceDoubleToString(Double.parseDouble(array[0])));
        }
    }
}
