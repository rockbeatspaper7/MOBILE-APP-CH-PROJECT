package com.example.mycontactlist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.map_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.map_activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initListButton();
        initMapButton();
        initSettingsButton();
    }


    protected void initListButton() {
        ImageButton imgButton = findViewById(R.id.contactIcon);
        imgButton.setOnClickListener(b -> {
            Intent intent = new Intent(MapActivity.this, ContactListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    protected void initMapButton() {
        ImageButton imgButton = findViewById(R.id.mapIcon);
        imgButton.setEnabled(false);
    }
    protected void initSettingsButton() {
        ImageButton imgButton = findViewById(R.id.settingsIcon);
        imgButton.setOnClickListener(b -> {
            Intent intent = new Intent(MapActivity.this, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

}
