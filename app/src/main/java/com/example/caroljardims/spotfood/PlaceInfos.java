package com.example.caroljardims.spotfood;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

public class PlaceInfos extends AppCompatActivity {

    private String id;
    private String name;
    private SpotfoodLocation infos;
    private int photoIndex = 0;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference refDB = database.getReference("Locations").child("local");
    private DatabaseReference refUser = database.getReference("userData");
    private StorageReference refStorage = storage.getReference("Locations").child("Locations");
    private Profile profile = Profile.getCurrentProfile();
    private ArrayList<Photos> photos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_infos);

        id = getIntent().getStringExtra("place_id");
        name = getIntent().getStringExtra("place_name");

        getInfosFromDB();

        TextView nameInfo = (TextView) findViewById(R.id.nameInfo);
        nameInfo.setText(name);
        setTitle(name);
    }

    protected void getInfosFromDB(){
        refDB.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                infos = dataSnapshot.getValue(SpotfoodLocation.class);
                for(DataSnapshot data : dataSnapshot.child("photos").getChildren()){
                    Photos p = data.getValue(Photos.class);
//                    sendToast(p.getUrl());
                    photos.add(p);
                }
                updateDataInfos(infos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    protected void updateDataInfos(SpotfoodLocation data){
        TextView nameInfo = (TextView) findViewById(R.id.nameInfo);
        TextView typeInfo = (TextView) findViewById(R.id.typeInfo);
        final TextView statusInfo = (TextView) findViewById(R.id.statusInfo);
        TextView rateInfo = (TextView) findViewById(R.id.rateInfo);
        Switch statusSwitch = (Switch) findViewById(R.id.statusInfoSwitch);
        ImageView imageInfo = (ImageView) findViewById(R.id.imageInfo);
        ImageView placePhotos = (ImageView) findViewById(R.id.placePhotos);
        nameInfo.setText(data.getName());
        typeInfo.setText(data.getType());
        rateInfo.setText(data.getRate());
        int status = data.getStatus();
        if(status==1){
            statusSwitch.setChecked(true);
            isNowOpen(statusInfo);
        }
        else {
            statusSwitch.setChecked(false);
            isNowClosed(statusInfo);
        }
        statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && infos.getStatus()!=1){
                    isNowOpen(statusInfo);
                    infos.setStatus(1);
                    refDB.child(id).child("status").setValue(1);
                }
                if(!isChecked && infos.getStatus()!=0){
                    isNowClosed(statusInfo);
                    infos.setStatus(0);
                    refDB.child(id).child("status").setValue(0);
                }
            }
        });

        if(!infos.getLogo().isEmpty())
            Picasso.with(this).load(infos.getLogo()).into(imageInfo);

        if(!photos.isEmpty())
            Picasso.with(this).load(photos.get(0).getUrl()).into(placePhotos);

    }

    protected void isNowOpen(TextView statusInfo){
        statusInfo.setTextColor(Color.parseColor("#0CC14F"));
        statusInfo.setText("• Aberto agora");
    }

    protected void isNowClosed(TextView statusInfo){
        statusInfo.setTextColor(Color.parseColor("#C10C43"));
        statusInfo.setText("• Fechado");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void checkIn(View v){
        Button checkInButton = (Button) findViewById(R.id.checkInButton);
        ImageButton cameraButton = (ImageButton) findViewById(R.id.getImageLogo);
//        checkInButton.setTextColor(Color.WHITE);
        if(infos.getStatus() == 1) {
            String smile = "\ud83d\ude03";
            checkInButton.setText(smile);
//            checkInButton.setBackgroundColor(Color.parseColor("#099a3f"));
            sendToast("Você está aqui!");
            infos.setVisitors();
            refDB.child(id).child("visitors").setValue(infos.getVisitors());
            if(profile != null){
                Calendar c = Calendar.getInstance();
                refUser.child(profile.getId())
                        .child("checkedIn")
                        .child(c.getTime().toString())
                        .child("id")
                        .setValue(infos.getId());
                refUser.child(profile.getId())
                        .child("checkedIn")
                        .child(c.getTime().toString())
                        .child("name")
                        .setValue(infos.getName());
            }
            cameraButton.setVisibility(1);
        } else {
            String cry = "\uD83D\uDE12";
            checkInButton.setText(cry);
            String msg = infos.getName() + " se encontra fechado no momento...";
            sendToast(msg);
        }
    }

    void sendToast(String message){
        Context c = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
    }

    public void logoFileSelector(View view) {
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);
    }

    public void addPhoto(View view) {
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ImageView imageInfo = (ImageView) findViewById(R.id.imageInfo);
            Uri selectedImage = data.getData();
            imageInfo.setImageURI(selectedImage);
            UploadTask uploadTask = refStorage.child(id).child(id).putFile(selectedImage);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String urlToDownload = taskSnapshot.getDownloadUrl().toString();
                    refDB.child(id).child("logo").setValue(urlToDownload);
                }
            });
        }

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String key = UUID.randomUUID().toString();
            UploadTask uploadTask = refStorage.child(id).child(id)
                    .child(key).putFile(selectedImage);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String urlToDownload = taskSnapshot.getDownloadUrl().toString();
                    String key = UUID.randomUUID().toString();
                    refDB.child(id).child("photos").child(key)
                            .child("url").setValue(urlToDownload);
                    sendToast("Foto enviada com sucesso!");
                    getInfosFromDB();
                }
            });

        }
    }

    public void gotoNextPhoto(View view) {
        ImageView placePhotos = (ImageView) findViewById(R.id.placePhotos);
        if(photoIndex < photos.size()-1){
            photoIndex++;
        } else {
            photoIndex = 0;
        }
        Picasso.with(this).load(photos.get(photoIndex).getUrl()).into(placePhotos);
    }
}