package com.example.mycontactlist;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{
    final int PERMISSION_REQUEST_LOCATION = 101;
    GoogleMap gMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    ArrayList<Contact> contacts = new ArrayList<>();
    Contact currentContact = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.map_activity);
        Bundle extras = getIntent().getExtras();
        try {
            ContactDataSource ds = new ContactDataSource(MapActivity.this);
            ds.open();
            if (extras != null) {
                currentContact = ds.getSpecificContacts(extras.getInt("contactID"));
            }
            else {
                contacts = ds.getContacts("contactname", "ASC");
            }
            ds.close();
        }
        catch (Exception e ) {
            Toast.makeText(this, "Contact(s) could not be retrieved.", Toast.LENGTH_LONG).show();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.map_activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        createLocationRequest();
        createLocationCallback();

        initListButton();
        initMapButton();
        initSettingsButton();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &
                        ContextCompat.checkSelfPermission(getBaseContext(),
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            //locationManager.removeUpdates(gpsListener);
            //locationManager.removeUpdates(networkListener);
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

    private void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return ;
                }
                for (Location location : locationResult.getLocations()) {
                    Toast.makeText(getBaseContext(), "Lat: " + location.getLatitude() +
                            " Long: " + location.getLongitude() + " Accuracy: " +
                            location.getAccuracy(), Toast.LENGTH_LONG).show();
                }
            }
        };
    }



    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 &&
        ContextCompat.checkSelfPermission(getBaseContext(),
        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &
                ContextCompat.checkSelfPermission(getBaseContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        gMap.setMyLocationEnabled(true);

    }

    private void stopLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &
                        ContextCompat.checkSelfPermission(getBaseContext(),
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLocationUpdates();

                } else {
                    Toast.makeText(MapActivity.this, "MyContactList will not locate your contacts.",
                    Toast.LENGTH_LONG).show();
                }

            }
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Point size = new Point();
        WindowManager w = getWindowManager();
        w.getDefaultDisplay().getSize(size);
        int measuredWidth = size.x;
        int measuredHeight = size.y;

        if (contacts.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < contacts.size(); i++) {
                currentContact = contacts.get(i);

                Geocoder geo = new Geocoder(this);
                List<Address> addresses = null;

                String address = currentContact.getStreetAddress() + ", " +
                        currentContact.getCity() + ", " +
                        currentContact.getState() + ", " +
                        currentContact.getZipCode();

                try {
                    addresses = geo.getFromLocationName(address, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LatLng point = new
                        LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

                builder.include(point);

                gMap.addMarker(new MarkerOptions().position(point).
                        title(currentContact.getContactName()).snippet(address));
            }
            gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),
                    measuredWidth, measuredHeight, 450));
        }
        else {
            if (currentContact != null) {
                Geocoder geo = new Geocoder(this);
                List<Address> addresses = null;

                String address = currentContact.getStreetAddress() + ", " +
                        currentContact.getCity() + ", " +
                        currentContact.getState() + ", " +
                        currentContact.getZipCode();
                try {
                    addresses = geo.getFromLocationName(address, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LatLng point = new
                        LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

                gMap.addMarker(new MarkerOptions().position(point).
                        title(currentContact.getContactName()).snippet(address));
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
            }
            else {
                AlertDialog alertDialog = new AlertDialog.Builder (MapActivity.this).create();
                alertDialog.setTitle("No Data");
                alertDialog.setMessage("No data is available for the mapping function.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new Dialog);
            }
        }
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(MapActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                        Snackbar.make(findViewById(R.id.map_activity_main),
                                        "MyContactList requires this permission to locate " +
                                                "your contacts", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        ActivityCompat.requestPermissions(
                                                MapActivity.this,
                                                new String[]{
                                                        android.Manifest.permission.ACCESS_FINE_LOCATION},
                                                PERMISSION_REQUEST_LOCATION);
                                    }
                                })
                                .show();
                    } else {
                        ActivityCompat.requestPermissions(MapActivity.this, new
                                        String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_REQUEST_LOCATION);
                    }
                } else {
                    startLocationUpdates();
                }
            } else {
                startLocationUpdates();
            }
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error requesting permissions", Toast.LENGTH_LONG). show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
