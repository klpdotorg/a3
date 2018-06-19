package com.gka.akshara.assesseasy;

import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by suresh on 14-06-2018.
 */

public class audioManager {

    public static MediaPlayer mplayer = new MediaPlayer();

    public static void playAudio(String base64EncodedString) {

        try {
            String url = "data:audio/mp3;base64,"+base64EncodedString;

            mplayer.reset();
            mplayer.setDataSource(url);
            mplayer.prepare();
            mplayer.start();
        }
        catch (Exception e) {
            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "audioManager: playAudio: Exception"+e.toString());
        }
    }
}
