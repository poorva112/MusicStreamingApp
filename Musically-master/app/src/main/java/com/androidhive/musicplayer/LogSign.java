package com.androidhive.musicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogSign extends AppCompatActivity {

    Button registerBtn, loginBtn;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_sign);

        initializeViews();
//
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogSign.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogSign.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        //checking if user is logged in or not
        //Avoids unnecessarily logging in again and again
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            Toast.makeText(getApplicationContext(), "Logged in as '" +user.getEmail()+ "'", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LogSign.this, AndroidBuildingMusicPlayerActivity.class);
            startActivity(intent);
        }

    }

    private void initializeViews() {
        registerBtn = (Button) findViewById(R.id.btnRegister);
        loginBtn = (Button) findViewById(R.id.btnLogin);
    }
}