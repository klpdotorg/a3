package com.gka.akshara.assesseasy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.akshara.assessment.a3.R;

import java.util.ArrayList;
import java.util.Random;

public class qp_fib_image_text extends AppCompatActivity {

    AssessEasyKeyboard aekbd;
    int questionid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qp_fib_image_text);

        // set the background image (pick an image randomly from the QP_BGRND_IMGS array)
        int bkgrndimagearrayindex = new Random().nextInt(globalvault.QP_BGRND_IMGS.length-1);
        ConstraintLayout clayout = (ConstraintLayout) findViewById(R.id.ConstraintLayout_parent_fibimagetext);
        clayout.setBackgroundResource(globalvault.QP_BGRND_IMGS[bkgrndimagearrayindex]);

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "qp_fib_image_text: set background done");

        // Saves the questionid passed to this page
        Intent intent = getIntent(); // get the Intent that started this activity
        questionid =  intent.getIntExtra("EASYASSESS_QUESTIONID",0);

        TextView tvquestiontext = (TextView)findViewById(R.id.textViewQuestionFIB);
        tvquestiontext.setText(globalvault.questions[questionid-1].getQuestionText());

        ArrayList qdatalist = globalvault.questions[questionid-1].getQuestionDataList();

        ImageView questionimg = (ImageView)findViewById(R.id.fibQuestionImage);

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
                    }
                }
            }
        }
        catch(Exception e) {
            Log.e("EASYASSESS", "qp_fib_image_text: exception: "+e.getMessage());
        }


        // sets the answer field (when navigating backwards, can fill the answer entered earlier in the answer field)
        String answer = globalvault.questions[questionid-1].getAnswerGiven();
        EditText tvAnswer = (EditText) findViewById(R.id.editTextAnswer);
        if(!TextUtils.isEmpty(answer)) {
             tvAnswer.setText(answer);
        }

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "qp_fib_image_text: answer set");

        // To hide the keyboard initially, remove the focus from the EditText field and move it to the dummy view.
        // When user clicks on the EditText field, the keyboard will appear
        View dummyview = (View) findViewById(R.id.dummyViewForFocus);
        tvAnswer.clearFocus();
        dummyview.requestFocus();

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "qp_fib_image_text: focus removed");


        // create the keyboard
        aekbd = new AssessEasyKeyboard(this,R.id.aenumberkbd, R.xml.assesseasynumberkbd);
        //aekbd.showCustomKeyboard(findViewById(R.id.aenumberkbd));

        // Register the EditText box with the custom keyboard
        aekbd.registerEditText(R.id.editTextAnswer);

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "qp_fib_image_text: registered edit");
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
                    Log.d("EASYASSESS", "qp_fib_image_text: clickedNext: answer is empty string");
                if(globalvault.allowskipquestions)
                    this.invokeAssessmentManagerActivity();
                else
                    return;
            }
            else {
                globalvault.questions[questionid-1].setAnswerGiven(answer);
                this.invokeAssessmentManagerActivity();
            }
        }
        else {
            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "qp_fib_image_text: clickedNext: answer_editable is null");
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
            Log.d("EASYASSESS", "qp_fib_image_text: clickedNext: fromactivvity: "+fromactivityname+" questionid:"+questionid);

    }
}