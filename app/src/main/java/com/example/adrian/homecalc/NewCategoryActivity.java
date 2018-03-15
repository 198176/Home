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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;

public class NewCategoryActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private ImageView imageColor, imageIcon;
    private FloatingActionButton floating;
    private RecyclerView view;
    private AlertDialog dialog;
    private CheckBox check;
    private EditText title;
    private int colour, ics;
    private TypedArray icons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);
        icons = getResources().obtainTypedArray(R.array.category_icon);
        title = (EditText) findViewById(R.id.new_title);
        imageColor = (ImageView) findViewById(R.id.new_color);
        Drawable drawable = imageColor.getBackground();
        colour = ((ColorDrawable) drawable).getColor();
        imageIcon = (ImageView) findViewById(R.id.new_icon);
        floating = (FloatingActionButton) findViewById(R.id.new_floating);
        CardView cardColor = (CardView) findViewById(R.id.card_color);
        cardColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorpicker();
            }
        });
        CardView cardIcon = (CardView) findViewById(R.id.card_icon);
        cardIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceIcon();
            }
        });
        check = (CheckBox) findViewById(R.id.check_default);
        Button button = (Button) findViewById(R.id.new_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().trim().length()!=0) {
                    createCategory();
                }
                else{
                    Toast.makeText(NewCategoryActivity.this, "Kategoria musi zawierać tytuł", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void colorpicker(){
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, colour, new OnAmbilWarnaListener(){

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                colour = color;
                imageColor.setBackgroundColor(color);
                floating.setBackgroundTintList(ColorStateList.valueOf(color));
            }
        });
        dialog.show();
    }

    public void choiceIcon(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        view = (RecyclerView) inflater.inflate(R.layout.dialog_category, null);
        IconAdapter iconAdapter = new IconAdapter(icons);
        view.setAdapter(iconAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        view.setLayoutManager(layoutManager);
        builder.setView(view);
        builder.setTitle("Wybierz ikonę");
        dialog = builder.create();
        iconAdapter.setListener(new IconAdapter.IconListener(){
            @Override
            public void setIcon(int ic) {
                ics = ic;
                floating.setImageDrawable(icons.getDrawable(ic));
                imageIcon.setImageDrawable(icons.getDrawable(ic));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void createCategory(){
        SQLiteOpenHelper helper = new ApplicationDatabase(this);
        ContentValues values = new ContentValues();
        try {
            db = helper.getWritableDatabase();
            if (check.isChecked()) {
                values.put("DEFAULTS", 0);
                db.update("CATEGORY", values, "DEFAULTS = 1", null);
            }
            values.put("NAME", title.getText().toString());
            values.put("COLOR", colour);
            values.put("ICON_ID", icons.getResourceId(ics, 0));
            values.put("DEFAULTS", check.isChecked()?1:0);
            db.insert("CATEGORY", null, values);
            db.close();
        }
        catch(SQLiteException w){
            Toast toast = Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
        }
        finish();
    }

}
