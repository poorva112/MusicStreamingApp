package com.androidhive.musicplayer;

import android.app.Activity;
import android.content.Intent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.media.MediaPlayer;
import android.net.Uri;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
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

public class SongsManager
{
	// SDCard Path
	final String MEDIA_PATH = android.os.Environment.getExternalStorageDirectory().getPath() + "/";
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	public Context context;
	private FirebaseAuth mAuth;
	private DatabaseReference mDatabase;
	boolean local_loaded=false;

	// Constructor
	public SongsManager(Context context)
	{
		this.context=context;
		if(local_loaded==false) {
			local_loaded = false;
		}
		FirebaseApp.initializeApp(this.context);

		mAuth = FirebaseAuth.getInstance();

	}

	/**
	 * Function to read all mp3 files from sdcard
	 * and store the details in ArrayList
	 * */
	public ArrayList<HashMap<String, String>> getPlayList()
	{
		get_cloud_music();
		Log.d("local storage","starting addition");
		if (MEDIA_PATH != null)
		{
			File home = new File(MEDIA_PATH);
			File[] listFiles = home.listFiles();
			System.out.println(home.listFiles());
			if (listFiles != null)
			{
				for (File file : listFiles)
				{
					System.out.println(file.getAbsolutePath());
					if (file.isDirectory())
					{
						scanDirectory(file);
					}
					else
					{
						addSongToList(file);
					}
				}
			}
		}
		Log.d("local storage","completed addition");
// return songs list array
		return songsList;
	}


	private void scanDirectory(File directory)
	{
		if (directory != null)
		{
			File[] listFiles = directory.listFiles();
			if (listFiles != null && listFiles.length > 0)
			{
				for (File file : listFiles)
				{
					if (file.isDirectory())
					{
						scanDirectory(file);
					}
					else
					{
						addSongToList(file);
					}
				}
			}
		}
	}


	private void addSongToList(File song)
	{
		if (song.getName().endsWith(".mp3")||song.getName().endsWith(".wav"))
		{
			HashMap<String, String> songMap = new HashMap<String, String>();
			songMap.put("songTitle", song.getName().substring(0, (song.getName().length() - 4)));
			songMap.put("songPath", song.getPath());
			// Adding each song to SongList
			Log.d("name",song.getName());
			songsList.add(songMap);
		}
	}

	public void add_cloud_music(String name, String path)
	{
		HashMap<String, String> songMap = new HashMap<String, String>();

		songMap.put("songTitle", name.substring(0, (name.length() - 4)));
		songMap.put("songPath", path);
		Log.d("path",path);
		songsList.add(songMap);
		Log.d("add",name);

	}

	public void get_cloud_music(){
		mDatabase = FirebaseDatabase.getInstance().getReference();
		FirebaseUser user = mAuth.getCurrentUser();
		if(user != null){
			Log.d("email",user.getEmail());
		}

		Log.d("login", "sign:success");
		StorageReference storageReference;
		//list= new ArrayList<>();
		Log.d("firebase","loading data from firebase");
		storageReference=FirebaseStorage.getInstance().getReference().child("Songs/shallow.wav");//("gs://rhythm1-b1f76.appspot.com/Songs/shallow.wav");

		storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
			@Override
			public void onSuccess(Uri uri) {
				// Download url of file
				try {
					final String url = uri.toString();
					Log.d("music from firebase", url);
					add_cloud_music("Shallow.wav",url);
				}
				catch (Exception e) {
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


		/*mAuth.signInAnonymously().addOnCompleteListener((Activity) this.context, new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {

				if (task.isSuccessful()) {
					// Sign in success, update UI with the signed-in user's information
					Log.d("login", "signInAnonymously:success");
					FirebaseUser user = mAuth.getCurrentUser();
					StorageReference storageReference;
					//list= new ArrayList<>();
					Log.d("firebase","loading data from firebase");
					storageReference=FirebaseStorage.getInstance().getReference().child("Songs/shallow.wav");//("gs://rhythm1-b1f76.appspot.com/Songs/shallow.wav");

					storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
						@Override
						public void onSuccess(Uri uri) {
							// Download url of file
							try {
								final String url = uri.toString();
								Log.d("music from firebase", url);
								add_cloud_music("Shallow.wav",url);
							}
							catch (Exception e) {
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
			}
		});*/
	}
}

