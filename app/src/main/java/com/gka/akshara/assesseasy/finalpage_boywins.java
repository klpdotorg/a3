package com.gka.akshara.assesseasy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.akshara.assessment.a3.R;

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

        //Intent intent = new Intent(this, finalpage_rabbitwins.class);
        //startActivity(intent);

        //Intent intent = new Intent(this, MainActivity.class);
        // Return to the ContainerApp by invoking the com.akshara.assessment.a3.StudentListMainActivity Activity
        Intent intent = new Intent(this, com.akshara.assessment.a3.StudentListMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("A3APP_INSTITUTIONID", globalvault.a3app_institutionId);
        intent.putExtra("A3APP_GRADEID", globalvault.a3app_gradeId);
        intent.putExtra("A3APP_GRADESTRING", globalvault.a3app_gradeString);
        intent.putExtra("EASYASSESS_CLICKEDBACKARROW", false);

        Log.d("shri","from Assessment Insti:"+globalvault.a3app_institutionId);
        Log.d("shri","from Assessment gradeId:"+globalvault.a3app_gradeId);
        Log.d("shri","from Assessment gradeString:"+globalvault.a3app_gradeString);

        startActivity(intent);

    }
}
