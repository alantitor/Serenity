package ntou.cs.lab505.serenity.stream.device;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by alan on 6/11/15.
 */
public class Microphone {

    private int sampleRate;
    //private int channelNumber;
    private int recordBufSize;
    private AudioRecord audioRecord;
    private short[] dataVector;


    public Microphone(int sampleRate) {
        this.sampleRate = sampleRate;

        recordBufSize = AudioRecord.getMinBufferSize(sampleRate,
                                                        AudioFormat.CHANNEL_CONFIGURATION_MONO,
                                                        AudioFormat.ENCODING_PCM_16BIT);

        recordBufSize = recordBufSize / 2;

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                        sampleRate,
                                        AudioFormat.CHANNEL_CONFIGURATION_MONO,  // CHANNEL_IN_MONO
                                        AudioFormat.ENCODING_PCM_16BIT,
                                        recordBufSize);
        dataVector = new short[recordBufSize];
        Log.d("debug", "microphone buffer size: " + recordBufSize);
    }

    public void open() {
        //Log.d("Microphone", "in open.");
        audioRecord.startRecording();
    }

    public void close() {
        //Log.d("Microphone", "in close.");
        audioRecord.release();
    }

    public short[] read() {

        int buffReadResult = audioRecord.read(dataVector, 0, recordBufSize);

        if (buffReadResult > 0){
            return dataVector;
        } else {
            return null;
        }
    }
}
