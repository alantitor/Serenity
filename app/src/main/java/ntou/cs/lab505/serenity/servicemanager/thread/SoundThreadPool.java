package ntou.cs.lab505.serenity.servicemanager.thread;

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
    TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.NANOSECONDS;
    private final BlockingQueue<Runnable> mProcessWorkQueue = new LinkedBlockingQueue<Runnable>();
    // Creates a thread pool manager
    ThreadPoolExecutor mProcessThreadPool;

    // sound parameters..
    private int sampleRate = SystemParameters.SAMPLERATE_VERYHIGH;
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
        bandGain = new BandGain(SystemParameters.SAMPLERATE_LOW, 200, (sampleRate / 2 - 1), 10, 10, 10);
        soundOutputPool = new SoundOutputPool(SystemParameters.SAMPLERATE_LOW, 1, 2, 0);
    }

    public SoundThreadPool(int sampleRate, IOSetUnit ioSetUnit, int semitoneValue, ArrayList<BandGainSetUnit> bandGainSetUnitArrayList) {
        // thread control parameters.
        threadState = false;
        // thread manager parameters.
        mProcessThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES,
                                                    NUMBER_OF_CORES,
                                                    KEEP_ALIVE_TIME,
                                                    KEEP_ALIVE_TIME_UNIT,
                                                    mProcessWorkQueue);

        soundInputPool = new SoundInputPool(sampleRate, ioSetUnit.getInputType());
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

        Runnable soundRunnable = new SoundRunnable();

        while (threadState) {
            mProcessThreadPool.execute(soundRunnable);

            //Log.d("SoundThreadPool", "in run. work queue size: " + mProcessWorkQueue.size());
            Log.d("SoundThreadPool", "in run. exclude time: " + recordAvg + ", " + processAvg + ", " + playAvg);

            if (mProcessWorkQueue.size() > NUMBER_OF_CORES) {
                mProcessWorkQueue.clear();
            }
        }

        mProcessThreadPool.shutdown();

        Log.d("SoundThreadPool", "in run. thread stop.");
    }

    /**
     *
     */
    private class SoundRunnable implements Runnable {

        double ns0, ns1, ns2, ns3;
        SoundVectorUnit dataUnit;

        @Override
        public void run() {
            Thread.currentThread().setPriority(MAX_PRIORITY);

            ns0 = System.nanoTime() / 1000000.0;

            // get sound data.
            dataUnit = soundInputPool.read();

            if (dataUnit == null) {
                Thread.currentThread().interrupt();
            }

            ns1 = System.nanoTime() / 1000000.0;

            // shift sound frequency.
            dataUnit = frequencyShift.process(dataUnit);

            if (dataUnit == null) {
                Thread.currentThread().interrupt();
            }

            // cut sound and gain bands.
            dataUnit = bandGain.process(dataUnit);

            if (dataUnit == null) {
                Thread.currentThread().interrupt();
            }

            ns2 = System.nanoTime() / 1000000.0;

            // output sound data.
            soundOutputPool.write(dataUnit);

            ns3 = System.nanoTime() / 1000000.0;

            recordAvg = (recordAvg + (ns1 - ns0)) / 2;
            processAvg = (processAvg + (ns2 - ns1)) / 2;
            playAvg = (playAvg + (ns3 - ns2)) / 2;
        }
    }
}
