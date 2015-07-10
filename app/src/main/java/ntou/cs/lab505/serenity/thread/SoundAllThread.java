package ntou.cs.lab505.serenity.thread;

import android.util.Log;

import java.util.ArrayList;

import ntou.cs.lab505.serenity.datastructure.BandGainSetUnit;
import ntou.cs.lab505.serenity.datastructure.IOSetUnit;
import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;
import ntou.cs.lab505.serenity.sound.bandgain.BandGain;
import ntou.cs.lab505.serenity.sound.frequencyshift.FrequencyShift;
import ntou.cs.lab505.serenity.stream.SoundInputPool;
import ntou.cs.lab505.serenity.stream.SoundOutputPool;

/**
 * Created by alan on 2015/7/9.
 */
public class SoundAllThread extends Thread {

    private boolean threadState;

    SoundInputPool soundInputPool;
    FrequencyShift frequencyShift;
    BandGain bandGain;
    SoundOutputPool soundOutputPool;


    public SoundAllThread() {
        soundInputPool = new SoundInputPool(8000, 0);
        frequencyShift = new FrequencyShift(8000, 1, 0, 0, 0);
        bandGain = new BandGain(8000, 200, 3000, 10, 10, 10);
        soundOutputPool = new SoundOutputPool(8000, 1, 2, 0);
    }

    public SoundAllThread(int sampleRate, IOSetUnit ioSetUnit, int semiValue, ArrayList<BandGainSetUnit> bandGainSetUnits) {
        soundInputPool = new SoundInputPool(sampleRate, ioSetUnit.getInputType());
        frequencyShift = new FrequencyShift(sampleRate, ioSetUnit.getChannelNumber(), semiValue, 0, 0);
        bandGain = new BandGain(sampleRate, bandGainSetUnits);
        soundOutputPool = new SoundOutputPool(sampleRate, ioSetUnit.getChannelNumber(), 2, ioSetUnit.getOutputType());
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

    public void run() {
         Log.d("SoundAllThread", "in run. thread start.");
        SoundVectorUnit dataUnit = null;


        while(threadState) {
            //this.setPriority(MAX_PRIORITY);
            //android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            //Log.d("SoundAllThread", "in run. priority: " + this.getPriority());

            long timeStartMs = System.currentTimeMillis();
            double timeStartNs = System.nanoTime() / 1000000.0;

            // read sound data from microphone.
            dataUnit = soundInputPool.read();

            long timeMs1 = System.currentTimeMillis();
            double timeNs1 = System.nanoTime() / 1000000.0;

            // shift sound frequency.
            dataUnit = frequencyShift.process(dataUnit);

            long timeMs2 = System.currentTimeMillis();
            double timeNs2 = System.nanoTime() / 1000000.0;

            // cut bands and gain db.
            dataUnit = bandGain.process(dataUnit);

            long timeMs3 = System.currentTimeMillis();
            double timeNs3 = System.nanoTime() / 1000000.0;

            // output sound data to speaker.
            soundOutputPool.write(dataUnit);


            // record information.
            long timeStopMs = System.currentTimeMillis();
            double timeStopNs = System.nanoTime() / 1000000;

            Log.d("SoundAllThread", "in run. exclude time: " + (timeStopNs - timeStartNs) + " " + (timeStopMs - timeStartMs));

            Log.d("SoundAllThread", "in run. module time: " + "(" + (timeNs1 - timeStartNs) + " " + (timeMs1 - timeStartMs) + ") "
                                                            + "(" + (timeNs2 - timeNs1) + " " + (timeMs2 - timeMs1) + ") "
                                                            + "(" +  (timeNs3 - timeNs2) + " " + (timeMs3 - timeMs2) + ") "
                                                            + "(" +  (timeStopNs - timeNs3) + " " + (timeStopMs - timeMs3) + ")");
        }

        Log.d("SoundAllThread", "in run. thread stop.");
    }
}
