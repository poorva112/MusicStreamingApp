package com.androidhive.musicplayer;

import android.content.Intent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.net.Uri;
import android.content.Context;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SongsManager
{
	// SDCard Path
	final String MEDIA_PATH = android.os.Environment.getExternalStorageDirectory().getPath() + "/";
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	// Constructor
	public SongsManager()
	{
		
	}
	
	/**
	 * Function to read all mp3 files from sdcard
	 * and store the details in ArrayList
	 * */
	public ArrayList<HashMap<String, String>> getPlayList()
	{
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
		if (song.getName().endsWith(".mp3"))
		{
			HashMap<String, String> songMap = new HashMap<String, String>();
			songMap.put("songTitle", song.getName().substring(0, (song.getName().length() - 4)));
			songMap.put("songPath", song.getPath());
			// Adding each song to SongList
			songsList.add(songMap);
		}
	}


}

