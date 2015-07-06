package ntou.cs.lab505.serenity.thread;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;
import ntou.cs.lab505.serenity.stream.SoundInputPool;

/**
 * Created by alan on 2015/7/4.
 */
public class SoundInputThread implements Runnable {

    SoundInputPool soundInputPool;
    LinkedBlockingQueue<SoundVectorUnit> soundVectorUnits;


    public SoundInputThread(int sampleRate, int mode, LinkedBlockingQueue<SoundVectorUnit> soundVectorUnits) {
        this.soundInputPool = new SoundInputPool(sampleRate, mode);
        this.soundVectorUnits = soundVectorUnits;
    }

    public void open() {
        soundInputPool.open();
    }

    public void close() {
        soundInputPool.close();
    }

    @Override
    public void run() {

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        while (true) {
            soundVectorUnits.add(soundInputPool.read());
            Log.d("SoundInptThread", "in run. length: " + soundVectorUnits.size());
        }
    }
}
