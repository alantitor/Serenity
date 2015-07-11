package ntou.cs.lab505.serenity.servicemanager.thread;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

import ntou.cs.lab505.serenity.datastructure.IOSetUnit;
import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;
import ntou.cs.lab505.serenity.stream.SoundInputPool;

/**
 * Created by alan on 2015/7/9.
 *
 * this class used to manager sound input function.
 */
public class SoundInputThread extends Thread {

    private boolean threadState = false;

    SoundInputPool soundInputPool;
    private LinkedBlockingQueue<SoundVectorUnit> soundOutputQueue = new LinkedBlockingQueue<>();


    public SoundInputThread() {
        soundInputPool = new SoundInputPool(16000, 0);
    }

    public SoundInputThread(int sampleRate, int mode) {
        soundInputPool = new SoundInputPool(sampleRate, mode);
    }

    public void setOutputQueue(LinkedBlockingQueue<SoundVectorUnit> soundOutputQueue) {
        this.soundOutputQueue = soundOutputQueue;
    }

    public void threadStart() {
        this.threadState = true;
        soundInputPool.open();
        this.start();
    }

    public void threadStop() {
        this.threadState = false;
        soundInputPool.close();
        this.interrupt();
    }

    public boolean getThreadState() {
        return this.threadState;
    }

    public void run() {
        Log.d("SoundInputThread", "in run. thread start.");
        SoundVectorUnit dataUnit = null;

        while (threadState) {
            dataUnit = soundInputPool.read();

            if (dataUnit != null && dataUnit.getVectorLength() > 0) {
                soundOutputQueue.add(dataUnit);
            }
        }

        Log.d("SoundInputThread", "in run. thread stop.");
    }
}
