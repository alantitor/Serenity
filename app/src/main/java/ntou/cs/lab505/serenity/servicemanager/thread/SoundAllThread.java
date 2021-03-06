package ntou.cs.lab505.serenity.servicemanager.thread;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import java.util.ArrayList;

import ntou.cs.lab505.serenity.sound.SoundTool;
import ntou.cs.lab505.serenity.system.SystemParameters;
import ntou.cs.lab505.serenity.datastructure.BandGainSetUnit;
import ntou.cs.lab505.serenity.datastructure.IOSetUnit;
import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;
import ntou.cs.lab505.serenity.sound.bandgain.BandGain;
import ntou.cs.lab505.serenity.sound.frequencyshift.FrequencyShift;
import ntou.cs.lab505.serenity.stream.SoundInputPool;
import ntou.cs.lab505.serenity.stream.SoundOutputPool;

/**
 * Created by alan on 2015/7/9.
 *
 * wrap sound process modules.
 * this class contains sound input, frequency shift, band gain and sound output modules.
 * all works run at one thread.
 */
public class SoundAllThread extends Thread {

    // control thread state.
    private boolean threadState;
    // sound process object.
    SoundInputPool soundInputPool;
    FrequencyShift frequencyShift;
    BandGain bandGain;
    SoundOutputPool soundOutputPool;

    private int sampleRate;
    private int channelNumber;


    /**
     * construct default object.
     */
    public SoundAllThread() {
        this.sampleRate = SystemParameters.SAMPLERATE_LOW;
        this.channelNumber = 1;

        soundInputPool = new SoundInputPool(sampleRate, 0);
        frequencyShift = new FrequencyShift(sampleRate, 1, 0, 0, 0);
        bandGain = new BandGain(sampleRate, 200, 7999, 10, 10, 10);
        soundOutputPool = new SoundOutputPool(sampleRate, 1, 2, 0);
    }

    /**
     * construct object with parameters.
     * @param sampleRate
     * @param ioSetUnit
     * @param semiValue
     * @param bandGainSetUnits
     */
    public SoundAllThread(int sampleRate, IOSetUnit ioSetUnit, int semiValue, ArrayList<BandGainSetUnit> bandGainSetUnits) {
        soundInputPool = new SoundInputPool(sampleRate, ioSetUnit.getInputType());
        frequencyShift = new FrequencyShift(sampleRate, 1, semiValue, 0, 0);  // do not set channel number as 2.
        bandGain = new BandGain(sampleRate, bandGainSetUnits);
        soundOutputPool = new SoundOutputPool(sampleRate, ioSetUnit.getChannelNumber(), 2, ioSetUnit.getOutputType());

        this.sampleRate = sampleRate;
        this.channelNumber = ioSetUnit.getChannelNumber();
    }

    /**
     * start thread.
     */
    public void threadStart(){
        soundInputPool.open();
        soundOutputPool.open();
        this.threadState = true;
        this.start();
    }

    /**
     * stop thread.
     */
    public void threadStop() {
        soundInputPool.close();
        soundOutputPool.close();
        this.threadState = true;
        this.interrupt();
    }

    /**
     * return thread state.
     * @return
     */
    public boolean getThreadState() {
        return this.threadState;
    }

    /**
     * process sound.
     */
    public void run() {
        //Log.d("SoundAllThread", "in run. thread start.");

        // set thread priority.
        this.setPriority(MAX_PRIORITY);
        SoundVectorUnit dataUnit = null;
        // record time.
        long timeStartMs, timeMs1, timeMs2, timeMs3, timeStopMs;
        double timeStartNs, timeNs1, timeNs2, timeNs3, timeStopNs;
        double avg1 = 0, avg2 = 0, avg3 = 0, avg4 = 0;

        while(threadState) {
            // record start process time. record microphone start read time.
            timeStartMs = System.currentTimeMillis();
            timeStartNs = System.nanoTime() / 1000000.0;

            // read sound data from microphone.
            dataUnit = soundInputPool.read();

            // record microphone finish read time. record star shift frequency time.
            timeMs1 = System.currentTimeMillis();
            timeNs1 = System.nanoTime() / 1000000.0;

            if (dataUnit == null) {
                //Log.d("SoundAllThread", "in run. dataUnit is null at step 1.");
                continue;
            }

            // shift sound frequency.
            dataUnit = frequencyShift.process(dataUnit);

            // record finish shift frequency time. record start band gain time.
            timeMs2 = System.currentTimeMillis();
            timeNs2 = System.nanoTime() / 1000000.0;

            if (dataUnit == null) {
                //Log.d("SoundAllThread", "in run. dataUnit is null. at step 2.");
                continue;
            }

            // cut bands and gain db.
            dataUnit = bandGain.process(dataUnit);

            // record finish band gain time. record speaker star write time.
            timeMs3 = System.currentTimeMillis();
            timeNs3 = System.nanoTime() / 1000000.0;

            if (dataUnit == null) {
                //Log.d("SoundAllThread", "in run. dataUnit is null. at step 3.");
                continue;
            }

            // output sound data to speaker.
            soundOutputPool.write(dataUnit);

            // record speaker finish write time.
            timeStopMs = System.currentTimeMillis();
            timeStopNs = System.nanoTime() / 1000000;

            // output time information.
            /*
            if (dataUnit != null) {
                Log.d("SoundAllThread", "in run. exclude time: " + (timeStopNs - timeStartNs) + " " + (timeStopMs - timeStartMs));
                Log.d("SoundAllThread", "in run. module time: " + "(" + (timeNs1 - timeStartNs) + " " + (timeMs1 - timeStartMs) + ") "
                                                                        + "(" + (timeNs2 - timeNs1) + " " + (timeMs2 - timeMs1) + ") "
                                                                        + "(" + (timeNs3 - timeNs2) + " " + (timeMs3 - timeMs2) + ") "
                                                                        + "(" + (timeStopNs - timeNs3) + " " + (timeStopMs - timeMs3) + ")");
            }
            */
            if (dataUnit != null) {
                Log.d("SoundAllThread", "in run. exclude time: " + (avg1 +avg2 + avg3 + avg4));
                avg1 = ((timeNs1 - timeStartNs) + avg1) / 2;
                avg2 = ((timeNs2 - timeNs1) + avg2) / 2;
                avg3 = ((timeNs3 - timeNs2) + avg3) / 2;
                avg4 = ((timeStopNs - timeNs3) + avg4) / 2;
                Log.d("SoundAllThread", "in run. module time: " + "(" + avg1 + ") "
                                                                + "(" + avg2 + ") "
                                                                + "(" + avg3 + ") "
                                                                + "(" + avg4 + ")");
            }
        }

        //Log.d("SoundAllThread", "in run. thread stop.");
    }
}
