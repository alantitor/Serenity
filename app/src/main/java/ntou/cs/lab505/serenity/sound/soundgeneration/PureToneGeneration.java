package ntou.cs.lab505.serenity.sound.soundgeneration;

/**
 * Created by alan on 6/10/15.
 */
public class PureToneGeneration {

    private int sampleRate;

    /**
     * initial sample rate.
     * @param sampleRate
     */
    public PureToneGeneration(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    /**
     * Generate pure tone.
     * @param frequency
     * @param second
     * @param db
     * @return
     */
    public short[] generate(int frequency, int second, double db) {

        double targetDb = Math.pow(10, db / 20);
        // create sin wave
        short[] sin = new short[second * sampleRate];
        double samplingInterval = (double) (sampleRate / frequency);

        for (int i = 0; i < sin.length; i++) {
            double angle = (2.0 * Math.PI * i) / samplingInterval;

            int temp = (int) (Math.sin(angle) * targetDb);

            // avoid vector overflow.
            if (temp > Short.MAX_VALUE) {
                sin[i] = Short.MAX_VALUE;
            } else if (temp < Short.MIN_VALUE) {
                sin[i] = Short.MIN_VALUE;
            } else {
                sin[i] = (short) (Math.sin(angle) * targetDb);
            }
        }

        return sin;
    }

    public static int pureToneExpectedLength(int sampleRate, int sec) {
        return sampleRate * sec;
    }
}
