package com.example.adrian.homecalc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import yuku.ambilwarna.AmbilWarnaDialog;

public class NewUserActivity extends AppCompatActivity {

    public static final String EDIT = "edit";
    private SQLiteDatabase db;
    private ImageView imageColor;
    private EditText title;
    private int colour, id;
    private ImageView image;
    private String name, cursorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        SQLiteOpenHelper helper = new ApplicationDatabase(this);
        db = helper.getWritableDatabase();
        id = getIntent().getIntExtra(EDIT, -1);
        title = (EditText) findViewById(R.id.new_title);
        imageColor = (ImageView) findViewById(R.id.new_color);
        Button button = (Button) findViewById(R.id.button_user);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = title.getText().toString().trim();
                if (name.length() != 0) {
                    createUser();
                } else {
                    Toast.makeText(NewUserActivity.this, "Użytkownik musi mieć nazwę", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (id != -1) {
            try {
                Cursor cursor = db.query("PERSON", new String[]{"NAME", "COLOR", "_id"},
                        "_id = ?", new String[]{Integer.toString(id)}, null, null, null);
                cursor.moveToFirst();
                cursorName = cursor.getString(0);
                title.setText(cursorName);
                imageColor.setBackgroundColor(cursor.getInt(1));
                setTitle("Edycja użytkownika");
                button.setText("Edytuj");
                cursor.close();
            } catch (SQLiteException w) {
                Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT).show();
            }
        }
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
    }

    public void colorpicker() {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, colour, new AmbilWarnaDialog.OnAmbilWarnaListener() {

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

    public void createUser() {
        Cursor cursor = db.query("PERSON", new String[]{"COUNT(NAME)"},
                "NAME = ?", new String[]{name}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getInt(0) == 0 || name.equals(cursorName)) {
            ContentValues values = new ContentValues();
            values.put("NAME", title.getText().toString());
            values.put("COLOR", colour);
            try {
                if (id == -1) {
                    db.insert("PERSON", null, values);
                } else {
                    db.update("PERSON", values, "_id = ?", new String[]{Integer.toString(id)});
                }
            } catch (SQLiteException w) {
                Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT).show();
            }
            setResult(RESULT_OK, getSupportParentActivityIntent());
            finish();
        } else {
            Toast.makeText(this, "Użytkownik o takiej nazwie już istnieje", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (id != -1) {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    deleteUser();
                    return true;
                }
            });
        }
        return true;
    }

    public void deleteUser() {
        try {
            Cursor cursor = db.query("PAYMENT", new String[]{"COUNT(PERSON_ID)"},
                    "PERSON_ID = ?", new String[]{Integer.toString(id)}, null, null, null);
            cursor.moveToFirst();
            if (cursor.getInt(0) == 0) {
                db.delete("PERSON", "_id = ?", new String[]{Integer.toString(id)});
                setResult(RESULT_OK, getSupportParentActivityIntent());
                finish();
            } else {
                Toast.makeText(this, "Nie można usunąć użytkownika posiadającego transakcje", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } catch (SQLiteException w) {
            Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
