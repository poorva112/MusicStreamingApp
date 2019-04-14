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

public class UserActivity extends AppCompatActivity {

    private TextView WelUsr;
    private Button logout;

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextView accType;
    private Button changeType;

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


        /* Dislay username */
        if(user != null){
                WelUsr.setText(user.getEmail());
        }



        changeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accType.setText("-> "+radioButton.getText());

                FirebaseUser user = mAuth.getCurrentUser();
                String userEmail = user.getEmail();
                userEmail = userEmail.split("@")[0];

                //update to firebase database

                //access to firebase database
//                mDatabase.child("Users").child(userEmail).setValue("empty");
//
                if(radioButton.getText() == "free")
                    Toast.makeText(UserActivity.this, "Delete Users", Toast.LENGTH_SHORT).show();
                else
//                    //add premium users to database
                    mDatabase.child("Users").child(userEmail).child("Playlist").setValue("empty");


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


    }

    public void checkButton(View v){
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(radioId);
        Toast.makeText(this, "Click on \"Change Account Type\" to change subscription", Toast.LENGTH_SHORT).show();
    }


//    public void addPremiumUser(){
//
//    }
}
