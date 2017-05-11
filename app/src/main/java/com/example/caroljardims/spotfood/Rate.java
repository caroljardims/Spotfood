package com.example.caroljardims.spotfood;

import android.app.DialogFragment;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Rate extends DialogFragment implements View.OnClickListener {
    private View view;
    private Button rateButton;
    private ImageButton star1;
    private ImageButton star2;
    private ImageButton star3;
    private ImageButton star4;
    private ImageButton star5;
    private SpotfoodLocation spotfood;
    private String id = "";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference refDB = database.getReference("Locations").child("local");
    private Double currentRate;
    private Double newRate;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        super.onCreate(savedInstance);
        view = inflater.inflate(R.layout.fragment_rate,null);

        id = getArguments().getString("id");

        rateButton = (Button) view.findViewById(R.id.sendRate);
        rateButton.setOnClickListener(this);
        star1 = (ImageButton) view.findViewById(R.id.star1);
        star1.setOnClickListener(this);
        star2 = (ImageButton) view.findViewById(R.id.star2);
        star2.setOnClickListener(this);
        star3 = (ImageButton) view.findViewById(R.id.star3);
        star3.setOnClickListener(this);
        star4 = (ImageButton) view.findViewById(R.id.star4);
        star4.setOnClickListener(this);
        star5 = (ImageButton) view.findViewById(R.id.star5);
        star5.setOnClickListener(this);

        refDB.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                spotfood = dataSnapshot.getValue(SpotfoodLocation.class);
                if(spotfood.getRate() != null)
                    currentRate = Double.valueOf(spotfood.getRate());
                else
                    currentRate = 0.0;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendRate:
                if(newRate == null) newRate = 0.0;
                refDB.child(id).child("rate").setValue(String.valueOf(newRate));
                getDialog().dismiss();
                break;
            case R.id.star1:
                cleanStars();
                star1.setImageResource(R.drawable.golden_star);

                Double visitors = (double) spotfood.getVisitors();
                if(currentRate > 0.0)
                    newRate = ((visitors - 1.0)*currentRate + 1.0) / (visitors);
                else
                    newRate = 1.0;

                break;
            case R.id.star2:
                cleanStars();
                star1.setImageResource(R.drawable.golden_star);
                star2.setImageResource(R.drawable.golden_star);

                visitors = (double) spotfood.getVisitors();
                if(currentRate > 0.0)
                    newRate = ((visitors - 1.0)*currentRate + 2.0) / (visitors);
                else
                    newRate = 2.0;

                break;
            case R.id.star3:
                cleanStars();
                star1.setImageResource(R.drawable.golden_star);
                star2.setImageResource(R.drawable.golden_star);
                star3.setImageResource(R.drawable.golden_star);

                visitors = (double) spotfood.getVisitors();
                if(currentRate > 0.0)
                    newRate = ((visitors - 1.0)*currentRate + 3.0) / (visitors);
                else
                    newRate = 3.0;

                break;
            case R.id.star4:
                cleanStars();
                star1.setImageResource(R.drawable.golden_star);
                star2.setImageResource(R.drawable.golden_star);
                star3.setImageResource(R.drawable.golden_star);
                star4.setImageResource(R.drawable.golden_star);

                visitors = (double) spotfood.getVisitors();
                if(currentRate > 0.0)
                    newRate = ((visitors - 1.0)*currentRate + 4.0) / (visitors);
                else
                    newRate = 4.0;

                break;

            case R.id.star5:
                cleanStars();
                star1.setImageResource(R.drawable.golden_star);
                star2.setImageResource(R.drawable.golden_star);
                star3.setImageResource(R.drawable.golden_star);
                star4.setImageResource(R.drawable.golden_star);
                star5.setImageResource(R.drawable.golden_star);

                visitors = (double) spotfood.getVisitors();
                if(currentRate > 0.0)
                    newRate = ((visitors - 1.0)*currentRate + 5.0) / (visitors);
                else
                    newRate = 5.0;

                break;
        }
    }

    private void cleanStars(){
        star1.setImageResource(R.drawable.empty_star);
        star2.setImageResource(R.drawable.empty_star);
        star3.setImageResource(R.drawable.empty_star);
        star4.setImageResource(R.drawable.empty_star);
        star5.setImageResource(R.drawable.empty_star);
    }
}
