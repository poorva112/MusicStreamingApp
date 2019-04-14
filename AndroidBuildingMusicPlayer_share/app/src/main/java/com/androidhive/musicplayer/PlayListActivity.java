package com.androidhive.musicplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.net.Uri;


import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;

public class PlayListActivity extends ListActivity
{
	// Songs list
	public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	private MediaPlayer mMediaplayer;
	private FirebaseAuth mAuth;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//Log.d("entered", "signInAnonymously:failure");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);
		ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();
		SongsManager plm = new SongsManager();

		mMediaplayer = new MediaPlayer();
		mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		// get all songs from sdcard
		this.songsList = plm.getPlayList();

		// looping through playlist
		for (int i = 0; i < songsList.size(); i++)
		{
			// creating new HashMap
			HashMap<String, String> song = songsList.get(i);

			// adding HashList to ArrayList
			songsListData.add(song);
		}

		// Adding menuItems to ListView
		ListAdapter adapter = new SimpleAdapter(this, songsListData,
				R.layout.playlist_item, new String[] { "songTitle" }, new int[] {
				R.id.songTitle });

		setListAdapter(adapter);

		// selecting single ListView item
		ListView lv = getListView();
		// listening to single listitem click
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// getting listitem index
				int songIndex = position;

				// Starting new intent
				Intent in = new Intent(getApplicationContext(), AndroidBuildingMusicPlayerActivity.class);
				// Sending songIndex to PlayerActivity
				in.putExtra("songIndex", songIndex);
				setResult(100, in);
				// Closing PlayListView
				finish();
			}
		});
		FirebaseApp.initializeApp(this);
		mAuth = FirebaseAuth.getInstance();
		mAuth.signInAnonymously()
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d("login", "signInAnonymously:success");
							FirebaseUser user = mAuth.getCurrentUser();
							StorageReference storageReference;
							List list= new ArrayList<>();
							Log.d("firebase","loading data from firebase");
							storageReference=FirebaseStorage.getInstance().getReference().child("Songs/shallow.wav");//("gs://rhythm1-b1f76.appspot.com/Songs/shallow.wav");

							storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
								@Override
								public void onSuccess(Uri uri) {
									// Download url of file
									try {
										final String url = uri.toString();
										Log.d("music from firebase", url);
										mMediaplayer.setDataSource(url);

										// wait for media player to get prepare
										mMediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){


											@Override
											public void onPrepared(MediaPlayer mp) {
												mp.start();
											}
										});

										mMediaplayer.prepareAsync();
									}
									catch (IOException e) {
										e.printStackTrace();
									}
								}
							})
									.addOnFailureListener(new OnFailureListener() {
										@Override
										public void onFailure(@NonNull Exception e) {
											Log.i("TAG", e.getMessage());
										}
									});

						} else {
							// If sign in fails, display a message to the user.
							Log.d("login", "signInAnonymously:failure");


						}

						// ...
					}
				});



		/*DatabaseReference databaseReference;
		databaseReference = FirebaseDatabase.getInstance().getReference("Songs");
		databaseReference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(final DataSnapshot dataSnapshot) {

				list.clear();
				Log.d("firebase","song1");
				for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
				{
					ModelData Mod = dataSnapshot1.getValue(ModelData.class);
					list.add(Mod);
					Log.d("music",Mod.toString());
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});*/

	}

}
