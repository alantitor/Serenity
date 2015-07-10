package ntou.cs.lab505.serenity.sound.bandgain;

/*
 * IIR 濾波器
 * 此部分不用修改
 * 主要負責產生濾波器參數
 * 產生濾波器參數結果已驗證是正確的
 */

import android.util.Log;

import ntou.cs.lab505.serenity.math.Complex;


//enum Prototype{BUTTERWORTH,CHEBYSHEV};
//enum FilterType{LP,HP,BP};

public class IIR {
    final double PI = 3.14159;

    private int n;
    private double[] x;
    private double[] y;

    private double[] Num;
    private double[] DenC;

    // 預設濾波器參數
    private double[] band1_143_180_b   = {1.0e-003 * 0.1432, 0, 1.0e-003 * -0.4297, 0, 1.0e-003 * 0.4297, 0, 1.0e-003 * -0.1432};
    private double[] band1_143_180_a   = {-5.7117, 13.6650, -17.5272, 12.7116, -4.9426, 0.8050};
    private double[] band2_281_561_b   = {0.0011, 0, -0.0032, 0, 0.0032, 0, -0.0011};
    private double[] band2_281_561_a   = {-5.2918, 11.9196, -14.6161, 10.2887, -3.9433, 0.6436};
    private double[] band3_561_1120_b  = {0.0071, 0, -0.0214, 0, 0.0214, 0, -0.0071};
    private double[] band3_561_1120_a  = {-4.1504, 7.9278, -8.7551, 5.8955, -2.2947, 0.4125};
    private double[] band4_1110_2240_b = {0.0428,0, -0.1284, 0, 0.1284, 0, -0.0428};
    private double[] band4_1110_2240_a = {-1.1925, 1.7576, -1.2048, 1.0200, -0.3454, 0.1569};
    private double[] band5_2230_3540_b = {0.0612,0, -0.1836, 0, 0.1836, 0, -0.0612};
    private double[] band5_2230_3540_a = {2.9426, 4.0312, 3.4351, 1.9729, 0.6874, 0.1112};

    private double[] band_coefficient_a;
    private double[] band_coefficient_b;


    public IIR(int band)
    {
        switch(band)
        {
            case 1:
                band_coefficient_a = band1_143_180_a.clone();
                band_coefficient_b = band1_143_180_b.clone();
                break;
            case 2:
                band_coefficient_a = band2_281_561_a.clone();
                band_coefficient_b = band2_281_561_b.clone();
                break;
            case 3:
                band_coefficient_a = band3_561_1120_a.clone();
                band_coefficient_b = band3_561_1120_b.clone();
                break;
            case 4:
                band_coefficient_a = band4_1110_2240_a.clone();
                band_coefficient_b = band4_1110_2240_b.clone();
                break;
            case 5:
                band_coefficient_a = band5_2230_3540_a.clone();
                band_coefficient_b = band5_2230_3540_b.clone();
                break;
        }

        x = new double[band_coefficient_b.length - 1];
        y = new double[band_coefficient_a.length];
    }

    public IIR(int FilterOrder, int sampleRate, double Lcutoff, double Ucutoff)
    {
        Log.d("IIR", "in IIR. IIR filter range: " + Lcutoff + " to " + Ucutoff);

        this.n = FilterOrder;
        Lcutoff = Lcutoff / (sampleRate / 2);
        Ucutoff = Ucutoff / (sampleRate / 2);
        DenC = this.ComputeDenCoeffs(FilterOrder, Lcutoff, Ucutoff);
        band_coefficient_a = DenC;

        Num = this.ComputeNumCoeffs(FilterOrder, Lcutoff, Ucutoff, DenC);

        band_coefficient_b = Num;
        band_coefficient_a = getDen();

        Log.d("IIR", "in IIR. parameter a: ");
        for(int i=0;i<band_coefficient_a.length;i++) {
            Log.d("IIR", "in IIR. parameter a = " + Double.toString(band_coefficient_a[i]));
        }

        Log.d("IIR", "in IIR. parameter b: ");
        for(int i=0;i<2*FilterOrder+1;i++) {
            Log.d("IIR", "in IIR. parameter b = " + Double.toString(band_coefficient_b[i]));
        }

        x = new double[band_coefficient_b.length-1];
        y = new double[band_coefficient_a.length];
    }

