package ntou.cs.lab505.serenity.sound;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import ntou.cs.lab505.serenity.database.BandSettingAdapter;
import ntou.cs.lab505.serenity.database.FreqSettingAdapter;
import ntou.cs.lab505.serenity.database.IOSettingAdapter;
import ntou.cs.lab505.serenity.datastructure.BandGainSetUnit;
import ntou.cs.lab505.serenity.datastructure.IOSetUnit;
import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;
import ntou.cs.lab505.serenity.thread.SoundAllThread;
import ntou.cs.lab505.serenity.thread.SoundIOThread;
import ntou.cs.lab505.serenity.thread.SoundProcessThread;

/**
 * Created by alan on 2015/7/3.
 */
public class SoundService extends Service {

    // service state.
    private boolean serviceState = false;

    // read data from database.
    IOSetUnit ioSetUnit;
    int semitoneValue;
    ArrayList<BandGainSetUnit> bandGainSetUnitArrayList;

    // sound process threads object.


    public class SoundServiceBinder extends Binder {
        public SoundService getService() {
            return SoundService.this;
        }
    }

    private final IBinder mBinder = new SoundServiceBinder();

    @Override
    public void onCreate() {
        Log.d("SoundService", "in onCreate.");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d("SoundService", "in onDestroy.");
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

    public void serviceInitParams() {

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


    }

    //SoundIOThread soundIOThread;
    //SoundProcessThread soundProcessThread;
    //private LinkedBlockingQueue<SoundVectorUnit> soundQueue1 = new LinkedBlockingQueue<>();
    //private LinkedBlockingQueue<SoundVectorUnit> soundQueue2 = new LinkedBlockingQueue<>();

    SoundAllThread soundAllThread;

    public void serviceStart() {
        Log.d("SoundService", "in serviceStart. success.");
        serviceState = true;

        soundAllThread = new SoundAllThread();
        soundAllThread.setPriority(Thread.MAX_PRIORITY);
        soundAllThread.threadStart();


        //soundProcessThread = new SoundProcessThread();
        //soundProcessThread.setSoundInputQueue(soundQueue1);
        //soundProcessThread.setSoundOutputQueue(soundQueue2);
        //soundProcessThread.setPriority(Thread.MIN_PRIORITY);
        //soundProcessThread.threadStart();


        //soundIOThread = new SoundIOThread();
        //soundIOThread.setSoundInputQueue(soundQueue1);
        //soundIOThread.setSoundOutputQueue(soundQueue2);
        //soundIOThread.setPriority(Thread.MAX_PRIORITY);
        //soundIOThread.threadStart();
    }

    public void serviceStop() {
        Log.d("SoundService", "in serviceStop. success.");
        serviceState = false;
        soundAllThread.threadStop();
        //soundIOThread.threadStop();
        //soundIOThread = null;
        //soundProcessThread.threadStop();
        //soundProcessThread = null;
    }

    public boolean getServiceState() {
        return serviceState;
    }
}
