package com.gka.akshara.assesseasy;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by suresh on 14-06-2018.
 */

public class audioManager {

    public static MediaPlayer mplayer = new MediaPlayer();

    public static void playAudio(String base64EncodedString) {

        if(TextUtils.isEmpty(base64EncodedString)) {
            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "audioManager: playAudio: Passed empty base64EncodedString for the Audio");
            return;
        }

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

    public static void playErrorBeepAudio(Context ctx) {

        try {

            AssetFileDescriptor descriptor = ctx.getAssets().openFd("audio_errorbeep.mp3");
            mplayer.reset();
            mplayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            mplayer.prepare();
            mplayer.start();
        }
        catch (Exception e) {
            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "audioManager: playErrorBeepAudio: Exception"+e.toString());
        }
    }
}
