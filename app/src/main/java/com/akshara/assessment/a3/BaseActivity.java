package com.akshara.assessment.a3;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**
 * Created by shridhars on 3/30/2018.
 */

public class BaseActivity  extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
// Show status bar
       // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
       super.attachBaseContext(A3Application.updateLanguage(newBase));
    }


}
