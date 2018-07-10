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


public class qp_arithmetic_multiply_horiz extends AppCompatActivity {


    AssessEasyKeyboard aekbd;
    int questionid = 0;
    String audio_base64string = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qp_arithmetic_multiply_horiz);

        // set the background image (pick an image randomly from the QP_BGRND_IMGS array)
        int bkgrndimagearrayindex = new Random().nextInt(globalvault.QP_BGRND_IMGS.length-1);
        if((bkgrndimagearrayindex >= (globalvault.QP_BGRND_IMGS.length -1)) || (bkgrndimagearrayindex < 0))
            bkgrndimagearrayindex = 0;
        ConstraintLayout clayout = (ConstraintLayout) findViewById(R.id.ConstraintLayout_parent_arithmeticmultiply_horiz);
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

        String[] firstnumberset = null;
        String[] secondnumberset = null;

        for(int i = 0; i < qdatalist.size(); i++) {
            assessquestiondata qdata = (assessquestiondata)qdatalist.get(i);
            if(qdata.name.equals("firstnumber"))
                firstnumberset = qdata.value.split(",");
            else if(qdata.name.equals("secondnumber"))
                secondnumberset = qdata.value.split(",");
            else ;

            if(qdata.datatype.equals("audio")) {
                audio_base64string = qdata.filecontent_base64;
                ImageView audiobuttonimageview = (ImageView)findViewById(R.id.buttonAudio);
                //int res = getResources().getIdentifier("audiosymbol", "drawable", this.getPackageName());
                //audiobuttonimageview.setImageResource(res);
                audiobuttonimageview.setVisibility(View.VISIBLE);
            }

        }

        TextView tvNumber1 = (TextView)findViewById(R.id.textViewNumber1);
        int randindex = 0;
        if(firstnumberset != null) {
            randindex = rand.nextInt(firstnumberset.length);
            tvNumber1.setText(firstnumberset[randindex]);
        }

        TextView tvNumber2 = (TextView)findViewById(R.id.textViewNumber2);
        if(secondnumberset != null) {
            if (randindex >= secondnumberset.length) {
                randindex = 0;
            }
            tvNumber2.setText(secondnumberset[randindex]); // Choose the value for the Second Number that corresponds (same index) to the First Number value
        }

        // sets the number and answer fields (when navigating backwards, fill the numbers randomly generated earlier and the answer entered before)
        String answer = globalvault.questions[questionid-1].getAnswerGiven();
        EditText tvAnswer = (EditText) findViewById(R.id.editTextAnswer);
        if(!TextUtils.isEmpty(answer)) {  // Display the previously shown values, including the Answer if Child has entered the Answer previously (reached this screen by pressing back button)
            String[] arrAnswer = answer.split(","); // Answer is stored as firstnumber, secondnumber, givenanswer
            tvNumber1.setText(arrAnswer[0]); // First Number
            tvNumber2.setText(arrAnswer[1]); // Second Number
            if(arrAnswer.length == 3) { // If the Child has entered the Answer earlier
                if (!TextUtils.isEmpty(arrAnswer[2])) //
                    tvAnswer.setText(arrAnswer[2]); // Answer Entered
            }
        }

        // To hide the keyboard initially, remove the focus from the EditText field and move it to the dummy view.
        // When user clicks on the EditText field, the keyboard will appear
        View dummyview = (View) findViewById(R.id.dummyViewForFocus);
        tvAnswer.clearFocus();
        dummyview.requestFocus();

        // create the keyboard
        aekbd = new AssessEasyKeyboard(this,R.id.aenumberkbd, R.xml.assesseasynumberkbd1);
        //aekbd.showCustomKeyboard(findViewById(R.id.aenumberkbd));

        // Register the EditText box with the custom keyboard
        aekbd.registerEditText(R.id.editTextAnswer);

        // Play Audio file (if any) associated with the Question, if the flag globalvault.audioautoplay is set to true
        if(!TextUtils.isEmpty(this.audio_base64string)) {
            if(globalvault.audioautoplay) { // If the Audio to be played when the screen opens
                audioManager.playAudio(this.audio_base64string);
            }
        }
    }

    // When the phone/system back button is pressed
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
            TextView tvNumber1 = (TextView) findViewById(R.id.textViewNumber1);
            String firstnumber = tvNumber1.getText().toString();
            TextView tvNumber2 = (TextView) findViewById(R.id.textViewNumber2);
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

        EditText editTextAnswer = (EditText)findViewById(R.id.editTextAnswer);
        Editable answer_editable = editTextAnswer.getText();

        if(answer_editable != null) {
            String answer = answer_editable.toString();
            if(answer.equals("")) {
                if (MainActivity.debugalerts)
                    Log.d("EASYASSESS", "qp_arithmetic_multiply_horiz: clickedNext: answer is empty string");
                if(globalvault.allowskipquestions) {
                    globalvault.questions[questionid - 1].setPass("S");

                    TextView tvNumber1 = (TextView)findViewById(R.id.textViewNumber1);
                    String firstnumber = tvNumber1.getText().toString();
                    TextView tvNumber2 = (TextView)findViewById(R.id.textViewNumber2);
                    String secondnumber = tvNumber2.getText().toString();
                    String answerstr = firstnumber+","+secondnumber; // Store the two numbers as this need to be displayed if Child comes back to this screen (even if the Child has skipped this Question earlier)
                    globalvault.questions[questionid-1].setAnswerGiven(answerstr);

                    this.invokeAssessmentManagerActivity();
                    return;
                }
                else
                    return;
            }
            else {
                TextView tvNumber1 = (TextView)findViewById(R.id.textViewNumber1);
                String firstnumber = tvNumber1.getText().toString();
                TextView tvNumber2 = (TextView)findViewById(R.id.textViewNumber2);
                String secondnumber = tvNumber2.getText().toString();
                String answerstr = firstnumber+","+secondnumber+","+answer;

                try {
                    int intFirstnum = Integer.parseInt(firstnumber.trim());
                    int intSecondnum = Integer.parseInt(secondnumber.trim());
                    int correctAnswer = intFirstnum * intSecondnum;
                    globalvault.questions[questionid - 1].setAnswerCorrect(Integer.toString(correctAnswer));

                    if (correctAnswer == Integer.parseInt(answer.trim()))
                        globalvault.questions[questionid - 1].setPass("P");
                    else
                        globalvault.questions[questionid - 1].setPass("F");
                }
                catch(Exception e) {
                    Log.e("EASYASSESS", "qp_qrithmetic_multiply_horiz: clickedNext: Exception: "+e.toString());
                }

                globalvault.questions[questionid-1].setAnswerGiven(answerstr); // Given answer field will have string in the format firstnumber,secondnumber,givenanswer
                this.invokeAssessmentManagerActivity();
            }
        }
        else {
            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "qp_arithmetic_multiply_horiz: clickedNext: answer_editable is null");
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
            Log.d("EASYASSESS", "qp_arithmetic_multiply_horiz: clickedNext: fromactivity: "+fromactivityname+" questionid:"+questionid);

    }

    public void clickedAudio(View view) {

        audioManager.playAudio(this.audio_base64string);
    }

}
