package com.androidhive.musicplayer;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

//The activity class view database is an extension of the maain class acivity. This class checks the logged in user and displays the respective playlist
public class View_Database extends Activity {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    private DatabaseReference mDatabase;
    private String userID;
    private ArrayAdapter adapter;
    private Context context;
    private ArrayList<String> list=new ArrayList<String>();
    private ListView mListView;
    //pls add to git
    // onCreate initialises the function if its being executed first time 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__database);
        mListView = findViewById(R.id.listview);
        Log.d("lisview",mListView.toString());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(); 
        FirebaseUser user = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        if(user != null){
            Log.d("email",user.getEmail());
        }
        String userEmail = user.getEmail();
        userEmail = userEmail.split("@")[0]; //the name before the @ consists of userid
        DatabaseReference userNameRef = mDatabase.child("Users").child(userEmail); // the schema has Users has the parent

        DatabaseReference playlist=userNameRef.child("Playlist"); // Users has Playlist as one of its child

        playlist.addValueEventListener(new ValueEventListener() {
            //@Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot); // data snapshot is an inbuilt class which extracts all the data from the database
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }
    private void showData(DataSnapshot dataSnapshot) {
        //HashMap<String,String> s;
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            //s=new HashMap<String,String>();

            Log.d("play", ds.getValue().toString());
           // s.put("songTitle",ds.getValue().toString());
            //s.put("songPath","meh");
            //list.add(s);
            list.add(ds.getValue().toString());

        }

        adapter = new ArrayAdapter<String>(this,R.layout.simple_list_item_1,list); //ArrayAdapter acts like an intermediate for arraylist data source and view of a list
        mListView.setAdapter(adapter);
    }

}
