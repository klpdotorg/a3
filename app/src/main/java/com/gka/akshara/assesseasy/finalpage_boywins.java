package com.gka.akshara.assesseasy;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.akshara.assessment.a3.R;

import java.net.HttpURLConnection;
import java.net.URL;
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
        }

        String[] records_assessment = new String[5];
        records_assessment[0] = globalvault.a3app_childId;
        records_assessment[1] = Integer.toString(globalvault.questionsetid);
        records_assessment[2] = Integer.toString(score);

        if(globalvault.datetime_assessment_start  == 0)
            globalvault.datetime_assessment_start = System.currentTimeMillis();
        records_assessment[3] = Long.toString(globalvault.datetime_assessment_start / 1000);
        records_assessment[4] = Long.toString(System.currentTimeMillis() / 1000); // Submission time

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

        // Sync the telemetry data with the Server, if the device is online
        if(globalvault.autosynctelemetry) {
            if(isDeviceOnline())
                globalvault.dsmgr.syncTelemetry(globalvault.a3_telemetryapi_baseurl);
        }


        globalvault.questions = null;

        try {
            // Return to the ContainerApp
            Intent intent = new Intent(this, Class.forName(globalvault.containerapp_returntoactivity));
            intent.putExtra("A3APP_INSTITUTIONID", globalvault.a3app_institutionId);
            intent.putExtra("A3APP_GRADEID", globalvault.a3app_gradeId);
            intent.putExtra("A3APP_GRADESTRING", globalvault.a3app_gradeString);
            intent.putExtra("EASYASSESS_CLICKEDBACKARROW", false);
            intent.putExtra("A3APP_LANGUAGE", globalvault.a3app_language);
            intent.putExtra("EASYASSESS_QUESTIONSETID", globalvault.questionsetid);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        }
        catch(Exception e) {
            Log.e("EASYASSESS", "finalpage_boywins:clickedFinish: Exception:"+e.toString());
        }
    }

    // This methods will check if the device has network
    // doesn't check if it has Internet access. Assumes that if network is available, it has the Internet access
    public boolean isDeviceOnline() {

            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

            if(networkInfo != null && networkInfo.isConnectedOrConnecting()){
                if (MainActivity.debugalerts)
                    Log.d("EASYASSESS", "finalpage_boywins:isDeviceOnline(). Device is Online.");

                return true;
            }
            else{
                if (MainActivity.debugalerts)
                    Log.d("EASYASSESS", "finalpage_boywins:isDeviceOnline(). Device is NOT Online.");

                return false;
            }
    }
}
