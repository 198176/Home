package com.example.adrian.homecash.fragment;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.adrian.homecash.adapter.IconAdapter;
import com.example.adrian.homecash.MyApplication;
import com.example.adrian.homecash.R;
import com.example.adrian.homecash.database.CategoryDBUtils;
import com.example.adrian.homecash.database.PaymentDBUtils;
import com.example.adrian.homecash.model.Category;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import yuku.ambilwarna.AmbilWarnaDialog;

public class NewCategoryFragment extends Fragment {

    public static final String EDIT = "edit";
    @BindView(R.id.new_color)
    ImageView imageColor;
    @BindView(R.id.new_icon)
    ImageView imageIcon;
    @BindView(R.id.new_floating)
    FloatingActionButton floating;
    @BindView(R.id.check_default)
    CheckBox check;
    @BindView(R.id.new_title)
    EditText title;
    @BindView(R.id.new_button)
    Button button;
    @BindView(R.id.card_color)
    CardView cardColor;
    @BindView(R.id.card_icon)
    CardView cardIcon;
    RecyclerView view;
    private Unbinder unbinder;
    private AlertDialog dialog;
    private int colour, cursorId, ics;
    private TypedArray icons;
    private String cursorName, name;
    private Category category;
    private Listener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_category, null);
        unbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        FloatingActionButton fab = getActivity().findViewById(R.id.drawer_fab);
        fab.setVisibility(View.GONE);
        if (bundle != null && bundle.containsKey(EDIT)) {
            category = (Category) bundle.getSerializable(EDIT);
        }
        icons = getResources().obtainTypedArray(R.array.category_icon);
        cardColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorpicker();
            }
        });
        cardIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceIcon();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = title.getText().toString().trim();
                if (name.length() != 0) {
                    createCategory();
                } else {
                    Toast.makeText(getActivity(), "Kategoria musi zawierać tytuł", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (category != null) {
            getActivity().setTitle("Edycja kategorii");
            cursorName = category.getName();
            title.setText(category.getName());
            imageColor.setBackgroundColor(category.getColor());
            floating.setBackgroundTintList(ColorStateList.valueOf(category.getColor()));
            ics = -1;
            cursorId = category.getIcon();
            floating.setImageResource(category.getIcon());
            imageIcon.setImageResource(category.getIcon());
            boolean isCheck = category.isDefaults();
            check.setChecked(isCheck);
            if (isCheck) {
                check.setClickable(false);
            }
            button.setText("Edytuj");
        } else {
            getActivity().setTitle(R.string.title_activity_new_category);
        }
        Drawable drawable = imageColor.getBackground();
        colour = ((ColorDrawable) drawable).getColor();
        return view;
    }

    public void colorpicker() {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(getActivity(), colour, new AmbilWarnaDialog.OnAmbilWarnaListener() {

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = (RecyclerView) inflater.inflate(R.layout.dialog_category, null);
        IconAdapter iconAdapter = new IconAdapter(icons);
        view.setAdapter(iconAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        view.setLayoutManager(layoutManager);
        builder.setView(view);
        builder.setTitle("Wybierz ikonę");
        dialog = builder.create();
        iconAdapter.setListener(new IconAdapter.IconListener() {
            @Override
            public void setIcon(int ic) {
                cursorId =
                        ics = ic;
                floating.setImageDrawable(icons.getDrawable(ic));
                imageIcon.setImageDrawable(icons.getDrawable(ic));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void createCategory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int count = MyApplication.getHomeRoomDatabase().categoryDao().getCountTheSameNameCategory(name);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (count == 0 || name.equals(cursorName)) {
                            if (check.isChecked()) {
                                CategoryDBUtils.updateDefaultOnFalse();
                            }
                            if (category == null) {
                                category = new Category(name, colour, (ics == -1) ? cursorId : icons.getResourceId(ics, 0), check.isChecked());
                                CategoryDBUtils.insert(category);
                                if (listener != null) {
                                    listener.onClick(category, false);
                                }
                            } else {
                                category.setName(name);
                                category.setColor(colour);
                                category.setIcon((ics == -1) ? cursorId : icons.getResourceId(ics, 0));
                                category.setDefaults(check.isChecked());
                                CategoryDBUtils.update(category);
                            }
                            getFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(getActivity(), "Kategoria o takiej nazwie już istnieje", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (category != null) {
            inflater.inflate(R.menu.menu_delete, menu);
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
    }

    public void deleteCategory() {
        if (!category.isDefaults()) {
            PaymentDBUtils.updatePaymentsCategoryIdByCategory(category.getId());
            CategoryDBUtils.delete(category);
            if (listener != null) {
                listener.onClick(category, true);
            }
            getFragmentManager().popBackStack();
        } else {
            Toast.makeText(getActivity(), "Nie można usunąć domyślnej kategorii", Toast.LENGTH_SHORT).show();
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public interface Listener {
        void onClick(Category category, boolean isDelete);
    }
}
