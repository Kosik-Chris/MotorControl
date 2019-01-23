package j.chris.kosik.sensorcontrol.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import j.chris.kosik.sensorcontrol.R;

public class SingleAxis extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_axis);
    }

}
