package com.example.mycontactlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {
    ArrayList<Contact> contacts;
    private View.OnClickListener onItemClickListener = view -> {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder)view.getTag();
        int position = viewHolder.getAdapterPosition();
        long contactID = contacts.get(position).getId();
        Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
        intent.putExtra("contactID", contactID);
        startActivity(intent);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.list_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.list_activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initListButton();
        initMapButton();
        initSettingsButton();


        ContactDataSource ds = new ContactDataSource(this);

        try {
            ds.open();
            Log.d("Test!", "Database opened successfully");
            contacts = ds.getContacts();
            ds.close();
            Log.d("Test!", "Contacts retrieved: " + contacts.size());
            Log.d("Test!", "Contacts size: " + contacts.size());

            if (contacts.isEmpty()) {
                Toast.makeText(this, "No contacts found", Toast.LENGTH_LONG).show();
            }

            RecyclerView contactList = findViewById(R.id.rvContacts);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            contactList.setLayoutManager(layoutManager);
            ContactAdapter contactAdapter = new ContactAdapter(contacts);
            contactList.setAdapter(contactAdapter);

            contactAdapter.setmOnItemClickListener(onItemClickListener);
        }
        catch (Exception e) {
            Toast.makeText(this, "Error receiving contacts", Toast.LENGTH_LONG).show();
        }

    }

    protected void initListButton() {
        ImageButton imgButton = findViewById(R.id.contactIcon);
        imgButton.setEnabled(false);
    }

    protected void initMapButton() {
        ImageButton imgButton = findViewById(R.id.mapIcon);
        imgButton.setOnClickListener(b -> {
            Intent intent = new Intent(ContactListActivity.this, MapActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
    protected void initSettingsButton() {
        ImageButton imgButton = findViewById(R.id.settingsIcon);
        imgButton.setOnClickListener(b -> {
            Intent intent = new Intent(ContactListActivity.this, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
}