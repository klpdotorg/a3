package com.gka.akshara.assesseasy;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.akshara.assessment.a3.A3Application;
import com.akshara.assessment.a3.R;
import com.akshara.assessment.a3.UtilsPackage.AnalyticsConstants;
import com.crashlytics.android.Crashlytics;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;

public class finalpage_boywins extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalpage_boywins);

        // Set the Title of the App on the Action Bar at the top
        try {
            setTitle(globalvault.a3app_titletext);
        }
        catch(Exception e) {
            Log.e("EASYASSESS", "setTitle Exception: errormsg:"+e.toString());
        }
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

    public void clickedConfirmRight(View view) { // Clicked 'Right tick' on the 'You want to Exit?' confirmation pop-up

        ConstraintLayout confirmlayout = (ConstraintLayout)findViewById(R.id.insideconfirmationmsgconstraintlayout);
        confirmlayout.setVisibility(View.INVISIBLE);

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "finalpage_boywins:clickedConfirmRight(). Clicked RIGHT on Confirmation popup." );


        saveAndSyncTelemetry();
    }

    public void clickedConfirmWrong(View view) { // Clicked 'Wrong X' on the 'You want to Exit?' confirmation pop-up

        ConstraintLayout confirmlayout = (ConstraintLayout)findViewById(R.id.insideconfirmationmsgconstraintlayout);
        confirmlayout.setVisibility(View.INVISIBLE);

        ImageButton finishbutton = (ImageButton) findViewById(R.id.buttonFinish);
        finishbutton.setEnabled(true); // Enable the 'Finish' button again

        ImageButton backbutton = (ImageButton) findViewById(R.id.buttonBack);
        backbutton.setEnabled(true); // Enable the 'Back' button again

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "finalpage_boywins:clickedConfirmWrong(). Clicked WRONG on Confirmation popup." );
    }

    public void clickedFinish(View view) { // Clicked Tick button (Finish the Assessment)

        ConstraintLayout confirmlayout = (ConstraintLayout)findViewById(R.id.insideconfirmationmsgconstraintlayout);
        confirmlayout.setVisibility(View.VISIBLE); // Make the 'You want to Exit' confirmation pop-up visible

        ImageButton finishbutton = (ImageButton) findViewById(R.id.buttonFinish);
        finishbutton.setEnabled(false); // Disable the 'Finish' button once clicked

        ImageButton backbutton = (ImageButton) findViewById(R.id.buttonBack);
        backbutton.setEnabled(false); // Disable the 'Back' button as well when the 'Finish' button is clicked

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "finalpage_boywins:clickedFinish(). Clicked Finish." );
    }

    public void saveAndSyncTelemetry() {

        if(globalvault.finished) // If already clicked on 'Exit' and the app is in this function and User clicked 'Exit' again
            return;
        else
            globalvault.finished = true;

        // Save the telemetry data in the local device database
        try {

            int score = 0;

            for (int i = 0; i < globalvault.questions.length; i++) {
                if (globalvault.questions[i].getPass() == "P")
                    score += 1;
            }

            String[] records_assessment = new String[5];
            records_assessment[0] = globalvault.a3app_childId;
            records_assessment[1] = Integer.toString(globalvault.questionsetid);
            records_assessment[2] = Integer.toString(score);

            if (globalvault.datetime_assessment_start == 0)
                globalvault.datetime_assessment_start = System.currentTimeMillis();
            records_assessment[3] = Long.toString(globalvault.datetime_assessment_start / 1000);
            records_assessment[4] = Long.toString(System.currentTimeMillis() / 1000); // Submission time

            String assementid = globalvault.dsmgr.saveAssessment(records_assessment);

            if (assementid != null) {

                for (int i = 0; i < globalvault.questions.length; i++) {

                    if (MainActivity.debugalerts)
                        Log.d("EASYASSESS", "finalpage_boywins:saveAndSyncTelemetry(). correctAnswer:" + globalvault.questions[i].getAnswerCorrect() + " givenAnswer:" + globalvault.questions[i].getAnswerGiven() + " Pass:" + globalvault.questions[i].getPass());

                    String[] records_assessment_detail = new String[4];
                    records_assessment_detail[0] = assementid;
                    records_assessment_detail[1] = globalvault.questions[i].getQustionID();
                    records_assessment_detail[2] = globalvault.questions[i].getAnswerGiven();
                    records_assessment_detail[3] = globalvault.questions[i].getPass();

                    globalvault.dsmgr.saveAssessmentDetail(records_assessment_detail);
                }
            }

            // Sync the telemetry data with the Server, if the device is online
            if (globalvault.autosynctelemetry) {
                if (isDeviceOnline())
                    globalvault.dsmgr.syncTelemetry(globalvault.a3_telemetryapi_baseurl);
            }
        }
        catch(Exception e) {
            Log.e("EASYASSESS", "finalpage_boywins:saveAndSyncTelemetry: Exception: "+e.toString());
        }

        globalvault.questions = null;
        try {
            try {
                Bundle bundle = new Bundle();
                bundle.putString(AnalyticsConstants.Assessment, "END");
                A3Application.getAnalyticsObject().logEvent("ASSESSMENT_START", bundle);
            } catch (Exception e) {
                Crashlytics.log("Analytics exception in assessment start");
            }

            // Return to the ContainerApp
            Intent intent = new Intent(this, Class.forName(globalvault.containerapp_returntoactivity));
            intent.putExtra("A3APP_INSTITUTIONID", globalvault.a3app_institutionId);
            intent.putExtra("A3APP_GRADEID", globalvault.a3app_gradeId);
            intent.putExtra("A3APP_GRADESTRING", globalvault.a3app_gradeString);
            intent.putExtra("A3APP_LANGUAGE",globalvault.a3app_language);
            intent.putExtra("A3APP_CHILDID",globalvault.a3app_childId);
            intent.putExtra("A3APP_TITLETEXT",globalvault.a3app_titletext);
            intent.putExtra("EASYASEESS_QUESTIONSETID", globalvault.questionsetid);

            intent.putExtra("EASYASSESS_CLICKEDBACKARROW", false);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        catch(Exception e) {
            Log.e("EASYASSESS", "finalpage_boywins:saveAndSyncTelemetry: Exception:"+e.toString());
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
