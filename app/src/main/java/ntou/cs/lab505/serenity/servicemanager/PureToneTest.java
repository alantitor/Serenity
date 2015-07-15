package ntou.cs.lab505.serenity.servicemanager;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import ntou.cs.lab505.serenity.system.SystemParameters;
import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;
import ntou.cs.lab505.serenity.sound.bandgain.BandGain;
import ntou.cs.lab505.serenity.sound.frequencyshift.FrequencyShift;
import ntou.cs.lab505.serenity.sound.soundgeneration.HarmonicsGeneration;
import ntou.cs.lab505.serenity.stream.SoundOutputPool;

/**
 * Created by alan on 2015/7/12.
 */
public class PureToneTest extends Service {

    // sound parameters
    int valueFreq;
    int valueDb;
    int valueHarm;
    int valueSec;
    int valueBcLow;
    int valueBcHigh;
    int valueSemitone;
    int valueGain;
    int valueChannel = 0;
    int valueOutput = 0;
    int sampleRate = SystemParameters.SAMPLERATE_LOW;
    int frameSize = 1400;


    public class PureToneTestBinder extends Binder {
        public PureToneTest getService() {
            return PureToneTest.this;
        }
    }

    private final IBinder mBinder = new PureToneTestBinder();

    @Override
    public void onCreate() {
        //Log.d("PureToneTest", "in onCreate.");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //Log.d("PureToneTest", "in onDestroy.");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d("PureToneTest", "in onStartCommand.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Log.d("PureToneTest", "in onBind.");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //Log.d("PureToneTest", "in onUnbind.");
        return super.onUnbind(intent);
    }

    public void initParameters(int valueFreq, int valueDb, int valueHarm, int valueSec, int valueBcLow, int valueBcHigh, int valueSemitone, int valueGain, int valueChannel, int valueOutput) {
        this.valueFreq = valueFreq;
        this.valueDb = valueDb;
        this.valueHarm = valueHarm;
        this.valueSec = valueSec;
        this.valueBcLow = valueBcLow;
        this.valueBcHigh = valueBcHigh;
        this.valueSemitone = valueSemitone;
        this.valueGain = valueGain;
        this.valueChannel = valueChannel;
        this.valueOutput = valueOutput;
    }

    public void runTest() {

        HarmonicsGeneration harmonicsGeneration = new HarmonicsGeneration(sampleRate);
        FrequencyShift frequencyShift = new FrequencyShift(sampleRate, 1, valueSemitone, 0, 0);
        BandGain bandGain = new BandGain(sampleRate, valueBcLow, valueBcHigh, valueGain, valueGain, valueGain);
        SoundOutputPool soundOutputPool = new SoundOutputPool(sampleRate, 2, valueChannel, 0);
        SoundVectorUnit dataUnit = null;
        short[] soundVector = null;

        soundOutputPool.open();

        soundVector = harmonicsGeneration.generate(valueFreq, valueSec, valueDb, valueHarm);
        dataUnit = new SoundVectorUnit(soundVector);
        dataUnit = frequencyShift.process(dataUnit);
        dataUnit = bandGain.process(dataUnit);
        soundOutputPool.write(dataUnit);

        soundOutputPool.close();
    }
}