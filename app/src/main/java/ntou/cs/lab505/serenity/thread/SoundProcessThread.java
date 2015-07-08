package ntou.cs.lab505.serenity.thread;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;

/**
 * Created by alan on 7/7/15.
 */
public class SoundProcessThread extends Thread {

    private boolean threadState;
    private LinkedBlockingQueue<SoundVectorUnit> soundInputQueue;
    private LinkedBlockingQueue<SoundVectorUnit> soundOutputQueue;


    public SoundProcessThread() {

    }

    public void setSoundInputQueue(LinkedBlockingQueue<SoundVectorUnit> soundInputQueue) {
        this.soundInputQueue = soundInputQueue;
    }

    public void setSoundOutputQueue(LinkedBlockingQueue<SoundVectorUnit> soundOutputQueue) {
        this.soundOutputQueue = soundOutputQueue;
    }

    public void threadStart() {
        this.threadState = true;
        this.start();
    }

    public void threadStop() {
        this.threadState = false;
        this.interrupt();
    }

    public void run() {
        Log.d("SoundProcessThread", "in run. thread start.");

        while (threadState) {

        }

        Log.d("soundProcessThread", "in run. thread stop.");
    }
}
