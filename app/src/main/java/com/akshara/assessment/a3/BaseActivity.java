package com.akshara.assessment.a3;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by shridhars on 3/30/2018.
 */

public class BaseActivity  extends AppCompatActivity {


   @Override
    protected void attachBaseContext(Context newBase) {


       super.attachBaseContext(A3Application.updateLanguage(newBase));
    }
}
