package ntou.cs.lab505.serenity.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ntou.cs.lab505.serenity.R;

/**
 * Design the layout of Logo Activity.
 */
public class LogoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
    }

    public void changeView(View view) {
        Intent intent = new Intent(this, ServiceActivity.class);
        startActivity(intent);
    }
}