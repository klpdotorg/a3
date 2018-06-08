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

public class qp_mtf_image_image extends AppCompatActivity {

    int questionid = 0;

    DragAndDropListenerForImage blankbox1Listener = new DragAndDropListenerForImage();
    DragAndDropListenerForImage blankbox2Listener = new DragAndDropListenerForImage();
    DragAndDropListenerForImage blankbox3Listener = new DragAndDropListenerForImage();
    DragAndDropListenerForImage blankbox4Listener = new DragAndDropListenerForImage();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qp_mtf_image_image);

        // set the background image (pick an image randomly from the QP_BGRND_IMGS array)
        int bkgrndimagearrayindex = new Random().nextInt(globalvault.QP_BGRND_IMGS.length-1);
        ConstraintLayout clayout = (ConstraintLayout) findViewById(R.id.ConstraintLayout_parent_mtfimageimage);
        clayout.setBackgroundResource(globalvault.QP_BGRND_IMGS[bkgrndimagearrayindex]);


        // Saves the questionid passed to this page
        Intent intent = getIntent(); // get the Intent that started this activity
        questionid =  intent.getIntExtra("EASYASSESS_QUESTIONID",0);


        TextView tvquestiontext = (TextView)findViewById(R.id.textViewQuestionText);
        tvquestiontext.setText(globalvault.questions[questionid-1].getQuestionText());

        ArrayList qdatalist = globalvault.questions[questionid-1].getQuestionDataList();

        // Leftside Option ImageViews
        ImageView option1 = (ImageView)findViewById(R.id.imageViewOption1);
        ImageView option2 = (ImageView)findViewById(R.id.imageViewOption2);
        ImageView option3 = (ImageView)findViewById(R.id.imageViewOption3);
        ImageView option4 = (ImageView)findViewById(R.id.imageViewOption4);
        // Rightside Choices ImageViews - Views to drag
        ImageView choice1 = (ImageView)findViewById(R.id.imageViewChoice1);
        ImageView choice2 = (ImageView)findViewById(R.id.imageViewChoice2);
        ImageView choice3 = (ImageView)findViewById(R.id.imageViewChoice3);
        ImageView choice4 = (ImageView)findViewById(R.id.imageViewChoice4);
        // Middle Blank ImageViews - Views to drop onto
        ImageView blankbox1 = (ImageView)findViewById(R.id.imageViewBlankbox1);
        ImageView blankbox2 = (ImageView)findViewById(R.id.imageViewBlankbox2);
        ImageView blankbox3 = (ImageView)findViewById(R.id.imageViewBlankbox3);
        ImageView blankbox4 = (ImageView)findViewById(R.id.imageViewBlankbox4);

        Bitmap[] choiceImages = new Bitmap[4];

        try {
            for (int i = 0; i < qdatalist.size(); i++) {

                assessquestiondata qdata = (assessquestiondata) qdatalist.get(i);
                String paramname = qdata.name;

                String imageString = qdata.filecontent_base64;
                byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                if (paramname.equals("option1img")) {
                    option1.setImageBitmap(decodedImage);
                }
                else if (paramname.equals("option2img")) {
                    option2.setImageBitmap(decodedImage);
                }
                else if (paramname.equals("option3img")) {
                    option3.setImageBitmap(decodedImage);
                }
                else if (paramname.equals("option4img")) {
                    option4.setImageBitmap(decodedImage);
                }
                else if (paramname.equals("choice1img")) {
                    choice1.setImageBitmap(decodedImage);
                    choiceImages[0] = decodedImage;
                }
                else if (paramname.equals("choice2img")) {
                    choice2.setImageBitmap(decodedImage);
                    choiceImages[1] = decodedImage;
                }
                else if (paramname.equals("choice3img")) {
                    choice3.setImageBitmap(decodedImage);
                    choiceImages[2] = decodedImage;
                }
                else if (paramname.equals("choice4img")) {
                    choice4.setImageBitmap(decodedImage);
                    choiceImages[3] = decodedImage;
                }
                else ;
            }
        }
        catch(Exception e) {
            Log.e("EASYASSESS", "qp_mtf_image_image: exception: "+e.getMessage());
        }

        // sets the options selected (when navigating backwards, can fill the answer entered earlier in the answer field)
        String answer = globalvault.questions[questionid-1].getAnswerGiven();
        if(!TextUtils.isEmpty(answer)) {
            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "qp_mtf_image_image: setting the answer. answer:"+answer);

            String[] arrAnswer = answer.split(",");

            if(arrAnswer.length == 4) {
                blankbox1.setImageBitmap(choiceImages[Integer.parseInt(arrAnswer[0])-1]);
                blankbox2.setImageBitmap(choiceImages[Integer.parseInt(arrAnswer[1])-1]);
                blankbox3.setImageBitmap(choiceImages[Integer.parseInt(arrAnswer[2])-1]);
                blankbox4.setImageBitmap(choiceImages[Integer.parseInt(arrAnswer[3])-1]);
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

        int[] arrImageViewIDs = new int[4];

        arrImageViewIDs[0] = this.blankbox1Listener.getChoiceImageViewId();
        arrImageViewIDs[1] = this.blankbox2Listener.getChoiceImageViewId();
        arrImageViewIDs[2] = this.blankbox3Listener.getChoiceImageViewId();
        arrImageViewIDs[3] = this.blankbox4Listener.getChoiceImageViewId();

        int[] arrAnswer = new int[4];

        for(int i=0; i < 4; i++) {

            if(arrImageViewIDs[i] == 0) {
                if(TextUtils.isEmpty(currentanswerstring)) {
                    if (MainActivity.debugalerts)
                        Log.d("EASYASSESS", "qp_mtf_image_image: clickedNext: The blankbox#"+i+" not filled");
                    if(globalvault.allowskipquestions) {
                        globalvault.questions[questionid - 1].setPass("S");
                        this.invokeAssessmentManagerActivity();
                        return;
                    }
                    else
                        return;
                }
            }

            if(arrImageViewIDs[i] == R.id.imageViewChoice1)
                arrAnswer[i] = 1; // choice1 dropped in the blankbox #i
            else if(arrImageViewIDs[i] == R.id.imageViewChoice2)
                arrAnswer[i] = 2; // choice2 dropped in the blankbox #i
            else if(arrImageViewIDs[i] == R.id.imageViewChoice3)
                arrAnswer[i] = 3;
            else if(arrImageViewIDs[i] == R.id.imageViewChoice4)
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

        if(globalvault.questions[questionid-1].getAnswerCorrect().equals(answerStr.trim()))
            globalvault.questions[questionid - 1].setPass("P");
        else
            globalvault.questions[questionid - 1].setPass("F");

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
            Log.d("EASYASSESS", "qp_mtf_image_image: clickedNext: fromactivvity: "+fromactivityname+" questionid:"+questionid);
    }
}
