package ntou.cs.lab505.serenity.servicemanager.thread;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;
import ntou.cs.lab505.serenity.stream.SoundOutputPool;

/**
 * Created by alan on 2015/7/9.
 *
 * this class used to manager sound output function.
 */
public class SoundOutputThread extends Thread {

    private boolean threadState = false;

    SoundOutputPool soundOutputPool;
    private LinkedBlockingQueue<SoundVectorUnit> soundInputQueue = new LinkedBlockingQueue<>();


    public SoundOutputThread() {
        soundOutputPool = new SoundOutputPool(16000, 1, 2, 0);
    }

    public SoundOutputThread(int sampleRate, int channelNumber, int lr, int mode) {
        soundOutputPool = new SoundOutputPool(sampleRate, channelNumber, lr, mode);
    }

    public void setInputQueue(LinkedBlockingQueue<SoundVectorUnit> soundInputQueue) {
        this.soundInputQueue = soundInputQueue;
    }

    public void threadStart() {
        this.threadState = true;
        soundOutputPool.open();
        this.start();
    }

    public void threadStop() {
        this.threadState = false;
        soundOutputPool.close();
        this.interrupt();
    }

    public boolean getThreadState() {
        return this.threadState;
    }

    public void run() {
        Log.d("SoundOutputThread", "in run. thread start.");
        SoundVectorUnit dataUnit = null;

        while (threadState) {
            dataUnit = soundInputQueue.poll();
            if (dataUnit != null && dataUnit.getVectorLength() > 0) {
                soundOutputPool.write(dataUnit);
            }
        }

        Log.d("SoundOutputThread", "in run. thread stop.");
    }
}
