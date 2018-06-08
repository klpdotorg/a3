package com.gka.akshara.assesseasy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.akshara.assessment.a3.R;

import java.time.LocalTime;

public class finalpage_boywins extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalpage_boywins);
    }

    public void clickedBack(View vw) {

        Intent intent = new Intent(this, assessment_manager.class);
        String fromactivityname = this.getLocalClassName();
        intent.putExtra("EASYASSESS_FROMACTIVITY", fromactivityname);
        intent.putExtra("EASYASSESS_QUESTIONID", globalvault.questions.length+1); // questionid count starts from 1. Hence the last questionid (index of the questions array) is the length of the array
        intent.putExtra("EASYASSESS_CLICKEDBACKARROW", true);
        if(MainActivity.debugalerts)
            Log.d("EASYASSESS","finalpage_boywins: clicked Back button. Passing questionid:"+globalvault.questions.length);
        startActivity(intent);

    }

    public void clickedScreen(View view) { // clicking anywhere on the screen
    }

    public void clickedFinish(View view) { // Clicked Tick button (Finish the Assessment)

        // Save the telemetry data in the local device database

        int score = 0;

        for(int i = 0; i < globalvault.questions.length; i++) {
            if(globalvault.questions[i].getPass() == "P")
                score += 1;
           // globalvault.dsmgr.saveAssessmentDetail();
        }

        String[] records_assessment = new String[5];
        records_assessment[0] = globalvault.a3app_childId;
        records_assessment[1] = Integer.toString(globalvault.questionsetid);
        records_assessment[2] = Integer.toString(score);
        records_assessment[3] = Long.toString(globalvault.datetime_assessment_start);
        records_assessment[4] = Long.toString(System.currentTimeMillis()); // Submission time

        String assementid = globalvault.dsmgr.saveAssessment(records_assessment);

        if(assementid != null) {

            for (int i = 0; i < globalvault.questions.length; i++) {

                if (MainActivity.debugalerts)
                    Log.d("EASYASSESS", "finalpage_boywins:clickedFinish(). correctAnswer:" + globalvault.questions[i].getAnswerCorrect() + " givenAnswer:" + globalvault.questions[i].getAnswerGiven() + " Pass:" + globalvault.questions[i].getPass());

                String[] records_assessment_detail = new String[4];
                records_assessment_detail[0] = assementid;
                records_assessment_detail[1] = globalvault.questions[i].getQustionID();
                records_assessment_detail[2] = globalvault.questions[i].getAnswerGiven();
                records_assessment_detail[3] = globalvault.questions[i].getPass();

                globalvault.dsmgr.saveAssessmentDetail(records_assessment_detail);
            }
        }

        globalvault.questions = null;

        try {
            // Return to the ContainerApp
            Intent intent = new Intent(this, Class.forName(globalvault.containerapp_returntoactivity));
            intent.putExtra("A3APP_INSTITUTIONID", globalvault.a3app_institutionId);
            intent.putExtra("A3APP_GRADEID", globalvault.a3app_gradeId);
            intent.putExtra("A3APP_GRADESTRING", globalvault.a3app_gradeString);
            intent.putExtra("EASYASSESS_CLICKEDBACKARROW", false);
            startActivity(intent);
        }
        catch(Exception e) {
            Log.e("EASYASSESS", "finalpage_boywins:clickedFinish: Exception:"+e.toString());
        }
    }
}
