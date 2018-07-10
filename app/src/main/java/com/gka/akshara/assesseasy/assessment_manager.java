package com.gka.akshara.assesseasy;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.akshara.assessment.a3.R;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class assessment_manager extends AppCompatActivity {

    String fromactivityname = "";
    String toactivityname = "";
    int questionid_last = 0;
    int questionid_next = 0;
    boolean fromanimation = false; // Whether came back from animation page
    boolean showfinalpage = false; // set to true after the last question is submitted
    boolean showfirstpage = false; // show first page after first question when backarrow is pressed and goes backwards
    boolean clickedbackarrow = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // setContentView(R.layout.activity_assessment_manager);
        if(MainActivity.debugalerts)
            Log.d("EASYASSESS","Enter assessment manager onCreate");

        globalvault.finished = false;

        Intent intent = getIntent(); // get the Intent that started this activity
        Bundle databundle = intent.getExtras();

        if(databundle != null) {
            this.fromactivityname = databundle.getString("EASYASSESS_FROMACTIVITY");
            this.questionid_last = databundle.getInt("EASYASSESS_QUESTIONID");
            this.clickedbackarrow = databundle.getBoolean("EASYASSESS_CLICKEDBACKARROW");

            if(this.fromactivityname.equals(globalvault.containerapp_invokefromactivity)) {

                globalvault.a3app_institutionId = databundle.getLong("A3APP_INSTITUTIONID");
                globalvault.a3app_gradeId = databundle.getInt("A3APP_GRADEID");
                globalvault.a3app_gradeString = databundle.getString("A3APP_GRADESTRING");
                globalvault.a3app_childId = databundle.getString("A3APP_CHILDID");
                globalvault.a3app_language = databundle.getString("A3APP_LANGUAGE");
                globalvault.a3app_titletext = databundle.getString("A3APP_TITLETEXT");
                //Log.d("sssss", databundle.getString("A3APP_TITLETEXT"));

                if (MainActivity.debugalerts)
                    Log.d("EASYASSESS", "assessment_manager: Recived parameters from the ContainerApp: EASYASSESS_FROMACTIVITY:" + this.fromactivityname + " A3APP_GRADEID:" + globalvault.a3app_gradeId + " A3APP_GRADESTRING:" + globalvault.a3app_gradeString + " A3APP_INSTITUTIONID:" + globalvault.a3app_institutionId + " A3APP_CHILDID:" + globalvault.a3app_childId + " A3APP_LANGUAGE:" + globalvault.a3app_language);

                globalvault.datetime_assessment_start = System.currentTimeMillis(); // In seconds

            }

            if(!TextUtils.isEmpty(this.fromactivityname)) {
                if (fromactivityname.contains("animpage")) {
                    fromanimation = true;
                }
            }
        }

        if(MainActivity.debugalerts)
            Log.d("EASYASSESS","fromactivityname:"+fromactivityname+" questionid_last:"+questionid_last);


        if(globalvault.questions == null) {  // Load Questions if the array is null (no Questions are loaded)

            if(MainActivity.debugalerts)
                Log.d("EASYASSESS","assessment_manager: Loading QuestionSet from the database");

            globalvault.questionsetid = databundle.getInt("EASYASSESS_QUESTIONSETID");

            if(globalvault.questionsetid == 0) {
                if(MainActivity.debugalerts)
                    Log.d("EASYASSESS","assessment_manager: No EASYASSESS_QUESTIONSETID received");
            }

            // Read the QuestionSet from the database
            globalvault.dsmgr = new deviceDatastoreMgr();
            globalvault.dsmgr.initializeDS(this);

            // Add Test Questions to the Database For testing (Used ONLY for testing)
            // this.addTestQuestionsToDatabase();

            // Read the Questions from the Database (for the given QuestionSet ID)
            boolean rtn = globalvault.dsmgr.readQuestions(globalvault.questionsetid); // returns false if failed to read the QuestionSet from the DB

            // If failed to read QuestionSet from the database, show the message (go to messagedisplay activity screen)
            if(!rtn) {
                if(!globalvault.demomodeifnodb) {
                    String msg = "Failed to load the QuestionSet from the Device Database. QuestionSet ID:"+globalvault.questionsetid;
                    String gotoactivity = "MainActivity"; // The activity to go when user clicks 'OK' on the message screen
                    this.invokeMessageDisplayActivity(msg, gotoactivity);
                    return;
                }
                else { // Switch to DEMO-MODE and load test questions
                    if(globalvault.demomodeifnodb) {
                        if (MainActivity.debugalerts)
                            Log.d("EASYASSESS", "assessment_manager: No Questions loaded from DB. Switching to DEMO-MODE");
                        this.createTestQuestions();
                    }
                }
            }
            else {
                if (MainActivity.debugalerts)
                    Log.d("EASYASSESS", "assessment_manager: Read Questions from the Database. Total #of Questions:"+globalvault.questions.length);
            }


            /*
            // To use DialogueFragment to display error message (instead of displaymessage activity screen)
            Bundle args = new Bundle();
            args.putString("positiveactivityname","MainActivity");
            args.putString("negativityactivityname","MainActivity");
            args.putString("message","No QuestionSet is loaded");
            messageDisplayMgr displaymgr = new messageDisplayMgr();
            displaymgr.setArguments(args);
            displaymgr.show(getSupportFragmentManager(), "EASYASSESS");
            */

        }


        if((!fromanimation) && (!clickedbackarrow) && (globalvault.showanimation) && (questionid_last % globalvault.animationdisplayinterval == 0) && (questionid_last < (globalvault.questions.length-1))) { // Show animation page (if not already displayed animation for this questionid and this questionid needs animation to be displayed

            toactivityname = "animpage1";
            questionid_next = questionid_last;

            if(MainActivity.debugalerts)
                Log.d("EASYASSESS","toactivityname:"+toactivityname+" questionid_next:"+questionid_next);

        }
        else { // Call next question page
            if(clickedbackarrow) {
                questionid_next = questionid_last - 1;

                if((globalvault.integrated) && (questionid_next == 0))
                    questionid_next = questionid_last; // When integrated with the ContainerApp, there is no home screen for the AssessmentApp. So, if clicked 'back' from the first Question, display the same page instead of going to Home page
            }
            else {
                questionid_next = questionid_last + 1;
            }

            String questiontempltype_next = " ";

            if(questionid_next == globalvault.questions.length + 1) { // Last question over
                showfinalpage = true;
                showfirstpage = false;
            }
            else if(questionid_next == 0) { // Will reach this condition only if the globalvault.integrated is set to false (if set to false, take User to Home/Start page when clicked the back arrow on first Question screen)
                showfirstpage = true;
                showfinalpage = false;
            }
            else {
                showfinalpage = false;
                showfirstpage = false;
            }

            if((!showfinalpage) && (!showfirstpage)) {

                questiontempltype_next = globalvault.questions[questionid_next - 1].getQuestionTemplType(); // questionid_next-1 as the global.questions array index is starting at 0
                if(MainActivity.debugalerts)
                    Log.d("EASYASSESS","assessment_manager:onCreate - questiontempltype_next = "+questiontempltype_next);

                int ntempltypes = globalvault.questionTemplTypes.length;

                for(int i=0; i < ntempltypes; i++) {
                    if(questiontempltype_next.equals(globalvault.questionTemplTypes[i])) {
                        toactivityname = globalvault.activities[i];
                        break;
                    }
                }
            }
        }


        if(showfinalpage){
            // Show the final page
            // this.finish();
            // System.exit(0);

            //How finish() and System.exit works:
            //finish() - finishes the activity where it is called from and you see the previous activity.
            //System.exit(0) - restarts the app with one fewer activity on the stack.
            //So, if you called ActivityB from ActivityA, and System.exit(0) is called in ActivityB,
            //then the application will be killed and started immediately with only one activity ActivityA

            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "showfinalpage is true. Invoking the finalpage.");

            try {
                String toactivityclassname = globalvault.finalpageactivity;
                if (MainActivity.debugalerts)
                    Log.d("EASYASSESS", "assessment_manager: invoking the finalpage");

                Intent intentnxt = new Intent(this, Class.forName(toactivityclassname));
                startActivity(intentnxt);
            }
            catch (Exception e) {
                Log.e("EASYASSESS", "assessment_manager: Exception."+e.toString());
            }
        }
        else if(showfirstpage) {

            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "showfirststage is true. Invoking the homescreen.");

            try {
                String toactivityclassname = "com.gka.akshara.assesseasy.MainActivity";
                if (MainActivity.debugalerts)
                    Log.d("EASYASSESS", "invoking the firstpage");

                Intent intentnxt = new Intent(this, Class.forName(toactivityclassname));
                startActivity(intentnxt);
            }
            catch (Exception e) {
                Log.e("EASYASSESS", "assessment_manager: Exception."+e.toString());
            }
        }
        else {  // Invoke the next activity
            try {
                String toactivityclassname = "com.gka.akshara.assesseasy." + toactivityname;
                if (MainActivity.debugalerts)
                    Log.d("EASYASSESS", "assessmentmanager: Starting toactivityclassname:" + toactivityclassname+" questionid_next: "+questionid_next);

                Intent intentnxt = new Intent(this, Class.forName(toactivityclassname));

                intentnxt.putExtra("EASYASSESS_QUESTIONID", questionid_next);
                startActivity(intentnxt);
            }
            catch (Exception e) {
                Log.e("EASYASSESS", "assessment_manager: Exception."+e.toString());
            }
        }

    }

    public void invokeMessageDisplayActivity(String message, String gotoactivity) {

        Intent intent = new Intent(this, messagedisplay.class);
        intent.putExtra("EASYASSESS_MESSAGE", message);
        intent.putExtra("EASYASSESS_GOTOACTIVITY", gotoactivity);
        startActivity(intent);

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "assessment_manager: invoking MessageDisplay Activity");

    }

    public void addTestQuestionsToDatabase() {

        int qsetid = 1;

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "assessmentmanager: Enter addTestQuestionsToDatabase");

        globalvault.dsmgr.initializeDS(this);
        globalvault.dsmgr.dropTables();
        globalvault.dsmgr.createTables();

        globalvault.dsmgr.addQuestionSet(qsetid);

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "assessmentmanager:addTestQuestionsToDatabase. addQuestionSet done");

        assessquestion[] dbquestions;
        dbquestions = new assessquestion[2];

        // Sample Question for Question Template Type 'FIB_ADD'
        dbquestions[0] = new assessquestion();

        String randomstr = UUID.randomUUID().toString();
        String qid1 =  randomstr.substring(2, 5)+  randomstr.substring(14,17)+randomstr.substring(24,32);

        dbquestions[0].setQuestionID(qid1);
        dbquestions[0].setQuestionTemplType("FIB_ADD");
        dbquestions[0].setQuestionText("Add the two numbers");
        dbquestions[0].setAnswerCorrect("");

        assessquestiondata paramadd1 = new assessquestiondata();
        paramadd1.name = "firstnumber";
        paramadd1.label = "First Number";
        paramadd1.datatype = "text";
        paramadd1.role = "parameter";
        paramadd1.position = "left";
        paramadd1.value = "8, 12, 14";
        paramadd1.filecontent_base64 = "";
        dbquestions[0].addQuestionData(paramadd1);

        assessquestiondata paramadd2 = new assessquestiondata();
        paramadd2.name = "secondnumber";
        paramadd2.label = "Second Number";
        paramadd2.datatype = "text";
        paramadd2.role = "parameter";
        paramadd2.position = " ";
        paramadd2.value = "5, 9, 7";
        paramadd2.filecontent_base64 = "";
        dbquestions[0].addQuestionData(paramadd2);

        Log.d("EASYASSESS", "assessmentmanager:addTestQuestionsToDatabase. Added paramadd2");

        // Add an Audio file to the Question (for Testing)

        assessquestiondata paramaudio = new assessquestiondata();
        paramaudio.name = "audio";
        paramaudio.label = "";
        paramaudio.datatype = "audio";
        paramaudio.role = "parameter";
        paramaudio.value = "";

        String base64audiostring = "";
        try {
            //FileInputStream fis = new FileInputStream("assets/testaudio.mp3");
            AssetManager mngr = getAssets();
            InputStream fis =  mngr.open("testaudio.mp3");

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int read = 0;
            byte[] buffer = new byte[1024];
            while (read != -1) {
                read = fis.read(buffer);
                if (read != -1)
                    out.write(buffer,0,read);
            }
            out.close();
            byte[] bytes = out.toByteArray();
            base64audiostring = Base64.encodeToString(bytes, Base64.DEFAULT);
            Log.e("EASYASSESS", "assessmentmanager:addTestQuestionsToDatabase. Audio string:"+base64audiostring);

        }
        catch(Exception e) {
            Log.e("EASYASSESS", "assessmentmanager:addTestQuestionsToDatabase. File exception"+e.toString());

        }
        paramaudio.filecontent_base64 = base64audiostring;
        dbquestions[0].addQuestionData(paramaudio);


        globalvault.dsmgr.addQuestion(dbquestions[0], qsetid);

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "assessmentmanager:addTestQuestionsToDatabase. Added FIB_ADD question");


        //*********   Sample Question for Question Type 'MCQ_IMG' ****************

        dbquestions[1] = new assessquestion();

        String randomstr1= UUID.randomUUID().toString();
        String qid2 =  randomstr1.substring(2, 5)+  randomstr1.substring(14,17)+randomstr1.substring(24,32);
        dbquestions[1].setQuestionID(qid2);

        dbquestions[1].setQuestionTemplType("MCQ_IMG");
        dbquestions[1].setQuestionText("Select Many Tomatoes");

        assessquestiondata parammcqimg1 = new assessquestiondata();
        parammcqimg1.name = "option1img";
        parammcqimg1.label = "Option1";
        parammcqimg1.datatype = "image";
        parammcqimg1.role = "parameter";
        parammcqimg1.value = "";

        ByteArrayOutputStream baos7 = new ByteArrayOutputStream();
        Bitmap bitmap7 = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        bitmap7.compress(Bitmap.CompressFormat.PNG, 100, baos7);
        byte[] imageBytes7 = baos7.toByteArray();
        String imageString7 = Base64.encodeToString(imageBytes7, Base64.DEFAULT);

        parammcqimg1.filecontent_base64 = imageString7;
        dbquestions[1].addQuestionData(parammcqimg1);


        assessquestiondata parammcqimg2 = new assessquestiondata();
        parammcqimg2.name = "option2img";
        parammcqimg2.label = "Option2";
        parammcqimg2.datatype = "image";
        parammcqimg2.role = "parameter";
        parammcqimg2.value = "";

        ByteArrayOutputStream baos8 = new ByteArrayOutputStream();
        Bitmap bitmap8 = BitmapFactory.decodeResource(getResources(), R.drawable.cake);
        bitmap8.compress(Bitmap.CompressFormat.PNG, 100, baos8);
        byte[] imageBytes8 = baos8.toByteArray();
        String imageString8 = Base64.encodeToString(imageBytes8, Base64.DEFAULT);

        parammcqimg2.filecontent_base64 = imageString8;
        dbquestions[1].addQuestionData(parammcqimg2);

        assessquestiondata parammcqimg3 = new assessquestiondata();
        parammcqimg3.name = "option3img";
        parammcqimg3.label = "Option3";
        parammcqimg3.datatype = "image";
        parammcqimg3.role = "parameter";
        parammcqimg3.value = "";

        ByteArrayOutputStream baos9 = new ByteArrayOutputStream();
        Bitmap bitmap9 = BitmapFactory.decodeResource(getResources(), R.drawable.soup);
        bitmap9.compress(Bitmap.CompressFormat.PNG, 100, baos9);
        byte[] imageBytes9 = baos9.toByteArray();
        String imageString9 = Base64.encodeToString(imageBytes9, Base64.DEFAULT);

        parammcqimg3.filecontent_base64 = imageString9;
        dbquestions[1].addQuestionData(parammcqimg3);

        assessquestiondata parammcqimg4 = new assessquestiondata();
        parammcqimg4.name = "option4img";
        parammcqimg4.label = "Option4";
        parammcqimg4.datatype = "image";
        parammcqimg4.role = "parameter";
        parammcqimg4.value = "";

        ByteArrayOutputStream baos10 = new ByteArrayOutputStream();
        Bitmap bitmap10 = BitmapFactory.decodeResource(getResources(), R.drawable.manytomatoes);
        bitmap10.compress(Bitmap.CompressFormat.PNG, 100, baos10);
        byte[] imageBytes10 = baos10.toByteArray();
        String imageString10 = Base64.encodeToString(imageBytes10, Base64.DEFAULT);

        parammcqimg4.filecontent_base64 = imageString10;
        dbquestions[1].addQuestionData(parammcqimg4);

        dbquestions[1].setAnswerCorrect("2");

        globalvault.dsmgr.addQuestion(dbquestions[1], qsetid);

    }

    public void createTestQuestions() {

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "assessmentmanager: in createTestQuestions");


        int totalnumberofquestions = 19;

        globalvault.questions = new assessquestion[totalnumberofquestions]; // Add  questions

        for(int i =0 ; i < totalnumberofquestions; i++)
            globalvault.questions[i] = new assessquestion();

        int indexq = 0;

         // Sample Question for Question Template Type 'FIB_ADD'
        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("Add the two numbers");

        assessquestiondata paramadd1 = new assessquestiondata();
        paramadd1.name = "firstnumber";
        paramadd1.label = "First Number";
        paramadd1.datatype = "text";
        paramadd1.role = "parameter";
        paramadd1.value = "10, 12, 15";
        globalvault.questions[indexq].addQuestionData(paramadd1);

        assessquestiondata paramadd2 = new assessquestiondata();
        paramadd2.name = "secondnumber";
        paramadd2.label = "Second Number";
        paramadd2.datatype = "text";
        paramadd2.role = "parameter";
        paramadd2.value = "5, 9, 7";
        globalvault.questions[indexq].addQuestionData(paramadd2);

        indexq++;

        // Sample Question for Question Template Type 'FIB_SUBTRACT'
        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("Subtract the second number from the first number");

        assessquestiondata paramsub1 = new assessquestiondata();
        paramsub1.name = "firstnumber";
        paramsub1.label = "First Number";
        paramsub1.datatype = "text";
        paramsub1.role = "parameter";
        paramsub1.value = "20, 22, 25";
        globalvault.questions[indexq].addQuestionData(paramsub1);

        assessquestiondata paramsub2 = new assessquestiondata();
        paramsub2.name = "secondnumber";
        paramsub2.label = "Second Number";
        paramsub2.datatype = "text";
        paramsub2.role = "parameter";
        paramsub2.value = "15, 19, 17";
        globalvault.questions[indexq].addQuestionData(paramsub2);

        indexq++;

        // Sample Question for Question Template Type 'FIB_MULTIPLY'
        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("Multiply the two numbers");

        assessquestiondata parammul1 = new assessquestiondata();
        parammul1.name = "firstnumber";
        parammul1.label = "First Number";
        parammul1.datatype = "text";
        parammul1.role = "parameter";
        parammul1.value = "8, 5, 3";
        globalvault.questions[indexq].addQuestionData(parammul1);

        assessquestiondata parammul2= new assessquestiondata();
        parammul2.name = "secondnumber";
        parammul2.label = "Second Number";
        parammul2.datatype = "text";
        parammul2.role = "parameter";
        parammul2.value = "2, 5, 3";
        globalvault.questions[indexq].addQuestionData(parammul2);

        indexq++;

        // Sample Question for Question Template Type 'FIB_DIVISION_NOREMINDER'

        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("Perform the division and enter the Quotient");

        assessquestiondata paramdivwhole1 = new assessquestiondata();
        paramdivwhole1.name = "dividend";
        paramdivwhole1.label = "Dividend";
        paramdivwhole1.datatype = "text";
        paramdivwhole1.role = "parameter";
        paramdivwhole1.value = "20, 10, 30";
        globalvault.questions[indexq].addQuestionData(paramdivwhole1);

        assessquestiondata paramdivwhole2= new assessquestiondata();
        paramdivwhole2.name = "divisor";
        paramdivwhole2.label = "Divisor";
        paramdivwhole2.datatype = "text";
        paramdivwhole2.role = "parameter";
        paramdivwhole2.value = "5, 2, 10";
        globalvault.questions[indexq].addQuestionData(paramdivwhole2);

        indexq++;

        // Sample Question for Question Template Type 'FIB_DIVISION_WITHREMINDER'

        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("Perform the division and enter the Quotient and the Reminder");

        assessquestiondata paramdivrem1 = new assessquestiondata();
        paramdivrem1.name = "dividend";
        paramdivrem1.label = "Dividend";
        paramdivrem1.datatype = "text";
        paramdivrem1.role = "parameter";
        paramdivrem1.value = "15, 9, 17";
        globalvault.questions[indexq].addQuestionData(paramdivrem1);

        assessquestiondata paramdivrem2= new assessquestiondata();
        paramdivrem2.name = "divisor";
        paramdivrem2.label = "Divisor";
        paramdivrem2.datatype = "text";
        paramdivrem2.role = "parameter";
        paramdivrem2.value = "4, 6, 2";
        globalvault.questions[indexq].addQuestionData(paramdivrem2);

        indexq++;

       //*********   Sample Question for Question Type 'MCQ_IMG_IMG' ****************

        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("Select the matching type from the options");
        globalvault.questions[indexq].setAnswerCorrect("1");

        assessquestiondata attribmcqimgimg = new assessquestiondata();
        attribmcqimgimg.name = "questionimg";
        attribmcqimgimg.label = "Question Image";
        attribmcqimgimg.datatype = "image";
        attribmcqimgimg.role = "attribute";
        attribmcqimgimg.value = "";

        //Create base64 image (for test. In actual app, this base64 image would be fetched from the database)

        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        // Bitmap bitmap = BitmapFactory.decodeFile(pathOfYourImage);  // to convert from a file
        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.tomatoes);
        bitmap1.compress(Bitmap.CompressFormat.PNG, 100, baos1);
        byte[] imageBytes1 = baos1.toByteArray();
        String imageString1 = Base64.encodeToString(imageBytes1, Base64.DEFAULT);

        attribmcqimgimg.filecontent_base64 = imageString1;
        globalvault.questions[indexq].addQuestionData(attribmcqimgimg);

        assessquestiondata parammcqimgimg1 = new assessquestiondata();
        parammcqimgimg1.name = "option1img";
        parammcqimgimg1.label = "Option1";
        parammcqimgimg1.datatype = "image";
        parammcqimgimg1.role = "parameter";
        parammcqimgimg1.value = "";

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        bitmap2.compress(Bitmap.CompressFormat.PNG, 100, baos2);
        byte[] imageBytes2 = baos2.toByteArray();
        String imageString2 = Base64.encodeToString(imageBytes2, Base64.DEFAULT);

        parammcqimgimg1.filecontent_base64 = imageString2;
        globalvault.questions[indexq].addQuestionData(parammcqimgimg1);


        assessquestiondata parammcqimgimg2 = new assessquestiondata();
        parammcqimgimg2.name = "option2img";
        parammcqimgimg2.label = "Option2";
        parammcqimgimg2.datatype = "image";
        parammcqimgimg2.role = "parameter";
        parammcqimgimg2.value = "";

        ByteArrayOutputStream baos3 = new ByteArrayOutputStream();
        Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.cake);
        bitmap3.compress(Bitmap.CompressFormat.PNG, 100, baos3);
        byte[] imageBytes3 = baos3.toByteArray();
        String imageString3 = Base64.encodeToString(imageBytes3, Base64.DEFAULT);

        parammcqimgimg2.filecontent_base64 = imageString3;
        globalvault.questions[indexq].addQuestionData(parammcqimgimg2);

        assessquestiondata parammcqimgimg3 = new assessquestiondata();
        parammcqimgimg3.name = "option3img";
        parammcqimgimg3.label = "Option3";
        parammcqimgimg3.datatype = "image";
        parammcqimgimg3.role = "parameter";
        parammcqimgimg3.value = "";

        ByteArrayOutputStream baos4 = new ByteArrayOutputStream();
        Bitmap bitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.soup);
        bitmap4.compress(Bitmap.CompressFormat.PNG, 100, baos4);
        byte[] imageBytes4 = baos4.toByteArray();
        String imageString4 = Base64.encodeToString(imageBytes4, Base64.DEFAULT);

        parammcqimgimg3.filecontent_base64 = imageString4;
        globalvault.questions[indexq].addQuestionData(parammcqimgimg3);

        assessquestiondata parammcqimgimg4 = new assessquestiondata();
        parammcqimgimg4.name = "option4img";
        parammcqimgimg4.label = "Option4";
        parammcqimgimg4.datatype = "image";
        parammcqimgimg4.role = "parameter";
        parammcqimgimg4.value = "";

        ByteArrayOutputStream baos5 = new ByteArrayOutputStream();
        Bitmap bitmap5 = BitmapFactory.decodeResource(getResources(), R.drawable.onetomatoe);
        bitmap5.compress(Bitmap.CompressFormat.PNG, 100, baos5);
        byte[] imageBytes5 = baos5.toByteArray();
        String imageString5 = Base64.encodeToString(imageBytes5, Base64.DEFAULT);

        parammcqimgimg4.filecontent_base64 = imageString5;
        globalvault.questions[indexq].addQuestionData(parammcqimgimg4);


        indexq++;


        //*********   Sample Question for Question Type 'MCQ_IMG_TXT' ****************

        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("How many tomatoes are there in the picture?");
        globalvault.questions[indexq].setAnswerCorrect("1");

        assessquestiondata attribmcqimgtxt = new assessquestiondata();
        attribmcqimgtxt.name = "questionimg";
        attribmcqimgtxt.label = "Question Image";
        attribmcqimgtxt.datatype = "image";
        attribmcqimgtxt.role = "attribute";
        attribmcqimgtxt.value = "";

        ByteArrayOutputStream baos6 = new ByteArrayOutputStream();
        // Bitmap bitmap = BitmapFactory.decodeFile(pathOfYourImage);  // to convert from a file
        Bitmap bitmap6 = BitmapFactory.decodeResource(getResources(), R.drawable.tomatoes);
        bitmap6.compress(Bitmap.CompressFormat.PNG, 100, baos6);
        byte[] imageBytes6 = baos6.toByteArray();
        String imageString6 = Base64.encodeToString(imageBytes6, Base64.DEFAULT);

        attribmcqimgtxt.filecontent_base64 = imageString6;
        globalvault.questions[indexq].addQuestionData(attribmcqimgtxt);

        assessquestiondata parammcqimgtxt1 = new assessquestiondata();
        parammcqimgtxt1.name = "option1txt";
        parammcqimgtxt1.label = "Option1";
        parammcqimgtxt1.datatype = "text";
        parammcqimgtxt1.role = "parameter";
        parammcqimgtxt1.value = "2";
        globalvault.questions[indexq].addQuestionData(parammcqimgtxt1);

        assessquestiondata parammcqimgtxt2 = new assessquestiondata();
        parammcqimgtxt2.name = "option2txt";
        parammcqimgtxt2.label = "Option2";
        parammcqimgtxt2.datatype = "text";
        parammcqimgtxt2.role = "parameter";
        parammcqimgtxt2.value = "7";
        globalvault.questions[indexq].addQuestionData(parammcqimgtxt2);


        assessquestiondata parammcqimgtxt3 = new assessquestiondata();
        parammcqimgtxt3.name = "option3txt";
        parammcqimgtxt3.label = "Option3";
        parammcqimgtxt3.datatype = "text";
        parammcqimgtxt3.role = "parameter";
        parammcqimgtxt3.value = "5";
        globalvault.questions[indexq].addQuestionData(parammcqimgtxt3);


        assessquestiondata parammcqimgtxt4 = new assessquestiondata();
        parammcqimgtxt4.name = "option4txt";
        parammcqimgtxt4.label = "Option4";
        parammcqimgtxt4.datatype = "text";
        parammcqimgtxt4.role = "parameter";
        parammcqimgtxt4.value = "4";
        globalvault.questions[indexq].addQuestionData(parammcqimgtxt4);

        indexq++;


        //*********   Sample Question for Question Type 'MCQ_IMG' ****************

        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("Select Many Tomatoes");
        globalvault.questions[indexq].setAnswerCorrect("1");

        assessquestiondata parammcqimg1 = new assessquestiondata();
        parammcqimg1.name = "option1img";
        parammcqimg1.label = "Option1";
        parammcqimg1.datatype = "image";
        parammcqimg1.role = "parameter";
        parammcqimg1.value = "";

        ByteArrayOutputStream baos7 = new ByteArrayOutputStream();
        Bitmap bitmap7 = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        bitmap7.compress(Bitmap.CompressFormat.PNG, 100, baos7);
        byte[] imageBytes7 = baos7.toByteArray();
        String imageString7 = Base64.encodeToString(imageBytes7, Base64.DEFAULT);

        parammcqimg1.filecontent_base64 = imageString7;
        globalvault.questions[indexq].addQuestionData(parammcqimg1);


        assessquestiondata parammcqimg2 = new assessquestiondata();
        parammcqimg2.name = "option2img";
        parammcqimg2.label = "Option2";
        parammcqimg2.datatype = "image";
        parammcqimg2.role = "parameter";
        parammcqimg2.value = "";

        ByteArrayOutputStream baos8 = new ByteArrayOutputStream();
        Bitmap bitmap8 = BitmapFactory.decodeResource(getResources(), R.drawable.cake);
        bitmap8.compress(Bitmap.CompressFormat.PNG, 100, baos8);
        byte[] imageBytes8 = baos8.toByteArray();
        String imageString8 = Base64.encodeToString(imageBytes8, Base64.DEFAULT);

        parammcqimg2.filecontent_base64 = imageString8;
        globalvault.questions[indexq].addQuestionData(parammcqimg2);

        assessquestiondata parammcqimg3 = new assessquestiondata();
        parammcqimg3.name = "option3img";
        parammcqimg3.label = "Option3";
        parammcqimg3.datatype = "image";
        parammcqimg3.role = "parameter";
        parammcqimg3.value = "";

        ByteArrayOutputStream baos9 = new ByteArrayOutputStream();
        Bitmap bitmap9 = BitmapFactory.decodeResource(getResources(), R.drawable.soup);
        bitmap9.compress(Bitmap.CompressFormat.PNG, 100, baos9);
        byte[] imageBytes9 = baos9.toByteArray();
        String imageString9 = Base64.encodeToString(imageBytes9, Base64.DEFAULT);

        parammcqimg3.filecontent_base64 = imageString9;
        globalvault.questions[indexq].addQuestionData(parammcqimg3);

        assessquestiondata parammcqimg4 = new assessquestiondata();
        parammcqimg4.name = "option4img";
        parammcqimg4.label = "Option4";
        parammcqimg4.datatype = "image";
        parammcqimg4.role = "parameter";
        parammcqimg4.value = "";

        ByteArrayOutputStream baos10 = new ByteArrayOutputStream();
        Bitmap bitmap10 = BitmapFactory.decodeResource(getResources(), R.drawable.manytomatoes);
        bitmap10.compress(Bitmap.CompressFormat.PNG, 100, baos10);
        byte[] imageBytes10 = baos10.toByteArray();
        String imageString10 = Base64.encodeToString(imageBytes10, Base64.DEFAULT);

        parammcqimg4.filecontent_base64 = imageString10;
        globalvault.questions[indexq].addQuestionData(parammcqimg4);

        indexq++;

        //*********   Sample Question for Question Type 'MCQ_TXT' ****************

        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("Select Highest number");
        globalvault.questions[indexq].setAnswerCorrect("1");

        assessquestiondata parammcqtxt1 = new assessquestiondata();
        parammcqtxt1.name = "option1txt";
        parammcqtxt1.label = "Option1";
        parammcqtxt1.datatype = "text";
        parammcqtxt1.role = "parameter";
        parammcqtxt1.value = "2";
        globalvault.questions[indexq].addQuestionData(parammcqtxt1);

        assessquestiondata parammcqtxt2 = new assessquestiondata();
        parammcqtxt2.name = "option2txt";
        parammcqtxt2.label = "Option2";
        parammcqtxt2.datatype = "text";
        parammcqtxt2.role = "parameter";
        parammcqtxt2.value = "7";
        globalvault.questions[indexq].addQuestionData(parammcqtxt2);


        assessquestiondata parammcqtxt3 = new assessquestiondata();
        parammcqtxt3.name = "option3txt";
        parammcqtxt3.label = "Option3";
        parammcqtxt3.datatype = "text";
        parammcqtxt3.role = "parameter";
        parammcqtxt3.value = "5";
        globalvault.questions[indexq].addQuestionData(parammcqtxt3);


        assessquestiondata parammcqtxt4 = new assessquestiondata();
        parammcqtxt4.name = "option4txt";
        parammcqtxt4.label = "Option4";
        parammcqtxt4.datatype = "text";
        parammcqtxt4.role = "parameter";
        parammcqtxt4.value = "4";
        globalvault.questions[indexq].addQuestionData(parammcqtxt4);

        indexq++;

        //*********   Sample Question for Question Type 'MTF_IMG_IMG' ****************

        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("Match the items");
        globalvault.questions[indexq].setAnswerCorrect("1,2,3,4");

        assessquestiondata parammtfimgimg1 = new assessquestiondata();
        parammtfimgimg1.name = "option1img";
        parammtfimgimg1.label = "Option1";
        parammtfimgimg1.datatype = "image";
        parammtfimgimg1.role = "parameter";
        parammtfimgimg1.value = "";

        ByteArrayOutputStream baos11 = new ByteArrayOutputStream();
        Bitmap bitmap11 = BitmapFactory.decodeResource(getResources(), R.drawable.cake);
        bitmap11.compress(Bitmap.CompressFormat.PNG, 100, baos11);
        byte[] imageBytes11 = baos11.toByteArray();
        String imageString11 = Base64.encodeToString(imageBytes11, Base64.DEFAULT);

        parammtfimgimg1.filecontent_base64 = imageString11;
        globalvault.questions[indexq].addQuestionData(parammtfimgimg1);

        assessquestiondata parammtfimgimg2 = new assessquestiondata();
        parammtfimgimg2.name = "option2img";
        parammtfimgimg2.label = "Option2";
        parammtfimgimg2.datatype = "image";
        parammtfimgimg2.role = "parameter";
        parammtfimgimg2.value = "";

        ByteArrayOutputStream baos12 = new ByteArrayOutputStream();
        Bitmap bitmap12 = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        bitmap12.compress(Bitmap.CompressFormat.PNG, 100, baos12);
        byte[] imageBytes12 = baos12.toByteArray();
        String imageString12 = Base64.encodeToString(imageBytes12, Base64.DEFAULT);

        parammtfimgimg2.filecontent_base64 = imageString12;
        globalvault.questions[indexq].addQuestionData(parammtfimgimg2);


        assessquestiondata parammtfimgimg3 = new assessquestiondata();
        parammtfimgimg3.name = "option3img";
        parammtfimgimg3.label = "Option3";
        parammtfimgimg3.datatype = "image";
        parammtfimgimg3.role = "parameter";
        parammtfimgimg3.value = "";

        ByteArrayOutputStream baos13 = new ByteArrayOutputStream();
        Bitmap bitmap13 = BitmapFactory.decodeResource(getResources(), R.drawable.frenchfries);
        bitmap13.compress(Bitmap.CompressFormat.PNG, 100, baos13);
        byte[] imageBytes13 = baos13.toByteArray();
        String imageString13 = Base64.encodeToString(imageBytes13, Base64.DEFAULT);

        parammtfimgimg3.filecontent_base64 = imageString13;
        globalvault.questions[indexq].addQuestionData(parammtfimgimg3);

        assessquestiondata parammtfimgimg4 = new assessquestiondata();
        parammtfimgimg4.name = "option4img";
        parammtfimgimg4.label = "Option4";
        parammtfimgimg4.datatype = "image";
        parammtfimgimg4.role = "parameter";
        parammtfimgimg4.value = "";

        ByteArrayOutputStream baos14 = new ByteArrayOutputStream();
        Bitmap bitmap14 = BitmapFactory.decodeResource(getResources(), R.drawable.soup);
        bitmap14.compress(Bitmap.CompressFormat.PNG, 100, baos14);
        byte[] imageBytes14 = baos14.toByteArray();
        String imageString14 = Base64.encodeToString(imageBytes14, Base64.DEFAULT);

        parammtfimgimg4.filecontent_base64 = imageString14;
        globalvault.questions[indexq].addQuestionData(parammtfimgimg4);


        assessquestiondata parammtfimgimg5 = new assessquestiondata();
        parammtfimgimg5.name = "choice1img";
        parammtfimgimg5.label = "Choice1";
        parammtfimgimg5.datatype = "image";
        parammtfimgimg5.role = "parameter";
        parammtfimgimg5.value = "";

        ByteArrayOutputStream baos15 = new ByteArrayOutputStream();
        Bitmap bitmap15 = BitmapFactory.decodeResource(getResources(), R.drawable.soup);
        bitmap15.compress(Bitmap.CompressFormat.PNG, 100, baos15);
        byte[] imageBytes15 = baos15.toByteArray();
        String imageString15 = Base64.encodeToString(imageBytes15, Base64.DEFAULT);

        parammtfimgimg5.filecontent_base64 = imageString15;
        globalvault.questions[indexq].addQuestionData(parammtfimgimg5);


        assessquestiondata parammtfimgimg6 = new assessquestiondata();
        parammtfimgimg6.name = "choice2img";
        parammtfimgimg6.label = "Choice2";
        parammtfimgimg6.datatype = "image";
        parammtfimgimg6.role = "parameter";
        parammtfimgimg6.value = "";

        ByteArrayOutputStream baos16 = new ByteArrayOutputStream();
        Bitmap bitmap16 = BitmapFactory.decodeResource(getResources(), R.drawable.cake);
        bitmap16.compress(Bitmap.CompressFormat.PNG, 100, baos16);
        byte[] imageBytes16 = baos16.toByteArray();
        String imageString16 = Base64.encodeToString(imageBytes16, Base64.DEFAULT);

        parammtfimgimg6.filecontent_base64 = imageString16;
        globalvault.questions[indexq].addQuestionData(parammtfimgimg6);


        assessquestiondata parammtfimgimg7 = new assessquestiondata();
        parammtfimgimg7.name = "choice3img";
        parammtfimgimg7.label = "Choice3";
        parammtfimgimg7.datatype = "image";
        parammtfimgimg7.role = "parameter";
        parammtfimgimg7.value = "";

        ByteArrayOutputStream baos17 = new ByteArrayOutputStream();
        Bitmap bitmap17 = BitmapFactory.decodeResource(getResources(), R.drawable.frenchfries);
        bitmap17.compress(Bitmap.CompressFormat.PNG, 100, baos17);
        byte[] imageBytes17 = baos17.toByteArray();
        String imageString17 = Base64.encodeToString(imageBytes17, Base64.DEFAULT);

        parammtfimgimg7.filecontent_base64 = imageString17;
        globalvault.questions[indexq].addQuestionData(parammtfimgimg7);


        assessquestiondata parammtfimgimg8 = new assessquestiondata();
        parammtfimgimg8.name = "choice4img";
        parammtfimgimg8.label = "Choice4";
        parammtfimgimg8.datatype = "image";
        parammtfimgimg8.role = "parameter";
        parammtfimgimg8.value = "";

        ByteArrayOutputStream baos18 = new ByteArrayOutputStream();
        Bitmap bitmap18 = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        bitmap18.compress(Bitmap.CompressFormat.PNG, 100, baos18);
        byte[] imageBytes18 = baos18.toByteArray();
        String imageString18 = Base64.encodeToString(imageBytes18, Base64.DEFAULT);

        parammtfimgimg8.filecontent_base64 = imageString18;
        globalvault.questions[indexq].addQuestionData(parammtfimgimg8);


        indexq++;

        //*********   Sample Question for Question Type 'MTF_IMG_TXT' ****************

        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("Match the items");
        globalvault.questions[indexq].setAnswerCorrect("1,2,3,4");

        assessquestiondata parammtfimgtxt1 = new assessquestiondata();
        parammtfimgtxt1.name = "option1img";
        parammtfimgtxt1.label = "Option1";
        parammtfimgtxt1.datatype = "image";
        parammtfimgtxt1.role = "parameter";
        parammtfimgtxt1.value = "";

        ByteArrayOutputStream baos19 = new ByteArrayOutputStream();
        Bitmap bitmap19 = BitmapFactory.decodeResource(getResources(), R.drawable.cake);
        bitmap19.compress(Bitmap.CompressFormat.PNG, 100, baos19);
        byte[] imageBytes19 = baos19.toByteArray();
        String imageString19 = Base64.encodeToString(imageBytes19, Base64.DEFAULT);

        parammtfimgtxt1.filecontent_base64 = imageString19;
        globalvault.questions[indexq].addQuestionData(parammtfimgtxt1);

        assessquestiondata parammtfimgtxt2 = new assessquestiondata();
        parammtfimgtxt2.name = "option2img";
        parammtfimgtxt2.label = "Option2";
        parammtfimgtxt2.datatype = "image";
        parammtfimgtxt2.role = "parameter";
        parammtfimgtxt2.value = "";

        ByteArrayOutputStream baos20 = new ByteArrayOutputStream();
        Bitmap bitmap20 = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        bitmap20.compress(Bitmap.CompressFormat.PNG, 100, baos20);
        byte[] imageBytes20 = baos20.toByteArray();
        String imageString20 = Base64.encodeToString(imageBytes20, Base64.DEFAULT);

        parammtfimgtxt2.filecontent_base64 = imageString20;
        globalvault.questions[indexq].addQuestionData(parammtfimgtxt2);


        assessquestiondata parammtfimgtxt3 = new assessquestiondata();
        parammtfimgtxt3.name = "option3img";
        parammtfimgtxt3.label = "Option3";
        parammtfimgtxt3.datatype = "image";
        parammtfimgtxt3.role = "parameter";
        parammtfimgtxt3.value = "";

        ByteArrayOutputStream baos21 = new ByteArrayOutputStream();
        Bitmap bitmap21 = BitmapFactory.decodeResource(getResources(), R.drawable.soup);
        bitmap21.compress(Bitmap.CompressFormat.PNG, 100, baos21);
        byte[] imageBytes21 = baos21.toByteArray();
        String imageString21 = Base64.encodeToString(imageBytes21, Base64.DEFAULT);

        parammtfimgtxt3.filecontent_base64 = imageString21;
        globalvault.questions[indexq].addQuestionData(parammtfimgtxt3);

        assessquestiondata parammtfimgtxt4 = new assessquestiondata();
        parammtfimgtxt4.name = "option4img";
        parammtfimgtxt4.label = "Option4";
        parammtfimgtxt4.datatype = "image";
        parammtfimgtxt4.role = "parameter";
        parammtfimgtxt4.value = "";

        ByteArrayOutputStream baos22 = new ByteArrayOutputStream();
        Bitmap bitmap22 = BitmapFactory.decodeResource(getResources(), R.drawable.frenchfries);
        bitmap22.compress(Bitmap.CompressFormat.PNG, 100, baos22);
        byte[] imageBytes22 = baos22.toByteArray();
        String imageString22 = Base64.encodeToString(imageBytes22, Base64.DEFAULT);

        parammtfimgtxt4.filecontent_base64 = imageString22;
        globalvault.questions[indexq].addQuestionData(parammtfimgtxt4);


        assessquestiondata parammtfimgtxt5 = new assessquestiondata();
        parammtfimgtxt5.name = "choice1txt";
        parammtfimgtxt5.label = "Choice1";
        parammtfimgtxt5.datatype = "text";
        parammtfimgtxt5.role = "parameter";
        parammtfimgtxt5.value = "Apple";
        globalvault.questions[indexq].addQuestionData(parammtfimgtxt5);

        assessquestiondata parammtfimgtxt6 = new assessquestiondata();
        parammtfimgtxt6.name = "choice2txt";
        parammtfimgtxt6.label = "Choice2";
        parammtfimgtxt6.datatype = "text";
        parammtfimgtxt6.role = "parameter";
        parammtfimgtxt6.value = "Soup";
        globalvault.questions[indexq].addQuestionData(parammtfimgtxt6);

        assessquestiondata parammtfimgtxt7 = new assessquestiondata();
        parammtfimgtxt7.name = "choice3txt";
        parammtfimgtxt7.label = "Choice3";
        parammtfimgtxt7.datatype = "text";
        parammtfimgtxt7.role = "parameter";
        parammtfimgtxt7.value = "French Fries";
        globalvault.questions[indexq].addQuestionData(parammtfimgtxt7);

        assessquestiondata parammtfimgtxt8 = new assessquestiondata();
        parammtfimgtxt8.name = "choice4txt";
        parammtfimgtxt8.label = "Choice4";
        parammtfimgtxt8.datatype = "text";
        parammtfimgtxt8.role = "parameter";
        parammtfimgtxt8.value = "Cake";
        globalvault.questions[indexq].addQuestionData(parammtfimgtxt8);

        indexq++;


        //*********   Sample Question for Question Type 'MTF_TXT_IMG' ****************

        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("Match the items");
        globalvault.questions[indexq].setAnswerCorrect("1,2,3,4");

        assessquestiondata parammtftxtimg1 = new assessquestiondata();
        parammtftxtimg1.name = "option1txt";
        parammtftxtimg1.label = "Option1";
        parammtftxtimg1.datatype = "text";
        parammtftxtimg1.role = "parameter";
        parammtftxtimg1.value = "Apple";
        globalvault.questions[indexq].addQuestionData(parammtftxtimg1);

        assessquestiondata parammtftxtimg2 = new assessquestiondata();
        parammtftxtimg2.name = "option2txt";
        parammtftxtimg2.label = "Option2";
        parammtftxtimg2.datatype = "text";
        parammtftxtimg2.role = "parameter";
        parammtftxtimg2.value = "Soup";
        globalvault.questions[indexq].addQuestionData(parammtftxtimg2);

        assessquestiondata parammtftxtimg3 = new assessquestiondata();
        parammtftxtimg3.name = "option3txt";
        parammtftxtimg3.label = "Option3";
        parammtftxtimg3.datatype = "text";
        parammtftxtimg3.role = "parameter";
        parammtftxtimg3.value = "French Fries";
        globalvault.questions[indexq].addQuestionData(parammtftxtimg3);

        assessquestiondata parammtftxtimg4 = new assessquestiondata();
        parammtftxtimg4.name = "option4txt";
        parammtftxtimg4.label = "Option4";
        parammtftxtimg4.datatype = "text";
        parammtftxtimg4.role = "parameter";
        parammtftxtimg4.value = "Cake";
        globalvault.questions[indexq].addQuestionData(parammtftxtimg4);


        assessquestiondata parammtftxtimg5 = new assessquestiondata();
        parammtftxtimg5.name = "choice1img";
        parammtftxtimg5.label = "Choice1";
        parammtftxtimg5.datatype = "image";
        parammtftxtimg5.role = "parameter";
        parammtftxtimg5.value = "";

        ByteArrayOutputStream baos23 = new ByteArrayOutputStream();
        Bitmap bitmap23 = BitmapFactory.decodeResource(getResources(), R.drawable.cake);
        bitmap23.compress(Bitmap.CompressFormat.PNG, 100, baos23);
        byte[] imageBytes23 = baos23.toByteArray();
        String imageString23 = Base64.encodeToString(imageBytes23, Base64.DEFAULT);

        parammtftxtimg5.filecontent_base64 = imageString23;
        globalvault.questions[indexq].addQuestionData(parammtftxtimg5);

        assessquestiondata parammtftxtimg6 = new assessquestiondata();
        parammtftxtimg6.name = "choice2img";
        parammtftxtimg6.label = "Choice2";
        parammtftxtimg6.datatype = "image";
        parammtftxtimg6.role = "parameter";
        parammtftxtimg6.value = "";

        ByteArrayOutputStream baos24 = new ByteArrayOutputStream();
        Bitmap bitmap24 = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        bitmap24.compress(Bitmap.CompressFormat.PNG, 100, baos24);
        byte[] imageBytes24 = baos24.toByteArray();
        String imageString24 = Base64.encodeToString(imageBytes24, Base64.DEFAULT);

        parammtftxtimg6.filecontent_base64 = imageString24;
        globalvault.questions[indexq].addQuestionData(parammtftxtimg6);


        assessquestiondata parammtftxtimg7 = new assessquestiondata();
        parammtftxtimg7.name = "choice3img";
        parammtftxtimg7.label = "Choice3";
        parammtftxtimg7.datatype = "image";
        parammtftxtimg7.role = "parameter";
        parammtftxtimg7.value = "";

        ByteArrayOutputStream baos25 = new ByteArrayOutputStream();
        Bitmap bitmap25 = BitmapFactory.decodeResource(getResources(), R.drawable.soup);
        bitmap25.compress(Bitmap.CompressFormat.PNG, 100, baos25);
        byte[] imageBytes25 = baos25.toByteArray();
        String imageString25 = Base64.encodeToString(imageBytes25, Base64.DEFAULT);

        parammtftxtimg7.filecontent_base64 = imageString25;
        globalvault.questions[indexq].addQuestionData(parammtftxtimg7);


        assessquestiondata parammtftxtimg8 = new assessquestiondata();
        parammtftxtimg8.name = "choice4img";
        parammtftxtimg8.label = "Choice4";
        parammtftxtimg8.datatype = "image";
        parammtftxtimg8.role = "parameter";
        parammtftxtimg8.value = "";

        ByteArrayOutputStream baos26 = new ByteArrayOutputStream();
        Bitmap bitmap26 = BitmapFactory.decodeResource(getResources(), R.drawable.frenchfries);
        bitmap26.compress(Bitmap.CompressFormat.PNG, 100, baos26);
        byte[] imageBytes26 = baos26.toByteArray();
        String imageString26 = Base64.encodeToString(imageBytes26, Base64.DEFAULT);

        parammtftxtimg8.filecontent_base64 = imageString26;
        globalvault.questions[indexq].addQuestionData(parammtftxtimg8);


        indexq++;


        //*********   Sample Question for Question Type 'MTF_TXT_TXT' ****************

        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("Match the items");
        globalvault.questions[indexq].setAnswerCorrect("1,2,3,4");

        assessquestiondata parammtftxttxt1 = new assessquestiondata();
        parammtftxttxt1.name = "option1txt";
        parammtftxttxt1.label = "Option1";
        parammtftxttxt1.datatype = "text";
        parammtftxttxt1.role = "parameter";
        parammtftxttxt1.value = "Apple";
        globalvault.questions[indexq].addQuestionData(parammtftxttxt1);

        assessquestiondata parammtftxttxt2 = new assessquestiondata();
        parammtftxttxt2.name = "option2txt";
        parammtftxttxt2.label = "Option2";
        parammtftxttxt2.datatype = "text";
        parammtftxttxt2.role = "parameter";
        parammtftxttxt2.value = "Dog";
        globalvault.questions[indexq].addQuestionData(parammtftxttxt2);

        assessquestiondata parammtftxttxt3 = new assessquestiondata();
        parammtftxttxt3.name = "option3txt";
        parammtftxttxt3.label = "Option3";
        parammtftxttxt3.datatype = "text";
        parammtftxttxt3.role = "parameter";
        parammtftxttxt3.value = "Carrot";
        globalvault.questions[indexq].addQuestionData(parammtftxttxt3);

        assessquestiondata parammtftxttxt4 = new assessquestiondata();
        parammtftxttxt4.name = "option4txt";
        parammtftxttxt4.label = "Option4";
        parammtftxttxt4.datatype = "text";
        parammtftxttxt4.role = "parameter";
        parammtftxttxt4.value = "Crow";
        globalvault.questions[indexq].addQuestionData(parammtftxttxt4);

        assessquestiondata parammtftxttxt5 = new assessquestiondata();
        parammtftxttxt5.name = "choice1txt";
        parammtftxttxt5.label = "Choice1";
        parammtftxttxt5.datatype = "text";
        parammtftxttxt5.role = "parameter";
        parammtftxttxt5.value = "Bird";
        globalvault.questions[indexq].addQuestionData(parammtftxttxt5);

        assessquestiondata parammtftxttxt6 = new assessquestiondata();
        parammtftxttxt6.name = "choice2txt";
        parammtftxttxt6.label = "Choice2";
        parammtftxttxt6.datatype = "text";
        parammtftxttxt6.role = "parameter";
        parammtftxttxt6.value = "Vegetable";
        globalvault.questions[indexq].addQuestionData(parammtftxttxt6);

        assessquestiondata parammtftxttxt7 = new assessquestiondata();
        parammtftxttxt7.name = "choice3txt";
        parammtftxttxt7.label = "Choice3";
        parammtftxttxt7.datatype = "text";
        parammtftxttxt7.role = "parameter";
        parammtftxttxt7.value = "Fruit";
        globalvault.questions[indexq].addQuestionData(parammtftxttxt7);

        assessquestiondata parammtftxttxt8 = new assessquestiondata();
        parammtftxttxt8.name = "choice4txt";
        parammtftxttxt8.label = "Choice4";
        parammtftxttxt8.datatype = "text";
        parammtftxttxt8.role = "parameter";
        parammtftxttxt8.value = "Animal";
        globalvault.questions[indexq].addQuestionData(parammtftxttxt8);

        indexq++;

        //*********   Sample Question for Question Type 'RAR_IMG' ****************

        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("Arrange in alphabetical order");
        globalvault.questions[indexq].setAnswerCorrect("1,2,3,4");

        assessquestiondata paramrarimg1 = new assessquestiondata();
        paramrarimg1.name = "option1img";
        paramrarimg1.label = "Option1";
        paramrarimg1.datatype = "image";
        paramrarimg1.role = "parameter";
        paramrarimg1.value = "";

        ByteArrayOutputStream baos27= new ByteArrayOutputStream();
        Bitmap bitmap27 = BitmapFactory.decodeResource(getResources(), R.drawable.cake);
        bitmap27.compress(Bitmap.CompressFormat.PNG, 100, baos27);
        byte[] imageBytes27 = baos27.toByteArray();
        String imageString27 = Base64.encodeToString(imageBytes27, Base64.DEFAULT);

        paramrarimg1.filecontent_base64 = imageString27;
        globalvault.questions[indexq].addQuestionData(paramrarimg1);

        assessquestiondata paramrarimg2 = new assessquestiondata();
        paramrarimg2.name = "option2img";
        paramrarimg2.label = "Option2";
        paramrarimg2.datatype = "image";
        paramrarimg2.role = "parameter";
        paramrarimg2.value = "";

        ByteArrayOutputStream baos28= new ByteArrayOutputStream();
        Bitmap bitmap28 = BitmapFactory.decodeResource(getResources(), R.drawable.soup);
        bitmap28.compress(Bitmap.CompressFormat.PNG, 100, baos28);
        byte[] imageBytes28 = baos28.toByteArray();
        String imageString28 = Base64.encodeToString(imageBytes28, Base64.DEFAULT);

        paramrarimg2.filecontent_base64 = imageString28;
        globalvault.questions[indexq].addQuestionData(paramrarimg2);

        assessquestiondata paramrarimg3 = new assessquestiondata();
        paramrarimg3.name = "option3img";
        paramrarimg3.label = "Option3";
        paramrarimg3.datatype = "image";
        paramrarimg3.role = "parameter";
        paramrarimg3.value = "";

        ByteArrayOutputStream baos29 = new ByteArrayOutputStream();
        Bitmap bitmap29 = BitmapFactory.decodeResource(getResources(), R.drawable.frenchfries);
        bitmap29.compress(Bitmap.CompressFormat.PNG, 100, baos29);
        byte[] imageBytes29 = baos29.toByteArray();
        String imageString29 = Base64.encodeToString(imageBytes29, Base64.DEFAULT);

        paramrarimg3.filecontent_base64 = imageString29;
        globalvault.questions[indexq].addQuestionData(paramrarimg3);


        assessquestiondata paramrarimg4 = new assessquestiondata();
        paramrarimg4.name = "option4img";
        paramrarimg4.label = "Option4";
        paramrarimg4.datatype = "image";
        paramrarimg4.role = "parameter";
        paramrarimg4.value = "";

        ByteArrayOutputStream baos30 = new ByteArrayOutputStream();
        Bitmap bitmap30 = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        bitmap30.compress(Bitmap.CompressFormat.PNG, 100, baos30);
        byte[] imageBytes30 = baos30.toByteArray();
        String imageString30 = Base64.encodeToString(imageBytes30, Base64.DEFAULT);

        paramrarimg4.filecontent_base64 = imageString30;
        globalvault.questions[indexq].addQuestionData(paramrarimg4);


        indexq++;

        //*********   Sample Question for Question Type 'RAR_TXT' ****************

        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("Arrange in ascending order");
        globalvault.questions[indexq].setAnswerCorrect("1,2,3,4");

        assessquestiondata paramrartxt1 = new assessquestiondata();
        paramrartxt1.name = "option1txt";
        paramrartxt1.label = "Option1";
        paramrartxt1.datatype = "text";
        paramrartxt1.role = "parameter";
        paramrartxt1.value = "12";
        globalvault.questions[indexq].addQuestionData(paramrartxt1);

        assessquestiondata paramrartxt2 = new assessquestiondata();
        paramrartxt2.name = "option2txt";
        paramrartxt2.label = "Option2";
        paramrartxt2.datatype = "text";
        paramrartxt2.role = "parameter";
        paramrartxt2.value = "17";
        globalvault.questions[indexq].addQuestionData(paramrartxt2);

        assessquestiondata paramrartxt3 = new assessquestiondata();
        paramrartxt3.name = "option3txt";
        paramrartxt3.label = "Option3";
        paramrartxt3.datatype = "text";
        paramrartxt3.role = "parameter";
        paramrartxt3.value = "9";
        globalvault.questions[indexq].addQuestionData(paramrartxt3);

        assessquestiondata paramrartxt4 = new assessquestiondata();
        paramrartxt4.name = "option4txt";
        paramrartxt4.label = "Option4";
        paramrartxt4.datatype = "text";
        paramrartxt4.role = "parameter";
        paramrartxt4.value = "6";
        globalvault.questions[indexq].addQuestionData(paramrartxt4);

        indexq++;


        //*********   Sample Question for Question Type 'TOF_TXT' ****************

        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("Tiger eats grass");
        globalvault.questions[indexq].setAnswerCorrect("1");

        assessquestiondata paramtoftxt1 = new assessquestiondata();
        paramtoftxt1.name = "option1txt";
        paramtoftxt1.label = "Option1";
        paramtoftxt1.datatype = "text";
        paramtoftxt1.role = "parameter";
        paramtoftxt1.value = "True";
        globalvault.questions[indexq].addQuestionData(paramtoftxt1);

        assessquestiondata paramtoftxt2 = new assessquestiondata();
        paramtoftxt2.name = "option2txt";
        paramtoftxt2.label = "Option2";
        paramtoftxt2.datatype = "text";
        paramtoftxt2.role = "parameter";
        paramtoftxt2.value = "False";
        globalvault.questions[indexq].addQuestionData(paramtoftxt2);


        indexq++;

        //*********   Sample Question for Question Type 'TOF_TXT_IMG' ****************

        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("There are 7 Tomatoes in the picture");
        globalvault.questions[indexq].setAnswerCorrect("1");

        assessquestiondata attribtoftxtimg = new assessquestiondata();
        attribtoftxtimg.name = "questionimg";
        attribtoftxtimg.label = "Question Image";
        attribtoftxtimg.datatype = "image";
        attribtoftxtimg.role = "attribute";
        attribtoftxtimg.value = "";

        ByteArrayOutputStream baos31 = new ByteArrayOutputStream();
        // Bitmap bitmap = BitmapFactory.decodeFile(pathOfYourImage);  // to convert from a file
        Bitmap bitmap31 = BitmapFactory.decodeResource(getResources(), R.drawable.tomatoes);
        bitmap31.compress(Bitmap.CompressFormat.PNG, 100, baos31);
        byte[] imageBytes31 = baos31.toByteArray();
        String imageString31 = Base64.encodeToString(imageBytes31, Base64.DEFAULT);

        attribtoftxtimg.filecontent_base64 = imageString31;
        globalvault.questions[indexq].addQuestionData(attribtoftxtimg);


        assessquestiondata paramtoftxtimg1 = new assessquestiondata();
        paramtoftxtimg1.name = "option1txt";
        paramtoftxtimg1.label = "Option1";
        paramtoftxtimg1.datatype = "text";
        paramtoftxtimg1.role = "parameter";
        paramtoftxtimg1.value = "True";
        globalvault.questions[indexq].addQuestionData(paramtoftxtimg1);

        assessquestiondata paramtoftxtimg2 = new assessquestiondata();
        paramtoftxtimg2.name = "option2txt";
        paramtoftxtimg2.label = "Option2";
        paramtoftxtimg2.datatype = "text";
        paramtoftxtimg2.role = "parameter";
        paramtoftxtimg2.value = "False";
        globalvault.questions[indexq].addQuestionData(paramtoftxtimg2);

        indexq++;


        //*********   Sample Question for Question Type 'WORD_TXT_IMG' ****************

        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("There are _________ number of Tomatoes in the picture below");
        globalvault.questions[indexq].setAnswerCorrect("1");

        assessquestiondata attribwordtxtimg = new assessquestiondata();
        attribwordtxtimg.name = "questionimg";
        attribwordtxtimg.label = "Question Image";
        attribwordtxtimg.datatype = "image";
        attribwordtxtimg.role = "attribute";
        attribwordtxtimg.value = "";

        ByteArrayOutputStream baos32 = new ByteArrayOutputStream();
        // Bitmap bitmap = BitmapFactory.decodeFile(pathOfYourImage);  // to convert from a file
        Bitmap bitmap32 = BitmapFactory.decodeResource(getResources(), R.drawable.manytomatoes);
        bitmap32.compress(Bitmap.CompressFormat.PNG, 100, baos32);
        byte[] imageBytes32 = baos32.toByteArray();
        String imageString32 = Base64.encodeToString(imageBytes32, Base64.DEFAULT);

        attribwordtxtimg.filecontent_base64 = imageString32;
        globalvault.questions[indexq].addQuestionData(attribwordtxtimg);

        indexq++;

        //*********   Sample Question for Question Type 'WORD_TXT' ****************

        globalvault.questions[indexq].setQuestionTemplType(globalvault.questionTemplTypes[indexq]);
        globalvault.questions[indexq].setQuestionText("Cat has _______ number of legs");
        globalvault.questions[indexq].setAnswerCorrect("1");


        indexq++;

    }
}
