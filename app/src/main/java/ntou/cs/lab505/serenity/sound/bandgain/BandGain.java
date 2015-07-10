package ntou.cs.lab505.serenity.sound.bandgain;

import android.util.Log;

import java.util.ArrayList;

import ntou.cs.lab505.serenity.datastructure.BandGainSetUnit;
import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;

import static ntou.cs.lab505.serenity.sound.SoundTool.calculateDb;
import static ntou.cs.lab505.serenity.sound.SoundTool.channelMix;
import static ntou.cs.lab505.serenity.sound.SoundTool.mag2db;

/**
 * Created by alan on 7/6/15.
 */
public class  BandGain {

    // filter bank parameters.
    private int sampleRate;
    private int channelNumber;
    private final int filterOrder = 3;  // 設定濾波器階數. 階數愈高愈佳.
    private int filterBankNumberLeft = 0;  // 記錄濾波器個數(頻帶數).
    private int filterBankNumberRight = 0;

    // 動態頻帶切割
    private ArrayList<IIR> iirLeftList = new ArrayList<>();
    private ArrayList<IIR> iirRightList = new ArrayList<>();

    // gain parameters.
    private ArrayList<Double> gain40L = new ArrayList<>();
    private ArrayList<Double> gain60L = new ArrayList<>();
    private ArrayList<Double> gain80L = new ArrayList<>();
    private ArrayList<Double> gain40R = new ArrayList<>();
    private ArrayList<Double> gain60R = new ArrayList<>();
    private ArrayList<Double> gain80R = new ArrayList<>();

    // used to save processing sound.
    private ArrayList<short[]> soundBandListL = new ArrayList<>();
    private ArrayList<short[]> soundBandListR = new ArrayList<>();


    public BandGain(int sampleRate, int lowBand, int highBand, int gain40, int gain60, int gain80) {
        this.sampleRate = sampleRate;
        this.channelNumber = 1;

        iirLeftList.add(new IIR(this.filterOrder, sampleRate, lowBand, highBand));
        gain40L.add(mag2db(gain40));
        gain60L.add(mag2db(gain60));
        gain80L.add(mag2db(gain80));

        filterBankNumberLeft = 1;
    }

    public BandGain(int sampleRate, ArrayList<BandGainSetUnit> bandGainSetUnits) {
        // set sample rate.
        this.sampleRate = sampleRate;

        // initial IIR object.
        for (int count = 0; count < bandGainSetUnits.size(); count++) {
            if (bandGainSetUnits.get(count).getLr() == 0) {
                // left channel.
                iirLeftList.add(new IIR(this.filterOrder, sampleRate, bandGainSetUnits.get(count).getLowBand(), bandGainSetUnits.get(count).getHighBand()));
                gain40L.add(mag2db(bandGainSetUnits.get(count).getGain40()));
                gain60L.add(mag2db(bandGainSetUnits.get(count).getGain60()));
                gain80L.add(mag2db(bandGainSetUnits.get(count).getGain80()));

                filterBankNumberLeft++;
            } else {
                // right channel.
                iirRightList.add(new IIR(this.filterOrder, sampleRate, bandGainSetUnits.get(count).getLowBand(), bandGainSetUnits.get(count).getHighBand()));
                gain40R.add(mag2db(bandGainSetUnits.get(count).getGain40()));
                gain60R.add(mag2db(bandGainSetUnits.get(count).getGain60()));
                gain80R.add(mag2db(bandGainSetUnits.get(count).getGain80()));

                filterBankNumberRight++;
            }
        }

        // check channel number.
        if (filterBankNumberRight == 0) {
            this.channelNumber = 1;
        } else {
            this.channelNumber = 2;
        }
    }

