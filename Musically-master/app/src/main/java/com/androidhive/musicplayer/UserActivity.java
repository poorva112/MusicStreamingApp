package com.androidhive.musicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.LayoutInflater;

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
//
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ImageButton addtoPlaylist;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initializeUI();

        FirebaseUser user = mAuth.getCurrentUser();     //get current User

        String userEmail = user.getEmail();
        final String username = userEmail.split("@")[0];

        //Username is assumed to be part of email ID
        //final keyword makes it accessible to to other functions within this scope. For ex: changeType.setOnClickListener

        /* Dislay username */
        if(user != null){
            WelUsr.setText(username);
        }


        /*Checking if User Data exists in Users Reference in Firebase Storage or not
            and accordingly decide if a current user is a premium or free user.
        */
        DatabaseReference userNameRef = mDatabase.child("Users").child(username).child("Role");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getValue().equals("free")) {
                    //user data doesnt exist  -> free user
                    //Display the same on Users page
                    free.setChecked(true);
                    accType.setText("-> FREE");

                }
                else if(dataSnapshot.exists() && dataSnapshot.getValue().equals("premium")){
                    //user data exists -> premium user
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
                //Changing account type based on the radio button selected.

                accType.setText("-> " + radioButton.getText());

                if ( radioButton.getText().equals("Free") ){
                    mDatabase.child("Users").child(username).child("Role").setValue("free");


                }
                else {
                    mDatabase.child("Users").child(username).child("Role").setValue("premium");
                }
            }
        });

        //Logging out
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

    //Check which radio button is selected
    public void checkButton(View v){
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(radioId);
        Toast.makeText(this, "Click on \"Change Account Type\" to change subscription", Toast.LENGTH_SHORT).show();
    }


}