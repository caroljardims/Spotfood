package com.example.caroljardims.spotfood;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    List<SpotfoodLocation> local = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(!isOnline()){
            sendToast("Não encontramos conexão com a Internet.");
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_menu, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(this);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            mMap.setMyLocationEnabled(true);
        }

        updateLocations();
    }

    public void updateLocations() {
        DatabaseReference ref = database.getReference("Locations").child("local");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                    SpotfoodLocation location = postData.getValue(SpotfoodLocation.class);
                    local.add(location);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(location.getLat()), Double.parseDouble(location.getLon())))
                            .title(location.getName())
                            .draggable(true));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void updateMap(View v) {
//        Button updateMapButton = (Button) findViewById(R.id.update_map);
//        if(v.getId() == R.id.update_map) { }
        Intent maps = new Intent(this, MapsActivity.class);
        startActivity(maps);
    }

    public void addNew(View v){
        Intent addNew = new Intent(this, NewPlace.class);
        Double lat = mMap.getMyLocation().getLatitude();
        Double lon = mMap.getMyLocation().getLongitude();
        addNew.putExtra("latitude", String.valueOf(lat));
        addNew.putExtra("longitude", String.valueOf(lon));
        startActivity(addNew);
    }

    public void getData(View v) {
        final Button testButton = (Button) findViewById(R.id.testButton2);
        if(v.getId() == R.id.testButton2) {
            DatabaseReference ref = database.getReference("test");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);
                    testButton.setText(value);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                }
            });
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    void sendToast(String message){
        Context c = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(final Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        final DatabaseReference ref = database.getReference("Locations").child("local");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                    SpotfoodLocation location = postData.getValue(SpotfoodLocation.class);
                    if(location.getName().equals(marker.getTitle())){
                        location.setLat(String.valueOf(marker.getPosition().latitude));
                        location.setLon(String.valueOf(marker.getPosition().longitude));
                        String key = postData.getKey();
                        ref.child(key).setValue(location);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
