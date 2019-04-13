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

public class UserActivity extends AppCompatActivity {

    private TextView WelUsr;
    private Button logout;

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextView accType;
    private Button changeType;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();

        initializeUI();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
//            if(user.getDisplayName() != null)
                WelUsr.setText(user.getEmail());
        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserActivity.this, LogSign.class);
                startActivity(intent);
            }
        });

        changeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accType.setText("-> "+radioButton.getText());

                //update to firebase database

                //access to firebase database

                if(radioButton.getText() == "free"){
                    //delete
                }
                else{
                    //add premium users to database
                }

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


}
