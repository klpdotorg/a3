package com.gka.akshara.assesseasy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.akshara.assessment.a3.R;

import java.util.ArrayList;
import java.util.Random;

public class qp_truefalse_text_image extends AppCompatActivity {

    int questionid = 0;
    String audio_base64string = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qp_truefalse_text_image);

        // set the background image (pick an image randomly from the QP_BGRND_IMGS array)
        int bkgrndimagearrayindex = new Random().nextInt(globalvault.QP_BGRND_IMGS.length-1);
        if((bkgrndimagearrayindex >= (globalvault.QP_BGRND_IMGS.length -1)) || (bkgrndimagearrayindex < 0))
            bkgrndimagearrayindex = 0;
        ConstraintLayout clayout = (ConstraintLayout) findViewById(R.id.ConstraintLayout_parent_truefalse_textimage);
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

        ArrayList qdatalist = globalvault.questions[questionid-1].getQuestionDataList();

        //TouchImageView questionimg = (TouchImageView)findViewById(R.id.mcqQuestionImage);
        ImageView questionimg = (ImageView)findViewById(R.id.mcqQuestionImage);
        ImageView questionimgexpanded = (ImageView)findViewById(R.id.expanded_image); // This is the invisible full screen ImageView to show the expanded Image when clicked on the original Question Image

        RadioGroup radiogrp_mcqoptions = (RadioGroup)findViewById(R.id.radiogroup_optionbuttonsgrp);

        try {
            for (int i = 0; i < qdatalist.size(); i++) {

                assessquestiondata qdata = (assessquestiondata) qdatalist.get(i);
                String paramname = qdata.name;

                if(!TextUtils.isEmpty(paramname)) {
                    if (paramname.equals("questionimg")) {
                        String imageString = qdata.filecontent_base64;
                        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
                        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        questionimg.setImageBitmap(decodedImage);   // to set image for an 'ImageView'
                        questionimgexpanded.setImageBitmap(decodedImage);
                    } else if (paramname.equals("option1txt")) {
                        ((RadioButton) radiogrp_mcqoptions.getChildAt(0)).setText(qdata.value);
                    } else if (paramname.equals("option2txt")) {
                        ((RadioButton) radiogrp_mcqoptions.getChildAt(1)).setText(qdata.value);
                    }
                    else ;
                }

                if(qdata.datatype.equals("audio")) {
                    audio_base64string = qdata.filecontent_base64;
                    ImageView audiobuttonimageview = (ImageView)findViewById(R.id.buttonAudio);
                    //int res = getResources().getIdentifier("audiosymbol", "drawable", this.getPackageName());
                    //audiobuttonimageview.setImageResource(res);
                    audiobuttonimageview.setVisibility(View.VISIBLE);
                }
            }
        }
        catch(Exception e) {
            Log.e("EASYASSESS", "qp_truefalse_text_image: exception: "+e.getMessage());
        }


        // sets the option selected (when navigating backwards, can fill the answer entered earlier in the answer field)
        String answer = globalvault.questions[questionid-1].getAnswerGiven();
        if(!TextUtils.isEmpty(answer)) {
            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "qp_truefalse_text_image: setting the answer.");

            int intAnswer = Integer.parseInt(answer);
            for (int i = 0; i < radiogrp_mcqoptions.getChildCount(); i++) {
                if(intAnswer == (i + 1)) {
                    ((RadioButton) radiogrp_mcqoptions.getChildAt(i)).setChecked(true);
                }
            }
        }

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

        Intent intent = new Intent(this, assessment_manager.class);
        String fromactivityname = this.getLocalClassName();
        intent.putExtra("EASYASSESS_FROMACTIVITY", fromactivityname);
        intent.putExtra("EASYASSESS_QUESTIONID", questionid);
        intent.putExtra("EASYASSESS_CLICKEDBACKARROW", true);
        startActivity(intent);

    }

    public void clickedNext(View view) {

        RadioGroup radiogrp_mcqoptions = (RadioGroup)findViewById(R.id.radiogroup_optionbuttonsgrp);
        int selectedRadioButtonId = radiogrp_mcqoptions.getCheckedRadioButtonId();

        if(selectedRadioButtonId == -1) { // Nothing selected
            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "qp_truefalse_text_image: clickedNext: No option selected");
            if(globalvault.allowskipquestions) {
                globalvault.questions[questionid - 1].setPass("S");
                this.invokeAssessmentManagerActivity();
                return;
            }
            else
                return;
        }

        RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioButtonId);
        //String selectedRadioButtonText = selectedRadioButton.getText().toString();
        int selectedoption = radiogrp_mcqoptions.indexOfChild(selectedRadioButton) + 1; // Index starts from 0. So add +1 as options are numbered 1 to 4
        globalvault.questions[questionid-1].setAnswerGiven(Integer.toString(selectedoption));

        try {
            if (globalvault.questions[questionid - 1].getAnswerCorrect().equals(Integer.toString(selectedoption)))
                globalvault.questions[questionid - 1].setPass("P");
            else
                globalvault.questions[questionid - 1].setPass("F");
        }
        catch(Exception e) {
            Log.e("EASYASSESS", "qp_truefalse_text_image: clickedNext: Exception:CorrectAnswer is null"+e.toString());
            globalvault.questions[questionid - 1].setPass("F");
        }
        this.invokeAssessmentManagerActivity();
    }

    public void invokeAssessmentManagerActivity() {

        Intent intent = new Intent(this, assessment_manager.class);
        String fromactivityname = this.getLocalClassName();
        intent.putExtra("EASYASSESS_FROMACTIVITY", fromactivityname);
        intent.putExtra("EASYASSESS_QUESTIONID", questionid);
        intent.putExtra("EASYASSESS_CLICKEDBACKARROW", false);
        startActivity(intent);

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "qp_truefalse_text_image: clickedNext: fromactivity: "+fromactivityname+" questionid:"+questionid);
    }

    public void clickedAudio(View view) {

        audioManager.playAudio(this.audio_base64string);
    }

    public void clickedToZoomQuestionImage(View view) {

        ImageView questionimg = (ImageView)findViewById(R.id.expanded_image);
        questionimg.setVisibility(View.VISIBLE);
    }

    public void clickedToCloseExpandedQuestionImage(View view) {

        ImageView questionimg = (ImageView)findViewById(R.id.expanded_image);
        questionimg.setVisibility(View.INVISIBLE);
    }

}
