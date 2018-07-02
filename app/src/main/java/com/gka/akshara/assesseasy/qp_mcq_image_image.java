package com.gka.akshara.assesseasy;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
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

public class qp_mcq_image_image extends AppCompatActivity {

    int questionid = 0;
    String audio_base64string = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qp_mcq_image_image);

        // set the background image (pick an image randomly from the QP_BGRND_IMGS array)
        int bkgrndimagearrayindex = new Random().nextInt(globalvault.QP_BGRND_IMGS.length-1);
        ConstraintLayout clayout = (ConstraintLayout) findViewById(R.id.ConstraintLayout_parent_mcq_image_image);
        clayout.setBackgroundResource(globalvault.QP_BGRND_IMGS[bkgrndimagearrayindex]);

        // Saves the questionid passed to this page
        Intent intent = getIntent(); // get the Intent that started this activity
        questionid =  intent.getIntExtra("EASYASSESS_QUESTIONID",0);

        TextView tvquestiontext = (TextView)findViewById(R.id.textViewQuestionText);
        tvquestiontext.setText(globalvault.questions[questionid-1].getQuestionText());

        ArrayList qdatalist = globalvault.questions[questionid-1].getQuestionDataList();

        TouchImageView questionimg = (TouchImageView)findViewById(R.id.mcqQuestionImage);
        RadioGroup radiogrp_mcqoptions = (RadioGroup)findViewById(R.id.radiogroup_optionbuttonsgrp);

     try {
         for (int i = 0; i < qdatalist.size(); i++) {

             assessquestiondata qdata = (assessquestiondata) qdatalist.get(i);
             String paramname = qdata.name;

             String imageString = qdata.filecontent_base64;
             byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
             Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
             Drawable drawableimg = new BitmapDrawable(getResources(), decodedImage);

             if (paramname.equals("questionimg")) {
                 questionimg.setImageBitmap(decodedImage);   // to set image for an 'ImageView'
             }
             else if (paramname.equals("option1img")) {
                 // ((RadioButton) radiogrp_mcqoptions.getChildAt(0)).setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawableimg); // align image to bottom. This function doesnt display the image properly scaled. Use setBackground(drawable) instead)
                 // ((RadioButton) radiogrp_mcqoptions.getChildAt(0)).setBackground(drawableimg); // // This wont work either as there will be no change visible to the button when clicked/selected since the background is no more set to the 'selector' xml file (optionbox.xml), but to an image.
                 // So, create a 'selector' dynamically using StateListDrawable to define images/shapes for various states of the RadioButton
                 // Codes for Various states: Ref: https://developer.android.com/reference/android/graphics/drawable/StateListDrawable

                 StateListDrawable state = new StateListDrawable();
                 state.addState(new int[] {-android.R.attr.state_checked},drawableimg); // for default state (-ve of checked state). Set the background to the option image, which will be displayed by default
                 state.addState(new int[] {android.R.attr.state_pressed},getDrawable(R.drawable.optionbox)); // Rectangle Shape defined in the optionbox.xml file. Can set to a drawable image also here
                 state.addState(new int[] {android.R.attr.state_checked},getDrawable(R.drawable.optionbox)); // When this option is selected. Shape is defined in optionbox.xml in the drawable directory

                 ((RadioButton) radiogrp_mcqoptions.getChildAt(0)).setBackground(state);
             }
             else if (paramname.equals("option2img")) {
                 StateListDrawable state = new StateListDrawable();
                 state.addState(new int[] {-android.R.attr.state_checked},drawableimg); // for default state (-ve of checked state). Set the background to the option image, which will be displayed by default
                 state.addState(new int[] {android.R.attr.state_pressed},getDrawable(R.drawable.optionbox)); // Rectangle Shape defined in the optionbox.xml file. Can set to a drawable image also here
                 state.addState(new int[] {android.R.attr.state_checked},getDrawable(R.drawable.optionbox)); // When this option is selected. Shape is defined in optionbox.xml in the drawable directory

                 ((RadioButton) radiogrp_mcqoptions.getChildAt(1)).setBackground(state);
             }
             else if (paramname.equals("option3img")) {
                 StateListDrawable state = new StateListDrawable();
                 state.addState(new int[] {-android.R.attr.state_checked},drawableimg); // for default state (-ve of checked state). Set the background to the option image, which will be displayed by default
                 state.addState(new int[] {android.R.attr.state_pressed},getDrawable(R.drawable.optionbox)); // Rectangle Shape defined in the optionbox.xml file. Can set to a drawable image also here
                 state.addState(new int[] {android.R.attr.state_checked},getDrawable(R.drawable.optionbox)); // When this option is selected. Shape is defined in optionbox.xml in the drawable directory

                 ((RadioButton) radiogrp_mcqoptions.getChildAt(2)).setBackground(state);
             }
             else if (paramname.equals("option4img")) {
                 StateListDrawable state = new StateListDrawable();
                 state.addState(new int[] {-android.R.attr.state_checked},drawableimg); // for default state (-ve of checked state). Set the background to the option image, which will be displayed by default
                 state.addState(new int[] {android.R.attr.state_pressed},getDrawable(R.drawable.optionbox)); // Rectangle Shape defined in the optionbox.xml file. Can set to a drawable image also here
                 state.addState(new int[] {android.R.attr.state_checked},getDrawable(R.drawable.optionbox)); // When this option is selected. Shape is defined in optionbox.xml in the drawable directory

                 ((RadioButton) radiogrp_mcqoptions.getChildAt(3)).setBackground(state);
             }
             else ;

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
         Log.e("EASYASSESS", "qp_mcq_image_image: exception: "+e.getMessage());
     }


        // sets the option selected (when navigating backwards, can fill the answer entered earlier in the answer field)
        String answer = globalvault.questions[questionid-1].getAnswerGiven();
        if(!TextUtils.isEmpty(answer)) {
            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "qp_mcq_image_image: setting the answer. answer:"+answer);

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
                Log.d("EASYASSESS", "qp_mcq_img_img: clickedNext: No option selected");
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

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "qp_mcq_img_img: clickedNext: selectedoption: "+selectedoption+" correctAnswer:"+globalvault.questions[questionid-1].getAnswerCorrect());

        try {
            if (globalvault.questions[questionid - 1].getAnswerCorrect().equals(Integer.toString(selectedoption)))
                globalvault.questions[questionid - 1].setPass("P");
            else
                globalvault.questions[questionid - 1].setPass("F");
        }
        catch(Exception e) {
            Log.e("EASYASSESS", "qp_mcq_img_img: clickedNext: Exception:CorrectAnswer is null"+e.toString());
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
            Log.d("EASYASSESS", "qp_mcq_img_img: clickedNext: fromactivvity: "+fromactivityname+" questionid:"+questionid);

    }

    public void clickedAudio(View view) {

        audioManager.playAudio(this.audio_base64string);
    }

}
