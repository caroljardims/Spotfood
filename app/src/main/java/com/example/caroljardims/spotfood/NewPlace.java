package com.example.caroljardims.spotfood;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class NewPlace extends AppCompatActivity {

    private String lat;
    private String lon;
    private EditText name;
    private EditText type;
    private Switch status;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_place);

        name = (EditText) findViewById(R.id.place_name);
        type = (EditText) findViewById(R.id.place_type);
        status = (Switch) findViewById(R.id.place_status);

        lat = getIntent().getStringExtra("latitude");
        lon = getIntent().getStringExtra("longitude");
    }

    public void addNewPlace(View v){
        Intent maps = new Intent(this, MainActivity.class);
        final DatabaseReference ref = database.getReference("Locations").child("local");
        SpotfoodLocation place = new SpotfoodLocation();

        String key = UUID.randomUUID().toString();

        if(name.getText().toString().isEmpty()){
            sendToast("Ops, faltou o nome!");
        } else {
            place.setName(name.getText().toString());
            place.setLat(lat);
            place.setLon(lon);
            place.setLogo(" - ");
            if(status.isChecked()){
                place.setStatus(1);
            } else {
                place.setStatus(0);
            }
            place.setType(type.getText().toString());
            place.setId(key);

            ref.child(key).setValue(place);
            startActivity(maps);
        }
    }

    void sendToast(String message){
        Context c = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
    }
}
