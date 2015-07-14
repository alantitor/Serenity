package ntou.cs.lab505.serenity.stream.device;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Array;

import ntou.cs.lab505.serenity.database.SystemSetting;

/**
 * Created by alan on 6/11/15.
 */
public class Speaker {

    private int speakerBufSize;
    private AudioTrack audioTrack;
    private int sampleRate;
    private int channelNumber;

    /**
     *
     * @param sampleRate
     * @param channelNumber 1: one channel. 2: two channels.
     */
    public Speaker(int sampleRate, int channelNumber) {

        this.sampleRate = sampleRate;

        if (channelNumber == 1) {
            speakerBufSize = AudioTrack.getMinBufferSize(sampleRate,
                                                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                                                            AudioFormat.ENCODING_PCM_16BIT);
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                                        sampleRate,
                                        AudioFormat.CHANNEL_CONFIGURATION_MONO,
                                        AudioFormat.ENCODING_PCM_16BIT,
                                        speakerBufSize,
                                        AudioTrack.MODE_STREAM);
        } else if (channelNumber == 2) {
            speakerBufSize = AudioTrack.getMinBufferSize(sampleRate,
                                                            AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                                                            AudioFormat.ENCODING_PCM_16BIT);
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                                        sampleRate,
                                        AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                                        AudioFormat.ENCODING_PCM_16BIT,
                                        speakerBufSize,
                                        AudioTrack.MODE_STREAM);
        } else {
            //
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void open() {
        audioTrack.play();
        audioTrack.setVolume(0.1f);
    }

    public void close() {
        audioTrack.stop();
        audioTrack.release();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void write(short[] dataVector) {

        if (dataVector == null || dataVector.length == 0) {
            return ;
        }

        //audioTrack.setVolume(1.0f);
        audioTrack.write(dataVector, 0, dataVector.length);
        //audioTrack.setVolume(0.0f);
        //audioTrack.flush();  // is this code danger?
    }

    /**
     * check which output device is used.
     * @param type
     * @return
     */
    public static boolean checkOutputDeviceState(int type) {

        boolean state = false;

        switch (type) {
            case 0:
                // build in speaker
                state = isSpeakerphoneOn();
                break;
            case 1:
                // headset speaker
                state = isWiredHeadsetOn();
                break;
            case 2:
                // bluetooth speaker
                state = isBluetoothA2dpOn();
                break;
            default:
                state = false;
        }

        return state;
    }

    private static boolean isWiredHeadsetOn() {
        boolean state = false;

        return state;
    }

    private static boolean isSpeakerphoneOn() {
        boolean state = false;

        return state;
    }

    private static boolean isBluetoothA2dpOn() {
        boolean state = false;

        return state;
    }
}
