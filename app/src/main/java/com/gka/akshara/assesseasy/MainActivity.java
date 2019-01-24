package com.gka.akshara.assesseasy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.akshara.assessment.a3.R;

public class MainActivity extends AppCompatActivity {

    public  static boolean debugalerts = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // To find out if a device is mdpi, ldpi, hdpi etc
        // return 0.75 if it's LDPI
        // return 1.0 if it's MDPI
        // return 1.5 if it's HDPI
        // return 2.0 if it's XHDPI
        // return 3.0 if it's XXHDPI
        // return 4.0 if it's XXXHDPI
        float density = getResources().getDisplayMetrics().density;
        if(debugalerts)
            Log.d("EASYASSESS","MainActivity: DEVICE DENSITY: "+density);

    }

    public void clicked_buttonplay(View v) {

        if(debugalerts)
             Log.d("EASYASSESS","Enter clicked_buttonplay");

        Intent intent = new Intent(this, assessment_manager.class);
        String fromactivityname = this.getLocalClassName();
        intent.putExtra("EASYASSESS_FROMACTIVITY", fromactivityname);
        intent.putExtra("EASYASSESS_CLICKEDBACKARROW", false);
        intent.putExtra("EASYASSESS_QUESTIONSETID", 1); // for testing
        startActivity(intent);
    }
}
