package com.example.adrian.homecalc;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

public class ArrearAdapter extends RecyclerView.Adapter<ArrearAdapter.ViewHolder> {

    private ArrayList<String[]> list;

    ArrearAdapter(ArrayList<String[]> list) {
        this.list = list;
    }

    @Override
    public ArrearAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_arrear, parent, false);
        return new ArrearAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ArrearAdapter.ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView userFrom = (ImageView) cardView.findViewById(R.id.image_user_from);
        ImageView userTo = (ImageView) cardView.findViewById(R.id.image_user_to);
        TextView nameFrom = (TextView) cardView.findViewById(R.id.name_user_from);
        TextView nameTo = (TextView) cardView.findViewById(R.id.name_user_to);
        TextView value = (TextView) cardView.findViewById(R.id.value_arrear);
        String[] array = list.get(position);
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }
}
