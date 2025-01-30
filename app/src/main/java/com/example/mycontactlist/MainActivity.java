package com.example.mycontactlist;

import android.app.DatePickerDialog;
import android.text.format.DateFormat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements com.example.mycontactlist.DatePickerDialog.SaveDateListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initListButton();
        initMapButton();
        initSettingsButton();
        initSettingsButton();
        initToggleButton();
        initChangeDateButton();
        setForEditing(false);
    }

    protected void initListButton() {
        ImageButton imgButton = findViewById(R.id.contactIcon);
        imgButton.setOnClickListener(b -> {
            Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    protected void initMapButton() {
        ImageButton imgButton = findViewById(R.id.mapIcon);
        imgButton.setOnClickListener(b -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
    protected void initSettingsButton() {
        ImageButton imgButton = findViewById(R.id.settingsIcon);
        imgButton.setOnClickListener(b -> {
            Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    protected void initToggleButton() {
        final ToggleButton editToggle = (ToggleButton)findViewById(R.id.toggleButton);

        editToggle.setOnClickListener( t -> {
            setForEditing(editToggle.isChecked());
        });
    }

    private void setForEditing(boolean enabled) {
        EditText editName = findViewById(R.id.contactEdit);
        EditText editStreet = findViewById(R.id.addressEdit);
        EditText editCity = findViewById(R.id.cityEdit);
        EditText editState = findViewById(R.id.stateEdit);
        EditText editZipcode = findViewById(R.id.zipcodeEdit);
        EditText editCell = findViewById(R.id.cellPhoneEdit);
        EditText editHome = findViewById(R.id.homePhoneEdit);
        EditText editEmail = findViewById(R.id.emailEdit);

        editName.setEnabled(enabled);
        editStreet.setEnabled(enabled);
        editCity.setEnabled(enabled);
        editState.setEnabled(enabled);
        editZipcode.setEnabled(enabled);
        editCell.setEnabled(enabled);
        editHome.setEnabled(enabled);
        editEmail.setEnabled(enabled);

        if (enabled) {
            editName.requestFocus();
        }

    }

    @Override
    public void didFinishDatePickerDialog(Calendar selectedTime) {
        TextView birthDay = findViewById(R.id.textBirthday);
        birthDay.setText(DateFormat.format("MM/dd/yyyy", selectedTime));
    }

    private void initChangeDateButton() {
        Button changeDate  = findViewById(R.id.changeButton);
        changeDate.setOnClickListener( c -> {
            FragmentManager fm = getSupportFragmentManager();
            com.example.mycontactlist.DatePickerDialog datePickerDialog = new com.example.mycontactlist.DatePickerDialog();
            datePickerDialog.show(fm, "DatePick");
        });
    }
}