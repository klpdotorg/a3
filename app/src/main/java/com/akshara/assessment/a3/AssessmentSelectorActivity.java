package com.akshara.assessment.a3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.akshara.assessment.a3.TelemetryReport.TelemetryRreportActivity;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.db.KontactDatabase;
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
    static String  A3APP_LANGUAGE="";
   SessionManager sessionManager;
    AsssessmentSelectorAdapter asssessmentSelectorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_selector);
        recycler=findViewById(R.id.recycler);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.assessment_header));

        questionSetTables=new ArrayList<>();
        gradeName = getIntent().getStringExtra("A3APP_GRADESTRING");
      //  Toast.makeText(getApplicationContext(),gradeName,Toast.LENGTH_SHORT).show();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        //   student_list_recycler.addItemDecoration(new DividerItemDecoration(activity, 1));
        recycler.setItemAnimator(new DefaultItemAnimator());
        sessionManager=new SessionManager(getApplicationContext());
        A3APP_LANGUAGE=sessionManager.getLanguage();


        database = new KontactDatabase(this);

    //   asssessmentSelectorAdapter.notifyDataSetChanged();

    }


    @Override
    protected void onResume() {
        super.onResume();
        questionSetTables.clear();
        Query quesryQuestionSet = Query.select().from(QuestionSetTable.TABLE)
                .where(QuestionSetTable.GRADE_NAME.eqCaseInsensitive(gradeName).and(QuestionSetTable.PROGRAM_NAME.eqCaseInsensitive(sessionManager.getProgramFromSession())))
                .orderBy(QuestionSetTable.ID_QUESTIONSET.asc());
        questionsetCursor = database.query(QuestionSetTable.class, quesryQuestionSet);
        if (questionsetCursor != null) {
            while (questionsetCursor.moveToNext()) {

                QuestionSetTable questionSetTable=new QuestionSetTable(questionsetCursor);
                questionSetTables.add(questionSetTable);

            }
        }

        asssessmentSelectorAdapter=new AsssessmentSelectorAdapter(this,questionSetTables);
        recycler.setAdapter(asssessmentSelectorAdapter);



      //  startActivity(new Intent(getApplicationContext(), TelemetryRreportActivity.class));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateBack();
    }


    public void navigateBack()
    {
       //Intent intent=new Intent(getApplicationContext(),GradeActivity.class);
    Intent intent=new Intent(getApplicationContext(),NavigationDrawerActivity.class);
      //  intent.putExtra("A3APP_INSTITUTIONID",getIntent().getLongExtra("A3APP_INSTITUTIONID", 0l));
       startActivity(intent);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        finish();

    }

    public String getLanguage()
    {
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