    double[] ComputeLP(int FilterOrder)
    {
        double[] NumCoeffs = null;
        int m;
        int i;

        NumCoeffs = new double[FilterOrder + 1];
        if ( NumCoeffs == null ) {
            return ( null );
        }

        NumCoeffs[0] = 1;
        NumCoeffs[1] = FilterOrder;
        m = FilterOrder / 2;

        for (i = 2; i <= m; ++i) {
            NumCoeffs[i] = (double) (FilterOrder - i + 1) * NumCoeffs[i - 1] / i;
            NumCoeffs[FilterOrder - i] = NumCoeffs[i];
        }

        NumCoeffs[FilterOrder - 1] = FilterOrder;
        NumCoeffs[FilterOrder] = 1;

        return NumCoeffs;
    }

    private double[] ComputeHP( int FilterOrder )
    {
        double[] NumCoeffs;

        NumCoeffs = ComputeLP(FilterOrder);
        if (NumCoeffs == null ) {
            return ( null );
        }

        for (int i = 0; i <= FilterOrder; ++i) {
            if (i % 2 == 1) {
                NumCoeffs[i] = -NumCoeffs[i];
            }
        }

        return NumCoeffs;
    }

    private double[] TrinomialMultiply( int FilterOrder, double[] b, double[] c )
    {
        int i, j;
        double[] RetVal;

        RetVal =new double[4 * FilterOrder];
        if( RetVal == null ) return( null );

        RetVal[2] = c[0];
        RetVal[3] = c[1];
        RetVal[0] = b[0];
        RetVal[1] = b[1];

        for ( i = 1; i < FilterOrder; ++i ) {
            RetVal[2 * (2 * i + 1)]   += c[2 * i] * RetVal[2 * (2 * i - 1)]   - c[2 * i + 1] * RetVal[2 * (2 * i - 1) + 1];
            RetVal[2 * (2 * i + 1) + 1] += c[2 * i] * RetVal[2 * (2 * i - 1) + 1] + c[2 * i + 1] * RetVal[2 * (2 * i - 1)];

            for (j = 2 * i; j > 1; --j) {
                RetVal[2 * j] += b[2 * i] * RetVal[2 * (j - 1)]
                                    - b[2 * i + 1] * RetVal[2 * (j - 1) + 1]
                                    + c[2 * i] * RetVal[2 * (j - 2)]
                                    - c[2 * i + 1] * RetVal[2 * (j - 2) + 1];

                RetVal[2 * j + 1] += b[2 * i] * RetVal[2 * (j - 1) + 1]
                                        + b[2 * i + 1] * RetVal[2 * (j - 1)]
                                        + c[2 * i] * RetVal[2 * (j - 2) + 1]
                                        + c[2 * i + 1] * RetVal[2 * (j - 2)];
            }

            RetVal[2] += b[2 * i] * RetVal[0] - b[2 * i + 1] * RetVal[1] + c[2 * i];
            RetVal[3] += b[2 * i] * RetVal[1] + b[2 * i + 1] * RetVal[0] + c[2 * i + 1];
            RetVal[0] += b[2 * i];
            RetVal[1] += b[2 * i + 1];
        }

        return RetVal;
    }

    private double[] ComputeNumCoeffs(int FilterOrder,double Lcutoff, double Ucutoff,double[] DenC)
    {
        double[] TCoeffs;
        double[] NumCoeffs;

        Complex[] NormalizedKernel;

        double[] Numbers = new double[2 * FilterOrder + 1];


        for (int k = 0; k < 2 * FilterOrder + 1; k++)
        {
            Numbers[k] = k;
        }
        int i;

        NumCoeffs = new double[2 * FilterOrder + 1];
        if ( NumCoeffs == null ) {
            return( null );
        }

        NormalizedKernel = new Complex[2 * FilterOrder + 1];
        if ( NormalizedKernel == null ) {
            return( null );
        }

        TCoeffs = ComputeHP(FilterOrder);
        if ( TCoeffs == null ) {
            return( null );
        }

        for( i = 0; i < FilterOrder; ++i)
        {
            NumCoeffs[2 * i] = TCoeffs[i];
            NumCoeffs[2 * i + 1] = 0.0;
        }

        NumCoeffs[2 * FilterOrder] = TCoeffs[FilterOrder];
        double[] cp = new double[2];
        double Bw, Wn;
        cp[0] = 2 * 2.0 * Math.tan(PI * Lcutoff / 2.0);
        cp[1] = 2 * 2.0 * Math.tan(PI * Ucutoff / 2.0);

        Bw = cp[1] - cp[0];
        //center frequency
        Wn = Math.sqrt(cp[0]*cp[1]);
        Wn = 2*Math.atan2(Wn,4);
        //double kern;
        final Complex result = new Complex(-1,0);

        for(int k = 0; k < 2 * FilterOrder + 1; k++)
        {
            NormalizedKernel[k] = ((result.sqrt().times(-1)).times(Wn).times(Numbers[k])).exp();
        }

        double b=0;
        double den=0;

        for(int d = 0; d < 2 * FilterOrder + 1; d++)
        {
            b += (NormalizedKernel[d].times(NumCoeffs[d])).re();
            den += (NormalizedKernel[d].times(DenC[d])).re();
        }

        for(int c = 0; c < 2 * FilterOrder + 1; c++)
        {
            NumCoeffs[c] = (NumCoeffs[c] * den) / b;
        }

        return NumCoeffs;
    }

