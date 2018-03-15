package com.example.adrian.homecalc;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class NumbersFragment extends DialogFragment {

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private View view;
    private TextView numbers;
    private boolean pointer, marker;
    private byte countAfter, countBefore, countIndex;

    interface ValueListener{
        void setValue(String text);
    }

    private ValueListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_numbers, null);
        dialogNumbers();
        return dialog;
    }

    private void dialogNumbers(){
        pointer = false;
        marker = false;
        countBefore = 0;
        countIndex = 0;
        countAfter = 0;

        numbers = (TextView) view.findViewById(R.id.numbers);
        final Button nine = (Button) view.findViewById(R.id.nine);
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(nine.getText());
            }
        });
        final Button eight = (Button) view.findViewById(R.id.eight);
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(eight.getText());
            }
        });
        final Button seven = (Button) view.findViewById(R.id.seven);
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(seven.getText());            }
        });
        final Button six = (Button) view.findViewById(R.id.six);
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(six.getText());
            }
        });
        final Button five = (Button) view.findViewById(R.id.five);
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(five.getText());
            }
        });
        final Button four = (Button) view.findViewById(R.id.four);
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(four.getText());
            }
        });
        final Button three = (Button) view.findViewById(R.id.three);
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(three.getText());
            }
        });
        final Button two = (Button) view.findViewById(R.id.two);
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(two.getText());
            }
        });
        final Button one = (Button) view.findViewById(R.id.one);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(one.getText());
            }
        });
        final Button zero = (Button) view.findViewById(R.id.zero);
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(zero.getText());
            }
        });
        final Button div = (Button) view.findViewById(R.id.division);
        div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertMark(div.getText());
            }
        });
        final Button multi = (Button) view.findViewById(R.id.multiplication);
        multi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertMark(multi.getText());
            }
        });
        final Button sub = (Button) view.findViewById(R.id.subtraction);
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertMark(sub.getText());
            }
        });
        final Button add = (Button) view.findViewById(R.id.addition);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertMark(add.getText());
            }
        });
        final Button sep = (Button) view.findViewById(R.id.separator);
        sep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertPoint();
            }
        });
        Button eq = (Button) view.findViewById(R.id.equals);
        eq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    score();
                } catch (ToastException e) {
                    e.printStackTrace();
                }
            }
        });
        ImageButton back = (ImageButton) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backSpace();
            }
        });

        builder.setView(view);
        dialog = builder.create();

        Button cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button ok = (Button) view.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!numbers.getText().equals("")) {
                        score();
                        listener.setValue(numbers.getText() + " zł");
                    } else {
                        listener.setValue(numbers.getHint() + " zł");
                    }
                    dialog.dismiss();
                } catch (ToastException e) {
                    e.printStackTrace();
                }
            }
        });

        //dialog.show();
    }

    private void insertNumber(CharSequence num){
        String number = numbers.getText().toString();
        if(countBefore==1 && number.equals("0")){
            numbers.setText(num);
        }
        else if(countBefore==1 && marker && number.substring(countIndex+4).equals("0")){
            numbers.setText(number.substring(0,(countIndex+4))+num);
        }
        else if(!pointer && countBefore<6){
            countBefore++;
            numbers.setText(number+num);
        }
        else if(pointer && countAfter<2){
            countAfter++;
            numbers.setText(number+num);
        }
    }

    private void insertPoint(){
        if(!pointer) {
            if (countBefore == 0) {
                insertNumber("0");
            }
            pointer = true;
            countAfter = 0;
            numbers.setText(numbers.getText() + ",");
        }
    }

    private void insertMark(CharSequence mark){
        if(!marker) {
            insertZeros();
            pointer = false;
            marker = true;
            countAfter = 0;
            countIndex = countBefore;
            countBefore = 0;
            numbers.setText(numbers.getText() + mark.toString());
        }
        else {
            try {
                score();
                insertMark(mark);
            } catch (ToastException e) {
                e.printStackTrace();
            }
        }
    }

    private void backSpace(){
        boolean empty = false;
        int len = numbers.getText().length() - 1;
        if(countAfter>0){
            countAfter--;
        }
        else if(pointer){
            pointer = false;
        }
        else if(countBefore>0){
            countBefore--;
        }
        else if(marker){
            marker = false;
            pointer = true;
            countAfter = 2;
            countBefore = countIndex;
        }
        else{
            empty = true;
        }
        if(!empty) {
            numbers.setText(numbers.getText().subSequence(0, len));
        }
    }

    private void score() throws ToastException {
        if(marker && countBefore>0){
            double result = 0.0;
            String operation = numbers.getText().toString();
            operation = operation.replace(",", ".");
            String op1 = operation.substring(0, (countIndex+3));
            String opMark = operation.substring((countIndex+3),(countIndex+4));
            String op2 = operation.substring(countIndex+4);
            double num1 = Double.parseDouble(op1);
            double num2 = Double.parseDouble(op2);
            switch (opMark){
                case "+":
                    result=num1+num2;
                    break;
                case "-":
                    result=num1-num2;
                    if(result<0){
                        throw new ToastException();
                    }
                    break;
                case "*":
                    result=num1*num2;
                    break;
                case "/":
                    if(num2==0){
                        throw new ToastException();
                    }
                    result=num1/num2;
                    break;
            }
            operation = String.format("%.2f", result);
            operation = operation.replace(".", ",");
            countBefore =(byte) operation.substring(0,(operation.indexOf(","))).length();
            countAfter = 2;
            pointer = true;
            marker = false;
            numbers.setText(operation);
        }
        else if(marker){
            backSpace();
        }
        else {
            insertZeros();
        }
    }

    private void insertZeros(){
        while (countAfter < 2) {
            if (!pointer) {
                insertPoint();
            } else if (pointer) {
                insertNumber("0");
            }
        }
    }

    private class ToastException extends Exception{
        ToastException(){
            Toast.makeText(getActivity(), "Operacja nieprawidłowa", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (ValueListener) activity;
    }
}
