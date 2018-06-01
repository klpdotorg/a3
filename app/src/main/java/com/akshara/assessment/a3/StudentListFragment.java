package com.akshara.assessment.a3;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.StudentTable;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;
import java.util.List;

public class StudentListFragment extends Fragment {


    RecyclerView student_list_recycler;
    StudentListAdapter studentListAdapter;
    StudentListMainActivity activity;
    EditText edtStudentSearch;
    ArrayList<StudentPojo> studentsTemp;
    StudentListAdapter adapter;
    ArrayList<StudentPojo> students;
    KontactDatabase database;
    SquidCursor<StudentTable> studentTableSquidCursor;
    Long institution;
    int grade;
   static String gradeString="";
    List<StudentTable> studentTableList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.studentslist, container, false);
        student_list_recycler = view.findViewById(R.id.student_list_recycler);
        activity = (StudentListMainActivity) getActivity();
        studentTableList = new ArrayList<>();
        edtStudentSearch = view.findViewById(R.id.edtStudentSearch);
        studentsTemp = new ArrayList<>();
        institution = activity.getIntent().getLongExtra("A3APP_INSTITUTIONID", 0);
        gradeString = activity.getIntent().getStringExtra("A3APP_GRADESTRING");
        grade = activity.getIntent().getIntExtra("A3APP_GRADEID", 0);
        if(institution==0||grade==0)
        {
            Toast.makeText(activity,"Institution:"+institution+"\nGrade:"+grade,Toast.LENGTH_SHORT).show();

            activity.finish();
        }
        students = new ArrayList<StudentPojo>();
        database = new KontactDatabase(activity);
        Query listStudent = Query.select().from(StudentTable.TABLE)
                .where(StudentTable.INSTITUTION.eq(institution).and(StudentTable.STUDENT_GRADE.eq(grade)))
                .orderBy(StudentTable.FIRST_NAME.asc());
        studentTableSquidCursor = database.query(StudentTable.class, listStudent);
        if (studentTableSquidCursor != null) {
            while (studentTableSquidCursor.moveToNext()) {
                StudentTable b = new StudentTable(studentTableSquidCursor);
                StudentPojo studentPojo = new StudentPojo(b.getFirstName(), b.getGender(), b.getLastName(), b.getId(),b.getUid());
                students.add(studentPojo);


            }
        }


        student_list_recycler.setLayoutManager(new LinearLayoutManager(activity));
        //   student_list_recycler.addItemDecoration(new DividerItemDecoration(activity, 1));
        student_list_recycler.setItemAnimator(new DefaultItemAnimator());

        adapter = new StudentListAdapter(activity, students);
        student_list_recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        edtStudentSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //  Toast.makeText(getActivity(),cs+"",Toast.LENGTH_SHORT).show();
                AlterAdapter();

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        return view;
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void AlterAdapter() {
        if (edtStudentSearch.getText().toString().isEmpty()) {
            studentsTemp.clear();
            adapter = new StudentListAdapter(activity, students);

            student_list_recycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            studentsTemp.clear();
            for (int i = 0; i < students.size(); i++) {
                if (students.get(i).name.toLowerCase().startsWith(edtStudentSearch.getText().toString().trim().toLowerCase())) {
                    studentsTemp.add(students.get(i));
                    adapter = new StudentListAdapter(activity, studentsTemp);
                    student_list_recycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }


            }
            if (studentsTemp.size() == 0) {
                adapter = new StudentListAdapter(activity, studentsTemp);
                student_list_recycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
