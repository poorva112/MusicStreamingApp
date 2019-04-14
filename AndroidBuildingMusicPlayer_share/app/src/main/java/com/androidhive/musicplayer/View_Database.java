package com.androidhive.musicplayer;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



public class View_Database extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private  String userID;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("inside","inside");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view__database);
        //Log.d("insidehi","insidehi");
        mListView = (ListView) findViewById(R.id.listview);

        Log.d("insidehi1","insidehi1");
        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        //userID = user.getUid();
        Log.d("inside2","inside2");
        /*mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
                // ...
            }
        };*/

        myRef.addValueEventListener(new ValueEventListener() {
            //@Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("inside3","inside3");
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Log.d("inside4","inside4");
            Myplaylist uInfo = new Myplaylist();
            uInfo.setName(ds.child("/Users/poorvatiwari112/Playlist").getValue(Myplaylist.class).getName()); //set the name
            // uInfo.setEmail(ds.child(userID).getValue(Myplaylist.class).getEmail()); //set the email
            //uInfo.setPhone_num(ds.child(userID).getValue(Myplaylist.class).getPhone_num()); //set the phone_num

            //display all the information
            //Log.d(TAG, "showData: name: " + uInfo.getName());
            ;

            ArrayList<String> array = new ArrayList<>();
            array.add(uInfo.getName());

            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
            mListView.setAdapter(adapter);
        }
    }
}



