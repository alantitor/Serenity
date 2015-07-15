package ntou.cs.lab505.serenity.servicemanager.thread;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

import ntou.cs.lab505.serenity.system.SystemParameters;
import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;
import ntou.cs.lab505.serenity.stream.SoundInputPool;
import ntou.cs.lab505.serenity.stream.SoundOutputPool;

/**
 * Created by alan on 2015/7/8.
 *
 * this class used to manager sound input and sound output function.
 */
public class SoundIOThread extends Thread {

    private boolean threadState;
    private LinkedBlockingQueue<SoundVectorUnit> soundInputQueue;
    private LinkedBlockingQueue<SoundVectorUnit> soundOutputQueue;

    SoundInputPool soundInputPool;
    SoundOutputPool soundOutputPool;


    public SoundIOThread() {
        soundInputPool = new SoundInputPool(SystemParameters.SAMPLERATE_LOW, 0);
        soundOutputPool = new SoundOutputPool(SystemParameters.SAMPLERATE_LOW, 1, 2, 0);
    }

    public SoundIOThread(int sampleRate, int soundInMode, int soundOutChannelNumber, int soundOutLR, int SoundOutMode) {
        soundInputPool = new SoundInputPool(sampleRate, soundInMode);
        soundOutputPool = new SoundOutputPool(sampleRate, soundOutChannelNumber, soundOutLR, SoundOutMode);
    }

    public void setSoundInputQueue(LinkedBlockingQueue<SoundVectorUnit> soundInputQueue) {
        this.soundInputQueue = soundInputQueue;
    }

    public void setSoundOutputQueue(LinkedBlockingQueue<SoundVectorUnit> soundOutputQueue) {
        this.soundOutputQueue = soundOutputQueue;
    }

    public void threadStart(){
        soundInputPool.open();
        soundOutputPool.open();
        this.threadState = true;
        this.start();
    }

    public void threadStop() {
        soundInputPool.close();
        soundOutputPool.close();
        this.threadState = true;
        this.interrupt();
    }

    public boolean getThreadState() {
        return threadState;
    }

    public void run() {
        Log.d("SoundIOThread", "in run. thread start.");
        SoundVectorUnit dataUnit = null;

        while (threadState) {
            long timeStartMs = System.currentTimeMillis();
            long timeStartNs = System.nanoTime();
            //this.setPriority(MAX_PRIORITY);

            // read sound data from microphone.
            dataUnit = soundInputPool.read();

            // pipe data to output queue.
            if (dataUnit != null && dataUnit.getVectorLength() > 0) {
                soundOutputQueue.add(dataUnit);
            }

            // output sound data to speaker.
            //dataUnit = null;
            while (soundInputQueue.size() > 0) {
                dataUnit = soundInputQueue.poll();
                soundOutputPool.write(dataUnit);
            }

            // record information.
            long timeStopMs = System.currentTimeMillis();
            long timeStopNs = System.nanoTime();
            Log.d("SoundIOThread", "in run. time: " + (timeStopNs - timeStartNs) + " " + (timeStopMs - timeStartMs));
            Log.d("SoundIOThread", "in run. priority: " + this.getPriority());
        }

        Log.d("SoundIOThread", "in run. thread stop.");
    }
}
