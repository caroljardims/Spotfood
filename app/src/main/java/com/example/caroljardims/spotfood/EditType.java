package com.example.caroljardims.spotfood;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by caroljardims on 12/05/17.
 */

public class EditType extends DialogFragment implements View.OnClickListener {
    private View view;
    private String id = "";
    private String name = "";
    private Button editButton;
    private EditText editTypeText;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference refDB = database.getReference("Locations").child("local");
    private SpotfoodLocation spotfood;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        super.onCreate(savedInstance);
        view = inflater.inflate(R.layout.edit_type_fragment, null);

        id = getArguments().getString("id");
        name = getArguments().getString("name");

        editButton = (Button) view.findViewById(R.id.editButton);
        editButton.setOnClickListener(this);
        editTypeText = (EditText) view.findViewById(R.id.editTypeText);

        refDB.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                spotfood = dataSnapshot.getValue(SpotfoodLocation.class);
                if(spotfood.getType() != null || !spotfood.getType().isEmpty()){
                    editTypeText.setText(spotfood.getType());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editButton:
                String newType = editTypeText.getText().toString();
                refDB.child(id).child("type").setValue(newType);
                sendToast("Edição enviada com sucesso!");
                getDialog().dismiss();

                break;
        }
    }

    void sendToast(String message){
        Context c = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(c, message, duration);
        toast.show();
    }
}
