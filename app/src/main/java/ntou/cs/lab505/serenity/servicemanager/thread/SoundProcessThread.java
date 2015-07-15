package ntou.cs.lab505.serenity.servicemanager.thread;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

import ntou.cs.lab505.serenity.system.SystemParameters;
import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;
import ntou.cs.lab505.serenity.sound.bandgain.BandGain;
import ntou.cs.lab505.serenity.sound.frequencyshift.FrequencyShift;

/**
 * Created by alan on 7/7/15.
 *
 * this class used to manager sound process function.
 */
public class SoundProcessThread extends Thread {

    private boolean threadState;
    private LinkedBlockingQueue<SoundVectorUnit> soundInputQueue;
    private LinkedBlockingQueue<SoundVectorUnit> soundOutputQueue;

    FrequencyShift frequencyShift;
    BandGain bandGain;


    public SoundProcessThread() {
        frequencyShift = new FrequencyShift();
        bandGain = new BandGain(SystemParameters.SAMPLERATE_LOW, 200, 7999, 5, 5, 5);
    }

    public SoundProcessThread(int sampleRate, int channelNumber, int semi, int pitch, int rate, int tempo, int lowBand, int highBand, int gain40, int gain60, int gain80) {
        frequencyShift = new FrequencyShift(sampleRate, channelNumber, pitch, rate, tempo);
        bandGain = new BandGain(sampleRate, lowBand, highBand, gain40, gain60, gain80);
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
        SoundVectorUnit dataUnit = null;

        while (threadState) {
            long timeStartMs = System.currentTimeMillis();
            long timeStartNs = System.nanoTime();
            //this.setPriority(MAX_PRIORITY);

            // read sound data from queue.
            dataUnit = soundInputQueue.poll();
            // check data state.
            if (dataUnit != null && dataUnit.getVectorLength() > 0) {
                //Log.d("SoundProcessThread", "in run. no data, continue.");
                continue;
            }

            // process data.
            dataUnit = frequencyShift.process(dataUnit);

            // pipe data.
            soundOutputQueue.add(soundInputQueue.poll());

            // record information.
            long timeStopMs = System.currentTimeMillis();
            long timeStopNs = System.nanoTime();
            //Log.d("SoundProcessThread", "in run. time: " + (timeStopNs - timeStartNs) + " " + (timeStopMs - timeStartMs));
            //Log.d("SoundProcessThread", "in run. priority: " + this.getPriority());
        }

        Log.d("soundProcessThread", "in run. thread stop.");
    }
}
