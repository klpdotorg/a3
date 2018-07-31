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

public class qp_fib_text extends AppCompatActivity {

    AssessEasyKeyboard aekbd;
    int questionid = 0;
    String audio_base64string = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qp_fib_text);

        // set the background image (pick an image randomly from the QP_BGRND_IMGS array)
        int bkgrndimagearrayindex = new Random().nextInt(globalvault.QP_BGRND_IMGS.length-1);
        if((bkgrndimagearrayindex >= (globalvault.QP_BGRND_IMGS.length -1)) || (bkgrndimagearrayindex < 0))
            bkgrndimagearrayindex = 0;
        ConstraintLayout clayout = (ConstraintLayout) findViewById(R.id.ConstraintLayout_parent_fibtext);
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

        TextView tvquestiontext = (TextView)findViewById(R.id.textViewQuestionFIB);
        tvquestiontext.setText(globalvault.questions[questionid-1].getQuestionText());

        ArrayList qdatalist = globalvault.questions[questionid-1].getQuestionDataList();

        try {
            if(qdatalist != null) {
                for (int i = 0; i < qdatalist.size(); i++) {

                    assessquestiondata qdata = (assessquestiondata) qdatalist.get(i);

                    if (qdata.datatype.equals("audio")) {
                        audio_base64string = qdata.filecontent_base64;
                        ImageView audiobuttonimageview = (ImageView) findViewById(R.id.buttonAudio);
                        //int res = getResources().getIdentifier("audiosymbol", "drawable", this.getPackageName());
                        //audiobuttonimageview.setImageResource(res);
                        audiobuttonimageview.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        catch(Exception e) {
            Log.e("EASYASSESS", "qp_fib_text: exception: "+e.getMessage());
        }

        // sets the answer field (when navigating backwards, can fill the answer entered earlier in the answer field)
        String answer = globalvault.questions[questionid-1].getAnswerGiven();
        EditText tvAnswer = (EditText) findViewById(R.id.editTextAnswer);
        if(!TextUtils.isEmpty(answer)) {
            tvAnswer.setText(answer);
        }

        // set the Answer Unit Label
        String answerunitlabel = globalvault.questions[questionid-1].getAnswerUnitLabel();
        TextView txtviewunitlabel = (TextView)findViewById(R.id.textViewAnswerUnitLabel);
        txtviewunitlabel.setText(answerunitlabel);

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
                    Log.d("EASYASSESS", "qp_fib_text: clickedNext: answer is empty string");
                if(globalvault.allowskipquestions) {
                    globalvault.questions[questionid - 1].setPass("S");
                    this.invokeAssessmentManagerActivity();
                    return;
                }
                else
                    return;
            }
            else {
                try {
                    if (globalvault.questions[questionid - 1].getAnswerCorrect().equals(answer.trim()))
                        globalvault.questions[questionid - 1].setPass("P");
                    else
                        globalvault.questions[questionid - 1].setPass("F");
                }
                catch(Exception e) {
                    Log.e("EASYASSESS", "qp_fib_text: clickedNext: Exception:CorrectAnswer is null"+e.toString());
                    globalvault.questions[questionid - 1].setPass("F");
                }
                globalvault.questions[questionid-1].setAnswerGiven(answer);
                this.invokeAssessmentManagerActivity();
            }
        }
        else {
            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "qp_fib_text: clickedNext: answer_editable is null");
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
            Log.d("EASYASSESS", "qp_fib_text: clickedNext: fromactivity: "+fromactivityname+" questionid:"+questionid);

    }

    public void clickedAudio(View view) {

        audioManager.playAudio(this.audio_base64string);
    }
}
