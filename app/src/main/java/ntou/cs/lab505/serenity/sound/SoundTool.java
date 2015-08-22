package ntou.cs.lab505.serenity.sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;


/**
 * This class contain channel process function, file process function and db calculate function.
 */
public class SoundTool {

    /**
     * merge two one-channel sound vectors into a two-channels sound vector.
     * @param leftChannel
     * @param rightChannel
     * @return
     */
    public static short[] channelTwo2One(short[] leftChannel, short[] rightChannel) {

        short[] outputVector = null;

        if (leftChannel != null && rightChannel != null) {  // we have two channel data.
            //Log.d("SoundTool", "in channelTwo2One. I am here 1.");
            outputVector = new short[leftChannel.length * 2];
            // data: LRLRLRLR...
            for (int i = 0; i < leftChannel.length; i++) {
                outputVector[i * 2] = leftChannel[i];
                outputVector[i * 2 + 1] = rightChannel[i];
            }
        } else if (leftChannel == null && rightChannel != null){  // we only have right channel data. set zero to left channel.
            //Log.d("SoundTool", "in channelTwo2One. I am here 2.");
            outputVector = new short[rightChannel.length * 2];
            // data: 0R0R0R0R...
            for (int i = 0; i < rightChannel.length; i++) {
                outputVector[i * 2] = 0;
                outputVector[i * 2 + 1] = rightChannel[i];
            }
        } else if (leftChannel != null && rightChannel == null){  // we only have left channel data. set zero to right channel.
            //Log.d("SoundTool", "in channelTwo2One. I am here 3.");
            outputVector = new short[leftChannel.length * 2];
            // data: L0L0L0L0...
            for (int i = 0; i < leftChannel.length; i++) {
                outputVector[i * 2] = leftChannel[i];
                outputVector[i * 2 + 1] = 0;
            }
        } else {
            //Log.d("SoundTool", "in channelTwo2One. I am here 4.");
            outputVector = new short[2];
            outputVector[0] = 0;
            outputVector[1] = 0;
        }

        return outputVector;
    }

    /**
     * mix bands to a sound frame.
     * @param soundBands
     * @return
     */
    public static short[] channelMix(ArrayList<short[]> soundBands) {
        short[] tempVector = new short[soundBands.get(0).length];
        int temp = 0;

        for (int trace = 0; trace < soundBands.get(0).length; trace++) {
            for (int i = 0; i < soundBands.size(); i++) {
                temp += soundBands.get(i)[trace];
            }

            tempVector[trace] = (short) temp;
            temp = 0;
        }

        return tempVector;
    }

    /*
     * 計算音量
     * data - 欲計算的資料
     * return 音量
     */
    public static int calculateDb(short[] data) {
        double sum = 0;

        for (int i = 0; i < data.length; i++) {
            sum += Math.pow(data[i], 2);
        }
        sum = 10 * Math.log10(sum / data.length);

        return (int)sum;
    }

    /**
     * calculate sound frame db.
     * @param data
     * @return
     */
    public static double calculateDbDouble(short[] data) {
        double sum = 0;

        for (int i = 0; i < data.length; i++) {
            sum += Math.pow(data[i], 2);
        }
        sum = 10 * Math.log10(sum / data.length);

        return sum;
    }

    /**
     * save data vector to file.
     * @param data
     * @param fileName
     */
    public static void saveVectorToDataFile(short[] data, String fileName) {
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName + ".txt");
        FileOutputStream fOut;
        OutputStreamWriter fWriter;

        if (data == null) {
            return ;
        }

        try {
            file.createNewFile();
            fOut = new FileOutputStream(file);
            fWriter = new OutputStreamWriter(fOut);
            for (int i = 0; i < data.length; i++) {
                fWriter.append(data[i] + ",");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double mag2db(double value) {
        return Math.pow(10, value / 20);
    }

    /**
     * check device sample rate support.
     */
    public static void checkSystemSupportSampleRate() {
        for (int rate : new int[]{8000, 11025, 16000, 22050, 44100}) {  // add the rates you wish to check against
            int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_CONFIGURATION_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                Log.d("SoundTool", "in checkSystemSupportSampleRate. supprt sample rate: " + rate);
            }
        }
    }

    public static int getSpeakerBufferSize(int sampleRate, int channelNumber) {

        if (channelNumber == 1) {
            return AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
        } else {
            return AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        }
    }
}
