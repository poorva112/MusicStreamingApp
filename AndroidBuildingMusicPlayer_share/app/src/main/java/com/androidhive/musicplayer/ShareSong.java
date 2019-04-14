package com.androidhive.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

public class ShareSong {
    final String MEDIA_PATH = android.os.Environment.getExternalStorageDirectory().getPath() + "/";

    private void shareasong(Context context) {
        try {


            File f = new File(MEDIA_PATH);
            Uri uri = Uri.parse("file://" + f.getAbsolutePath());
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setType("audio/*");
            share.setPackage("com.whatsapp");
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //startActivity(share);
            context.startActivity(Intent.createChooser(share, "Share audio File"));

        }catch (NullPointerException e){
            throw new IllegalStateException("this is not possible ",e);
        }

    }
}
