package com.example.adrian.homecalc;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;

public class NewCategoryActivity extends AppCompatActivity {

    public static final String EDIT = "edit";
    private SQLiteDatabase db;
    private ImageView imageColor, imageIcon;
    private FloatingActionButton floating;
    private RecyclerView view;
    private AlertDialog dialog;
    private CheckBox check;
    private EditText title;
    private int colour, id, cursorId, ics;
    private TypedArray icons;
    private String cursorName, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);
        SQLiteOpenHelper helper = new ApplicationDatabase(this);
        db = helper.getWritableDatabase();
        id = getIntent().getIntExtra(EDIT, -1);
        icons = getResources().obtainTypedArray(R.array.category_icon);
        title = (EditText) findViewById(R.id.new_title);
        imageColor = (ImageView) findViewById(R.id.new_color);
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
                name = title.getText().toString().trim();
                if (name.length() != 0) {
                    createCategory();
                } else {
                    Toast.makeText(NewCategoryActivity.this, "Kategoria musi zawierać tytuł", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (id != -1) {
            try {
                Cursor cursor = db.query("CATEGORY", new String[]{"NAME", "COLOR", "ICON_ID", "DEFAULTS", "_id"},
                        "_id = ?", new String[]{Integer.toString(id)}, null, null, null);
                cursor.moveToFirst();
                cursorName = cursor.getString(0);
                title.setText(cursorName);
                imageColor.setBackgroundColor(cursor.getInt(1));
                floating.setBackgroundTintList(ColorStateList.valueOf(cursor.getInt(1)));
                ics = -1;
                cursorId = cursor.getInt(2);
                floating.setImageResource(cursorId);
                imageIcon.setImageResource(cursorId);
                boolean isCheck = cursor.getInt(3) == 1;
                check.setChecked(isCheck);
                if (isCheck) {
                    check.setClickable(false);
                }
                setTitle("Edycja kategorii");
                button.setText("Edytuj");
                cursor.close();
            } catch (SQLiteException w) {
                Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT).show();
            }
        }
        Drawable drawable = imageColor.getBackground();
        colour = ((ColorDrawable) drawable).getColor();
    }

    public void colorpicker() {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, colour, new OnAmbilWarnaListener() {

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

    public void choiceIcon() {
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
        iconAdapter.setListener(new IconAdapter.IconListener() {
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

    public void createCategory() {
        Cursor cursor = db.query("CATEGORY", new String[]{"COUNT(NAME)"},
                "NAME = ?", new String[]{name}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getInt(0) == 0 || name.equals(cursorName)) {
            ContentValues values = new ContentValues();
            try {
                if (check.isChecked()) {
                    values.put("DEFAULTS", 0);
                    db.update("CATEGORY", values, "DEFAULTS = 1", null);
                }
                values.put("NAME", title.getText().toString());
                values.put("COLOR", colour);
                values.put("DEFAULTS", check.isChecked() ? 1 : 0);
                if(ics ==- 1) {
                    values.put("ICON_ID", cursorId);
                } else {
                    values.put("ICON_ID", icons.getResourceId(ics, 0));
                }
                if(id == -1) {
                    db.insert("CATEGORY", null, values);
                } else {
                    db.update("CATEGORY", values, "_id = ?", new String[]{Integer.toString(id)});
                }
            } catch (SQLiteException w) {
                Toast toast = Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
                toast.show();
            }
            setResult(RESULT_OK, getSupportParentActivityIntent());
            finish();
        } else {
            Toast.makeText(this, "Kategoria o takiej nazwie już istnieje", Toast.LENGTH_SHORT).show();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewCategoryActivity.this);
                    builder.setTitle("Potwierdzenie")
                            .setMessage("Usunięcie kategorii spowoduje zastąpienie powiązanych z nią transakcji na domyślną kategorię." +
                                    "\nCzy na pewno chcesz usunąć kategorię?")
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteCategory();
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

    public void deleteCategory() {
        try {
            Cursor cursor = db.query("CATEGORY", new String[]{"_id"},
                    "DEFAULTS = ?", new String[]{"1"}, null, null, null);
            cursor.moveToFirst();
            if (cursor.getInt(0) != id) {
                ContentValues values = new ContentValues();
                values.put("CATEGORY_ID", cursor.getInt(0));
                db.update("PAYMENT", values, "CATEGORY_ID = ?", new String[]{Integer.toString(id)});
                db.delete("CATEGORY", "_id = ?", new String[]{Integer.toString(id)});
                setResult(RESULT_OK, getSupportParentActivityIntent());
                finish();
            } else {
                Toast.makeText(this, "Nie można usunąć domyślnej kategorii", Toast.LENGTH_SHORT).show();
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
