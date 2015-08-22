package ntou.cs.lab505.serenity.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ntou.cs.lab505.serenity.R;
import ntou.cs.lab505.serenity.database.BandSettingAdapter;
import ntou.cs.lab505.serenity.database.FreqSettingAdapter;
import ntou.cs.lab505.serenity.database.IOSettingAdapter;
import ntou.cs.lab505.serenity.servicemanager.SoundService;
import ntou.cs.lab505.serenity.stream.device.Microphone;
import ntou.cs.lab505.serenity.stream.device.Speaker;


/**
 * Design layout of service control layout.
 */
public class ServiceActivity extends Activity {

    boolean boundState = false; // denote service bind state.
    private SoundService soundService; // service object.
    ImageView controlButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        setVolumeControlStream(AudioManager.STREAM_MUSIC); // enable system volume buttons.
        controlButton = (ImageView) findViewById(R.id.servicecontrol_activity_service); // get layout object.
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Log.d("ServiceActivity", "onSrviceConnected.");
            SoundService.SoundServiceBinder mBinder = (SoundService.SoundServiceBinder) service;
            soundService = mBinder.getService();
            boundState = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //Log.d("ServiceActivity", "onServiceDisconnected.");
            boundState = false;
        }
    };

    @Override
    protected void onStart() {
        //Log.d("ServiceActivity", "in onStart.");
        super.onStart();

        Intent intent = new Intent(this, SoundService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (boundState) {
            if (soundService.getServiceState()) {
                soundService.serviceStop();
                controlButton.setImageResource(R.drawable.ic_music_player_play_orange_128);
            }

            unbindService(serviceConnection);
            boundState = false;
        }
    }

    public void buttonService(View view) {
        // check data state.
        if (checkServiceState() == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            TextView message = new TextView(this);
            message.setText("請先設定助聽器參數");
            builder.setView(message);
            builder.setPositiveButton("OK", null);
            AlertDialog dialong = builder.show();
            dialong.show();

            return ;
        }

        // set system volume to max.
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        // control service.
        if (soundService.getServiceState()) {
            // stop service.
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            soundService.serviceStop();
            controlButton.setImageResource(R.drawable.ic_music_player_play_orange_128);
        } else {
            // start service.
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            soundService.serviceStart();
            controlButton.setImageResource(R.drawable.ic_music_player_pause_lines_orange_128);
        }
    }

    public void buttonSetting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    /**
     * check hearing aid setting.
     * @return
     */
    private boolean checkServiceState() {

        boolean state = false;

        IOSettingAdapter ioSettingAdapter = new IOSettingAdapter(this.getApplicationContext());
        FreqSettingAdapter freqSettingAdapter = new FreqSettingAdapter(this.getApplicationContext());
        BandSettingAdapter bandSettingAdapter = new BandSettingAdapter(this.getApplicationContext());

        ioSettingAdapter.open();
        freqSettingAdapter.open();
        bandSettingAdapter.open();

        if (ioSettingAdapter.getDataNumber() == 0 || freqSettingAdapter.getDataNumber() == 0 || bandSettingAdapter.getDataNumber() == 0) {
            state = false;
        } else {
            state = true;
        }

        ioSettingAdapter.close();
        freqSettingAdapter.close();
        bandSettingAdapter.close();

        return state;
    }
}
