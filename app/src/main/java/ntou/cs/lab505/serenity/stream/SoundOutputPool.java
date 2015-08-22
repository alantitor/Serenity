package ntou.cs.lab505.serenity.stream;

import android.util.Log;

import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;
import ntou.cs.lab505.serenity.stream.device.Speaker;
import ntou.cs.lab505.serenity.stream.device.WriteFile;

import static ntou.cs.lab505.serenity.sound.SoundTool.channelTwo2One;

/**
 * Created by alan on 2015/7/3.
 */
public class SoundOutputPool {

    private int sampleRate;
    private int channelNumber;
    private int lr;
    private int mode;

    Speaker speaker;
    WriteFile writeFile;

    /**
     *
     * @param sampleRate
     * @param channelNumber
     * @param lr
     * @param mode 0: speaker, 1: write to data file, 2: write to wmv file
     */
    public SoundOutputPool(int sampleRate, int channelNumber, int lr, int mode) {
        //Log.d("SoundOutputPool", "in SoundOutputPool. initial success.");

        this.sampleRate = sampleRate;
        this.channelNumber = channelNumber;
        this.lr = lr;
        this.mode = mode;

        // initial object.
        switch (mode) {
            case 0:
                speaker = new Speaker(sampleRate, channelNumber);
                break;
            case 1:
                writeFile = new WriteFile(0, "speaker");
                break;
            case 2:
                writeFile = new WriteFile(1, "speaker");
                break;
            default:
        }
    }

    public void open() {
        switch (mode) {
            case 0:
                speaker.open();
                break;
            case 1:
            case 2:
                writeFile.open();
                break;
            default:
                break;
        }
    }

    public void close() {
        switch (mode) {
            case 0:
                speaker.close();
                break;
            case 1:
            case 2:
                writeFile.close();
                break;
            default:
                break;
        }
    }

    /**
     *
     */
    public void write(SoundVectorUnit soundUnit) {

        short[] soundVector = null;

        if (soundUnit == null) {
            return;
        }
        if (soundUnit.getLeftChannel() == null || soundUnit.getVectorLength() == 0) {
            return;
        }


        // merge channel sound data.
        if (this.channelNumber == 1) {
            // one channel.
            soundVector = soundUnit.getLeftChannel();
            //Log.d("SoundOutputPool", "in run. data: " + outputDataVector[101] + outputDataVector[102] + outputDataVector[103] + outputDataVector[104]);
        } else if (this.channelNumber == 2) {
            // two channel.
            if (soundUnit.getChannelNumber() == 1) {
                if (lr == 0) {  // only output left ear.
                    //Log.d("SoundOutputPool", "in run. play sound at left ear.");
                    soundVector = channelTwo2One(soundUnit.getLeftChannel(), null);
                } else if (lr == 1){  // only output right ear.
                    //Log.d("SoundOutputPool", "in run. play sound at right ear.");
                    soundVector = channelTwo2One(null, soundUnit.getLeftChannel());
                } else {
                    //Log.d("SoundOutputPool", "in run. play sound at two ears.");
                    soundVector = channelTwo2One(soundUnit.getLeftChannel(), soundUnit.getLeftChannel());
                }
            } else {
                //Log.d("SoundOutputPool", "in run. play sound at two ears.");
                soundVector = channelTwo2One(soundUnit.getLeftChannel(), soundUnit.getRightChannel());
            }
        } else {
            // one channel.
            this.channelNumber = 1;  // !!!
            soundVector = soundUnit.getLeftChannel();
        }


        // pipe sound data.
        switch (mode) {
            case 0:
                pipeVectorToSpeaker(soundVector);
                break;
            case 1:
            case 2:
                pipeVectorToFile(soundVector);
                break;
            default:
                break;
        }
    }

    private void pipeVectorToSpeaker(short[] outputVector) {
        speaker.write(outputVector);
    }

    private void pipeVectorToFile(short[] outputVector) {
        writeFile.write(outputVector);
    }
}
