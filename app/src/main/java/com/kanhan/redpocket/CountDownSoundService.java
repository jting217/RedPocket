package com.kanhan.redpocket;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by jting on 2016/12/20.
 */

public class CountDownSoundService extends Service {
    private static final String TAG = null;
    MediaPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    public IBinder onBind(Intent arg0) {
//
//        return null;
//    }
    @Override
    public void onCreate() {
        super.onCreate();

        player = MediaPlayer.create(this, R.raw.game_start_countdown);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);

    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        Log.d("playSound","CountDownSound");
        return Service.START_STICKY;
    }

//    public void onStart(Intent intent, int startId) {
//        // TO DO
//    }
    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {

    }
    public void onPause() {

    }


    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }
}