    private double[] ComputeDenCoeffs( int FilterOrder, double Lcutoff, double Ucutoff )
    {
        int k;            // loop variables
        double theta;     // PI * (Ucutoff - Lcutoff) / 2.0
        double cp;        // cosine of phi
        double st;        // sine of theta
        double ct;        // cosine of theta
        double s2t;       // sine of 2*theta
        double c2t;       // cosine 0f 2*theta
        double[] RCoeffs;     // z^-2 coefficients
        double[] TCoeffs;     // z^-1 coefficients
        double[] DenomCoeffs;     // dk coefficients
        double PoleAngle;      // pole angle
        double SinPoleAngle;     // sine of pole angle
        double CosPoleAngle;     // cosine of pole angle
        double a;         // workspace variables

        cp = Math.cos(PI * (Ucutoff + Lcutoff) / 2.0);
        theta = PI * (Ucutoff - Lcutoff) / 2.0;
        st = Math.sin(theta);
        ct = Math.cos(theta);
        s2t = 2.0 * st * ct;        // sine of 2*theta
        c2t = 2.0 * ct * ct - 1.0;  // cosine of 2*theta

        RCoeffs = new double[2 * FilterOrder];
        TCoeffs = new double[2 * FilterOrder];

        for( k = 0; k < FilterOrder; ++k )
        {
            PoleAngle = PI * (double) (2 * k + 1) / (double) (2 * FilterOrder);
            SinPoleAngle = Math.sin(PoleAngle);
            CosPoleAngle = Math.cos(PoleAngle);
            a = 1.0 + s2t * SinPoleAngle;
            RCoeffs[2 * k] = c2t / a;
            RCoeffs[2 * k + 1] = s2t * CosPoleAngle / a;
            TCoeffs[2 * k] = -2.0 * cp * (ct + st * SinPoleAngle) / a;
            TCoeffs[2 * k + 1] = -2.0 * cp*st * CosPoleAngle / a;
        }

        DenomCoeffs = TrinomialMultiply(FilterOrder, TCoeffs, RCoeffs );

        DenomCoeffs[1] = DenomCoeffs[0];
        DenomCoeffs[0] = 1.0;

        for( k = 3; k <= 2 * FilterOrder; ++k ) {
            DenomCoeffs[k] = DenomCoeffs[2 * k - 2];
        }

        return DenomCoeffs;
    }

    private double[] res = null;
    public short[] process(short[] value)
    {
        for(int i = 0; i < value.length; i++)
        {

            double tmp = band_coefficient_b[0]*value[i];
            for(int b_flag=1;b_flag<band_coefficient_b.length;b_flag++)
            {
                tmp += (band_coefficient_b[b_flag]*x[b_flag-1]);
            }

            for(int a_flag=0;a_flag<band_coefficient_a.length-1;a_flag++)
            {
                tmp -= (band_coefficient_a[a_flag+1]*y[a_flag]);
            }

            for(int x_flag=x.length;x_flag>=1;x_flag--)
            {
                if(x_flag!=1)
                    x[x_flag-1] = x[x_flag-2];
                else
                    x[x_flag-1] = value[i];
            }

            for(int y_flag=y.length;y_flag>=1;y_flag--)
            {
                if(y_flag!=1)
                    y[y_flag-1] = y[y_flag-2];
                else
                    y[y_flag-1] = tmp;
            }

            value[i] = (short)tmp;
        }

        return value;
    }

    public double[] getNum()
    {
        return band_coefficient_b;
    }

    public double[] getDen()
    {
        double[] res = new double[2 * n + 1];

        for(int i=0; i < 2 * n + 1; i++)
        {
            res[i] = DenC[i];
        }
        return res;
    }
}