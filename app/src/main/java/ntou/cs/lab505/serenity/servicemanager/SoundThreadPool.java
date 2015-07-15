package ntou.cs.lab505.serenity.servicemanager;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ntou.cs.lab505.serenity.datastructure.BandGainSetUnit;
import ntou.cs.lab505.serenity.datastructure.IOSetUnit;
import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;
import ntou.cs.lab505.serenity.sound.bandgain.BandGain;
import ntou.cs.lab505.serenity.sound.frequencyshift.FrequencyShift;
import ntou.cs.lab505.serenity.stream.SoundInputPool;
import ntou.cs.lab505.serenity.stream.SoundOutputPool;
import ntou.cs.lab505.serenity.system.SystemParameters;

/**
 * Created by alan on 2015/7/10.
 */
public class SoundThreadPool extends Thread {

    // thread manager parameters.
    private boolean threadState;

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    // Sets the amount of time an idle thread waits before terminating
    int KEEP_ALIVE_TIME = 1;
    // Sets the Time Unit to seconds
    TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.MILLISECONDS;
    private final BlockingQueue<Runnable> mProcessWorkQueue = new LinkedBlockingQueue<Runnable>();
    // Creates a thread pool manager
    ThreadPoolExecutor mProcessThreadPool;

    // sound parameters..
    private int sampleRate = SystemParameters.SAMPLERATE_HIGH;
    LinkedBlockingQueue<SoundVectorUnit> soundDataQueue = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<SoundVectorUnit> soundDataQueue2 = new LinkedBlockingQueue<>();
    // sound objects.
    SoundInputPool soundInputPool;
    FrequencyShift frequencyShift;
    BandGain bandGain;
    SoundOutputPool soundOutputPool;
    // record time.
    double recordAvg, processAvg, playAvg;


    public SoundThreadPool() {
        // thread control parameters.
        threadState = false;
        // thread manager parameters.
        mProcessThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES,
                                                    NUMBER_OF_CORES,
                                                    KEEP_ALIVE_TIME,
                                                    KEEP_ALIVE_TIME_UNIT,
                                                    mProcessWorkQueue);

        // sound parameter.
        soundInputPool = new SoundInputPool(SystemParameters.SAMPLERATE_LOW, 0);
        frequencyShift = new FrequencyShift(SystemParameters.SAMPLERATE_LOW, 1, 0, 0, 0);
        bandGain = new BandGain(SystemParameters.SAMPLERATE_LOW, 200, 7999, 10, 10, 10);
        soundOutputPool = new SoundOutputPool(SystemParameters.SAMPLERATE_LOW, 1, 2, 0);
    }

    public SoundThreadPool(int sampleRate, IOSetUnit ioSetUnit, int semitoneValue, ArrayList<BandGainSetUnit> bandGainSetUnitArrayList) {
        // thread control parameters.
        threadState = false;
        // thread manager parameters.
        mProcessThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES - 1,
                                                    NUMBER_OF_CORES - 1,
                                                    KEEP_ALIVE_TIME,
                                                    KEEP_ALIVE_TIME_UNIT,
                                                    mProcessWorkQueue);

        soundInputPool = new SoundInputPool(sampleRate, ioSetUnit.getInputType());
        //soundInputPool = new SoundInputPool(sampleRate, 4);
        frequencyShift = new FrequencyShift(sampleRate, 1, semitoneValue, 0, 0);  // do not set channel number as 2.
        bandGain = new BandGain(sampleRate, bandGainSetUnitArrayList);
        soundOutputPool = new SoundOutputPool(sampleRate, ioSetUnit.getChannelNumber(), 2, ioSetUnit.getOutputType());
    }

    public void threadStart() {
        threadState = true;
        soundInputPool.open();
        soundOutputPool.open();
        this.start();
    }

    public void threadStop() {
        threadState = false;
        soundInputPool.close();
        soundOutputPool.close();
        this.interrupt();
    }

    public void run() {
        Log.d("SoundThreadPool", "in run. thread start.");

        Runnable soundRecordRunnable = new SoundRecordRunnable();
        Runnable soundProcessRunnable = new SoundProcessRunnable();
        Runnable soundPlayRunnable = new SoundPlayRunnable();

        while (threadState) {
            mProcessThreadPool.execute(soundRecordRunnable);
            //mProcessThreadPool.execute(soundPlayRunnable);

            //Log.d("SoundThreadPool", "in run. queue size: " + soundDataQueue.size() + ", " + soundDataQueue2.size());
            Log.d("SoundThreadPool", "in run. exclude time: " + recordAvg + ", " + processAvg + ", " + playAvg);
        }

        mProcessThreadPool.shutdown();
        //soundDataQueue.clear();

        Log.d("SoundThreadPool", "in run. thread stop.");
    }

    /**
     *
     */
    private class SoundRecordRunnable implements Runnable {

        double ns0, ns1;
        SoundVectorUnit dataUnit;

        @Override
        public void run() {
            //Log.d("SoundThreadPool", "in SoundRecordRunnable run. in.");
            ns0 = System.nanoTime() / 1000000.0;

            dataUnit = soundInputPool.read();
            soundOutputPool.write(dataUnit);

            if (dataUnit != null) {
                soundDataQueue2.add(dataUnit);
            }

            ns1 = System.nanoTime() / 1000000.0;
            recordAvg = (recordAvg + (ns1 - ns0)) / 2;
        }
    }

    /**
     *
     */
    private class SoundProcessRunnable implements Runnable {

        double ns0, ns1;
        SoundVectorUnit dataUnit;

        @Override
        public void run() {
            //Log.d("SoundThreadPool", "in SoundProcessRunnable run. in.");
            ns0 = System.nanoTime() / 1000000.0;

            //Log.d("debug", "speaker size: " + getSpeakerBufferSize(sampleRate, 1));

            if (soundDataQueue.size() > 0) {
                dataUnit = soundDataQueue.poll();

                //if (dataUnit != null) {
                    //dataUnit = frequencyShift.process(dataUnit);


                    soundDataQueue2.add(dataUnit);
                //}
            } else {
                //Log.d("SoundThreadPool", "soundDataQueue no data.");
            }

            ns1 = System.nanoTime() / 1000000.0;
            processAvg = (processAvg + (ns1 - ns0)) / 2;
        }
    }

    /**
     *
     */
    private class SoundPlayRunnable implements Runnable {

        double ns0, ns1;
        SoundVectorUnit dataUnit;

        @Override
        public void run() {
            //Log.d("SoundThreadPool", "in SoundPlayRunnable run. in.");
            ns0 = System.nanoTime() / 1000000.0;

            if (soundDataQueue2.size() > 0) {
                dataUnit = soundDataQueue2.poll();

                if (dataUnit != null) {
                    soundOutputPool.write(dataUnit);
                }
            } else {
                //Log.d("SoundThreadPool", "soundDataQueue2 no data.");
            }

            ns1 = System.nanoTime() / 1000000.0;
            playAvg = (playAvg + (ns1 - ns0)) / 2;
        }
    }

    private class testRunnable implements Runnable {

        @Override
        public void run() {
            Log.d("SoundThreadPool", "run test.");
        }
    }
}
