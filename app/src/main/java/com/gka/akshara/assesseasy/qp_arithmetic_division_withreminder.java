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
import android.widget.ImageView;
import android.widget.TextView;

import com.akshara.assessment.a3.R;

import java.util.ArrayList;
import java.util.Random;

public class qp_arithmetic_division_withreminder extends AppCompatActivity {

    AssessEasyKeyboard aekbd;
    int questionid = 0;
    String audio_base64string = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qp_arithmetic_division_withreminder);

        // set the background image (pick an image randomly from the QP_BGRND_IMGS array)
        int bkgrndimagearrayindex = new Random().nextInt(globalvault.QP_BGRND_IMGS.length-1);
        if((bkgrndimagearrayindex >= (globalvault.QP_BGRND_IMGS.length -1)) || (bkgrndimagearrayindex < 0))
            bkgrndimagearrayindex = 0;
        ConstraintLayout clayout = (ConstraintLayout) findViewById(R.id.ConstraintLayout_parent_arithmeticdivisionwithreminder);
        clayout.setBackgroundResource(globalvault.QP_BGRND_IMGS[bkgrndimagearrayindex]);

        // Saves the questionid passed to this page
        Intent intent = getIntent(); // get the Intent that started this activity
        questionid =  intent.getIntExtra("EASYASSESS_QUESTIONID",0);

        TextView tvquestiontext = (TextView)findViewById(R.id.textViewQuestionText);
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

            if(qdata.datatype.equals("audio")) {
                audio_base64string = qdata.filecontent_base64;
                ImageView audiobuttonimageview = (ImageView)findViewById(R.id.buttonAudio);
                //int res = getResources().getIdentifier("audiosymbol", "drawable", this.getPackageName());
                //audiobuttonimageview.setImageResource(res);
                audiobuttonimageview.setVisibility(View.VISIBLE);
            }

        }

        TextView tvNumber1 = (TextView)findViewById(R.id.textViewDividend);
        int randindex = 0;
        if(dividendset != null) {
            randindex = rand.nextInt(dividendset.length);
            tvNumber1.setText(dividendset[randindex]);
        }

        TextView tvNumber2 = (TextView)findViewById(R.id.textViewDivisor);
        if(divisorset != null) {
            if (randindex >= divisorset.length) {
                randindex = 0;
            }
            tvNumber2.setText(divisorset[randindex]); // Choose the value for the Divisor that corresponds (same index) to the value of the Dividend
        }

        // sets the number and answer fields (when navigating backwards, fill the numbers randomly generated earlier and the answer entered before)
        String answer = globalvault.questions[questionid-1].getAnswerGiven();
        TextView tvQuotient = (EditText) findViewById(R.id.editTextQuotient);
        TextView tvReminder = (EditText) findViewById(R.id.editTextReminder);

        if(!TextUtils.isEmpty(answer)) {  // Display the previously shown values, including the Answer if Child has entered the Answer previously (reached this screen by pressing back button)
            String[] arrAnswer = answer.split(","); // Answer is stored as dividend, divisor, quotient, reminder
            tvNumber1.setText(arrAnswer[0]);
            tvNumber2.setText(arrAnswer[1]);

            int answarrlength = arrAnswer.length;
            if(answarrlength >= 3) {
                if (!TextUtils.isEmpty(arrAnswer[2]))
                    tvQuotient.setText(arrAnswer[2]);
            }
            if(answarrlength == 4) {
                if (!TextUtils.isEmpty(arrAnswer[3]))
                    tvReminder.setText(arrAnswer[3]);
            }
        }

        // To hide the keyboard initially, remove the focus from the EditText field and move it to the dummy view.
        // When user clicks on the EditText field, the keyboard will appear
        View dummyview = (View) findViewById(R.id.dummyViewForFocus);
        tvQuotient.clearFocus();
        tvReminder.clearFocus();
        dummyview.requestFocus();

        // create the keyboard
        aekbd = new AssessEasyKeyboard(this,R.id.aenumberkbd, R.xml.assesseasynumberkbd);

        // Register the EditText box with the custom keyboard
        aekbd.registerEditText(R.id.editTextQuotient);
        aekbd.registerEditText(R.id.editTextReminder);

        // Play Audio file (if any) associated with the Question, if the flag globalvault.audioautoplay is set to true
        if(!TextUtils.isEmpty(this.audio_base64string)) {
            if(globalvault.audioautoplay) { // If the Audio to be played when the screen opens
                audioManager.playAudio(this.audio_base64string);
            }
        }
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


        EditText editTextQuotient = (EditText)findViewById(R.id.editTextQuotient);
        Editable quotient_editable = editTextQuotient.getText();

        EditText editTextReminder = (EditText)findViewById(R.id.editTextReminder);
        Editable reminder_editable = editTextReminder.getText();

        if(quotient_editable != null) {
            String quotient = quotient_editable.toString();

            String reminder = "0";  // If Child has not entered anything for remainder, treat it as 0 internally
            if(reminder_editable != null)
                reminder = reminder_editable.toString();

            if(quotient.equals("")) {
                if (MainActivity.debugalerts)
                    Log.d("EASYASSESS", "qp_arithmetic_division_withreminder: clickedNext: quotient is empty string");
                if(globalvault.allowskipquestions) {
                    globalvault.questions[questionid - 1].setPass("S");

                    TextView tvNumber1 = (TextView)findViewById(R.id.textViewDividend);
                    String dividend = tvNumber1.getText().toString();
                    TextView tvNumber2 = (TextView)findViewById(R.id.textViewDivisor);
                    String divisor = tvNumber2.getText().toString();
                    String answerstr = dividend+","+divisor;
                    globalvault.questions[questionid-1].setAnswerGiven(answerstr);

                    this.invokeAssessmentManagerActivity();
                    return;
                }
                else
                    return;
            }
            else {
                TextView tvNumber1 = (TextView)findViewById(R.id.textViewDividend);
                String dividend = tvNumber1.getText().toString();
                TextView tvNumber2 = (TextView)findViewById(R.id.textViewDivisor);
                String divisor = tvNumber2.getText().toString();
                String answerstr = dividend+","+divisor+","+quotient+","+reminder;

                try {
                    int intFirstnum = Integer.parseInt(dividend.trim());
                    int intSecondnum = Integer.parseInt(divisor.trim());
                    int answer_quotient = intFirstnum / intSecondnum;
                    int answer_reminder = intFirstnum % intSecondnum;
                    String correct_answer = answer_quotient+","+answer_reminder;
                    globalvault.questions[questionid - 1].setAnswerCorrect(correct_answer);

                    if ( (quotient.equals(Integer.toString(answer_quotient))) && (reminder.equals(Integer.toString(answer_reminder))))
                        globalvault.questions[questionid - 1].setPass("P");
                    else
                        globalvault.questions[questionid - 1].setPass("F");
                }
                catch(Exception e) {
                    Log.e("EASYASSESS", "qp_qrithmetic_division_withreminder: clickedNext: Exception: "+e.toString());
                }

                globalvault.questions[questionid-1].setAnswerGiven(answerstr); // Given answer field will have string in the format dividend,divisor,quotient,reminder
                this.invokeAssessmentManagerActivity();
            }
        }
        else {
            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "qp_arithmetic_division_withreminder: clickedNext: answer_editable is null");
            if(globalvault.allowskipquestions) {
                globalvault.questions[questionid - 1].setPass("S");
                this.invokeAssessmentManagerActivity();
            }
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

    public void clickedAudio(View view) {

        audioManager.playAudio(this.audio_base64string);
    }
}
