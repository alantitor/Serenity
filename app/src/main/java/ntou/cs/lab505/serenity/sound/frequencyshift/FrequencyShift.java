package ntou.cs.lab505.serenity.sound.frequencyshift;

import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;

/**
 * Created by alan on 7/6/15.
 */
public class FrequencyShift {

    private JNISoundTouch soundtouch = new JNISoundTouch();  // sound process object

    private int sampleRate;
    private int channels;
    private int pitchSemiTones;
    private float rateChange;
    private float tempoChange;
    private long startTime;
    private long stopTime;


    /**
     * constructor
     */
    public FrequencyShift() {
        this.sampleRate = 16000;
        this.channels = 1;
        this.pitchSemiTones = 0;
        this.rateChange = 0.0f;
        this.tempoChange = 0.0f;

        // set sound parameters
        soundtouch.setSampleRate(sampleRate);
        soundtouch.setChannels(channels);
        soundtouch.setPitchSemiTones(pitchSemiTones);  // Changes the sound pitch or key while keeping the original tempo (speed).
        soundtouch.setRateChange(rateChange);  // Changes both tempo and pitch together as if a vinyl disc was played at different RPM rate.
        soundtouch.setTempoChange(tempoChange);  // Changes the sound to play at faster or slower tempo than originally without affecting the sound pitch.
    }

    /**
     *
     * @param sampleRate
     * @param channels
     * @param pitchSemiTones
     * @param rateChange
     * @param tempoChange
     */
    public FrequencyShift(int sampleRate, int channels, int pitchSemiTones, int rateChange, int tempoChange) {
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.pitchSemiTones = pitchSemiTones;
        this.rateChange = rateChange;
        this.tempoChange = tempoChange;

        // set sound parameters
        soundtouch.setSampleRate(sampleRate);
        soundtouch.setChannels(channels);
        soundtouch.setPitchSemiTones(pitchSemiTones);  // Changes the sound pitch or key while keeping the original tempo (speed).
        soundtouch.setRateChange(rateChange);  // Changes both tempo and pitch together as if a vinyl disc was played at different RPM rate.
        soundtouch.setTempoChange(tempoChange);  // Changes the sound to play at faster or slower tempo than originally without affecting the sound pitch.
    }

    /**
     * shift sound frequency
     * @param inputUnit
     * @return
     */
    public SoundVectorUnit process(SoundVectorUnit inputUnit) {

        // check input data state.
        if (inputUnit == null || inputUnit.getVectorLength() == 0) {
            return null;
        }


        // put sound data into SoundTouch Object.
        soundtouch.putSamples(inputUnit.getLeftChannel(), inputUnit.getLeftChannel().length);


        // temp variables.
        short[] temp, temp2;

        // receive sound from SoundTouch object.
        short[] outputVector = soundtouch.receiveSamples();
        while ((temp = soundtouch.receiveSamples()).length > 0) {
            temp2 = outputVector;  // swap old data.
            outputVector = new short[temp.length + temp2.length];  // extend array size.
            System.arraycopy(temp2, 0, outputVector, 0, temp2.length);  // copy old data.
            System.arraycopy(temp, 0, outputVector, temp2.length, temp.length);  // copy new data.
        }


        // make sound structure.
        if (outputVector != null && outputVector.length > 0) {
            return new SoundVectorUnit(outputVector);
        } else {
            return null;
        }
    }
}
