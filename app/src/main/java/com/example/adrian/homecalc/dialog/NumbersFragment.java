package com.example.adrian.homecalc.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adrian.homecalc.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NumbersFragment extends DialogFragment {

    @BindView(R.id.numbers)
    TextView numbers;
    @BindView(R.id.nine)
    Button nineButton;
    @BindView(R.id.eight)
    Button eightButton;
    @BindView(R.id.seven)
    Button sevenButton;
    @BindView(R.id.six)
    Button sixButton;
    @BindView(R.id.five)
    Button fiveButton;
    @BindView(R.id.four)
    Button fourButton;
    @BindView(R.id.three)
    Button threeButton;
    @BindView(R.id.two)
    Button twoButton;
    @BindView(R.id.one)
    Button oneButton;
    @BindView(R.id.zero)
    Button zeroButton;
    @BindView(R.id.division)
    Button divButton;
    @BindView(R.id.multiplication)
    Button multiButton;
    @BindView(R.id.subtraction)
    Button subButton;
    @BindView(R.id.addition)
    Button addButton;
    @BindView(R.id.separator)
    Button sepButton;
    @BindView(R.id.equals)
    Button eqButton;
    @BindView(R.id.back)
    ImageButton backButton;
    @BindView(R.id.cancel)
    Button cancelButton;
    @BindView(R.id.ok)
    Button okButton;
    private Unbinder unbinder;
    private AlertDialog dialog;
    private boolean pointer, marker;
    private byte countAfter, countBefore, countIndex;
    private ValueListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_numbers, null);
        unbinder = ButterKnife.bind(this, view);
        dialogNumbers();
        builder.setView(view);
        dialog = builder.create();
        return dialog;
    }

    private void dialogNumbers() {
        pointer = false;
        marker = false;
        countBefore = 0;
        countIndex = 0;
        countAfter = 0;

        nineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(nineButton.getText());
            }
        });
        eightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(eightButton.getText());
            }
        });
        sevenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(sevenButton.getText());
            }
        });
        sixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(sixButton.getText());
            }
        });
        fiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(fiveButton.getText());
            }
        });
        fourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(fourButton.getText());
            }
        });
        threeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(threeButton.getText());
            }
        });
        twoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(twoButton.getText());
            }
        });
        oneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(oneButton.getText());
            }
        });
        zeroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNumber(zeroButton.getText());
            }
        });
        divButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertMark(divButton.getText());
            }
        });
        multiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertMark(multiButton.getText());
            }
        });
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertMark(subButton.getText());
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertMark(addButton.getText());
            }
        });
        sepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertPoint();
            }
        });
        eqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    score();
                } catch (ToastException e) {
                    e.printStackTrace();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backSpace();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
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
    }

    private void insertNumber(CharSequence num) {
        String number = numbers.getText().toString();
        if (countBefore == 1 && number.equals("0")) {
            numbers.setText(num);
        } else if (countBefore == 1 && marker && number.substring(countIndex + 4).equals("0")) {
            numbers.setText(number.substring(0, (countIndex + 4)) + num);
        } else if (!pointer && countBefore < 6) {
            countBefore++;
            numbers.setText(number + num);
        } else if (pointer && countAfter < 2) {
            countAfter++;
            numbers.setText(number + num);
        }
    }

    private void insertPoint() {
        if (!pointer) {
            if (countBefore == 0) {
                insertNumber("0");
            }
            pointer = true;
            countAfter = 0;
            numbers.setText(numbers.getText() + ",");
        }
    }

    private void insertMark(CharSequence mark) {
        if (!marker) {
            insertZeros();
            pointer = false;
            marker = true;
            countAfter = 0;
            countIndex = countBefore;
            countBefore = 0;
            numbers.setText(numbers.getText() + mark.toString());
        } else {
            try {
                score();
                insertMark(mark);
            } catch (ToastException e) {
                e.printStackTrace();
            }
        }
    }

    private void backSpace() {
        boolean empty = false;
        int len = numbers.getText().length() - 1;
        if (countAfter > 0) {
            countAfter--;
        } else if (pointer) {
            pointer = false;
        } else if (countBefore > 0) {
            countBefore--;
        } else if (marker) {
            marker = false;
            pointer = true;
            countAfter = 2;
            countBefore = countIndex;
        } else {
            empty = true;
        }
        if (!empty) {
            numbers.setText(numbers.getText().subSequence(0, len));
        }
    }

    private void score() throws ToastException {
        if (marker && countBefore > 0) {
            double result = 0.0;
            String operation = numbers.getText().toString();
            operation = operation.replace(",", ".");
            String op1 = operation.substring(0, (countIndex + 3));
            String opMark = operation.substring((countIndex + 3), (countIndex + 4));
            String op2 = operation.substring(countIndex + 4);
            double num1 = Double.parseDouble(op1);
            double num2 = Double.parseDouble(op2);
            switch (opMark) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    if (result < 0) {
                        throw new ToastException();
                    }
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    if (num2 == 0) {
                        throw new ToastException();
                    }
                    result = num1 / num2;
                    break;
            }
            operation = String.format("%.2f", result);
            operation = operation.replace(".", ",");
            countBefore = (byte) operation.substring(0, (operation.indexOf(","))).length();
            countAfter = 2;
            pointer = true;
            marker = false;
            numbers.setText(operation);
        } else if (marker) {
            backSpace();
        } else {
            insertZeros();
        }
    }

    private void insertZeros() {
        while (countAfter < 2) {
            if (!pointer) {
                insertPoint();
            } else if (pointer) {
                insertNumber("0");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setListener(ValueListener listener) {
        this.listener = listener;
    }

    public interface ValueListener {
        void setValue(String text);
    }

    private class ToastException extends Exception {
        ToastException() {
            Toast.makeText(getActivity(), "Operacja nieprawidłowa", Toast.LENGTH_SHORT).show();
        }
    }
}
