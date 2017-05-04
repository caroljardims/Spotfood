package com.example.caroljardims.spotfood;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class UserProfile extends AppCompatActivity {

    Profile profile = Profile.getCurrentProfile();
    TextView userName;
    ImageView profilePicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName = (TextView) findViewById(R.id.userName);
        userName.setText("Olá, "+profile.getFirstName()+" :)");
        Uri uri = profile.getProfilePictureUri(70,70);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
        Picasso.with(this).load(uri).into(profilePicture);
    }

    public void logout(View view) {
        LoginManager.getInstance().logOut();
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
    }

    void sendToast(String message){
        Context c = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
    }
}
