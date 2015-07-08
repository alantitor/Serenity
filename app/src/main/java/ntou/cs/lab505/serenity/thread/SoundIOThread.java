package ntou.cs.lab505.serenity.thread;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;
import ntou.cs.lab505.serenity.stream.SoundInputPool;
import ntou.cs.lab505.serenity.stream.SoundOutputPool;

/**
 * Created by alan on 2015/7/8.
 */
public class SoundIOThread extends Thread {

    private boolean threadState;
    private LinkedBlockingQueue<SoundVectorUnit> soundInputQueue;
    private LinkedBlockingQueue<SoundVectorUnit> soundOutputQueue;

    SoundInputPool soundInputPool;
    SoundOutputPool soundOutputPool;


    public SoundIOThread() {
        soundInputPool = new SoundInputPool(8000, 0);
        soundOutputPool = new SoundOutputPool(8000, 1, 2, 0);
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
        Log.d("SoundIOThread", "in run. thread start priority: " + android.os.Process.getThreadPriority(android.os.Process.myTid()));

        SoundVectorUnit dataUnit;

        while (threadState) {
            //this.setPriority(5);
            Log.d("SoundIOThread", "in run. thread priority: " + android.os.Process.getThreadPriority(android.os.Process.myTid()));

            /*
            // read sound data from microphone.
            dataUnit = soundInputPool.read();

            // pipe data to output queue.
            if (dataUnit != null && dataUnit.getVectorLength() > 0) {
                soundOutputQueue.add(dataUnit);
            }


            // output sound data to speaker.
            dataUnit = null;
            while (soundInputQueue.size() > 0) {
                dataUnit = soundInputQueue.poll();
                soundOutputPool.write(dataUnit);
            }
            */
        }

        Log.d("SoundIOThread", "in run. thread stop.");
    }
}
