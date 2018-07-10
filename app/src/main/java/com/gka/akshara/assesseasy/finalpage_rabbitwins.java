package com.gka.akshara.assesseasy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.akshara.assessment.a3.R;

public class finalpage_rabbitwins extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalpage_rabbitwins);

        // Set the Title of the App on the Action Bar at the top
        try {
            setTitle(globalvault.a3app_titletext);
        }
        catch(Exception e) {
            Log.e("EASYASSESS", "setTitle Exception: errormsg:"+e.toString());
        }
    }
}
