package com.example.mycontactlist;

import static android.app.ProgressDialog.show;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements com.example.mycontactlist.DatePickerDialog.SaveDateListener {
    private Contact currentContact;
    final int PERMISSION_REQUEST_PHONE = 102;
    final int PERMISSION_REQUEST_CAMERA = 103;
    final int CAMERA_REQUEST = 1888;

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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initContact(extras.getLong("contactID"));
        } else {
            currentContact = new Contact();
        }

        setForEditing(false);
        initSaveButton();
        initTextChangedEvents();
        initCallFunction();
        initCameraButton();
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
            if (currentContact.getId() == -1) {
                Toast.makeText(getBaseContext(), "Contact must be saved before it can be mapped", Toast.LENGTH_LONG).show();
            } else {
                intent.putExtra("contactID", currentContact.getId());
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    protected void initSettingsButton() {
        ImageButton imgButton = findViewById(R.id.settingsIcon);
        imgButton.setOnClickListener(b -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    protected void initToggleButton() {
        final ToggleButton editToggle = (ToggleButton) findViewById(R.id.toggleButton);

        editToggle.setOnClickListener(t -> {
            setForEditing(editToggle.isChecked());
        });
    }

    private void initContact(long id) {

        ContactDataSource ds = new ContactDataSource(MainActivity.this);
        try {
            ds.open();
            currentContact = ds.getSpecificContacts(id);
            ds.close();
        } catch (Exception e) {
            Toast.makeText(this, "Load Contact Failed", Toast.LENGTH_LONG).show();
        }
        EditText editName = findViewById(R.id.contactEdit);
        EditText editAddress = findViewById(R.id.addressEdit);
        EditText editCity = findViewById(R.id.cityEdit);
        EditText editState = findViewById(R.id.stateEdit);
        EditText editZipcode = findViewById(R.id.zipcodeEdit);
        EditText editCell = findViewById(R.id.cellPhoneEdit);
        EditText editHome = findViewById(R.id.homePhoneEdit);
        EditText editEmail = findViewById(R.id.emailEdit);
        TextView birthDay = findViewById(R.id.textBirthday);
        ImageButton picture = (ImageButton) findViewById(R.id.ProfileButton);

        editName.setText(currentContact.getContactName());
        editAddress.setText(currentContact.getStreetAddress());
        editCity.setText(currentContact.getCity());
        editState.setText(currentContact.getState());
        editZipcode.setText(currentContact.getZipCode());

        editCell.setText(currentContact.getCellNumber());
        editHome.setText(currentContact.getHomePhoneNumber());
        editEmail.setText(currentContact.getEmail());
        birthDay.setText(DateFormat.format("MM/dd/yyyy",
                currentContact.getBirthday().getTimeInMillis()).toString());
        if (currentContact.getPicture() != null) {
            picture.setImageBitmap(currentContact.getPicture());
        }
        else {
            picture.setImageResource(R.drawable.profile_pic_icon);
        }

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
        ImageButton picture = findViewById(R.id.ProfileButton);

        picture.setEnabled(enabled);
        editName.setEnabled(enabled);
        editStreet.setEnabled(enabled);
        editCity.setEnabled(enabled);
        editState.setEnabled(enabled);
        editZipcode.setEnabled(enabled);
        editCell.setInputType(InputType.TYPE_CLASS_PHONE);
        editHome.setInputType(InputType.TYPE_CLASS_PHONE);
        editEmail.setEnabled(enabled);

        if (enabled) {
            editName.requestFocus();
        }
        else {
            editCell.setInputType(InputType.TYPE_NULL);
            editHome.setInputType(InputType.TYPE_NULL);
        }

    }

    @Override
    public void didFinishDatePickerDialog(Calendar selectedTime) {
        TextView birthDay = findViewById(R.id.textBirthday);
        birthDay.setText(DateFormat.format("MM/dd/yyyy", selectedTime));
        currentContact.setBirthday(selectedTime);
    }

    private void initChangeDateButton() {
        Button changeDate = findViewById(R.id.changeButton);
        changeDate.setOnClickListener(c -> {
            FragmentManager fm = getSupportFragmentManager();
            com.example.mycontactlist.DatePickerDialog datePickerDialog = new com.example.mycontactlist.DatePickerDialog();
            datePickerDialog.show(fm, "DatePick");
        });
    }

    private void initTextChangedEvents() {
        final EditText etContactName = findViewById(R.id.contactEdit);
        etContactName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setContactName(etContactName.getText().toString());
            }
        });

        final EditText etStreetAddress = findViewById(R.id.addressEdit);
        etStreetAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setStreetAddress(etStreetAddress.getText().toString());
            }
        });

        final EditText etCity = findViewById(R.id.cityEdit);
        etCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setCity(etCity.getText().toString());
            }
        });

        final EditText etState = findViewById(R.id.stateEdit);
        etState.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setState(etState.getText().toString());
            }
        });

        final EditText etZipcode = findViewById(R.id.zipcodeEdit);
        etZipcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setZipCode(etZipcode.getText().toString());
            }
        });

        final EditText etCellPhone = findViewById(R.id.cellPhoneEdit);
        etCellPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String formattedNumber = PhoneNumberUtils.formatNumber(s.toString(), Locale.getDefault().getCountry());
                currentContact.setCellNumber(formattedNumber);
            }
        });

        final EditText etHomePhone = findViewById(R.id.homePhoneEdit);
        etHomePhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String formattedNumber = PhoneNumberUtils.formatNumber(s.toString(), Locale.getDefault().getCountry());
                currentContact.setHomePhoneNumber(formattedNumber);
            }
        });

        final EditText etEmail = findViewById(R.id.emailEdit);
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setEmail(etEmail.getText().toString());
            }
        });

    }

    private void initSaveButton() {
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                hideKeyboard();
                boolean wasSuccessful;
                ContactDataSource ds = new ContactDataSource(MainActivity.this);
                try {
                    ds.open();

                    if (currentContact.getId() == -1) {
                        wasSuccessful = ds.insertContact(currentContact);
                        int newId = ds.getLastMarketID();
                        currentContact.setId(newId);
                    } else {
                        wasSuccessful = ds.updateContact(currentContact);
                    }
                    ds.close();
                } catch (Exception e) {
                    wasSuccessful = false;
                }

                if (wasSuccessful) {
                    ToggleButton editToggle = findViewById(R.id.toggleButton);
                    editToggle.toggle();
                    setForEditing(false);
                }
            }

        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText editName = findViewById(R.id.contactEdit);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
        EditText editAddress = findViewById(R.id.addressEdit);
        imm.hideSoftInputFromWindow(editAddress.getWindowToken(), 0);
        EditText editHomePhone = findViewById(R.id.homePhoneEdit);
        imm.hideSoftInputFromWindow(editHomePhone.getWindowToken(), 0);
        EditText editCellPhone = findViewById(R.id.cellPhoneEdit);
        imm.hideSoftInputFromWindow(editCellPhone.getWindowToken(), 0);
        EditText editEmail = findViewById(R.id.emailEdit);
        imm.hideSoftInputFromWindow(editEmail.getWindowToken(), 0);
    }

    private void initCallFunction() {
        EditText editPhone = (EditText) findViewById(R.id.homePhoneEdit);
        editPhone.setOnLongClickListener(p -> {
            checkPhonePermission(currentContact.getHomePhoneNumber());
            return false;
        });

        EditText editCell = (EditText) findViewById(R.id.cellPhoneEdit);
        editCell.setOnLongClickListener(c -> {
            checkPhonePermission(currentContact.getCellNumber());
            return false;
        });
    }

    private void checkPhonePermission(String phoneNumber) {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CALL_PHONE)) {

                Snackbar.make(findViewById(R.id.main),
                                "MyContactList requires this permission to place a call from the app.",
                                Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                            @Override

                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]
                                        {Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_PHONE);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}
                        , PERMISSION_REQUEST_PHONE);
            }
        } else {
            callContact(phoneNumber);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permission[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_PHONE: {
                if  (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "You may now call from this app.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "You will not be able to make calls "
                            + "from this app", Toast.LENGTH_LONG).show();
                }
            }
            case PERMISSION_REQUEST_CAMERA: {
                if  (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                }
                else {
                    Toast.makeText(MainActivity.this, "You will not be able to save contact photos"
                            + "from this app", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void callContact (String phoneNumber) {
        Intent intent = new Intent (Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if ( Build.VERSION.SDK_INT >= 23 &&
        ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE)
        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else {
            startActivity(intent);
        }
    }

    private void initCameraButton() {
        ImageButton ib = findViewById(R.id.ProfileButton);
        ib.setOnClickListener( i -> {
            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.CAMERA)) {

                    Snackbar.make(findViewById(R.id.main),
                                    "This app needs permission to take pictures.",
                                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                @Override

                                public void onClick(View view) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]
                                            {Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                                }
                            })
                            .show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}
                            , PERMISSION_REQUEST_CAMERA);
                }
            } else {
                takePhoto();
            }
        });
    }

    private void takePhoto() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, 144, 144, true);
                ImageButton imageContact = (ImageButton) findViewById(R.id.ProfileButton);
                imageContact.setImageBitmap(scaledPhoto);
                currentContact.setPicture(scaledPhoto);
            }
        }
    }

}