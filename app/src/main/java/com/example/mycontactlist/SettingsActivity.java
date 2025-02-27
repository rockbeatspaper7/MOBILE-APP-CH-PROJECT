package com.example.mycontactlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {
    RadioButton rbName;
    RadioButton rbCity;
    RadioButton rbBday;

    RadioButton rbAscend;
    RadioButton rbDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.settings_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initListButton();
        initMapButton();
        initSettingsButton();
        initSettings();
        initSortByClick();
        initSortOrderClick();
    }

    protected void initListButton() {
        ImageButton imgButton = findViewById(R.id.contactIcon);
        imgButton.setOnClickListener(b -> {
            Intent intent = new Intent(SettingsActivity.this, ContactListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    protected void initMapButton() {
        ImageButton imgButton = findViewById(R.id.mapIcon);
        imgButton.setOnClickListener(b -> {
            Intent intent = new Intent(SettingsActivity.this, MapActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
    protected void initSettingsButton() {
        ImageButton imgButton = findViewById(R.id.settingsIcon);
        imgButton.setEnabled(false);
    }

    private void initSettings() {
        String sortBy = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortfield", "contactname");
        String sortOrder = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortorder", "ASC");

        RadioButton rbName = findViewById(R.id.radioName);
        RadioButton rbCity = findViewById(R.id.radioCity);
        RadioButton rbBday = findViewById(R.id.radioBday);
        if (sortBy.equalsIgnoreCase("contactname")) {
            rbName.setChecked(true);
        }
        else if (sortBy.equalsIgnoreCase("city")) {
            rbCity.setChecked(true);
        }
        else {
            rbBday.setChecked(true);
        }

        rbAscend = findViewById(R.id.radioAsc);
        rbDesc = findViewById(R.id.radioDesc);
        if (sortOrder.equalsIgnoreCase("ASC")) {
            rbAscend.setChecked(true);
        }
        else {
            rbDesc.setChecked(true);
        }
    }

    private void initSortByClick() {
        RadioGroup rgSortBy = findViewById(R.id.radioGroupSortBy);
        rgSortBy.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rbName = SettingsActivity.this.findViewById(R.id.radioName);
            RadioButton rbCity = SettingsActivity.this.findViewById(R.id.radioCity);
            if (rbName.isChecked()) {
                SettingsActivity.this.getSharedPreferences("MyContactListPreferences",
                        Context.MODE_PRIVATE).edit().putString("sortfield", "contactname").apply();
            } else if (rbCity.isChecked()) {
                SettingsActivity.this.getSharedPreferences("MyContactListPreferences",
                        Context.MODE_PRIVATE).edit().putString("sortfield", "city").apply();
            } else {
                SettingsActivity.this.getSharedPreferences("MyContactListPreferences",
                        Context.MODE_PRIVATE).edit().putString("sortfield", "birthday").apply();
            }

        });
    }

    private void initSortOrderClick() {
        RadioGroup rgSortOrder = findViewById(R.id.radioGroupSortOrder);
        rgSortOrder.setOnCheckedChangeListener((group, checkedId) -> {
            rbAscend = SettingsActivity.this.findViewById(R.id.radioAsc);
            rbDesc = SettingsActivity.this.findViewById(R.id.radioDesc);
            if (rbAscend.isChecked()) {
                SettingsActivity.this.getSharedPreferences("MyContactListPreferences",
                        Context.MODE_PRIVATE).edit().putString("sortorder", "ASC").apply();
            } else if (rbDesc.isChecked()) {
                SettingsActivity.this.getSharedPreferences("MyContactListPreferences",
                        Context.MODE_PRIVATE).edit().putString("sortorder", "DESC").apply();
            }
        });
    }
}
