package com.example.mycontactlist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener gpsListener;

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
        initGetLocationButton();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            locationManager.removeUpdates(gpsListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void initGetLocationButton() {
        Button locationButton = findViewById(R.id.buttonGetLocation);
        locationButton.setOnClickListener( l -> {

            EditText editAddress = findViewById(R.id.addressEdit2);
            EditText editCity = findViewById(R.id.cityEdit2);
            EditText editState = findViewById(R.id.stateEdit2);
            EditText editZipcode = findViewById(R.id.zipcodeEdit2);

            String address = editAddress.getText().toString() + ", " +
                    editCity.getText().toString() + ", " +
                    editState.getText().toString() + ", " +
                    editZipcode.getText().toString();

            List<Address> addresses = null;
            Geocoder geo = new Geocoder(MapActivity.this);
            try {
                addresses = geo.getFromLocationName(address, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            TextView txtLatitude = findViewById(R.id.textLatitude);
            TextView txtLongitude = findViewById(R.id.textLongitude);

            if (addresses != null && !addresses.isEmpty()) {
                txtLatitude.setText(String.valueOf(addresses.get(0).getLatitude()));
                txtLongitude.setText(String.valueOf(addresses.get(0).getLongitude()));
            } else {
                txtLatitude.setText("Not found");
                txtLongitude.setText("Not found");
                Log.e("MapActivity", "No location found for address: " + address);
            }
        });


            /*
            try {
                locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
                gpsListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        TextView txtLatitude = findViewById(R.id.textLatitude);
                        TextView txtLongitude = findViewById(R.id.textLongitude);
                        TextView txtAccuracy = findViewById(R.id.textAccuracy);

                        txtLatitude.setText(String.valueOf(location.getLatitude()));
                        txtLongitude.setText(String.valueOf(location.getLongitude()));
                        txtAccuracy.setText(String.valueOf(location.getAccuracy()));
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {}
                    public void onProviderEnabled(String provider) {}
                    public void onProviderDisabled(String provider) {}
                };

                LocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Error, Location not Available", Toast.LENGTH_LONG).show();
            }

        });
    }

    private void startLocationUpdate() {
        if (Build.VERSION.SDK_INT >= 23 && v
        ContextCompat.checkSelfPermission(getBaseContext(),
        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

             */
    }

}
