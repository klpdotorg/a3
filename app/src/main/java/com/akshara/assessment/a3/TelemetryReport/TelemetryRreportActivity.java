package com.akshara.assessment.a3.TelemetryReport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.akshara.assessment.a3.A3Application;
import com.akshara.assessment.a3.AsssessmentSelectorAdapter;
import com.akshara.assessment.a3.GradeActivity;
import com.akshara.assessment.a3.R;
import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.QuestionSetDetailTable;
import com.akshara.assessment.a3.db.QuestionSetTable;
import com.akshara.assessment.a3.db.QuestionTable;
import com.akshara.assessment.a3.db.StudentTable;
import com.gka.akshara.assesseasy.deviceDatastoreMgr;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TelemetryRreportActivity extends AppCompatActivity {

    KontactDatabase db;
    long A3APP_INSTITUTIONID;
    int EASYASSESS_QUESTIONSETID;
    int A3APP_GRADEID;
    private deviceDatastoreMgr a3dsapiobj;
    RecyclerView reportRecyclerView;
    TelemetryReportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telemetry_rreport);
        db = ((A3Application) getApplicationContext()).getDb();
        a3dsapiobj = new deviceDatastoreMgr();
        a3dsapiobj.initializeDS(this);
        reportRecyclerView = findViewById(R.id.reportRecyclerView);
        A3APP_INSTITUTIONID = getIntent().getLongExtra("A3APP_INSTITUTIONID", 0L);
        EASYASSESS_QUESTIONSETID = getIntent().getIntExtra("EASYASSESS_QUESTIONSETID", 0);
        A3APP_GRADEID = getIntent().getIntExtra("A3APP_GRADEID", 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        ArrayList<QuestionTable> QuestionTitles = getAllQuestionSetTitle(EASYASSESS_QUESTIONSETID);
        reportRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        reportRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ArrayList<StudentTable> studentIds = getStudentIds(A3APP_INSTITUTIONID, A3APP_GRADEID);
        ArrayList<pojoReportData> data = a3dsapiobj.getAllStudentsForReports(EASYASSESS_QUESTIONSETID + "", studentIds);
        Collections.sort(data);

        ArrayList<String> titles=getAllQuestionSetTitle(EASYASSESS_QUESTIONSETID);
        adapter = new TelemetryReportAdapter(this, data, getAllQuestions(EASYASSESS_QUESTIONSETID),titles);
        reportRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateBack();
    }

    public void navigateBack()
    {

        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        finish();

    }
    public ArrayList<StudentTable> getStudentIds(long institution, int gradeId) {
        ArrayList<StudentTable> studentIds = new ArrayList<>();
        Query studentQuery = Query.select().from(StudentTable.TABLE)
                .where(StudentTable.INSTITUTION.eq(institution).and(StudentTable.STUDENT_GRADE.eq(gradeId))).orderBy(StudentTable.UID.asc());
        SquidCursor<StudentTable> studentCursor = db.query(StudentTable.class, studentQuery);
        if (studentCursor != null && studentCursor.getCount() > 0) {
            while (studentCursor.moveToNext()) {
                StudentTable studentTable = new StudentTable(studentCursor);
                studentIds.add(studentTable);

            }
        }
        return studentIds;


    }


    public ArrayList<String> getAllQuestioId(int questionsetId) {
        ArrayList<String> listQId = new ArrayList<>();
        Query QuestionsetQuery = Query.select().from(QuestionSetDetailTable.TABLE)
                .where(QuestionSetDetailTable.ID_QUESTIONSET.eq(questionsetId));
        SquidCursor<QuestionSetDetailTable> questionsetDetailCursor = db.query(QuestionSetDetailTable.class, QuestionsetQuery);
        if (questionsetDetailCursor != null && questionsetDetailCursor.getCount() > 0) {
            while (questionsetDetailCursor.moveToNext()) {
                QuestionSetDetailTable questionSetDetailTable = new QuestionSetDetailTable(questionsetDetailCursor);
                listQId.add(questionSetDetailTable.getIdQuestion());


            }
        }
        return listQId;
    }

    public ArrayList<QuestionTable> getAllQuestions(int questionsetId) {


        ArrayList<QuestionTable> listAllQuestions = new ArrayList<>();
        Query QuestionsetQuery = Query.select().from(QuestionSetDetailTable.TABLE)
                .where(QuestionSetDetailTable.ID_QUESTIONSET.eq(questionsetId));
        SquidCursor<QuestionSetDetailTable> questionsetDetailCursor = db.query(QuestionSetDetailTable.class, QuestionsetQuery);
        if (questionsetDetailCursor != null && questionsetDetailCursor.getCount() > 0) {
            while (questionsetDetailCursor.moveToNext()) {
                QuestionSetDetailTable questionSetDetailTable = new QuestionSetDetailTable(questionsetDetailCursor);

                Query QuestionQuery = Query.select().from(QuestionTable.TABLE)
                        .where(QuestionTable.ID_QUESTION.eq(questionSetDetailTable.getIdQuestion()));
                SquidCursor<QuestionTable> questionCursoe = db.query(QuestionTable.class, QuestionQuery);

                while (questionCursoe.moveToNext()) {
                    QuestionTable questionTable = new QuestionTable(questionCursoe);

                    listAllQuestions.add(questionTable);

                }

            }
        }
        return listAllQuestions;
    }

    public ArrayList<String> getAllQuestionSetTitle(int questionsetId) {


        ArrayList<String> listQuestionTitle = new ArrayList<>();
        Query QuestionsetQuery = Query.select().from(QuestionSetDetailTable.TABLE)
                .where(QuestionSetDetailTable.ID_QUESTIONSET.eq(questionsetId));
        SquidCursor<QuestionSetDetailTable> questionsetDetailCursor = db.query(QuestionSetDetailTable.class, QuestionsetQuery);
        if (questionsetDetailCursor != null && questionsetDetailCursor.getCount() > 0) {
            while (questionsetDetailCursor.moveToNext()) {
                QuestionSetDetailTable questionSetDetailTable = new QuestionSetDetailTable(questionsetDetailCursor);

                Query QuestionQuery = Query.select().from(QuestionTable.TABLE)
                        .where(QuestionTable.ID_QUESTION.eq(questionSetDetailTable.getIdQuestion()));
                SquidCursor<QuestionTable> questionCursoe = db.query(QuestionTable.class, QuestionQuery);

                while (questionCursoe.moveToNext()) {
                    QuestionTable questionTable = new QuestionTable(questionCursoe);
                    if (!listQuestionTitle.contains(questionTable.getConceptName())) {
                        listQuestionTitle.add(questionTable.getConceptName());
                    }
                }

            }
        }
        return listQuestionTitle;
    }
}
