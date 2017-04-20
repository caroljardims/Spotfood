package com.example.caroljardims.spotfood;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

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

//        for (SpotfoodLocation s : local) {
//            sendToast(s.getName());
//            mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(Double.parseDouble(s.getLat()), Double.parseDouble(s.getLon())))
//                    .title(s.getName()));
//        }

//        Profile profile = Profile.getCurrentProfile();
//        if (profile != null) {
//            String text = "Welcome, " + profile.getFirstName() + "!";
//            LoginManager.getInstance().logOut();
//        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_menu, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(-29.688011, -53.822259))
                .title("Hello world"));

        updateLocations();
    }

    public void updateLocations(){
        DatabaseReference ref = database.getReference("Locations");
        ref.child("local").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                    SpotfoodLocation location = postData.getValue(SpotfoodLocation.class);
                    local.add(location);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(location.getLat()), Double.parseDouble(location.getLon())))
                            .title(location.getName()));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void sendData(View v) {
        Button testButton = (Button) findViewById(R.id.testButton);
        if(v.getId() == R.id.testButton) {
            DatabaseReference ref = database.getReference("test");
            ref.setValue("works <3");
            testButton.setText("works");
        }
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


    void sendToast(String message){
        Context c = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
    }

}
