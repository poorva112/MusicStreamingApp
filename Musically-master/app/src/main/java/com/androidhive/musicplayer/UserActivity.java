package com.androidhive.musicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserActivity extends AppCompatActivity {

    private TextView WelUsr;
    private Button logout;


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
    }


    private void initializeUI() {
        WelUsr = (TextView) findViewById(R.id.welUsr);
        logout = (Button) findViewById(R.id.btnLogout);
    }

//    private void loadUserInformation(){
//        FirebaseUser user = mAuth.getCurrentUser();
//
//        if(user != null){
//            if(user.getDisplayName() != null)
//                WelUsr.setText("Welcome "+ user.getDisplayName());
//        }
//        String displayUsr = user.getDisplayName();
//    }

}
