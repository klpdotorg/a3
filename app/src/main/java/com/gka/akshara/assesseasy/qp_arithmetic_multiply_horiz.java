package com.gka.akshara.assesseasy;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.akshara.assessment.a3.R;

import java.util.ArrayList;
import java.util.Random;


public class qp_arithmetic_multiply_horiz extends AppCompatActivity {


    AssessEasyKeyboard aekbd;
    int questionid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qp_arithmetic_multiply_horiz);

        // set the background image (pick an image randomly from the QP_BGRND_IMGS array)
        int bkgrndimagearrayindex = new Random().nextInt(globalvault.QP_BGRND_IMGS.length-1);
        ConstraintLayout clayout = findViewById(R.id.ConstraintLayout_parent_arithmeticmultiply_horiz);
        clayout.setBackgroundResource(globalvault.QP_BGRND_IMGS[bkgrndimagearrayindex]);

        // Saves the questionid passed to this page
        Intent intent = getIntent(); // get the Intent that started this activity
        questionid =  intent.getIntExtra("EASYASSESS_QUESTIONID",0);

        TextView tvquestiontext = findViewById(R.id.textViewQuestionText);
        tvquestiontext.setText(globalvault.questions[questionid-1].getQuestionText());

        Random rand = new Random();

        ArrayList qdatalist = globalvault.questions[questionid-1].getQuestionDataList();

        String[] firstnumberset = null;
        String[] secondnumberset = null;

        for(int i = 0; i < qdatalist.size(); i++) {
            assessquestiondata qdata = (assessquestiondata)qdatalist.get(i);
            if(qdata.name.equals("firstnumber"))
                firstnumberset = qdata.value.split(",");
            else if(qdata.name.equals("secondnumber"))
                secondnumberset = qdata.value.split(",");
            else ;

        }

        TextView tvNumber1 = findViewById(R.id.textViewNumber1);
        if(firstnumberset != null) {
            int randindex = rand.nextInt(firstnumberset.length);
            tvNumber1.setText(firstnumberset[randindex]);
        }

        TextView tvNumber2 = findViewById(R.id.textViewNumber2);
        if(secondnumberset != null) {
            int randindex = rand.nextInt(secondnumberset.length);
            tvNumber2.setText(secondnumberset[randindex]);
        }

        // sets the answer field (when navigating backwards, can fill the answer entered earlier in the answer field)
        String answer = globalvault.questions[questionid-1].getAnswerGiven();
        EditText tvAnswer = findViewById(R.id.editTextAnswer);
        if(!TextUtils.isEmpty(answer)) {  // If answer is not empty, then the firstnumber, secondnumber and answer be set to the previously entered values (reached this screen by pressing back button)
            String[] arrAnswer = answer.split(","); // Answer is stored as firstnumber, secondnumber, givenanswer
            tvNumber1.setText(arrAnswer[0]);
            tvNumber2.setText(arrAnswer[1]);
            tvAnswer.setText(arrAnswer[2]);
        }

        // To hide the keyboard initially, remove the focus from the EditText field and move it to the dummy view.
        // When user clicks on the EditText field, the keyboard will appear
        View dummyview = findViewById(R.id.dummyViewForFocus);
        tvAnswer.clearFocus();
        dummyview.requestFocus();

        // create the keyboard
        aekbd = new AssessEasyKeyboard(this,R.id.aenumberkbd, R.xml.assesseasynumberkbd);
        //aekbd.showCustomKeyboard(findViewById(R.id.aenumberkbd));

        // Register the EditText box with the custom keyboard
        aekbd.registerEditText(R.id.editTextAnswer);

    }

    @Override
    public void onBackPressed() {

        /*
        if(aekbd.isCustomKeyboardVisible()) {
            aekbd.hideCustomKeyboard();
        }
        this.finish();
        */

    }

    public void clickedBack(View vw) {

        if(aekbd.isCustomKeyboardVisible()) {
            aekbd.hideCustomKeyboard();
        }

        Intent intent = new Intent(this, assessment_manager.class);
        String fromactivityname = this.getLocalClassName();
        intent.putExtra("EASYASSESS_FROMACTIVITY", fromactivityname);
        intent.putExtra("EASYASSESS_QUESTIONID", questionid);
        intent.putExtra("EASYASSESS_CLICKEDBACKARROW", true);
        startActivity(intent);

    }

    public void clickedNext(View view) {

        EditText editTextAnswer = findViewById(R.id.editTextAnswer);
        Editable answer_editable = editTextAnswer.getText();

        if(answer_editable != null) {
            String answer = answer_editable.toString();
            if(answer.equals("")) {
                if (MainActivity.debugalerts)
                    Log.d("EASYASSESS", "qp_arithmetic_multiply_horiz: clickedNext: answer is empty string");
                if(globalvault.allowskipquestions)
                    this.invokeAssessmentManagerActivity();
                else
                    return;
            }
            else {
                TextView tvNumber1 = findViewById(R.id.textViewNumber1);
                String firstnumber = tvNumber1.getText().toString();
                TextView tvNumber2 = findViewById(R.id.textViewNumber2);
                String secondnumber = tvNumber2.getText().toString();
                String answerstr = firstnumber+","+secondnumber+","+answer;

                globalvault.questions[questionid-1].setAnswerGiven(answerstr); // Given answer field will have string in the format firstnumber,secondnumber,givenanswer
                this.invokeAssessmentManagerActivity();
            }
        }
        else {
            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "qp_arithmetic_multiply_horiz: clickedNext: answer_editable is null");
            if(globalvault.allowskipquestions)
                this.invokeAssessmentManagerActivity();
            else
                return;
        }
    }


    public void invokeAssessmentManagerActivity() {

        Intent intent = new Intent(this, assessment_manager.class);
        String fromactivityname = this.getLocalClassName();
        intent.putExtra("EASYASSESS_FROMACTIVITY", fromactivityname);
        intent.putExtra("EASYASSESS_QUESTIONID", questionid);
        intent.putExtra("EASYASSESS_CLICKEDBACKARROW", false);
        startActivity(intent);

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "qp_arithmetic_multiply_horiz: clickedNext: fromactivity: "+fromactivityname+" questionid:"+questionid);

    }

}
