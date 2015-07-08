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
import ntou.cs.lab505.serenity.thread.SoundIOThread;
import ntou.cs.lab505.serenity.thread.examThread;

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

    //final Handler handler = new Handler();
    //Runnable runnable = new examThread();

    //SoundIOThread soundIOThread = new SoundIOThread();
    private LinkedBlockingQueue<SoundVectorUnit> soundInputQueue = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<SoundVectorUnit> soundOutputQueue = new LinkedBlockingQueue<>();


    public void serviceStart() {
        Log.d("SoundService", "in serviceStart. success.");
        serviceState = true;
        //soundIOThread.setSoundInputQueue(soundInputQueue);
        //soundIOThread.setSoundOutputQueue(soundOutputQueue);



        //soundIOThread.threadStart();

        //handler.post(runnable);
        Thread thread = new SoundIOThread();
        thread.setPriority(9);
        thread.start();


        /*
        SoundInputPool soundInputPool = new SoundInputPool(8000, 0);
        soundInputPool.open();
        SoundOutputPool soundOutputPool = new SoundOutputPool(8000, 1, 2, 0);
        soundOutputPool.open();

        FrequencyShift frequencyShift = new FrequencyShift(8000, 1, 1, 0, 0);

        BandGain bandGain = new BandGain(8000, 200, 3000, 5, 5, 5);



        while (serviceState) {
            //Log.d("SoundService", "debug: data: " + soundInputPool.read().getVectorLength());
            long timems0 = System.currentTimeMillis();
            long time0 = System.nanoTime();

            SoundVectorUnit data = soundInputPool.read();
            long timems1 = System.currentTimeMillis();
            long time1 = System.nanoTime();
            Log.d("SoundService", "debug: time1: " + (time1 - time0) / 1000000.0 + " " + (timems1 - timems0));

            data = frequencyShift.process(data);
            long timems2 = System.currentTimeMillis();
            long time2 = System.nanoTime();
            Log.d("SoundService", "debug: time2: " + (time2 - time1) / 1000000.0 + " " + (timems2 - timems1));

            //data = bandGain.process(data);
            long timems3 = System.currentTimeMillis();
            long time3 = System.nanoTime();
            Log.d("SoundService", "debug: time3: " + (time3 - time2) / 1000000.0 + " " + (timems3 - timems2));

            soundOutputPool.write(data);
            long timems4 = System.currentTimeMillis();
            long time4 = System.nanoTime();
            Log.d("SoundService", "debug: time4: " + (time4 - time0) / 1000000.0 + " " + (timems4 - timems0));
            Log.d("SoundService", "debug: total time: " + (time4 - time0) / 1000000.0 + " " + (timems4 - timems0));
        }
        */
    }

    public void serviceStop() {
        Log.d("SoundService", "in serviceStop. success.");
        serviceState = false;

        //handler.removeCallbacksAndMessages(runnable);
        //soundIOThread.threadStop();
    }

    public boolean getServiceState() {
        return serviceState;
    }
}
