
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
	boolean local_loaded=false;

	private String nam;
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
	public void getPlayList()
	{
		/* tmp = new MyClass();
		Thread thread = new Thread(tmp);
		thread.start();
		try {
			thread.join();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}*/

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
		StorageReference storageRef;
		//list= new ArrayList<>();
		Log.d("firebase","loading data from firebase");
		storageReference=FirebaseStorage.getInstance().getReference();
		songs.add("shallow.wav");
		songs.add("million reasons.wma");
		songs.add("always remember us this way.mp3");
		Thread t1;
		fetchURL hehe;
		for(int i=0;i<3;i++) {
			//("gs://rhythm1-b1f76.appspot.com/Songs/shallow.wav");

			storageRef = storageReference.child("Songs/" + songs.get(i));
			hehe = new fetchURL(storageRef, i);
			t1 = new Thread(hehe);
			t1.start();

			try {
				t1.join();
				Log.d("completed url fetch",i+"");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			/*
			storageRef = storageReference.child("Songs/" + songs.get(1));//("gs://rhythm1-b1f76.appspot.com/Songs/shallow.wav");
			storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
				@Override
				public void onSuccess(Uri uri) {
					// Download url of file
					try {
						final String url = uri.toString();
						Log.d("music from firebase", url);
						add_cloud_music(songs.get(1), url);
					} catch (Exception e) {
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
			storageRef = storageReference.child("Songs/" + songs.get(2));//("gs://rhythm1-b1f76.appspot.com/Songs/shallow.wav");
			storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
				@Override
				public void onSuccess(Uri uri) {
					// Download url of file
					try {
						final String url = uri.toString();
						Log.d("music from firebase", url);
						add_cloud_music(songs.get(2), url);
					} catch (Exception e) {
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
			*/
		}
	}

	private class MyClass implements Runnable
	{

		@Override
		public void run()
		{
			mDatabase = FirebaseDatabase.getInstance().getReference();
			FirebaseUser user = mAuth.getCurrentUser();
			if(user != null){
				Log.d("email",user.getEmail());
			}

			Log.d("login", "sign:success");
			StorageReference storageReference;
			StorageReference storageRef;
			Log.d("firebase","loading data from firebase");
			storageReference=FirebaseStorage.getInstance().getReference();
			songs.add("shallow.wav");
			songs.add("million reasons.wma");
			songs.add("always remember us this way.mp3");
			Thread t1;
			fetchURL hehe;
			for(int i=0;i<3;i++) {
				//("gs://rhythm1-b1f76.appspot.com/Songs/shallow.wav");
				storageRef = storageReference.child("Songs/" + songs.get(i));
				hehe = new fetchURL(storageRef, i);
				t1 = new Thread(hehe);
				t1.start();
				try {
					t1.join();
					Log.d("completed url fetch",i+"");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Log.d("thread","done");
		}

	}

	private class fetchURL implements Runnable{


		private Integer i;
		private StorageReference storageRef;
		public fetchURL(StorageReference a,Integer i)
		{
			// store parameter for later user
			this.storageRef=a;
			this.i=i;

		}
		@Override
		public void run()
		{

			storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
				@Override
				public void onSuccess(Uri uri) {
					// Download url of file
					try {
						final String url = uri.toString();
						Log.d("music from firebase", url);
						add_cloud_music(songs.get(i), url);

						if(i==3)
						{
							load_UI=true;
						}

					} catch (Exception e) {
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
		}
	}

}