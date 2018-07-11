package com.akshara.assessment.a3;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.akshara.assessment.a3.AssessmentPojoPack.AssessmentTempPojo;
import com.akshara.assessment.a3.NetworkRetrofitPackage.A3NetWorkCalls;
import com.akshara.assessment.a3.NetworkRetrofitPackage.A3Services;
import com.akshara.assessment.a3.NetworkRetrofitPackage.CurrentStateInterface;
import com.akshara.assessment.a3.Pojo.QuestionSetPojo;
import com.akshara.assessment.a3.Pojo.StatePojo;
import com.akshara.assessment.a3.Pojo.SubjectTempPojo;
import com.akshara.assessment.a3.UtilsPackage.RolesUtils;
import com.akshara.assessment.a3.UtilsPackage.StringWithTags;
import com.akshara.assessment.a3.db.KontactDatabase;

import java.util.ArrayList;

public class DownloadQuestionSetActivity extends BaseActivity {

    Button btnDownloadQuestion;
    Spinner spn_subject, spn_selectGrade, spn_selectAssessment;
    ProgressDialog progressDialog;
    com.akshara.assessment.a3.UtilsPackage.SessionManager smanger;
    private KontactDatabase db;
    ArrayList<SubjectTempPojo> subjectList;
    int A3APP_GRADEID=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_question_set);
        db = ((A3Application) getApplicationContext().getApplicationContext()).getDb();
        A3APP_GRADEID=getIntent().getIntExtra("A3APP_GRADEID",0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.downloadQuestion));
        smanger = new com.akshara.assessment.a3.UtilsPackage.SessionManager(getApplicationContext());
        btnDownloadQuestion = findViewById(R.id.btnDownloadQuestion);
        subjectList=RolesUtils.getSubjectForProgram(db,smanger.getProgramIdFromSession());
        ArrayList<AssessmentTempPojo> listAssessmentData = RolesUtils.getAssessmentType(smanger.getProgramFromSession(), smanger.getProgramIdFromSession(), db);
       // spn_language = findViewById(R.id.spn_language);
        spn_subject = findViewById(R.id.spn_subject);
        spn_selectGrade = findViewById(R.id.spn_selectGrade);
        spn_subject.setAdapter(new ArrayAdapter(DownloadQuestionSetActivity.this, R.layout.spinnertextview, subjectList));
        if(A3APP_GRADEID!=0) {
            spn_selectGrade.setSelection(A3APP_GRADEID-1);
        }
        spn_selectAssessment = findViewById(R.id.spn_selectAssessment);
        spn_selectAssessment.setAdapter(new ArrayAdapter(DownloadQuestionSetActivity.this, R.layout.spinnertextview, listAssessmentData));


        btnDownloadQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // String language = spn_language.getSelectedItem().toString().trim();
              String language = smanger.getLanguage();
             // Toast.makeText(getApplicationContext(),language,Toast.LENGTH_SHORT).show();
                String subject = spn_subject.getSelectedItem().toString().trim();
                String grade = spn_selectGrade.getSelectedItem().toString().trim();
                String assessment = spn_selectAssessment.getSelectedItem().toString().trim();
                QuestionSetPojo qestionPojo = new QuestionSetPojo(language, subject, grade, assessment, smanger.getProgramFromSession(), "A3APIAKSHARAAUTHKEY#2018");

                downloadQuestion(qestionPojo);
            }
        });
    }

    private void downloadQuestion(QuestionSetPojo qestionPojo) {

        initPorgresssDialogForSchool();
        String url = A3Services.QUESTIONSET_URL;

        // String url="http://www.kodvin.com/a3portal/getQuestionSets";
        new A3NetWorkCalls(DownloadQuestionSetActivity.this).downloadQuestionset(url, qestionPojo, new CurrentStateInterface() {
            @Override
            public void setSuccess(String message) {
                finishProgress();
                android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(DownloadQuestionSetActivity.this).create();

                alertDialog.setCancelable(false);
                alertDialog.setMessage(message);
                alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.response_neutral),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });


                alertDialog.show();
            }

            @Override
            public void setFailed(String message) {
                finishProgress();
                android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(DownloadQuestionSetActivity.this).create();

                alertDialog.setCancelable(false);
                alertDialog.setMessage(message);
                alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.response_neutral),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });


                alertDialog.show();
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void initPorgresssDialogForSchool() {
        progressDialog = new ProgressDialog(DownloadQuestionSetActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.questionsetDownloading));


        progressDialog.show();
        progressDialog.setCancelable(false);
    }


    private void finishProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }
}
