package com.akshara.assessment.a3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.akshara.assessment.a3.TelemetryReport.TelemetryRreportActivity;
import com.akshara.assessment.a3.UtilsPackage.ConstantsA3;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.QuestionSetPojo;
import com.akshara.assessment.a3.db.QuestionSetTable;
import com.akshara.assessment.a3.db.StudentTable;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;

public class AssessmentSelectorActivity extends BaseActivity {


    KontactDatabase database;
    String gradeName;
    SquidCursor<QuestionSetTable> questionsetCursor;
    ArrayList<QuestionSetTable> questionSetTables;
    RecyclerView recycler;
    static String A3APP_LANGUAGE = "";
    SessionManager sessionManager;
    Button btnGoToReport;
    ArrayList<QuestionSetPojo> questionSetTablesForList;
    long A3APP_INSTITUTIONID;
    int A3APP_GRADEID;
    String A3APP_GRADESTRING;
    // String A3APP_CHILDID;

    AsssessmentSelectorAdapter asssessmentSelectorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_selector);
        recycler = findViewById(R.id.recycler);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.assessment_header));
        database = new KontactDatabase(this);

        questionSetTables = new ArrayList<>();
        questionSetTablesForList = new ArrayList<>();
        gradeName = getIntent().getStringExtra("A3APP_GRADESTRING");
        //  Toast.makeText(getApplicationContext(),gradeName,Toast.LENGTH_SHORT).show();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        //   student_list_recycler.addItemDecoration(new DividerItemDecoration(activity, 1));
        recycler.setItemAnimator(new DefaultItemAnimator());
        sessionManager = new SessionManager(getApplicationContext());
        A3APP_LANGUAGE = sessionManager.getLanguage();


        A3APP_GRADEID = getIntent().getIntExtra("A3APP_GRADEID", 0);
        A3APP_INSTITUTIONID = getIntent().getLongExtra("A3APP_INSTITUTIONID", 0l);
        A3APP_GRADESTRING = getIntent().getStringExtra("A3APP_GRADESTRING");
        A3APP_LANGUAGE = getLanguage();






        btnGoToReport = findViewById(R.id.btnGoToReport);


        btnGoToReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(AssessmentSelectorActivity.this);

                builderSingle.setTitle(getResources().getString(R.string.string_select_AsseesmentType));

                final ArrayAdapter<QuestionSetPojo> arrayAdapter = new ArrayAdapter<QuestionSetPojo>(AssessmentSelectorActivity.this, android.R.layout.select_dialog_item,questionSetTablesForList);
                builderSingle.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which).assesstype_name;


                        Intent intent=new Intent(getApplicationContext(),TelemetryRreportActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putLong("A3APP_INSTITUTIONID", A3APP_INSTITUTIONID);
                        bundle.putInt("EASYASSESS_QUESTIONSETID", arrayAdapter.getItem(which).getId_questionset());
                        bundle.putInt("A3APP_GRADEID", A3APP_GRADEID);
                        // ConstantsA3.surveyTitle=questionSetTables.get(position).getQsetTitle();
                        ConstantsA3.subject=arrayAdapter.getItem(which).getSubject_name();
                        ConstantsA3.assessmenttype=arrayAdapter.getItem(which).getAssesstype_name();
                        intent.putExtras(bundle);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);








                    }
                });
                builderSingle.show();

            }
        });

        //   asssessmentSelectorAdapter.notifyDataSetChanged();

    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            questionSetTables.clear();
            questionSetTablesForList.clear();
            Query quesryQuestionSet = Query.select().from(QuestionSetTable.TABLE)
                    .where(QuestionSetTable.GRADE_NAME.eqCaseInsensitive(gradeName).and(QuestionSetTable.LANGUAGE_NAME.eqCaseInsensitive(sessionManager.getLanguage()))
                            .and(QuestionSetTable.PROGRAM_NAME.eqCaseInsensitive(sessionManager.getProgramFromSession())))
                    .orderBy(QuestionSetTable.ASSESSTYPE_NAME.asc());
            questionsetCursor = database.query(QuestionSetTable.class, quesryQuestionSet);
            if (questionsetCursor != null) {
                while (questionsetCursor.moveToNext()) {
                    QuestionSetTable questionSetTable = new QuestionSetTable(questionsetCursor);
                    questionSetTables.add(questionSetTable);
                    //  Log.d("shri",questionSetTablesForList.toString());
                    if (!questionSetTablesForList.toString().contains(questionSetTable.getAssesstypeName())) {
                        QuestionSetPojo questionSetPojo = new QuestionSetPojo();

                        questionSetPojo.setId_questionset(questionSetTable.getIdQuestionset());
                        questionSetPojo.setAssesstype_name(questionSetTable.getAssesstypeName());
                        questionSetPojo.setGrade_name(questionSetTable.getGradeName());
                        questionSetPojo.setProgram_name(questionSetTable.getProgramName());
                        questionSetPojo.setLanguage_name(questionSetTable.getLanguageName());
                        questionSetPojo.setQset_title(questionSetTable.getQsetTitle());
                        questionSetPojo.setSubject_name(questionSetTable.getSubjectName());
                        questionSetPojo.setQset_name(questionSetTable.getQsetName());


                        questionSetTablesForList.add(questionSetPojo);
                    }


                }
                if(questionSetTablesForList.size()>0)
                {
                    btnGoToReport.setVisibility(View.VISIBLE);

                }else {
                    btnGoToReport.setVisibility(View.GONE);
                }
               // Log.d("shri", questionSetTablesForList.size() + "no");
               // Log.d("shri", questionSetTables.size() + "yes");
            }

            asssessmentSelectorAdapter = new AsssessmentSelectorAdapter(this, questionSetTables);
            recycler.setAdapter(asssessmentSelectorAdapter);

        } finally {
            if (questionsetCursor != null) {
                questionsetCursor.close();
            }
        }

        //  startActivity(new Intent(getApplicationContext(), TelemetryRreportActivity.class));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateBack();
    }


    public void navigateBack() {
        //Intent intent=new Intent(getApplicationContext(),GradeActivity.class);
        Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
        //  intent.putExtra("A3APP_INSTITUTIONID",getIntent().getLongExtra("A3APP_INSTITUTIONID", 0l));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        finish();

    }

    public String getLanguage() {
        return A3APP_LANGUAGE;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                navigateBack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
