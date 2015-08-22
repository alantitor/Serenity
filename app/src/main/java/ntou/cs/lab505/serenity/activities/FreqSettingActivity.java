package ntou.cs.lab505.serenity.activities;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import ntou.cs.lab505.serenity.R;
import ntou.cs.lab505.serenity.database.FreqSettingAdapter;

/**
 * Design layout of frequency setting layout.
 */
public class FreqSettingActivity extends Activity {
    // get layout objects.
    TextView textView;
    SeekBar seekBar;
    int seekValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freq_setting);

        textView = (TextView) findViewById(R.id.count_activity_freq_setting);
        seekBar = (SeekBar) findViewById(R.id.seekbar_activity_freq_setting);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.d("FreqSettingActivity", "in oncreate. seekBar value: " + progress);
                seekValue = progress;
                textView.setText(String.valueOf(seekToSemi(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //
            }
        });
    }

    /**
     * get setting value from database.
     */
    @Override
    public void onResume() {
        super.onResume();

        FreqSettingAdapter freqSettingAdapter = new FreqSettingAdapter(this.getApplicationContext());
        freqSettingAdapter.open();
        seekValue = semiToSeek(freqSettingAdapter.getData());
        seekBar.setProgress(seekValue);
        freqSettingAdapter.close();
    }

    /**
     * save setting value to databse.
     */
    @Override
    public void onPause() {
        super.onPause();

        FreqSettingAdapter freqSettingAdapter = new FreqSettingAdapter(this.getApplicationContext());
        freqSettingAdapter.open();
        freqSettingAdapter.saveData(seekToSemi(seekValue));
        freqSettingAdapter.close();
    }

    /**
     * convert value unit from seekbar to semitone.
     * @param seekValue
     * @return
     */
    private int seekToSemi(int seekValue) {
        return seekValue - 12;
    }

    /**
     * convert value unit from semitone to seekbar.
     * @param semitone
     * @return
     */
    private int semiToSeek(int semitone) {
        return semitone + 12;
    }
}
