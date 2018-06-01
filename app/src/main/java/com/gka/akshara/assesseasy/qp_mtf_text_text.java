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

public class qp_mtf_text_text extends AppCompatActivity {

    int questionid = 0;


    DragAndDropDragListener blankbox1Listener = new DragAndDropDragListener();
    DragAndDropDragListener blankbox2Listener = new DragAndDropDragListener();
    DragAndDropDragListener blankbox3Listener = new DragAndDropDragListener();
    DragAndDropDragListener blankbox4Listener = new DragAndDropDragListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qp_mtf_text_text);

        // set the background image (pick an image randomly from the QP_BGRND_IMGS array)
        int bkgrndimagearrayindex = new Random().nextInt(globalvault.QP_BGRND_IMGS.length-1);
        ConstraintLayout clayout = findViewById(R.id.ConstraintLayout_parent_mtftexttext);
        clayout.setBackgroundResource(globalvault.QP_BGRND_IMGS[bkgrndimagearrayindex]);


        // Saves the questionid passed to this page
        Intent intent = getIntent(); // get the Intent that started this activity
        questionid =  intent.getIntExtra("EASYASSESS_QUESTIONID",0);


        TextView tvquestiontext = findViewById(R.id.textViewQuestionText);
        tvquestiontext.setText(globalvault.questions[questionid-1].getQuestionText());

        ArrayList qdatalist = globalvault.questions[questionid-1].getQuestionDataList();

        // Leftside Option TextViews
        TextView option1 = findViewById(R.id.textViewOption1);
        TextView option2 = findViewById(R.id.textViewOption2);
        TextView option3 = findViewById(R.id.textViewOption3);
        TextView option4 = findViewById(R.id.textViewOption4);
        // Rightside choices TextViews - Views to drag
        TextView choice1 = findViewById(R.id.textViewChoice1);
        TextView choice2 = findViewById(R.id.textViewChoice2);
        TextView choice3 = findViewById(R.id.textViewChoice3);
        TextView choice4 = findViewById(R.id.textViewChoice4);
        // Middle Blank TextViews - Views to drop onto
        TextView blankbox1 = findViewById(R.id.textViewBlankbox1);
        TextView blankbox2 = findViewById(R.id.textViewBlankbox2);
        TextView blankbox3 = findViewById(R.id.textViewBlankbox3);
        TextView blankbox4 = findViewById(R.id.textViewBlankbox4);

        String[] choiceTexts = new String[4];

        try {
            for (int i = 0; i < qdatalist.size(); i++) {

                assessquestiondata qdata = (assessquestiondata) qdatalist.get(i);
                String paramname = qdata.name;

                if (paramname.equals("option1txt")) {
                    option1.setText(qdata.value);
                } else if (paramname.equals("option2txt")) {
                    option2.setText(qdata.value);
                } else if (paramname.equals("option3txt")) {
                    option3.setText(qdata.value);
                } else if (paramname.equals("option4txt")) {
                    option4.setText(qdata.value);
                }
                else if (paramname.equals("choice1txt")) {
                    choiceTexts[0] = qdata.value;
                    choice1.setText(qdata.value);
                } else if (paramname.equals("choice2txt")) {
                    choiceTexts[1] = qdata.value;
                    choice2.setText(qdata.value);
                } else if (paramname.equals("choice3txt")) {
                    choiceTexts[2] = qdata.value;
                    choice3.setText(qdata.value);
                } else if (paramname.equals("choice4txt")) {
                    choiceTexts[3] = qdata.value;
                    choice4.setText(qdata.value);
                } else;

            }
        }
        catch(Exception e) {
            Log.e("EASYASSESS", "qp_mtf_text_text: exception: "+e.getMessage());
        }


        // sets the options selected (when navigating backwards, can fill the answer entered earlier in the answer field)
        String answer = globalvault.questions[questionid-1].getAnswerGiven();
        if(!TextUtils.isEmpty(answer)) {

            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "qp_mtf_text_text: setting the answer. answer:"+answer);

            String[] arrAnswer = answer.split(",");

            if(arrAnswer.length == 4) {
                blankbox1.setText(choiceTexts[Integer.parseInt(arrAnswer[0])-1]);
                blankbox2.setText(choiceTexts[Integer.parseInt(arrAnswer[1])-1]);
                blankbox3.setText(choiceTexts[Integer.parseInt(arrAnswer[2])-1]);
                blankbox4.setText(choiceTexts[Integer.parseInt(arrAnswer[3])-1]);
            }
        }

        // Set touch and drag listeners
        // Views to drag : set touch listeners
        choice1.setOnTouchListener(new DragAndDropTouchListener());
        choice2.setOnTouchListener(new DragAndDropTouchListener());
        choice3.setOnTouchListener(new DragAndDropTouchListener());
        choice4.setOnTouchListener(new DragAndDropTouchListener());
        // View to drop onto: set drag listeners
        blankbox1.setOnDragListener(blankbox1Listener);
        blankbox2.setOnDragListener(blankbox2Listener);
        blankbox3.setOnDragListener(blankbox3Listener);
        blankbox4.setOnDragListener(blankbox4Listener);
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

        String currentanswerstring = globalvault.questions[questionid-1].getAnswerGiven(); // If user has already submitted answer on this screen, this will have values

        int[] arrTextViewIDs = new int[4];

        arrTextViewIDs[0] = this.blankbox1Listener.getChoiceTextViewId();
        arrTextViewIDs[1] = this.blankbox2Listener.getChoiceTextViewId();
        arrTextViewIDs[2] = this.blankbox3Listener.getChoiceTextViewId();
        arrTextViewIDs[3] = this.blankbox4Listener.getChoiceTextViewId();

        int[] arrAnswer = new int[4];

        for(int i=0; i < 4; i++) {

            if(arrTextViewIDs[i] == 0) {
                if(TextUtils.isEmpty(currentanswerstring)) { // if currentanswerstring is not empty, user might have reached here by pressing back button on the screen ahead or going forward after traversing back
                    if (MainActivity.debugalerts)
                        Log.d("EASYASSESS", "qp_mtf_text_text: clickedNext: The blankbox#"+i+" not filled");
                    if(globalvault.allowskipquestions)
                        this.invokeAssessmentManagerActivity();
                    else
                        return;
                }
            }

            if(arrTextViewIDs[i] == R.id.textViewChoice1)
                arrAnswer[i] = 1; // choice1 dropped in the blankbox #i
            else if(arrTextViewIDs[i] == R.id.textViewChoice2)
                arrAnswer[i] = 2; // choice2 dropped in the blankbox #i
            else if(arrTextViewIDs[i] == R.id.textViewChoice3)
                arrAnswer[i] = 3;
            else if(arrTextViewIDs[i] == R.id.textViewChoice4)
                arrAnswer[i] = 4;
            else;
        }

        String answerStr = "";

        if(TextUtils.isEmpty(currentanswerstring)) { // currentanswerstring will be empty when user submit answer on this screen for the first time
            answerStr = arrAnswer[0] + "," + arrAnswer[1] + "," + arrAnswer[2] + "," + arrAnswer[3];
        }
        else { // User might have changed only two boxes and kept the other two boxex unchanged (for example, dragged only two images to two boxes to edit the answer when comes to the screen by back button)
            String[] arrCurrentAnswer = currentanswerstring.split(",");
            for(int k = 0; k < arrAnswer.length; k++) {
                if (arrAnswer[k] == 0) arrAnswer[k] = Integer.parseInt(arrCurrentAnswer[k]);
            }
            answerStr = arrAnswer[0] + "," + arrAnswer[1] + "," + arrAnswer[2] + "," + arrAnswer[3];
        }

        globalvault.questions[questionid-1].setAnswerGiven(answerStr);

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
            Log.d("EASYASSESS", "qp_mtf_text_text: clickedNext: fromactivvity: "+fromactivityname+" questionid:"+questionid);

    }
}
