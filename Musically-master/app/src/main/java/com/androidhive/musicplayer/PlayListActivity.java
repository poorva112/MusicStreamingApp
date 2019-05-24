package com.androidhive.musicplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

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


public class PlayListActivity extends ListActivity
{
	// Songs list
	//public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);
		SongsManager plm = new SongsManager(this);
		Log.d("call create playlist","Bleu");
//
		// get all songs from sdcard
		plm.getPlayList();
		/*
		try {
			TimeUnit.SECONDS.sleep(5);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}

*/

		// Adding menuItems to ListView
		ListAdapter adapter = new SimpleAdapter(this, plm.songsList,
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


	}

}