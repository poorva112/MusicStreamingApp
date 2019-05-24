package com.androidhive.musicplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DisplaySongs extends Activity {
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
//
    private DatabaseReference mDatabase;
    private String userID;
    private ArrayAdapter adapter;
    private Context context;
    private ArrayList<String> list=new ArrayList<String>();
    private ListView mListView;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_songs);
        String PlaylistName = getIntent().getStringExtra("PlaylistName");
        mListView = findViewById(R.id.songslist);
        title=findViewById(R.id.playlistname);
        title.setText(PlaylistName);
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
        final DatabaseReference userNameRef = mDatabase.child("Users").child(userEmail); // the schema has Users has the parent
        DatabaseReference playlist=userNameRef.child("Playlist").child(PlaylistName); // Users has Playlist as one of its child
        Log.d("playlistname",PlaylistName);
        Log.d("ref",playlist.toString());
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
        list.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            list.add(titlecase(ds.child("Song").getValue().toString())+"|"+titlecase(ds.child("Album").getValue().toString()));

        }
        if(list.isEmpty())
        {
            list.add("No songs yet");
        }
        Log.d("listofsongs",list.toString());
        adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.simple_list_item_1,list); //ArrayAdapter acts like an intermediate for arraylist data source and view of a list
        mListView.setAdapter(adapter);
    }
    public String titlecase(String inp)
    {
        String[] words = inp.split(" ");
        StringBuilder sb = new StringBuilder();
        if (words[0].length() > 0) {
            sb.append(Character.toUpperCase(words[0].charAt(0)) + words[0].subSequence(1, words[0].length()).toString().toLowerCase());
            for (int i = 1; i < words.length; i++) {
                sb.append(" ");
                sb.append(Character.toUpperCase(words[i].charAt(0)) + words[i].subSequence(1, words[i].length()).toString().toLowerCase());
            }
        }
        return sb.toString();
    }
}
