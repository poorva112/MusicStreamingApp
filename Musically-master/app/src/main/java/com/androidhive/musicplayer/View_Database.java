package com.androidhive.musicplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
    private Button CreatePlaylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__database);
        mListView = findViewById(R.id.listview);
        CreatePlaylist=findViewById(R.id.createPlaylist);
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
        final DatabaseReference userNameRef = mDatabase.child("Users").child(userEmail); // the schema has Users has the parent
//
        CreatePlaylist.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                playlistPrompt(userNameRef);


            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // getting listitem index
                int songIndex = position;

                // Starting new intent
                Intent in = new Intent(getApplicationContext(), DisplaySongs.class);
                String PlaylistName = mListView.getItemAtPosition(position).toString();
                in.putExtra("PlaylistName",PlaylistName);
                //setResult(100, in);
                startActivity(in);
            }
        });
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
        list.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            list.add(ds.getKey().toString());
            //list.add(ds.child("Song").getValue().toString()+"|"+ds.child("Album").getValue().toString());

        }

        adapter = new ArrayAdapter<String>(this,R.layout.simple_list_item_1,list); //ArrayAdapter acts like an intermediate for arraylist data source and view of a list
        mListView.setAdapter(adapter);
    }

    private void playlistPrompt(final DatabaseReference userNameRef) {


        View view = (LayoutInflater.from(View_Database.this)).inflate(R.layout.alertplaylist, null);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(View_Database.this);

        final EditText userInputPlaylist = (EditText) view.findViewById(R.id.userInputPlaylist);  //from alertPrompt.xml


        alertBuilder.setView(view)
                .setTitle("Enter Playlist Name")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("CREATE NEW PLAYLIST", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String playlistName = userInputPlaylist.getText().toString();


                        userNameRef.child("Playlist").child(playlistName).setValue("empty");
                        Log.d("ref",userNameRef.toString());
                        Log.d("refplay",userNameRef.child(playlistName).toString());

                        Toast.makeText(View_Database.this, "Playlist created", Toast.LENGTH_SHORT).show();

                    }
                });

        Dialog dialog = alertBuilder.create();
        dialog.show();

    }

}
