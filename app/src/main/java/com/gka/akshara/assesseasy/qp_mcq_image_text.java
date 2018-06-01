package com.gka.akshara.assesseasy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

public class qp_mcq_image_text extends AppCompatActivity {

    int questionid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qp_mcq_image_text);

        // set the background image (pick an image randomly from the QP_BGRND_IMGS array)
        int bkgrndimagearrayindex = new Random().nextInt(globalvault.QP_BGRND_IMGS.length-1);
        ConstraintLayout clayout = findViewById(R.id.ConstraintLayout_parent_mcqimagetext);
        clayout.setBackgroundResource(globalvault.QP_BGRND_IMGS[bkgrndimagearrayindex]);

        // Saves the questionid passed to this page
        Intent intent = getIntent(); // get the Intent that started this activity
        questionid =  intent.getIntExtra("EASYASSESS_QUESTIONID",0);


        TextView tvquestiontext = findViewById(R.id.textViewQuestionText);
        tvquestiontext.setText(globalvault.questions[questionid-1].getQuestionText());

        ArrayList qdatalist = globalvault.questions[questionid-1].getQuestionDataList();

        ImageView questionimg = findViewById(R.id.mcqQuestionImage);
        RadioGroup radiogrp_mcqoptions = findViewById(R.id.radiogroup_optionbuttonsgrp);

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
                    } else if (paramname.equals("option1txt")) {
                        ((RadioButton) radiogrp_mcqoptions.getChildAt(0)).setText(qdata.value);
                    } else if (paramname.equals("option2txt")) {
                        ((RadioButton) radiogrp_mcqoptions.getChildAt(1)).setText(qdata.value);
                    } else if (paramname.equals("option3txt")) {
                        ((RadioButton) radiogrp_mcqoptions.getChildAt(2)).setText(qdata.value);
                    } else if (paramname.equals("option4txt")) {
                        ((RadioButton) radiogrp_mcqoptions.getChildAt(3)).setText(qdata.value);
                    } else ;
                }
            }
        }
        catch(Exception e) {
            Log.e("EASYASSESS", "qp_mcq_image_text: exception: "+e.getMessage());
        }

        // sets the option selected (when navigating backwards, can fill the answer entered earlier in the answer field)
        String answer = globalvault.questions[questionid-1].getAnswerGiven();
        if(!TextUtils.isEmpty(answer)) {
            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "qp_mcq_image_text: setting the answer.");

            int intAnswer = Integer.parseInt(answer);
            for (int i = 0; i < radiogrp_mcqoptions.getChildCount(); i++) {
                if(intAnswer == (i + 1)) {
                    ((RadioButton) radiogrp_mcqoptions.getChildAt(i)).setChecked(true);
                }
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

        RadioGroup radiogrp_mcqoptions = findViewById(R.id.radiogroup_optionbuttonsgrp);
        int selectedRadioButtonId = radiogrp_mcqoptions.getCheckedRadioButtonId();

        if(selectedRadioButtonId == -1) { // Nothing selected
            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "qp_mcq_image_text: clickedNext: No option selected");
            if(globalvault.allowskipquestions)
                this.invokeAssessmentManagerActivity();
            else
                return;
        }

        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        //String selectedRadioButtonText = selectedRadioButton.getText().toString();
        int selectedoption = radiogrp_mcqoptions.indexOfChild(selectedRadioButton) + 1; // Index starts from 0. So add +1 as options are numbered 1 to 4
        globalvault.questions[questionid-1].setAnswerGiven(Integer.toString(selectedoption));

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
            Log.d("EASYASSESS", "qp_mcq_image_text: clickedNext: fromactivity: "+fromactivityname+" questionid:"+questionid);

    }
}
