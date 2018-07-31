package com.akshara.assessment.a3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akshara.assessment.a3.UtilsPackage.AnalyticsConstants;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.crashlytics.android.Crashlytics;

import java.text.Format;

public class FinalLauncherActivity extends BaseActivity {

    TextView tv_hello_name;
    Button btnnextAssessment;
    String studentName = "";
    SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_laucher_layout);
        tv_hello_name = findViewById(R.id.tv_hello_name);
        btnnextAssessment = findViewById(R.id.btnnextAssessment);
        studentName = getIntent().getStringExtra("NAME");
        sessionManager = new SessionManager(getApplicationContext());


        tv_hello_name.setText(String.format(
                getString(R.string.hello), studentName));


        btnnextAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Bundle bundle = new Bundle();
                    bundle.putString(AnalyticsConstants.Title, "Q.set:"+getIntent().getIntExtra("EASYASSESS_QUESTIONSETID", 0));
                    bundle.putString(AnalyticsConstants.Grade, getIntent().getStringExtra("A3APP_GRADESTRING"));
                    bundle.putString(AnalyticsConstants.Language, getIntent().getStringExtra("A3APP_LANGUAGE"));
                    bundle.putString(AnalyticsConstants.Program, sessionManager.getProgramFromSession());
                    bundle.putString(AnalyticsConstants.State, sessionManager.getState());
                    bundle.putString(AnalyticsConstants.Assessment, "START");
                    A3Application.getAnalyticsObject().logEvent("ASSESSMENT_START", bundle);
                } catch (Exception e) {
                    Crashlytics.log("Analytics exception in assessment start");
                }

                Intent intent = new Intent(getApplicationContext(), com.gka.akshara.assesseasy.assessment_manager.class);
                Bundle bundle = new Bundle();
                bundle.putString("EASYASSESS_FROMACTIVITY", getIntent().getStringExtra("EASYASSESS_FROMACTIVITY"));
                bundle.putInt("EASYASSESS_QUESTIONSETID", getIntent().getIntExtra("EASYASSESS_QUESTIONSETID", 0));
                bundle.putBoolean("EASYASSESS_CLICKEDBACKARROW", false);
                bundle.putLong("A3APP_INSTITUTIONID", getIntent().getLongExtra("A3APP_INSTITUTIONID", 0));
                bundle.putInt("A3APP_GRADEID", getIntent().getIntExtra("A3APP_GRADEID", 0));
                bundle.putString("A3APP_GRADESTRING", getIntent().getStringExtra("A3APP_GRADESTRING"));
                bundle.putString("A3APP_CHILDID", getIntent().getStringExtra("A3APP_CHILDID"));
                bundle.putString("A3APP_LANGUAGE", getIntent().getStringExtra("A3APP_LANGUAGE"));
                bundle.putString("A3APP_TITLETEXT", getIntent().getStringExtra("A3APP_TITLETEXT"));
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });


    }
}
