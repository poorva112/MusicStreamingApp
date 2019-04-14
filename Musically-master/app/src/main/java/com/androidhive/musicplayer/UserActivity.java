package com.androidhive.musicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class UserActivity extends AppCompatActivity {

    private TextView WelUsr;
    private Button logout;

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextView accType;
    private Button changeType;

    private RadioButton free;
    private RadioButton premium;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initializeUI();

        FirebaseUser user = mAuth.getCurrentUser();

        String userEmail = user.getEmail();
        userEmail = userEmail.split("@")[0];

        /* Dislay username */
        if(user != null){
            WelUsr.setText(userEmail);
        }

        DatabaseReference userNameRef = mDatabase.child("Users").child(userEmail);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    //free
                    free.setChecked(true);
                    accType.setText("-> FREE");
                }
                else{
                    premium.setChecked(true);
                    accType.setText("-> PREMIUM");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);


        changeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accType.setText("-> " + radioButton.getText());

                FirebaseUser user = mAuth.getCurrentUser();
                String userEmail = user.getEmail();
                userEmail = userEmail.split("@")[0];


                if ( radioButton.getText().equals("Free") ){
                    mDatabase.child("Users").child(userEmail).setValue(null);

                }
                else {
                    mDatabase.child("Users").child(userEmail).child("Playlist").setValue("empty");
                    mDatabase.child("Users").child(userEmail).child("Downloads").setValue("empty");

                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserActivity.this, LogSign.class);
                startActivity(intent);
            }
        });

    }


    private void initializeUI() {
        WelUsr = (TextView) findViewById(R.id.welUsr);
        logout = (Button) findViewById(R.id.btnLogout);

        radioGroup = (RadioGroup) findViewById(R.id.radioType);
        accType = (TextView) findViewById(R.id.accType);
        changeType = (Button) findViewById(R.id.changeType);

        free = (RadioButton) findViewById(R.id.radioFree);
        premium = (RadioButton) findViewById(R.id.radioPremium);

    }

    public void checkButton(View v){
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(radioId);
//        WelUsr.setText(radioButton.getText());
        Toast.makeText(this, "Click on \"Change Account Type\" to change subscription", Toast.LENGTH_SHORT).show();
    }


}