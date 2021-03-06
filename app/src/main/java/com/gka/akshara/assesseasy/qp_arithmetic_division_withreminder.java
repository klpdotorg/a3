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

        // Set the Title of the App on the Action Bar at the top
        try {
            setTitle(globalvault.a3app_titletext);
        }
        catch(Exception e) {
            Log.e("EASYASSESS", "setTitle Exception: errormsg:"+e.toString());
        }

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
            tvNumber1.setText(AssessEasyKeyboard.replaceEnglishnumeralsToLocal(dividendset[randindex]));
        }

        TextView tvNumber2 = (TextView)findViewById(R.id.textViewDivisor);
        if(divisorset != null) {
            if (randindex >= divisorset.length) {
                randindex = 0;
            }
            tvNumber2.setText(AssessEasyKeyboard.replaceEnglishnumeralsToLocal(divisorset[randindex])); // Choose the value for the Divisor that corresponds (same index) to the value of the Dividend
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

        /**** This is commented out on 11-oct-18 as the requirement changed to make the input field active by default and display the keyboard
        // To hide the keyboard initially, remove the focus from the EditText field and move it to the dummy view.
        // When user clicks on the EditText field, the keyboard will appear
        View dummyview = (View) findViewById(R.id.dummyViewForFocus);
        tvQuotient.clearFocus();
        tvReminder.clearFocus();
        dummyview.requestFocus();
         ***/

        // create the keyboard
        // aekbd = new AssessEasyKeyboard(this,R.id.aenumberkbd, R.xml.assesseasynumberkbd1);
        String keyboardxmlname = "assesseasynumberkbd_" + globalvault.keyboardlanguage.toLowerCase();

        int keyboardxmlresid = getResources().getIdentifier(keyboardxmlname, "xml", getPackageName());

        if(keyboardxmlresid != 0)
            aekbd = new AssessEasyKeyboard(this,R.id.aenumberkbd, keyboardxmlresid);
        else  // If Failed to load the resource (keyboard XML file corresponding to the language), use default english numerals keyboard
            aekbd = new AssessEasyKeyboard(this,R.id.aenumberkbd, R.xml.assesseasynumberkbd_english);

        // Register the EditText box with the custom keyboard
        aekbd.registerEditText(R.id.editTextQuotient);
        aekbd.registerEditText(R.id.editTextReminder);

        // Play Audio file (if any) associated with the Question, if the flag globalvault.audioautoplay is set to true
        if(!TextUtils.isEmpty(this.audio_base64string)) {
            if(globalvault.audioautoplay) { // If the Audio to be played when the screen opens
                audioManager.playAudio(this.audio_base64string);
            }
        }

        // Display the Question Number (on the right-top corner)
        try {
            TextView questionNumber = (TextView) findViewById(R.id.textViewQuestionNumber);
            String displayQuestionNumber = questionid + "/" + globalvault.questions.length;
            questionNumber.setText(displayQuestionNumber);
        }
        catch(Exception e) {
            Log.e("EASYASSESS", "Setting Question Number. Exception: errormsg:"+e.toString());
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

        String answergiven = globalvault.questions[questionid-1].getAnswerGiven();
        if(answergiven == null) { // Never has crossed this Question even once (neither 'Skipped' nor 'submitted' and pressed 'Back' on this Question screen. So, store the First and Second numbers so that same numbers can be displayed when User comes back to this Question.
            // answergiven will not be null if Child has crossed this Question at least once (clicking 'Next' on this Question)
            TextView tvNumber1 = (TextView) findViewById(R.id.textViewDividend);
            String firstnumber = tvNumber1.getText().toString();
            TextView tvNumber2 = (TextView) findViewById(R.id.textViewDivisor);
            String secondnumber = tvNumber2.getText().toString();
            String answerstr = firstnumber + "," + secondnumber; // Store the two numbers as this need to be displayed if Child comes back to this screen (even if the Child has skipped this Question earlier)
            globalvault.questions[questionid - 1].setAnswerGiven(answerstr);
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

            String reminder = "";
            if (reminder_editable != null) {
                reminder = reminder_editable.toString();

                // If Child has not entered anything for remainder, treat it as 0 internally (this default '0' is not displayed on the remainder field though
                if (reminder == null)  {
                    reminder = AssessEasyKeyboard.replaceEnglishnumeralsToLocal("0");
                }
                else if(reminder.length() == 0) {
                    reminder = AssessEasyKeyboard.replaceEnglishnumeralsToLocal("0");
                }
                else if(reminder.length() > 1) {  // If Child enters '00' or '000' etc , the value should be considered as '0'
                    boolean allzeros = true;
                    String remindereng = AssessEasyKeyboard.replaceLocalnumeralsToEnglish(reminder);
                    for(int i = 0; i < remindereng.length(); i++) {
                        if(remindereng.charAt(i) != '0') {
                            allzeros = false;
                            break;
                        }
                    }
                    if(allzeros) reminder = AssessEasyKeyboard.replaceEnglishnumeralsToLocal("0");;
                }
                else;
            }
            else
                reminder = AssessEasyKeyboard.replaceEnglishnumeralsToLocal("0");;

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
                    int intFirstnum = Integer.parseInt(AssessEasyKeyboard.replaceLocalnumeralsToEnglish(dividend.trim()));
                    int intSecondnum = Integer.parseInt(AssessEasyKeyboard.replaceLocalnumeralsToEnglish(divisor.trim()));
                    int answer_quotient = intFirstnum / intSecondnum;
                    int answer_reminder = intFirstnum % intSecondnum;
                    String correct_answer = answer_quotient+","+answer_reminder;
                    globalvault.questions[questionid - 1].setAnswerCorrect(correct_answer);

                    if ( (AssessEasyKeyboard.replaceLocalnumeralsToEnglish(quotient.trim()).equals(Integer.toString(answer_quotient))) && (AssessEasyKeyboard.replaceLocalnumeralsToEnglish(reminder.trim()).equals(Integer.toString(answer_reminder))))
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
