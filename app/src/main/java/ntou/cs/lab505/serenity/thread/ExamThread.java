package ntou.cs.lab505.serenity.thread;

import android.util.Log;

import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;
import ntou.cs.lab505.serenity.sound.bandgain.BandGain;
import ntou.cs.lab505.serenity.sound.frequencyshift.FrequencyShift;
import ntou.cs.lab505.serenity.stream.SoundInputPool;
import ntou.cs.lab505.serenity.stream.SoundOutputPool;

/**
 * Created by alan on 7/7/15.
 */
public class ExamThread implements Runnable {

    private boolean threadState = false;


    @Override
    public void run() {

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

        threadState = true;

        SoundInputPool soundInputPool = new SoundInputPool(8000, 0);
        soundInputPool.open();
        SoundOutputPool soundOutputPool = new SoundOutputPool(8000, 1, 2, 0);
        soundOutputPool.open();

        FrequencyShift frequencyShift = new FrequencyShift(8000, 1, 1, 0, 0);

        BandGain bandGain = new BandGain(8000, 200, 3000, 5, 5, 5);


        while (threadState) {
            //Log.d("SoundService", "debug: data: " + soundInputPool.read().getVectorLength());
            long timems0 = System.currentTimeMillis();
            long time0 = System.nanoTime();

            SoundVectorUnit data = soundInputPool.read();
            long timems1 = System.currentTimeMillis();
            long time1 = System.nanoTime();
            Log.d("SoundService", "debug: time1: " + (time1 - time0) / 1000000.0 + " " + (timems1 - timems0));

            data = frequencyShift.process(data);
            long timems2 = System.currentTimeMillis();
            long time2 = System.nanoTime();
            Log.d("SoundService", "debug: time2: " + (time2 - time1) / 1000000.0 + " " + (timems2 - timems1));

            data = bandGain.process(data);
            long timems3 = System.currentTimeMillis();
            long time3 = System.nanoTime();
            Log.d("SoundService", "debug: time3: " + (time3 - time2) / 1000000.0 + " " + (timems3 - timems2));

            soundOutputPool.write(data);
            long timems4 = System.currentTimeMillis();
            long time4 = System.nanoTime();
            Log.d("SoundService", "debug: time4: " + (time4 - time0) / 1000000.0 + " " + (timems4 - timems0));
            Log.d("SoundService", "debug: total time: " + (time4 - time0) / 1000000.0 + " " + (timems4 - timems0));

        }
    }
}
