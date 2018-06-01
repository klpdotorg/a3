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

public class qp_arithmetic_division_withreminder extends AppCompatActivity {

    AssessEasyKeyboard aekbd;
    int questionid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qp_arithmetic_division_withreminder);

        // set the background image (pick an image randomly from the QP_BGRND_IMGS array)
        int bkgrndimagearrayindex = new Random().nextInt(globalvault.QP_BGRND_IMGS.length-1);
        ConstraintLayout clayout = findViewById(R.id.ConstraintLayout_parent_arithmeticdivisionwithreminder);
        clayout.setBackgroundResource(globalvault.QP_BGRND_IMGS[bkgrndimagearrayindex]);

        // Saves the questionid passed to this page
        Intent intent = getIntent(); // get the Intent that started this activity
        questionid =  intent.getIntExtra("EASYASSESS_QUESTIONID",0);

        TextView tvquestiontext = findViewById(R.id.textViewQuestionText);
        tvquestiontext.setText(globalvault.questions[questionid-1].getQuestionText());

        Random rand = new Random();

        ArrayList qdatalist = globalvault.questions[questionid-1].getQuestionDataList();

        String[] dividendset = null;
        String[] divisorset = null;

        for(int i = 0; i < qdatalist.size(); i++) {
            assessquestiondata qdata = (assessquestiondata)qdatalist.get(i);
            if(qdata.name.equals("dividend"))
                dividendset = qdata.value.split(",");
            else if(qdata.name.equals("divisor"))
                divisorset = qdata.value.split(",");
            else ;

        }

        TextView tvNumber1 = findViewById(R.id.textViewDividend);
        if(dividendset != null) {
            int randindex = rand.nextInt(dividendset.length);
            tvNumber1.setText(dividendset[randindex]);
        }

        TextView tvNumber2 = findViewById(R.id.textViewDivisor);
        if(divisorset != null) {
            int randindex = rand.nextInt(divisorset.length);
            tvNumber2.setText(divisorset[randindex]);
        }

        // sets the answer field (when navigating backwards, can fill the answer entered earlier in the answer field)
        String answer = globalvault.questions[questionid-1].getAnswerGiven();
        TextView tvQuotient = (EditText) findViewById(R.id.editTextQuotient);
        TextView tvReminder = (EditText) findViewById(R.id.editTextReminder);

        if(!TextUtils.isEmpty(answer)) {  // If answer is not empty, then the dividend, divisor, quotient and reminder be set to the previously entered values (reached this screen by pressing back button)
            String[] arrAnswer = answer.split(","); // Answer is stored as dividend, divisor, quotient, reminder
            tvNumber1.setText(arrAnswer[0]);
            tvNumber2.setText(arrAnswer[1]);
            tvQuotient.setText(arrAnswer[2]);
            tvReminder.setText(arrAnswer[3]);
        }

        // To hide the keyboard initially, remove the focus from the EditText field and move it to the dummy view.
        // When user clicks on the EditText field, the keyboard will appear
        View dummyview = findViewById(R.id.dummyViewForFocus);
        tvQuotient.clearFocus();
        tvReminder.clearFocus();
        dummyview.requestFocus();

        // create the keyboard
        aekbd = new AssessEasyKeyboard(this,R.id.aenumberkbd, R.xml.assesseasynumberkbd);

        // Register the EditText box with the custom keyboard
        aekbd.registerEditText(R.id.editTextQuotient);
        aekbd.registerEditText(R.id.editTextReminder);
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


        EditText editTextQuotient = findViewById(R.id.editTextQuotient);
        Editable quotient_editable = editTextQuotient.getText();

        EditText editTextReminder = findViewById(R.id.editTextReminder);
        Editable reminder_editable = editTextReminder.getText();

        if((quotient_editable != null) && (reminder_editable != null)) {
            String quotient = quotient_editable.toString();
            String reminder = reminder_editable.toString();

            if((quotient.equals("")) || (reminder.equals(""))) {
                if (MainActivity.debugalerts)
                    Log.d("EASYASSESS", "qp_arithmetic_division_withreminder: clickedNext: quotient and/or reminder is empty string");
                if(globalvault.allowskipquestions)
                    this.invokeAssessmentManagerActivity();
                else
                    return;
            }
            else {
                TextView tvNumber1 = findViewById(R.id.textViewDividend);
                String dividend = tvNumber1.getText().toString();
                TextView tvNumber2 = findViewById(R.id.textViewDivisor);
                String divisor = tvNumber2.getText().toString();
                String answerstr = dividend+","+divisor+","+quotient+","+reminder;

                globalvault.questions[questionid-1].setAnswerGiven(answerstr); // Given answer field will have string in the format dividend,divisor,quotient,reminder
                this.invokeAssessmentManagerActivity();
            }
        }
        else {
            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "qp_arithmetic_division_withreminder: clickedNext: answer_editable is null");
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
            Log.d("EASYASSESS", "qp_arithmetic_division_withreminder: clickedNext: fromactivity: "+fromactivityname+" questionid:"+questionid);

    }
}
