package ntou.cs.lab505.serenity.system;

import ntou.cs.lab505.serenity.datastructure.BandGainSetUnit;

/**
 * Created by alan on 2015/7/15.
 */
public class HearingAidParameters {
    // low gain
    public static final BandGainSetUnit[] BAND_LOWGAIN_L = {new BandGainSetUnit(0, 176, 353, 8, 15, 5),
                                                            new BandGainSetUnit(0, 353, 707, 5, 15, 6),
                                                            new BandGainSetUnit(0, 707, 3500, 8, 15, 8)};
    public static final BandGainSetUnit[] BAND_LOWGAIN_R = {new BandGainSetUnit(1, 176, 353, 8, 15, 5),
                                                            new BandGainSetUnit(1, 353, 707, 5, 15, 6),
                                                            new BandGainSetUnit(1, 707, 3500, 8, 15, 8)};
    // high gain
    public static final BandGainSetUnit[] BAND_HIGHGAIN_L = {new BandGainSetUnit(0, 88, 707, 4, 13, 5),
                                                                new BandGainSetUnit(0, 707, 1414, 2, 8, 4),
                                                                new BandGainSetUnit(0, 1414, 2828, 2, 5, 3),
                                                                new BandGainSetUnit(0, 2828, 3500, 3, 8, 2)};
    public static final BandGainSetUnit[] BAND_HIGHGAIN_R = {new BandGainSetUnit(1, 88, 707, 4, 13, 5),
                                                                new BandGainSetUnit(1, 707, 1414, 2, 8, 4),
                                                                new BandGainSetUnit(1, 1414, 2828, 2, 5, 3),
                                                                new BandGainSetUnit(1, 2828, 3500, 3, 8, 2)};
    // mix gain
    public static final BandGainSetUnit[] BAND_MIXGAIN_L = {new BandGainSetUnit(0, 176, 353, 2, 7, -2),
                                                            new BandGainSetUnit(0, 353, 707, 5, 20, 7),
                                                            new BandGainSetUnit(0, 707, 1414, 3, 8, 2),
                                                            new BandGainSetUnit(0, 1414, 2828, 6, 13, 8),
                                                            new BandGainSetUnit(0, 2828, 3500, 2, 7, 4)};
    public static final BandGainSetUnit[] BAND_MIXGAIN_R = {new BandGainSetUnit(1, 176, 353, 2, 7, -2),
                                                            new BandGainSetUnit(1, 353, 707, 5, 20, 7),
                                                            new BandGainSetUnit(1, 707, 1414, 3, 8, 2),
                                                            new BandGainSetUnit(1, 1414, 2828, 6, 13, 8),
                                                            new BandGainSetUnit(1, 2828, 3500, 2, 7, 4)};
    // default gain 1
    public static final BandGainSetUnit[] BAND_DEFAULTGAIN1_L = {new BandGainSetUnit(0, 176, 353, 0, 0, 0),
                                                                    new BandGainSetUnit(0, 353, 707, 0, 0, 0),
                                                                    new BandGainSetUnit(0, 707, 1414, 0, 0, 0),
                                                                    new BandGainSetUnit(0, 1414, 2828, 0, 0, 0),
                                                                    new BandGainSetUnit(0, 2828, 3500, 0, 0, 0)};
    public static final BandGainSetUnit[] BAND_DEFAULTGAIN1_R = {new BandGainSetUnit(1, 176, 353, 0, 0, 0),
                                                                    new BandGainSetUnit(1, 353, 707, 0, 0, 0),
                                                                    new BandGainSetUnit(1, 707, 1414, 0, 0, 0),
                                                                    new BandGainSetUnit(1, 1414, 2828, 0, 0, 0),
                                                                    new BandGainSetUnit(1, 2828, 3500, 0, 0, 0)};
    // default gain 2
    public static final BandGainSetUnit[] BAND_DEFAULTGAIN2_L = {new BandGainSetUnit(0, 143, 280, 0, 0, 0),
                                                                    new BandGainSetUnit(0, 281, 561, 0, 0, 0),
                                                                    new BandGainSetUnit(0, 561, 1120, 0, 0, 0),
                                                                    new BandGainSetUnit(0, 1110, 2240, 0, 0, 0),
                                                                    new BandGainSetUnit(0, 2230, 3540, 0, 0, 0)};
    public static final BandGainSetUnit[] BAND_DEFAULTGAIN2_R = {new BandGainSetUnit(1, 143, 280, 0, 0, 0),
                                                                    new BandGainSetUnit(1, 281, 561, 0, 0, 0),
                                                                    new BandGainSetUnit(1, 561, 1120, 0, 0, 0),
                                                                    new BandGainSetUnit(1, 1110, 2240, 0, 0, 0),
                                                                    new BandGainSetUnit(1, 2230, 3540, 0, 0, 0)};
}
