package ntou.cs.lab505.serenity.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ntou.cs.lab505.serenity.R;
import ntou.cs.lab505.serenity.database.BandSettingAdapter;
import ntou.cs.lab505.serenity.database.IOSettingAdapter;
import ntou.cs.lab505.serenity.datastructure.BandGainSetUnit;
import ntou.cs.lab505.serenity.system.HearingAidParameters;

import static ntou.cs.lab505.serenity.R.layout.view_bandgain;

/**
 * Design layout of band setting activity.
 */
public class BandSettingActivity extends Activity {

    private int channelNumber;
    Spinner modeSpinner;
    private boolean spinnerFirst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_setting);

        LinearLayout rightControlLayout = (LinearLayout) findViewById(R.id.control_right_activity_band_setting);
        TextView leftControlTV = (TextView) findViewById(R.id.controltitle_left_activity_band_setting);

        // create drop down list.
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.mode_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner = (Spinner) findViewById(R.id.spinner_activity_band_setting);
        modeSpinner.setAdapter(spinnerAdapter);

        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerFirst == false) {
                    spinnerFirst = true;
                    return ;
                }

                switch (i) {
                    case 0:
                        loadData();
                        break;
                    case 1:
                        loadData(HearingAidParameters.BAND_LOWGAIN_L, HearingAidParameters.BAND_LOWGAIN_R);
                        break;
                    case 2:
                        loadData(HearingAidParameters.BAND_HIGHGAIN_L, HearingAidParameters.BAND_HIGHGAIN_R);
                        break;
                    case 3:
                        loadData(HearingAidParameters.BAND_MIXGAIN_L, HearingAidParameters.BAND_MIXGAIN_R);
                        break;
                    case 4:
                        loadData(HearingAidParameters.BAND_DEFAULTGAIN1_L, HearingAidParameters.BAND_DEFAULTGAIN1_R);
                        break;
                    case 5:
                        loadData(HearingAidParameters.BAND_DEFAULTGAIN2_L, HearingAidParameters.BAND_DEFAULTGAIN2_R);
                        break;
                    default:
                        loadData();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });


        // left channel border.
        SeekBar leftSeekbar = (SeekBar) findViewById(R.id.seekbar_left_activity_band_setting);
        // right channel border.
        SeekBar rightSeekBar = (SeekBar) findViewById(R.id.seekbar_right_activity_band_setting);

        // left channel listener
        leftSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LinearLayout leftRootLayout = (LinearLayout) findViewById(R.id.drawarea_left_activity_band_setting);
                TextView leftCountTV = (TextView) findViewById(R.id.count_left_activity_band_setting);

                leftRootLayout.removeAllViews();
                leftCountTV.setText(String.valueOf(progress));

                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                for (int count = 0; count < progress; count++) {
                    leftRootLayout.addView(layoutInflater.inflate(view_bandgain, null));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // right channel listener.
        rightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LinearLayout rightRootLayout = (LinearLayout) findViewById(R.id.drawarea_right_activity_band_setting);
                TextView rightCountTV = (TextView) findViewById(R.id.count_right_activity_band_setting);

                rightRootLayout.removeAllViews();
                rightCountTV.setText(String.valueOf(progress));

                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                for (int count = 0; count < progress; count++) {
                    rightRootLayout.addView(layoutInflater.inflate(view_bandgain, null));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // check channel number.
        IOSettingAdapter ioSettingAdapter = new IOSettingAdapter(this.getApplicationContext());
        ioSettingAdapter.open();
        this.channelNumber = ioSettingAdapter.getData().getChannelNumber();
        //Log.d("BandSettingActivity", "in onCreate. channel number: " + this.channelNumber);
        ioSettingAdapter.close();

        // if channel is one, invisible right channel border.
        if (this.channelNumber == 1) {
            leftControlTV.setText("聲道");
            rightControlLayout.setVisibility(View.GONE);
        }
    }

    /**
     * save data to database.
     */
    @Override
    public void onResume() {
        super.onResume();

        loadData();
    }

    /**
     * get data from database.
     */
    @Override
    public void onPause() {
        // get layout objects.
        LinearLayout leftRootLayout = (LinearLayout) findViewById(R.id.drawarea_left_activity_band_setting);
        LinearLayout rightRootLayout = (LinearLayout) findViewById(R.id.drawarea_right_activity_band_setting);
        View v;
        EditText lowBand, highBand, gain40, gain60, gain80;
        int leftItemCount = leftRootLayout.getChildCount();
        int rightItemCount = rightRootLayout.getChildCount();
        //Log.d("BandSettingActivity", "in onPause. left item count: " + leftItemCount);
        //Log.d("BandSettingActivity", "in onPause. right item count" + rightItemCount);

        BandSettingAdapter bandSettingAdapter = new BandSettingAdapter(this.getApplicationContext());
        bandSettingAdapter.open();
        // delete old data.
        bandSettingAdapter.deleteData();

        ArrayList<BandGainSetUnit> bandGainSetUnitArrayList = new ArrayList<>();
        BandGainSetUnit bandGainSetUnit;

        // insert new data.
        // process left channel data.
        for (int count = 0; count < leftItemCount; count++) {
            // get values from view fields.
            v = leftRootLayout.getChildAt(count);
            lowBand = (EditText) v.findViewById(R.id.lowBand_view_bandgain);
            highBand = (EditText) v.findViewById(R.id.highband_view_bandgain);
            gain40 = (EditText) v.findViewById(R.id.gain40_view_bandgain);
            gain60 = (EditText) v.findViewById(R.id.gain60_view_bandgain);
            gain80 = (EditText) v.findViewById(R.id.gain80_view_bandgain);

            // check data state.
            // delete empty bands.
            if (lowBand.getText().toString().matches("") && highBand.getText().toString().matches("")
                    && gain40.getText().toString().matches("") && gain60.getText().toString().matches("") && gain80.getText().toString().matches("")) {
                continue;
            }
            // insert default value.
            if (lowBand.getText().toString().matches("")) {
                lowBand.setText("200");
            }
            if (highBand.getText().toString().matches("")) {
                highBand.setText("3999");
            }
            if (gain40.getText().toString().matches("")) {
                gain40.setText("0");
            }
            if (gain60.getText().toString().matches("")) {
                gain60.setText("0");
            }
            if (gain80.getText().toString().matches("")) {
                gain80.setText("0");
            }

            //Log.d("BandSettingActivity", "in onPause. lowBand: " + lowBand.getText().toString());
            //Log.d("BandSettingActivity", "in onPause. highBand: " + highBand.getText().toString());
            //Log.d("BandSettingActivity", "in onPause. gain40: " + gain40.getText().toString());
            //Log.d("BandSettingActivity", "in onPause. gain60: " + gain60.getText().toString());
            //Log.d("BandSettingActivity", "in onPause. gain80: " + gain80.getText().toString());

            // generate object to save values.
            bandGainSetUnit = new  BandGainSetUnit(0,
                    Integer.parseInt(lowBand.getText().toString()),
                    Integer.parseInt(highBand.getText().toString()),
                    Integer.parseInt(gain40.getText().toString()),
                    Integer.parseInt(gain60.getText().toString()),
                    Integer.parseInt(gain80.getText().toString()));
            bandGainSetUnitArrayList.add(bandGainSetUnit);

        }

        // process right channel data.
        if (this.channelNumber == 2) {
            for (int count = 0; count < rightItemCount; count ++) {
                // get values from view fields.
                v = rightRootLayout.getChildAt(count);
                lowBand = (EditText) v.findViewById(R.id.lowBand_view_bandgain);
                highBand = (EditText) v.findViewById(R.id.highband_view_bandgain);
                gain40 = (EditText) v.findViewById(R.id.gain40_view_bandgain);
                gain60 = (EditText) v.findViewById(R.id.gain60_view_bandgain);
                gain80 = (EditText) v.findViewById(R.id.gain80_view_bandgain);

                // check data state.
                // delete empty bands.
                if (lowBand.getText().toString().matches("") && highBand.getText().toString().matches("")
                        && gain40.getText().toString().matches("") && gain60.getText().toString().matches("") && gain80.getText().toString().matches("")) {
                    continue;
                }
                // insert default value.
                if (lowBand.getText().toString().matches("")) {
                    lowBand.setText("200");
                }
                if (highBand.getText().toString().matches("")) {
                    highBand.setText("3999");
                }
                if (gain40.getText().toString().matches("")) {
                    gain40.setText("0");
                }
                if (gain60.getText().toString().matches("")) {
                    gain60.setText("0");
                }
                if (gain80.getText().toString().matches("")) {
                    gain80.setText("0");
                }

                // generate object to save values.
                bandGainSetUnit = new  BandGainSetUnit(1,
                        Integer.parseInt(lowBand.getText().toString()),
                        Integer.parseInt(highBand.getText().toString()),
                        Integer.parseInt(gain40.getText().toString()),
                        Integer.parseInt(gain60.getText().toString()),
                        Integer.parseInt(gain80.getText().toString()));
                bandGainSetUnitArrayList.add(bandGainSetUnit);
            }
        }

        // save data to database.
        bandSettingAdapter.saveData(bandGainSetUnitArrayList);
        // close database
        bandSettingAdapter.close();

        super.onPause();
    }

    private void loadData() {

        // get data from database.
        BandSettingAdapter bandSettingAdapter = new BandSettingAdapter(this.getApplicationContext());
        bandSettingAdapter.open();
        ArrayList<BandGainSetUnit> bandGainSetUnitArrayList = bandSettingAdapter.getData();
        bandSettingAdapter.close();

        // check band number.
        int leftItemCount = 0;
        int rightItemCount = 0;

        for (int count = 0; count < bandGainSetUnitArrayList.size(); count++) {
            if (bandGainSetUnitArrayList.get(count).getLr() == 0) {
                leftItemCount++;
            } else if (bandGainSetUnitArrayList.get(count).getLr() == 1) {
                rightItemCount++;
            } else {
                //
            }
        }

        // set seekBar progress.
        SeekBar leftSeekBar = (SeekBar) findViewById(R.id.seekbar_left_activity_band_setting);
        SeekBar rightSeekBar = (SeekBar) findViewById(R.id.seekbar_right_activity_band_setting);
        leftSeekBar.setProgress(leftItemCount);
        rightSeekBar.setProgress(rightItemCount);

        TextView leftCountTV = (TextView) findViewById(R.id.count_left_activity_band_setting);
        TextView rightCountTV = (TextView) findViewById(R.id.count_right_activity_band_setting);
        leftCountTV.setText(leftItemCount + "");  // integer to string.
        rightCountTV.setText(rightItemCount + "");

        // load data to layout.
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout leftBorder = (LinearLayout) findViewById(R.id.drawarea_left_activity_band_setting);
        LinearLayout rightBorder = (LinearLayout) findViewById(R.id.drawarea_right_activity_band_setting);
        leftBorder.removeAllViews();
        rightBorder.removeAllViews();

        for (int count = 0; count < bandGainSetUnitArrayList.size(); count++) {
            if (bandGainSetUnitArrayList.get(count).getLr() == 0) {
                View view = layoutInflater.inflate(view_bandgain, null);
                EditText ETLowBand = (EditText) view.findViewById(R.id.lowBand_view_bandgain);
                EditText ETHighBand = (EditText) view.findViewById(R.id.highband_view_bandgain);
                EditText ETGain40 = (EditText) view.findViewById(R.id.gain40_view_bandgain);
                EditText ETGain60 = (EditText) view.findViewById(R.id.gain60_view_bandgain);
                EditText ETGain80 = (EditText) view.findViewById(R.id.gain80_view_bandgain);

                ETLowBand.setText(bandGainSetUnitArrayList.get(count).getLowBand() + "");
                ETHighBand.setText(bandGainSetUnitArrayList.get(count).getHighBand() + "");
                ETGain40.setText(bandGainSetUnitArrayList.get(count).getGain40() + "");
                ETGain60.setText(bandGainSetUnitArrayList.get(count).getGain60() + "");
                ETGain80.setText(bandGainSetUnitArrayList.get(count).getGain80() + "");

                leftBorder.addView(view);
            } else if (bandGainSetUnitArrayList.get(count).getLr() == 1) {
                View view = layoutInflater.inflate(view_bandgain, null);
                EditText ETLowBand = (EditText) view.findViewById(R.id.lowBand_view_bandgain);
                EditText ETHighBand = (EditText) view.findViewById(R.id.highband_view_bandgain);
                EditText ETGain40 = (EditText) view.findViewById(R.id.gain40_view_bandgain);
                EditText ETGain60 = (EditText) view.findViewById(R.id.gain60_view_bandgain);
                EditText ETGain80 = (EditText) view.findViewById(R.id.gain80_view_bandgain);

                ETLowBand.setText(bandGainSetUnitArrayList.get(count).getLowBand() + "");
                ETHighBand.setText(bandGainSetUnitArrayList.get(count).getHighBand() + "");
                ETGain40.setText(bandGainSetUnitArrayList.get(count).getGain40() + "");
                ETGain60.setText(bandGainSetUnitArrayList.get(count).getGain60() + "");
                ETGain80.setText(bandGainSetUnitArrayList.get(count).getGain80() + "");

                rightBorder.addView(view);
            } else {
                //Log.d("BandSettingActivity", "in onResume. wrong data in database.");
            }
        }
    }

    private void loadData(BandGainSetUnit[] bandGainSetUnitsL, BandGainSetUnit[] bandGainSetUnitsR) {
        // set seekBar progress.
        SeekBar leftSeekBar = (SeekBar) findViewById(R.id.seekbar_left_activity_band_setting);
        SeekBar rightSeekBar = (SeekBar) findViewById(R.id.seekbar_right_activity_band_setting);
        leftSeekBar.setProgress(bandGainSetUnitsL.length);
        rightSeekBar.setProgress(bandGainSetUnitsR.length);

        TextView leftCountTV = (TextView) findViewById(R.id.count_left_activity_band_setting);
        TextView rightCountTV = (TextView) findViewById(R.id.count_right_activity_band_setting);
        leftCountTV.setText(bandGainSetUnitsL.length + "");  // integer to string.
        rightCountTV.setText(bandGainSetUnitsR.length + "");

        // load data to layout.
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout leftBorder = (LinearLayout) findViewById(R.id.drawarea_left_activity_band_setting);
        LinearLayout rightBorder = (LinearLayout) findViewById(R.id.drawarea_right_activity_band_setting);
        leftBorder.removeAllViews();
        rightBorder.removeAllViews();

        for (int count = 0; count < bandGainSetUnitsL.length; count++) {
            View view = layoutInflater.inflate(view_bandgain, null);
            EditText ETLowBand = (EditText) view.findViewById(R.id.lowBand_view_bandgain);
            EditText ETHighBand = (EditText) view.findViewById(R.id.highband_view_bandgain);
            EditText ETGain40 = (EditText) view.findViewById(R.id.gain40_view_bandgain);
            EditText ETGain60 = (EditText) view.findViewById(R.id.gain60_view_bandgain);
            EditText ETGain80 = (EditText) view.findViewById(R.id.gain80_view_bandgain);

            ETLowBand.setText(bandGainSetUnitsL[count].getLowBand() + "");
            ETHighBand.setText(bandGainSetUnitsL[count].getHighBand() + "");
            ETGain40.setText(bandGainSetUnitsL[count].getGain40() + "");
            ETGain60.setText(bandGainSetUnitsL[count].getGain60() + "");
            ETGain80.setText(bandGainSetUnitsL[count].getGain80() + "");

            leftBorder.addView(view);
        }

        for (int count = 0; count < bandGainSetUnitsR.length; count++) {
            View view = layoutInflater.inflate(view_bandgain, null);
            EditText ETLowBand = (EditText) view.findViewById(R.id.lowBand_view_bandgain);
            EditText ETHighBand = (EditText) view.findViewById(R.id.highband_view_bandgain);
            EditText ETGain40 = (EditText) view.findViewById(R.id.gain40_view_bandgain);
            EditText ETGain60 = (EditText) view.findViewById(R.id.gain60_view_bandgain);
            EditText ETGain80 = (EditText) view.findViewById(R.id.gain80_view_bandgain);

            ETLowBand.setText(bandGainSetUnitsL[count].getLowBand() + "");
            ETHighBand.setText(bandGainSetUnitsL[count].getHighBand() + "");
            ETGain40.setText(bandGainSetUnitsL[count].getGain40() + "");
            ETGain60.setText(bandGainSetUnitsL[count].getGain60() + "");
            ETGain80.setText(bandGainSetUnitsL[count].getGain80() + "");

            rightBorder.addView(view);
        }
    }

    private void alertDialog(String tt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView message = new TextView(this);
        message.setText("參數" + tt + "未設定");
        //message.setGravity(Gravity.CENTER);
        builder.setView(message);
        builder.setPositiveButton("OK", null);
        AlertDialog dialong = builder.show();
        dialong.show();
    }
}
