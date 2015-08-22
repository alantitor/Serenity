package ntou.cs.lab505.serenity.servicemanager;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;

import ntou.cs.lab505.serenity.database.BandSettingAdapter;
import ntou.cs.lab505.serenity.database.FreqSettingAdapter;
import ntou.cs.lab505.serenity.database.IOSettingAdapter;
import ntou.cs.lab505.serenity.servicemanager.thread.SoundThreadPool;
import ntou.cs.lab505.serenity.system.SystemParameters;
import ntou.cs.lab505.serenity.datastructure.BandGainSetUnit;
import ntou.cs.lab505.serenity.datastructure.IOSetUnit;
import ntou.cs.lab505.serenity.servicemanager.thread.SoundAllThread;

/**
 * Created by alan on 2015/7/3.
 */
public class SoundService extends Service {

    // service state.
    private boolean serviceState = false;
    // read data from database.
    int sampleRate = SystemParameters.SAMPLERATE_HIGH;
    IOSetUnit ioSetUnit;
    int semitoneValue;
    ArrayList<BandGainSetUnit> bandGainSetUnitArrayList;
    // sound process threads object.
    //SoundAllThread soundAllThread;
    SoundThreadPool soundThreadPool;


    public class SoundServiceBinder extends Binder {
        public SoundService getService() {
            return SoundService.this;
        }
    }

    private final IBinder mBinder = new SoundServiceBinder();

    @Override
    public void onCreate() {
        //Log.d("SoundService", "in onCreate.");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //Log.d("SoundService", "in onDestroy.");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void serviceStart() {
        //Log.d("SoundService", "in serviceStart. success.");

        // initial objects.
        // read IO setting data.
        IOSettingAdapter ioSettingAdapter = new IOSettingAdapter(this.getApplicationContext());
        ioSettingAdapter.open();
        ioSetUnit = ioSettingAdapter.getData();
        ioSettingAdapter.close();

        // read freqshift setting data.
        FreqSettingAdapter freqSettingAdapter = new FreqSettingAdapter(this.getApplicationContext());
        freqSettingAdapter.open();
        semitoneValue = freqSettingAdapter.getData();
        freqSettingAdapter.close();

        // read band gain setting data.
        BandSettingAdapter bandSettingAdapter = new BandSettingAdapter(this.getApplicationContext());
        bandSettingAdapter.open();
        bandGainSetUnitArrayList = bandSettingAdapter.getData();
        bandSettingAdapter.close();

        // initial sound process object.
        //soundAllThread = new SoundAllThread(sampleRate, ioSetUnit, semitoneValue, bandGainSetUnitArrayList);
        //soundAllThread.setPriority(Thread.MAX_PRIORITY);
        soundThreadPool = new SoundThreadPool(sampleRate, ioSetUnit, semitoneValue, bandGainSetUnitArrayList);
        soundThreadPool.setPriority(Thread.MAX_PRIORITY);

        serviceState = true;
        //soundAllThread.threadStart();
        soundThreadPool.threadStart();
    }

    public void serviceStop() {
        //Log.d("SoundService", "in serviceStop. success.");
        serviceState = false;
        //soundAllThread.threadStop();
        soundThreadPool.threadStop();
    }

    public boolean getServiceState() {
        return serviceState;
    }
}
