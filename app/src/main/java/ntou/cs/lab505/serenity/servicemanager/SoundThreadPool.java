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
    private int sampleRate;
    private IOSetUnit ioSetUnit;
    private ArrayList<BandGainSetUnit> bandGainSetUnitArrayList;


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
        this.sampleRate = 16000;
        this.ioSetUnit = new IOSetUnit();
        this.bandGainSetUnitArrayList = new ArrayList<>();
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
        // sound parameters.
        this.sampleRate = sampleRate;
        this.ioSetUnit = ioSetUnit;
        this.bandGainSetUnitArrayList = bandGainSetUnitArrayList;
    }

    public void threadStart() {
        threadState = true;
        this.start();
    }

    public void threadStop() {
        threadState = false;
        this.interrupt();
    }

    public void run() {
        Log.d("SoundThreadPool", "in run. thread start.");
        SoundVectorUnit soundVectorUnit = null;


        while (threadState) {

        }

        Log.d("SoundThreadPool", "in run. thread stop.");
    }
}
