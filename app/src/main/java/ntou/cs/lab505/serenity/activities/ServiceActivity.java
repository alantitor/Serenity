package ntou.cs.lab505.serenity.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import ntou.cs.lab505.serenity.R;

public class ServiceActivity extends Activity {

    ImageView controlButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        // allow volume buttons.
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // get object.
        controlButton = (ImageView) findViewById(R.id.servicecontrol_activity_service);
    }

    public void buttonService(View view) {

    }

    public void buttonSetting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }
}
