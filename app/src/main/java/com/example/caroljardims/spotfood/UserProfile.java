package com.example.caroljardims.spotfood;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StreamDownloadTask;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;

public class UserProfile extends AppCompatActivity {

    Profile profile = Profile.getCurrentProfile();
    TextView userName;
    TextView countCheckIns;
    TextView visitedPlaces;
    ImageView profilePicture;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference refUser = database.getReference("userData");
    private ArrayList<CheckInData> checkInDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName = (TextView) findViewById(R.id.userName);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);

        countCheckIns = (TextView) findViewById(R.id.countCheckIns);
        visitedPlaces = (TextView) findViewById(R.id.visitedPlaces);

        if(profile!=null){
            userName.setText("Olá, "+profile.getFirstName()+" :)");
            Uri uri = profile.getProfilePictureUri(70,70);
            Picasso.with(this).load(uri).into(profilePicture);
            refUser.child(profile.getId()).child("checkedIn").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        CheckInData checkInData = data.getValue(CheckInData.class);
                        checkInDatas.add(checkInData);
                    }
                    String count = String.valueOf(checkInDatas.size());
                    countCheckIns.setText("Você já visitou "+count+" locais!");
                    countCheckIns.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    String visits = "Últimas visitas:\n";
                    visitedPlaces.setText(checkInDatas.get(0).getName());
                    int size = checkInDatas.size()-1;
                    if(size >= 0) {
                        for (int i = 0; i < size+1; i++) {
                            visits = visits + "• " + checkInDatas.get(size-i).getName() + "\n";
                        }
                        visitedPlaces.setText(visits);
                    } else {
                        visitedPlaces.setText("Você ainda não tem nenhum check-in :(");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
        }
    }

    public void logout(View view) {
        if(profile!=null) {
            LoginManager.getInstance().logOut();
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
        }
    }

    void sendToast(String message){
        Context c = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
    }
}