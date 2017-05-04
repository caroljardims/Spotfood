package com.example.caroljardims.spotfood;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GetPermissions extends AppCompatActivity {

    private Intent maps;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        maps = new Intent(this, MapsActivity.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_permissions);

        fetchFacebookData();

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startActivity(maps);
        }

    }

    public void fetchFacebookData() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        Profile profile = Profile.getCurrentProfile();

        if (profile != null) {
            final DatabaseReference ref = database.getReference("userData").child(profile.getId());
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            // Application code
                            try {
                                ref.child("name").setValue(object.getString("name"));
                                ref.child("email").setValue(object.getString("email"));
                                ref.child("gender").setValue(object.getString("gender"));
                                ref.child("birthday").setValue(object.getString("birthday"));
                                ref.child("link").setValue(object.getString("link"));
                            } catch (JSONException e) {
                                sendToast("no1");
                            }

                            try{
                                ref.child("location").setValue(object.getString("location"));
                                ref.child("education").setValue(object.getString("education"));
                                ref.child("work").setValue(object.getString("work"));
                            }catch (JSONException e){
                                sendToast("no2");
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,gender,location,birthday,education,work,email");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    public void getPermission(View v){
        Button permission = (Button) findViewById(R.id.getPermission);
        if (v.getId() == R.id.getPermission) {
//
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                startActivity(maps);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                permission.setText("ir pro mapa! :D");
            }
        }
    }

    void sendToast(String message){
        Context c = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
    }
}
