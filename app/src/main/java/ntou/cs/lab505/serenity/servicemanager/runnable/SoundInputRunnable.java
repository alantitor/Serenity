package ntou.cs.lab505.serenity.servicemanager.runnable;

import ntou.cs.lab505.serenity.datastructure.IOSetUnit;

/**
 * Created by alan on 2015/7/11.
 */
public class SoundInputRunnable implements Runnable {

    // sound parameters
    private int sampleRate;
    IOSetUnit ioSetUnit;

    public SoundInputRunnable() {
        this.sampleRate = 16000;
        this.ioSetUnit = new IOSetUnit();
    }

    public SoundInputRunnable(int sampleRate, IOSetUnit ioSetUnit) {
        this.sampleRate = sampleRate;
        this.ioSetUnit = ioSetUnit;
    }

    @Override
    public void run() {



    }
}
