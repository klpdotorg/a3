package com.gka.akshara.assesseasy;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.TextViewCompat;
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

import static android.util.TypedValue.COMPLEX_UNIT_SP;

public class qp_arithmetic_add extends AppCompatActivity {

    AssessEasyKeyboard aekbd;
    int questionid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qp_arithmetic_add);

        // set the background image (pick an image randomly from the QP_BGRND_IMGS array)
        int bkgrndimagearrayindex = new Random().nextInt(globalvault.QP_BGRND_IMGS.length-1);
        ConstraintLayout clayout = (ConstraintLayout) findViewById(R.id.ConstraintLayout_parent_arithmeticadd);
        clayout.setBackgroundResource(globalvault.QP_BGRND_IMGS[bkgrndimagearrayindex]);


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

        }

        TextView tvNumber1 = (TextView)findViewById(R.id.textViewNumber1);
        if(firstnumberset != null) {
            int randindex = rand.nextInt(firstnumberset.length);
            tvNumber1.setText(firstnumberset[randindex]);
        }

        TextView tvNumber2 = (TextView)findViewById(R.id.textViewNumber2);
        if(secondnumberset != null) {
            int randindex = rand.nextInt(secondnumberset.length);
            tvNumber2.setText(secondnumberset[randindex]);
        }

        // sets the answer field (when navigating backwards, can fill the answer entered before)
        String answer = globalvault.questions[questionid-1].getAnswerGiven();
        EditText tvAnswer = (EditText) findViewById(R.id.editTextAnswer);
        if(!TextUtils.isEmpty(answer)) {  // If answer is not empty, then the firstnumber, secondnumber and answer be set to the previously entered values (reached this screen by pressing back button)
            String[] arrAnswer = answer.split(","); // Answer is stored as firstnumber, secondnumber, givenanswer
            tvNumber1.setText(arrAnswer[0]);
            tvNumber2.setText(arrAnswer[1]);
            tvAnswer.setText(arrAnswer[2]);
        }

        // To hide the keyboard initially, remove the focus from the EditText field and move it to the dummy view.
        // When user clicks on the EditText field, the keyboard will appear
        View dummyview = (View) findViewById(R.id.dummyViewForFocus);
        tvAnswer.clearFocus(); // (this not working as of now) Remove the focus from the EditText box so that the keyboard will not showup by default
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

        EditText editTextAnswer = (EditText)findViewById(R.id.editTextAnswer);
        Editable answer_editable = editTextAnswer.getText();

        if(answer_editable != null) {
            String answer = answer_editable.toString();
            if(answer.equals("")) {
                if (MainActivity.debugalerts)
                    Log.d("EASYASSESS", "qp_arithmetic_add: clickedNext: answer is empty");
                if(globalvault.allowskipquestions) {
                    globalvault.questions[questionid-1].setPass("S"); // 'S' for Skipped
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
                    int correctAnswer = intFirstnum + intSecondnum;
                    globalvault.questions[questionid - 1].setAnswerCorrect(Integer.toString(correctAnswer));

                    if (correctAnswer == Integer.parseInt(answer.trim()))
                        globalvault.questions[questionid - 1].setPass("P");
                    else
                        globalvault.questions[questionid - 1].setPass("F");
                }
                catch(Exception e) {
                    Log.e("EASYASSESS", "qp_qrithmetic_add: clickedNext: Exception: "+e.toString());
                }

                globalvault.questions[questionid-1].setAnswerGiven(answerstr); // Given answer field will have string in the format firstnumber,secondnumber,givenanswer
                this.invokeAssessmentManagerActivity();
            }
        }
        else {
            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "qp_arithmetic_add: clickedNext: answer_editable is null");
            if(globalvault.allowskipquestions) {
                globalvault.questions[questionid-1].setPass("S");
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
            Log.d("EASYASSESS", "qp_arithmetic_add: clickedNext: fromactivvity: "+fromactivityname+" questionid:"+questionid);

    }

    public void clickedAudio(View view) {

        //String base64encodedaudiostring = "";
        //audioManager.playAudio(base64encodedaudiostring);
    }

    public void drawShape() {

        //setContentView(new shapePainter(this)); // this call will change the current view and display this view
        // calling an invalidate() function on a View, will redraw it by calling onDraw() function
    }


    @Override
    public void onPause() {
        super.onPause();

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "qp_arithmetic_add: onPause(): questionid:"+questionid);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "qp_arithmetic_add: onResume(): questionid:"+questionid);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "qp_arithmetic_add: onStop(): questionid:"+questionid);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "qp_arithmetic_add: onDestroy(): questionid:"+questionid);
    }
}
