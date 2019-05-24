package com.androidhive.musicplayer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.List;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.InputType;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;
import android.widget.PopupMenu;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;


public class AndroidBuildingMusicPlayerActivity extends Activity implements PopupMenu.OnMenuItemClickListener
{

    // All player buttons

    private ImageButton User;
    private ImageButton btnShare;
    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnPlaylist;
    private ImageButton btnRepeat;
    private ImageButton btnmyplaylist;
    public ImageButton btnAddToPlaylist;
    public ImageButton btnDownload;
    private ImageButton btnspeedup;

    private ImageView thumbnail;

    private  PopupMenu popup;
    private  PopupMenu popupPlaylist;       //Pop up for playlist
    private  PopupMenu popupExistingPlaylist;

    //private ImageButton btnShuffle;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;


    // Media Player
    private  MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();;
    private SongsManager songManager;
    private Utilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private float speed= 1.0f;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private boolean isShare = false;
    //private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
//
    private List<String> existingPlaylist;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference userNameRef;

    @Override
    protected void onResume()
    {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userEmail = user.getEmail();
        final String username = userEmail.split("@")[0];
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userNameRef = mDatabase.child("Users").child(username);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("listener","entered listener");
                if(dataSnapshot.exists() && dataSnapshot.child("Role").getValue().equals("free") ) { ;
                    btnAddToPlaylist.setVisibility(View.GONE);
                    btnDownload.setVisibility(View.GONE);
                    btnmyplaylist.setVisibility(View.GONE);
                }
                else  if(dataSnapshot.exists() && dataSnapshot.child("Role").getValue().equals("premium")){
                    //user data exists -> premium user
                    //display AddToPlayList button
                    btnAddToPlaylist.setVisibility(View.VISIBLE);
                    btnDownload.setVisibility(View.VISIBLE);
                    btnmyplaylist.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);
        Intent  in=new Intent(AndroidBuildingMusicPlayerActivity.this,bottom_nav.class);
        startActivity(in);

    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        btnAddToPlaylist=(ImageButton) findViewById(R.id.addtoplaylist);

        /* Authenticated User and Database */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userEmail = user.getEmail();
        final String username = userEmail.split("@")[0];
        mDatabase = FirebaseDatabase.getInstance().getReference();

        /* Checking if authenticated user is a premium member
         * If yes, enable AddToPlaylist button. (make it visible, else hide it)
         * */
        userNameRef = mDatabase.child("Users").child(username);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("listener","entered listener");
                if(!dataSnapshot.exists() && dataSnapshot.child("Role").getValue().equals("free")) {
                    Log.d("listener","user deleted");
                    //user data doesnt exist  -> free user
                    //hide AddToPlayList button
                    Toast.makeText(getApplicationContext(), "Free user", Toast.LENGTH_SHORT).show();
                    btnAddToPlaylist.setVisibility(View.GONE);
                    btnDownload.setVisibility(View.GONE);
                }
                else if(dataSnapshot.exists() && dataSnapshot.child("Role").getValue().equals("premium")){
                    //user data exists -> premium user
                    //display AddToPlayList button

                    Toast.makeText(getApplicationContext(), "Premium user", Toast.LENGTH_SHORT).show();
                    btnAddToPlaylist.setVisibility(View.VISIBLE);
                    btnDownload.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);


        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnForward = (ImageButton) findViewById(R.id.btnForward);
        btnBackward = (ImageButton) findViewById(R.id.btnBackward);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        //btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        songTitleLabel = (TextView) findViewById(R.id.songTitle);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
        btnShare = (ImageButton) findViewById(R.id.btnShare);
        btnmyplaylist = (ImageButton) findViewById(R.id.btnmyplaylist);
        btnDownload = (ImageButton) findViewById(R.id.btnDownload);
        btnspeedup=(ImageButton) findViewById(R.id.speedup);
        thumbnail = (ImageView) findViewById(R.id.Thumbnail_image);
        User  = (ImageButton)findViewById(R.id.btnuser);



        mp = new MediaPlayer();



        popup = new PopupMenu(AndroidBuildingMusicPlayerActivity.this,findViewById(R.id.speedup));
        popup.setOnMenuItemClickListener(AndroidBuildingMusicPlayerActivity.this);
        popup.inflate(R.menu.popup_menu);

        /*
        needs to be checked
         */
        popupPlaylist = new PopupMenu(AndroidBuildingMusicPlayerActivity.this,findViewById(R.id.addtoplaylist));
        popupPlaylist.setOnMenuItemClickListener(AndroidBuildingMusicPlayerActivity.this);
        popupPlaylist.inflate(R.menu.popup_playlist);





        songManager = new SongsManager(this);

        // Getting all songs list
        songManager.getPlayList();

        Log.d("in main activity",songManager.songsList.toString());
        utils = new Utilities();

        songProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch)
            {

            }

