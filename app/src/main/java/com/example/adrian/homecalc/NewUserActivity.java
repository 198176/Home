package com.example.adrian.homecalc;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

    private SQLiteDatabase db;
    private ImageView imageColor;
    private EditText title;
    private int colour, idEdit;
    private ImageView image;
    private String name, cursorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        SQLiteOpenHelper helper = new ApplicationDatabase(this);
        db = helper.getWritableDatabase();
        idEdit = getIntent().getIntExtra(OperationActivity.EDIT, -1);
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
        if (idEdit != -1) {
            try {
                Cursor cursor = db.rawQuery("SELECT NAME, COLOR, _id FROM PERSON WHERE _id = ?",
                        new String[]{Integer.toString(idEdit)});
                cursor.moveToFirst();
                cursorName = cursor.getString(0);
                title.setText(cursorName);
                imageColor.setBackgroundColor(cursor.getInt(1));
                setTitle("Edycja użytkownika");
                button.setText("Edytuj");
                cursor.close();
            } catch (SQLiteException w) {
                Toast.makeText(this, R.string.database_error, Toast.LENGTH_SHORT).show();
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
        Cursor cursor = db.rawQuery("SELECT COUNT(NAME) FROM PERSON WHERE NAME = ?", new String[]{name});
        cursor.moveToFirst();
        if (cursor.getInt(0) == 0 || name.equals(cursorName)) {
            ContentValues values = new ContentValues();
            values.put(ApplicationDatabase.NAME, title.getText().toString());
            values.put(ApplicationDatabase.COLOR, colour);
            try {
                if (idEdit == -1) {
                    db.insert(ApplicationDatabase.PERSON, null, values);
                } else {
                    db.update(ApplicationDatabase.PERSON, values, "_id = ?", new String[]{Integer.toString(idEdit)});
                }
            } catch (SQLiteException w) {
                Toast.makeText(this, R.string.database_error, Toast.LENGTH_SHORT).show();
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
        if (idEdit != -1) {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewUserActivity.this);
                    builder.setTitle(R.string.confirmation)
                            .setMessage(R.string.ask_delete_user)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteUser();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    return true;
                }
            });
        }
        return true;
    }

    public void deleteUser() {
        try {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM (SELECT PERSON_ID, PAYING_ID " +
                    "FROM PAYMENT WHERE PERSON_ID = ? OR PAYING_ID = ?)", new String[]{Integer.toString(idEdit), Integer.toString(idEdit)});
            cursor.moveToFirst();
            if ((cursor.getInt(0) == 0) && (idEdit != 1)) {
                db.delete(ApplicationDatabase.PERSON, "_id = ?", new String[]{Integer.toString(idEdit)});
                setResult(RESULT_OK, getSupportParentActivityIntent());
                finish();
            } else {
                Toast.makeText(this, "Nie można usunąć użytkownika posiadającego transakcje", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } catch (SQLiteException w) {
            Toast.makeText(this, R.string.database_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
