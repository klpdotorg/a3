package com.akshara.assessment.a3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.akshara.assessment.a3.Pojo.StudentDetailGrade;
import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.StudentTable;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GradeActivity extends AppCompatActivity {

    long SchoolId;
    KontactDatabase database;
    ArrayList<StudentDetailGrade> listData;
    List<String> grades;
    String schoolName="";
    TextView tvSchoolName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp2);
        tvSchoolName= findViewById(R.id.tvSchoolName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Grade");
        RecyclerView list1 = findViewById(R.id.recyclerview);
        final LinearLayoutManager mLayoutManager;
        database = new KontactDatabase(this);
        listData=new ArrayList<>();
        SchoolId= getIntent().getLongExtra("A3APP_INSTITUTIONID",0l);
        schoolName= getIntent().getStringExtra("institutionName");
        //Toast.makeText(getApplicationContext(),SchoolId+"",Toast.LENGTH_SHORT).show();
        tvSchoolName.setText(schoolName);
        grades= Arrays.asList(getResources().getStringArray(R.array.array_grade));
        mLayoutManager = new LinearLayoutManager(this);
        list1.setLayoutManager(mLayoutManager);
        list1.setItemAnimator(new DefaultItemAnimator());
        ListAdapter adapter = new ListAdapter(this,listData);
        list1.setAdapter(adapter);


        for(int i=0;i<grades.size();i++)
        {
            int boys=0;
            int girls=0;
            int total=0;

                Query totalQuery = Query.select().from(StudentTable.TABLE)
                        .where(StudentTable.INSTITUTION.eq(SchoolId).and(StudentTable.STUDENT_GRADE.eq(i+1)));
                total = database.query(StudentTable.class, totalQuery).getCount();
                if (total > 0) {
                    Query boysQuery = Query.select().from(StudentTable.TABLE)
                            .where(StudentTable.INSTITUTION.eq(SchoolId)
                                    .and(StudentTable.GENDER.eqCaseInsensitive("male")
                                            .and(StudentTable.STUDENT_GRADE.eq(i+1))));
                    boys = database.query(StudentTable.class, boysQuery).getCount();
                    Query GirlsQuery = Query.select().from(StudentTable.TABLE)
                    .where(StudentTable.INSTITUTION.eq(SchoolId)
                            .and(StudentTable.GENDER.eqCaseInsensitive("female")
                                    .and(StudentTable.STUDENT_GRADE.eq(i+1))));
                    girls = database.query(StudentTable.class, GirlsQuery).getCount();
                }

            listData.add(new StudentDetailGrade(boys,girls,total,i+1,grades.get(i)));


        }
        ListAdapter adapter1=new ListAdapter(this,listData);
        list1.setAdapter( adapter1);
        adapter1.notifyDataSetChanged();







    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        finish();
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
}
