package com.example.adrian.homecalc;

import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import yuku.ambilwarna.AmbilWarnaDialog;

public class NewUserActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private ImageView imageColor;
    private EditText title;
    private int colour;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        title = (EditText) findViewById(R.id.new_title);
        imageColor = (ImageView) findViewById(R.id.new_color);
        Drawable drawable = imageColor.getBackground();
        colour = ((ColorDrawable) drawable).getColor();
        CardView cardColor = (CardView) findViewById(R.id.card_color);
        cardColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorpicker();
            }
        });
        image = (ImageView) findViewById(R.id.image_view);
        setColor();
        Button button = (Button) findViewById(R.id.button_user);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().trim().length()!=0) {
                    createUser();
                }
                else{
                    Toast.makeText(NewUserActivity.this, "Użytkownik musi mieć nazwę", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void colorpicker(){
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, colour, new AmbilWarnaDialog.OnAmbilWarnaListener(){

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                colour = color;
                imageColor.setBackgroundColor(color);
                setColor();
            }
        });
        dialog.show();
    }

    public void setColor() {
        TextDrawable textdrawable = TextDrawable.builder().buildRound("A", colour);
        image.setImageDrawable(textdrawable);
    }

    public void createUser(){
        SQLiteOpenHelper helper = new ApplicationDatabase(this);
        ContentValues values = new ContentValues();
        try {
            db = helper.getWritableDatabase();
            values.put("NAME", title.getText().toString());
            values.put("COLOR", colour);
            db.insert("PERSON", null, values);
            db.close();
        }
        catch(SQLiteException w){
            Toast toast = Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
        }
        setResult(RESULT_OK, getSupportParentActivityIntent());
        finish();
    }
}