            /**
             * When user starts moving the progress handler
             * */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                // remove message Handler from updating progress bar
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            /**
             * When user stops moving the progress hanlder
             * */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mp.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                mp.seekTo(currentPosition);

                // update timer progress again
                updateProgressBar();
            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {

            /**
             * On Song Playing completed
             * if repeat is ON play same song again
             * if shuffle is ON play random song
             */
            @Override
            public void onCompletion(MediaPlayer arg0)
            {

                // check for repeat is ON or OFF
                if (isRepeat)
                {
                    // repeat is on play same song again
                    Initialise(currentSongIndex);
                    playSong();
                }
                else if (isShuffle)
                {
                    // shuffle is on - play a random song
                    Random rand = new Random();
                    currentSongIndex = rand.nextInt((songManager.songsList.size() - 1) - 0 + 1) + 0;
                    Initialise(currentSongIndex);
                    playSong();
                }
                else
                {
                    // no repeat or shuffle ON - play next song
                    if (currentSongIndex < (songManager.songsList.size() - 1))
                    {

                        Initialise(currentSongIndex + 1);
                        playSong();
                        currentSongIndex = currentSongIndex + 1;
                    }
                    else
                    {
                        // play first song
                        Initialise(0);
                        playSong();
                        currentSongIndex = 0;
                    }
                }
            }
        });


        // By default play first song
        Initialise(0);
        btnPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        /**
         * Play button click event
         * plays a song and changes button to pause image
         * pauses a song and changes button to play image
         * */
        btnPlay.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                // check for already playing
                if(mp.isPlaying())
                {
                    if(mp!=null)
                    {
                        mp.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    }
                }
                else
                {
                    // Resume song
                    if(mp!=null)
                    {
                        playSong();
                        //mp.start();
                        // Changing button image to pause button
                        //btnPlay.setImageResource(R.drawable.btn_pause);
                    }
                }

            }
        });

        /**
         * Forward button click event
         * Forwards song specified seconds
         * */
        btnForward.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if(currentPosition + seekForwardTime <= mp.getDuration())
                {
                    // forward song
                    mp.seekTo(currentPosition + seekForwardTime);
                }
                else
                {
                    // forward to end position
                    mp.seekTo(mp.getDuration());
                }
            }
        });

        /**
         * Backward button click event
         * Backward song to specified seconds
         * */
        btnBackward.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if(currentPosition - seekBackwardTime >= 0)
                {
                    // forward song
                    mp.seekTo(currentPosition - seekBackwardTime);
                }
                else
                {
                    // backward to starting position
                    mp.seekTo(0);
                }

            }
        });

        /**
         * Next button click event
         * Plays next song by taking currentSongIndex + 1
         * */
        btnNext.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                // check if next song is there or not
                if(currentSongIndex < (songManager.songsList.size() - 1))
                {
                    Initialise(currentSongIndex + 1);
                    playSong();
                    currentSongIndex = currentSongIndex + 1;
                }
                else
                {
                    // play first song
                    Initialise(0);
                    playSong();
                    currentSongIndex = 0;
                }

            }
        });

        /**
         * Back button click event
         * Plays previous song by currentSongIndex - 1
         * */
        btnPrevious.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                if(currentSongIndex > 0)
                {
                    Initialise(currentSongIndex - 1);
                    playSong();
                    currentSongIndex = currentSongIndex - 1;
                }
                else
                {
                    // play last song
                    Initialise(songManager.songsList.size() - 1);
                    playSong();
                    currentSongIndex = songManager.songsList.size() - 1;
                }

            }
        });

        /**
         * Button Click event for Repeat button
         * Enables repeat flag to true
         * */
        btnRepeat.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                if(isRepeat)
                {
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.ic_loop_black_24dp);
                }
                else
                {
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.ic_loop_blue_24dp);
                }
            }
        });

        User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AndroidBuildingMusicPlayerActivity.this, UserActivity.class);

                startActivity(intent);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                String url=songManager.songsList.get(currentSongIndex).get("songPath");
                if (url.contains("emulated"))
                {
                    Toast.makeText(getApplicationContext(),"Cannot share local storage music", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent share = new Intent(); //Intent carries the message object
                share.setAction(Intent.ACTION_SEND); //The action is set to send for sharing
                share.putExtra(Intent.EXTRA_TEXT,url);
                share.setType("text/*");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(share, "Share url of audio file")); //the activity is started with the intent as share
            }
        });
        btnDownload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                String path=songManager.songsList.get(currentSongIndex).get("songPath");
                String title=songManager.songsList.get(currentSongIndex).get("songTitle");
                String url=path;

                if (url.contains("emulated"))
                {
                    Toast.makeText(getApplicationContext(),"Cannot download local storage music", Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));

            }
        });

        /**
         * Button Click event for Play list click event
         * Launches list activity which displays list of songs
         * */
        btnmyplaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AndroidBuildingMusicPlayerActivity.this, View_Database.class);
                startActivity(intent);
            }
        });
        new View_Database();
        btnPlaylist.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
                startActivityForResult(i, 100);
            }
        });


        btnspeedup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popup.show();
                Toast.makeText(getApplicationContext(), "Playback speed change", Toast.LENGTH_SHORT).show();

            }
        });

        btnAddToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playlistPrompt();
            }
        });



    }


    private void playlistPrompt() {


        View view = (LayoutInflater.from(AndroidBuildingMusicPlayerActivity.this)).inflate(R.layout.alertplaylist, null);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AndroidBuildingMusicPlayerActivity.this);

        final EditText userInputPlaylist = (EditText) view.findViewById(R.id.userInputPlaylist);  //from alertPrompt.xml


        alertBuilder.setView(view)
                .setTitle("Enter Playlist Name")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("Add to Playlist", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String playlistName = userInputPlaylist.getText().toString();

                        //userNameRef.child(playlistName).setValue("empty");
                        String url=songManager.songsList.get(currentSongIndex).get("songPath");
                        String songname=songManager.songsList.get(currentSongIndex).get("songTitle");
                        String albumname=songManager.songsList.get(currentSongIndex).get("songAlbum");
                        String thumbnail= songManager.songsList.get(currentSongIndex).get("imagePath");
                        if(url.contains("emulated")) {
                            url="";
                        }
                        String selectedSong = songname + '|' + albumname;
                        userNameRef.child("Playlist").child(playlistName).child(selectedSong).child("Song").setValue(songname);
                        userNameRef.child("Playlist").child(playlistName).child(selectedSong).child("Album").setValue(albumname);
                        userNameRef.child("Playlist").child(playlistName).child(selectedSong).child("Url").setValue(url);
                        userNameRef.child("Playlist").child(playlistName).child(selectedSong).child("thumbnail").setValue(thumbnail);

                        Toast.makeText(getApplicationContext(), songname+ " added to "+playlistName , Toast.LENGTH_SHORT).show();

                    }
                });


        Dialog dialog = alertBuilder.create();
        dialog.show();

    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.one:
                speed=1.0f;
                // do your code
                item.setChecked(true);
                Toast.makeText(getApplicationContext(), "Playback 1x", Toast.LENGTH_SHORT).show();
                break;
            case R.id.two:
                speed=2.0f;
                item.setChecked(true);
                Toast.makeText(getApplicationContext(), "Playback 2x", Toast.LENGTH_SHORT).show();
                // do your code
                break;
            case R.id.one_half:
                speed=1.5f;
                item.setChecked(true);
                Toast.makeText(getApplicationContext(), "Playback 1.5x", Toast.LENGTH_SHORT).show();
                // do your code
                break;
            case R.id.one_half_quarter:
                speed=1.75f;
                item.setChecked(true);
                Toast.makeText(getApplicationContext(), "Playback 1.75x", Toast.LENGTH_SHORT).show();
                // do your code
                break;
        }


        if (mp.isPlaying()) {
            mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(speed));

        } else {
            mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(speed));

            mp.pause();
        }


        return true;
    }
    /**
     * Receiving song index from playlist view
     * and play the song
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("find first","i'm here");
        if(resultCode == 100)
        {
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            Initialise(currentSongIndex);
            playSong();
        }

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

    public void  Initialise(int songIndex)
    {
        // Play song
        try
        {
            mp.reset();
            Log.d("thumbnail","blah");
            mp.setDataSource(songManager.songsList.get(songIndex).get("songPath"));
            mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(speed));
            mp.prepare();
            //mp.start();
            // Displaying Song title
            String songTitle = songManager.songsList.get(songIndex).get("songTitle");
            Log.d("thumbnail","blah1");
            String thumb= songManager.songsList.get(songIndex).get("imagePath");
            Log.d("thumbnail","bl"+thumb);
            new DownloadImageTask(thumbnail).execute(thumb);
            songTitleLabel.setText(titlecase(songTitle));

            // Changing Button Image to pause image


            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);
            long totalDuration = mp.getDuration();
            songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Updating progress bar
            updateProgressBar();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Function to play a song  */
    public void  playSong()
    {
        // Play song
        try
        {

            mp.start();
            // Displaying Song title
            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.ic_pause_black_24dp);

        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar()
    {

        mHandler.postDelayed(mUpdateTimeTask, 1000);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable()
    {
        public void run()
        {
            //Log.d("progress","updating");
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time
            //songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 1000);
        }
    };


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mp.release();
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView Thumbnail;

        public DownloadImageTask(ImageView bmImage) {
            this.Thumbnail = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            if (urldisplay=="")
            {
                Log.d("local image","def");
                return BitmapFactory.decodeResource(getResources(), R.drawable.adele);
            }
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            Thumbnail.setImageBitmap(result);
        }
    }

}