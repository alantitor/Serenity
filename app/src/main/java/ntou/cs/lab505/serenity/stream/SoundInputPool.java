package ntou.cs.lab505.serenity.stream;


import android.util.Log;

import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;
import ntou.cs.lab505.serenity.sound.soundgeneration.HarmonicsGeneration;
import ntou.cs.lab505.serenity.sound.soundgeneration.PureToneGeneration;
import ntou.cs.lab505.serenity.stream.device.Microphone;
import ntou.cs.lab505.serenity.stream.device.ReadFile;

/**
 * Created by alan on 2015/7/3.
 */
public class SoundInputPool {

    private int sampleRate;
    private int mode;

    Microphone microphone;
    ReadFile readFile;
    PureToneGeneration pureToneGeneration;


    /**
     *
     * @param sampleRate
     * @param mode 0: microphone, 1: read from inside stream, 2: read from data file, 3: read from wmv file, 4: pure tone
     */
    public SoundInputPool(int sampleRate, int mode) {
        Log.d("SoundInputPool", "in SoundInputPool. initial object.");
        this.sampleRate = sampleRate;
        this.mode = mode;

        // initial object.
        switch (mode) {
            case 0:
                microphone = new Microphone(sampleRate);
                break;
            case 1:
                break;
            case 2:
            case 3:
                break;
            case 4:
                pureToneGeneration = new PureToneGeneration(sampleRate);
                break;
            default:
        }
    }

    public void open() {
        switch (mode) {
            case 0:
                microphone.open();
                break;
            case 1:
                break;
            case 2:
            case 3:
                break;
            default:
                break;
        }
    }

    public void close() {
        switch (mode) {
            case 0:
                microphone.close();
                break;
            case 1:
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            default:
                break;
        }
    }

    public SoundVectorUnit read() {

        short[] soundVector = null;
        SoundVectorUnit soundUnit = null;

        switch (mode) {
            case 0:
                soundVector = pipeMicrophoneToVector();
                break;
            case 1:
                break;
            case 2:
            case 3:
                break;
            case 4:
                soundVector = pureToneGeneration.generate(1000, 1, 60);
                break;
            default:
                break;
        }


        if (soundVector != null && soundVector.length > 0) {
            soundUnit = new SoundVectorUnit(soundVector);
            return soundUnit;
        } else {
            return null;
        }
    }

    private short[] pipeMicrophoneToVector() {
        return microphone.read();
    }

    private short[] pipeFileToVector() {
        return null;
    }
}