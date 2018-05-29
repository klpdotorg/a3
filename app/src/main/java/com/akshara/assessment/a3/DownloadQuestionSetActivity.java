package com.akshara.assessment.a3;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.akshara.assessment.a3.NetworkRetrofitPackage.A3NetWorkCalls;
import com.akshara.assessment.a3.NetworkRetrofitPackage.CurrentStateInterface;
import com.akshara.assessment.a3.Pojo.QuestionSetPojo;
import com.akshara.assessment.a3.Pojo.StatePojo;
import com.akshara.assessment.a3.UtilsPackage.StringWithTags;

public class DownloadQuestionSetActivity extends AppCompatActivity {

    Button btnDownloadQuestion;
    Spinner spn_language, spn_subject, spn_selectGrade, spn_selectAssessment;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_question_set);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Download Question set");
        btnDownloadQuestion = (Button) findViewById(R.id.btnDownloadQuestion);
        spn_language = (Spinner) findViewById(R.id.spn_language);
        spn_subject = (Spinner) findViewById(R.id.spn_subject);
        spn_selectGrade = (Spinner) findViewById(R.id.spn_selectGrade);
        spn_selectAssessment = (Spinner) findViewById(R.id.spn_selectAssessment);


        btnDownloadQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String language = spn_language.getSelectedItem().toString().trim();
                String subject = spn_subject.getSelectedItem().toString().trim();
                String grade = spn_selectGrade.getSelectedItem().toString().trim();
                String assessment = spn_selectAssessment.getSelectedItem().toString().trim();
                QuestionSetPojo qestionPojo = new QuestionSetPojo(language, subject, grade, assessment, "A3APIAKSHARAAUTHKEY#2018");

                downloadQuestion(qestionPojo);
            }
        });
    }

    private void downloadQuestion(QuestionSetPojo qestionPojo) {

        initPorgresssDialogForSchool();
        String url = "http://dev.klp.org.in/a3/getQuestionSets";
        // String url="http://www.kodvin.com/a3portal/getQuestionSets";
        new A3NetWorkCalls(getApplicationContext()).downloadQuestionset(url, qestionPojo, new CurrentStateInterface() {
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
        progressDialog.setMessage("Question set downloading...");


        progressDialog.show();
        progressDialog.setCancelable(false);
    }


    private void finishProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }
}