    public SoundVectorUnit process(SoundVectorUnit inputUnit) {

        // check input data state.
        if (inputUnit == null || inputUnit.getVectorLength() == 0) {
            return null;
        }


        // extra sound data.
        short[] tempVector = inputUnit.getLeftChannel();

        //Log.d("BandGain", "in process. length: " + inputUnit.getLeftChannel().length + " " + tempVector.length);
        //Log.d("BandGain", "in run. db origin: " + calculateDb(tempVector));
        //Log.d("BandGain", "in process. data origin: " + tempVector[100] + tempVector[101] + tempVector[102] + tempVector[103]);
        SoundVectorUnit outputUnit = null;

        // process data.
        if (channelNumber == 1) {
            // read left channel data.
            for (int count = 0; count < filterBankNumberLeft; count++) {
                // cut bands.
                soundBandListL.add(iirLeftList.get(count).process(tempVector.clone()));  // should I use clone()?
                //Log.d("BandGain", "in run. db before: " + calculateDb(soundBandListL.get(count)));
                //Log.d("BandGain", "in process. data band: " + soundBandListL.get(count)[100] + soundBandListL.get(count)[101] + soundBandListL.get(count)[102] + soundBandListL.get(count)[103]);
                // gain db.
                soundBandListL.set(count, autoGain(soundBandListL.get(count), count, 0));
                //Log.d("BandGain", "in run. db after: " + calculateDb(soundBandListL.get(count)));
                //Log.d("BandGain", "in process. data band gain: " + soundBandListL.get(count)[100] + soundBandListL.get(count)[101] + soundBandListL.get(count)[102] + soundBandListL.get(count)[103]);
            }
        } else if (channelNumber == 2) {
            // read left channel data.
            for (int count = 0; count < filterBankNumberLeft; count++) {
                // cut bands.
                soundBandListL.add(iirLeftList.get(count).process(tempVector.clone()));  // should I use clone()?
                //Log.d("BandGain", "in run. left db before: " + calculateDb(soundBandListL.get(count)));
                // gain db.
                soundBandListL.set(count, autoGain(soundBandListL.get(count), count, 0));
                //Log.d("BandGain", "in run. left db after: " + calculateDb(soundBandListL.get(count)));
            }
            // read right channel data.
            for (int count = 0; count < filterBankNumberRight; count++) {
                // cut bands.
                soundBandListR.add(iirRightList.get(count).process(tempVector.clone()));
                //Log.d("BandGain", "in run. right db before: " + calculateDb(soundBandListR.get(count)));
                // gain db.
                soundBandListR.set(count, autoGain(soundBandListR.get(count), count, 1));
                //Log.d("BandGain", "in run. right db after: " + calculateDb(soundBandListR.get(count)));
            }
        } else {
            //
        }


        // mix bands.
        if (channelNumber == 1) {
            outputUnit = new SoundVectorUnit(channelMix(soundBandListL), null);
            //outputUnit = new SoundVectorUnit(tempVector);
            //Log.d("BandGain", "in run. db left mix: " + calculateDb(outputUnit.getLeftChannel()));
        } else if (channelNumber == 2) {
            outputUnit = new SoundVectorUnit(channelMix(soundBandListL), channelMix(soundBandListR));
            //Log.d("BandGain", "in run. db left mix: " + calculateDb(outputUnit.getLeftChannel()));
            //Log.d("BandGain", "in run. db right mix: " + calculateDb(outputUnit.getRightChannel()));
        } else {
            outputUnit = new SoundVectorUnit(channelMix(soundBandListL), null);
        }

        // clear variables.
        soundBandListL.clear();
        soundBandListR.clear();

        return outputUnit;
    }

    private short[] autoGain(short[] dataVector, int index, int lr) {

        int db = calculateDb(dataVector);

        if (lr == 0) {  // left channel.
            if (db>= 40 && db < 60) {
                dataVector = processGain(dataVector, gain40L.get(index), db);
            } else if (db >= 60 && db < 80) {
                dataVector = processGain(dataVector, gain60L.get(index), db);
            } else if (db >= 80) {
                dataVector = processGain(dataVector, gain80L.get(index), db);
            } else {
                dataVector = processGain(dataVector, gain40L.get(index), db);
            }
        } else {  // right channel.
            if (db>= 40 && db < 60) {
                dataVector = processGain(dataVector, gain40R.get(index), db);
            } else if (db >= 60 && db < 80) {
                dataVector = processGain(dataVector, gain60R.get(index), db);
            } else if (db >= 80) {
                dataVector = processGain(dataVector, gain80R.get(index), db);
            } else {
                dataVector = processGain(dataVector, gain40R.get(index), db);
            }
        }

        return dataVector;
    }

    private short[] processGain(short[] soundVector, double gainValue, int db) {

        int elem = 0;

        for (int i = 0; i < soundVector.length; i++) {
            elem = (int) (soundVector[i] * gainValue);

            if (elem > Short.MAX_VALUE) {
                soundVector[i] = Short.MAX_VALUE;
            } else if (elem < Short.MIN_VALUE) {
                soundVector[i] = Short.MIN_VALUE;
            } else {
                soundVector[i] = (short) elem;
            }
        }

        return soundVector;
    }
}
