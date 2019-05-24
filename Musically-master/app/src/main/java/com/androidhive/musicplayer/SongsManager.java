package com.androidhive.musicplayer;

import android.app.Activity;
import android.content.Intent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import android.media.MediaPlayer;
import android.net.Uri;
import android.content.Context;
import android.os.AsyncTask;
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
	//pls add to git
	final String MEDIA_PATH = android.os.Environment.getExternalStorageDirectory().getPath() + "/";
	public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private ArrayList<String> songs=new ArrayList<String>();
	public Context context;
	private FirebaseAuth mAuth;
	public boolean load_UI=false;
	private DatabaseReference mDatabase;
	private String ImageUrl;
//
	private String nam;
	// Constructor
	public SongsManager(Context context)
	{
		this.context=context;
		FirebaseApp.initializeApp(this.context);

		mAuth = FirebaseAuth.getInstance();
//
	}

	/**
	 * Function to read all mp3 files from sdcard
	 * and store the details in ArrayList
	 * */
	public void getPlayList()
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
		//return songsList;

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
			songMap.put("imagePath","");
			songMap.put("songAlbum","");
			// Adding each song to SongList
			Log.d("name",song.getName());
			songsList.add(songMap);
		}
	}

	public void add_cloud_music(String name, String path, String Imagepath, String Album)
	{
		HashMap<String, String> songMap = new HashMap<String, String>();

		songMap.put("songTitle", name);
		songMap.put("songPath", path);
		songMap.put("imagePath", Imagepath);
		songMap.put("songAlbum", Album);
		Log.d("path",path);
		songsList.add(songMap);
		Log.d("add",name);

	}


	private void showData(DataSnapshot dataSnapshot) {
		DatabaseReference thumbnailRef = mDatabase.child("Thumbnail");
		for (DataSnapshot ds : dataSnapshot.getChildren()) {

			String name=ds.child("Song").getValue().toString();
			String Album=ds.child("Album").getValue().toString();
			String url=ds.child("Url").getValue().toString();
			ImageUrl="https://firebasestorage.googleapis.com/v0/b/rhythm1-b1f76.appspot.com/o/Thumbnail%2Fa%20star%20is%20born.png?alt=media&token=024e41e2-03d3-4397-a93a-bf7ad6517c22";

			add_cloud_music(name,url,ImageUrl,Album);

			//Log.d("fetching music: url", url);
			//Log.d("fetching music: name", name);


		}
	}
	public void get_cloud_music(){
		mDatabase = FirebaseDatabase.getInstance().getReference();
		DatabaseReference musicRef = mDatabase.child("Music");
		DatabaseReference thumbnailRef = mDatabase.child("Thumbnail");


		FirebaseUser user = mAuth.getCurrentUser();
		if(user != null){
			Log.d("email",user.getEmail());
		}

		Log.d("login", "sign:success");

		musicRef.addValueEventListener(new ValueEventListener() {
			//@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				showData(dataSnapshot);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

	}


}