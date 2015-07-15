package ntou.cs.lab505.serenity.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import ntou.cs.lab505.serenity.R;
import ntou.cs.lab505.serenity.datastructure.SoundVectorUnit;
import ntou.cs.lab505.serenity.sound.soundgeneration.PureToneGeneration;
import ntou.cs.lab505.serenity.stream.SoundOutputPool;
import ntou.cs.lab505.serenity.system.SystemParameters;

public class AudiogramTestActivity extends Activity{

    PureToneGraph pureToneGraph;
    Spinner freqSpinner;
    PureToneGeneration pureToneGeneration;
    SoundOutputPool soundOutputPool;

    private int freqValue;
    private int dbValue;
    private int channelValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiogram_test);

        pureToneGraph = (PureToneGraph) findViewById(R.id.puretonegrapn_activity_audiogram_test);

        freqSpinner = (Spinner) findViewById(R.id.spinner_activity_audiogram_test);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.freq_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        freqSpinner.setAdapter(spinnerAdapter);


    }

   public void buttonTest(View view) {

       // get input value.
       freqValue = Integer.parseInt(freqSpinner.getSelectedItem().toString());
       EditText etDb = (EditText) findViewById(R.id.db_activity_activity_audiogram_test);
       dbValue = Integer.parseInt(etDb.getText().toString());

       RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup_activity_audiogram_test);
       switch (rg.getCheckedRadioButtonId()) {
           case R.id.leftchannel_activity_audiogram_test:
               channelValue = 0;
               break;
           case R.id.rightchannel_activityh_audiogram_test:
               channelValue = 1;
               break;
           default:
               channelValue = 0;
       }

       //Log.d("AudiogramTestActivity", "in buttonTest. freq value: " + freqValue);
       //Log.d("AudiogramTestActivity", "in buttonTest. db value: " + dbValue);
       //Log.d("AudiogramTestActivity", "in buttonTest. channel value: " + channelValue);

       // fix db degree.
       switch(freqValue)
				{
				case 250:
					dbValue = dbValue + 26;
					break;
				case 500:
                    dbValue += 12;
					break;
				case 1000:
                    dbValue += 7;
					break;
				case 2000:
                    dbValue += 7;
					break;
				case 4000:
                    dbValue += 10;
					break;
				}

       // generate sound.
       pureToneGeneration = new PureToneGeneration(SystemParameters.SAMPLERATE_LOW);
       SoundVectorUnit dataUnit = new SoundVectorUnit(pureToneGeneration.generate(freqValue, 2, dbValue));
       soundOutputPool = new SoundOutputPool(SystemParameters.SAMPLERATE_LOW, 2, channelValue, 0);
       soundOutputPool.open();
       soundOutputPool.write(dataUnit);
       soundOutputPool.close();

       // draw graph.
       if (channelValue == 0) {
           // left channel.
           pureToneGraph.setData(freqValue, dbValue, 0);
       } else {
           // right channel.
           pureToneGraph.setData(freqValue, dbValue, 1);
       }
       // refresh view.
       pureToneGraph.invalidate();
   }
}
