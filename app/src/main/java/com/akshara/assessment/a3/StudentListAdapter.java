package com.akshara.assessment.a3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akshara.assessment.a3.TelemetryReport.TelemetryReportAdapter;
import com.akshara.assessment.a3.TelemetryReport.TelemetryReportIndetail;
import com.akshara.assessment.a3.TelemetryReport.pojoReportData;
import com.akshara.assessment.a3.UtilsPackage.AppStatus;
import com.akshara.assessment.a3.UtilsPackage.ConstantsA3;
import com.akshara.assessment.a3.UtilsPackage.DailogUtill;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.QuestionSetDetailTable;
import com.akshara.assessment.a3.db.QuestionTable;
import com.akshara.assessment.a3.db.StudentTable;
import com.crashlytics.android.Crashlytics;
import com.gka.akshara.assesseasy.deviceDatastoreMgr;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;
import java.util.Collections;

class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentViewHolder> {

    StudentListMainActivity activity;
    ArrayList<StudentPojo> students;
    String A3APP_GRADESTRING;
    long A3APP_INSTITUTIONID;
    int A3APP_GRADEID;
    String A3APP_LANGUAGE;
    int EASYASEESS_QUESTIONSETID;
    private deviceDatastoreMgr a3dsapiobj;
    KontactDatabase db;
    String A3APP_TITLETEXT = "";

    public StudentListAdapter(StudentListMainActivity activity, ArrayList<StudentPojo> students) {

        this.activity = activity;
        this.students = students;
        A3APP_GRADESTRING = activity.getIntent().getStringExtra("A3APP_GRADESTRING");
        A3APP_INSTITUTIONID = activity.getIntent().getLongExtra("A3APP_INSTITUTIONID", 0L);
        A3APP_GRADEID = activity.getIntent().getIntExtra("A3APP_GRADEID", 0);
        EASYASEESS_QUESTIONSETID = activity.getIntent().getIntExtra("EASYASEESS_QUESTIONSETID", 0);
        A3APP_LANGUAGE = activity.getIntent().getStringExtra("A3APP_LANGUAGE");
        a3dsapiobj = new deviceDatastoreMgr();
        try {
            if (activity.getIntent().getStringExtra(ConstantsA3.A3APP_TITLETEXT) != null) {
                A3APP_TITLETEXT = activity.getIntent().getStringExtra(ConstantsA3.A3APP_TITLETEXT);
                A3APP_TITLETEXT = A3APP_TITLETEXT.split(":")[0];


            } else if (activity.getIntent().getStringExtra("A3APP_TITLETEXT") != null) {
                A3APP_TITLETEXT = activity.getIntent().getStringExtra("A3APP_TITLETEXT");
                A3APP_TITLETEXT = A3APP_TITLETEXT.split(":")[0];
            } else {
                A3APP_TITLETEXT = activity.getResources().getString(R.string.app_name);
            }
        }catch (Exception e)
        {
            A3APP_TITLETEXT = activity.getResources().getString(R.string.app_name);
        }
        a3dsapiobj.initializeDS(activity);
        db = ((A3Application) activity.getApplicationContext()).getDb();

      /*  Log.d("shri", "-------A3 SEND DATA------");
        Log.d("Shri", "GradeString:" + A3APP_GRADESTRING);
        Log.d("Shri", "Institution Id:" + A3APP_INSTITUTIONID);
        Log.d("Shri", "Grade Id:" + A3APP_GRADEID);
        Log.d("Shri", "Q set Id:" + EASYASEESS_QUESTIONSETID);
        Log.d("Shri", "App Language:" + A3APP_LANGUAGE);
        Log.d("shri", "-------------");*/
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.student_text, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentViewHolder holder, final int position) {

        holder.txtStudent.setText(students.get(position).name + " " + students.get(position).lastName);
        // Log.d("shri",students.get(position).stsid+":"+students.get(position).name);
        holder.txtStudentID.setText("STS ID: "
                + (students.get(position).uid != null &&
                !students.get(position).uid.equals("") ? students.get(position).uid : "NA"));

        if (students.get(position).gender.equalsIgnoreCase("male")) {
            holder.image_gender.setImageResource(R.drawable.boy);
        } else {
            holder.image_gender.setImageResource(R.drawable.girl);
        }


        holder.imageReportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    Toast.makeText(activity,"Coming soon",Toast.LENGTH_SHORT).show();
                //  DailogUtill.showDialog("Coming soon",activity.getSupportFragmentManager(),activity);
                try {
                    long id = students.get(position).stsid;
                    ArrayList<StudentTable> studentIds = new ArrayList<>();
                    Query studentQuery = Query.select().from(StudentTable.TABLE)
                            .where(StudentTable.INSTITUTION.eq(A3APP_INSTITUTIONID).and(StudentTable.STUDENT_GRADE.eq(A3APP_GRADEID).and(StudentTable.ID.eq(id)))).orderBy(StudentTable.UID.asc());
                    SquidCursor<StudentTable> studentCursor = db.query(StudentTable.class, studentQuery);
                    if (studentCursor != null && studentCursor.getCount() > 0) {
                        while (studentCursor.moveToNext()) {
                            StudentTable studentTable = new StudentTable(studentCursor);
                            studentIds.add(studentTable);

                        }
                    }

                    checkData(studentIds, position);
                }catch (Exception e)
                {
                    Crashlytics.log("report crash student list adapter:"+e.getMessage());
                    DailogUtill.showDialog(activity.getResources().getString(R.string.oops),activity.getSupportFragmentManager(),activity);

                    //   DailogUtill.showDialog("Oops some thing went wrong",activity.getSupportFragmentManager(),activity);

                }
                //Toast.makeText(activity,studentIds.size()+"",Toast.LENGTH_SHORT).show();


            }
        });

        holder.imagePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!activity.checkAssessmentTaken(EASYASEESS_QUESTIONSETID+"",students.get(position).stsid )) {
                    Intent intent = new Intent(activity, com.gka.akshara.assesseasy.assessment_manager.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("EASYASSESS_FROMACTIVITY", "com.akshara.assessment.a3.AssessmentSelectorActivity");
                    bundle.putInt("EASYASSESS_QUESTIONSETID", EASYASEESS_QUESTIONSETID);
                    bundle.putBoolean("EASYASSESS_CLICKEDBACKARROW", false);


                    bundle.putLong("A3APP_INSTITUTIONID", A3APP_INSTITUTIONID);
                    bundle.putInt("A3APP_GRADEID", A3APP_GRADEID);
                    bundle.putString("A3APP_GRADESTRING", A3APP_GRADESTRING);
                    bundle.putString("A3APP_CHILDID", students.get(position).stsid + "");
                    //   Log.d("shri",students.get(position).stsid + "student id");
                    bundle.putString("A3APP_LANGUAGE", A3APP_LANGUAGE);
                    bundle.putString("A3APP_TITLETEXT", A3APP_TITLETEXT+": "+students.get(position).name);


                    //    Log.d("shri",A3APP_TITLETEXT);
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }else {
               //     Toast.makeText(activity,"Already assesment taken",Toast.LENGTH_SHORT).show();
                    DailogUtill.showDialog(activity.getResources().getString(R.string.assessmentAlreadyTaken),activity.getSupportFragmentManager(),activity);
                }

            }
        });


    /*    holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

           *//*     Intent intent=new Intent(activity,AssessmentSelectorActivity.class);
                intent  .putExtra("A3APP_GRADESTRING",gradeString);
                intent  .putExtra("A3APP_INSTITUTIONID",institution);
                intent  .putExtra("A3APP_GRADEID",grade);
                intent.putExtra("A3APP_CHILDID",students.get(position).stsid);

                activity.startActivity(intent);*//*

                Intent intent = new Intent(activity, com.gka.akshara.assesseasy.assessment_manager.class);
                Bundle bundle = new Bundle();
                bundle.putString("EASYASSESS_FROMACTIVITY", "com.akshara.assessment.a3.AssessmentSelectorActivity");
                bundle.putInt("EASYASSESS_QUESTIONSETID", EASYASEESS_QUESTIONSETID);
                bundle.putBoolean("EASYASSESS_CLICKEDBACKARROW", false);


                bundle.putLong("A3APP_INSTITUTIONID", A3APP_INSTITUTIONID);
                bundle.putInt("A3APP_GRADEID", A3APP_GRADEID);
                bundle.putString("A3APP_GRADESTRING", A3APP_GRADESTRING);
                bundle.putString("A3APP_CHILDID", students.get(position).stsid + "");
                bundle.putString("A3APP_LANGUAGE", A3APP_LANGUAGE);

                intent.putExtras(bundle);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);


            }
        });*/

    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView txtStudent, txtStudentID;
        ImageView image_gender, imageReportbtn, imagePlay;
        CardView card_view;

        public StudentViewHolder(View itemView) {
            super(itemView);
            txtStudent = itemView.findViewById(R.id.txtStudent);
            txtStudentID = itemView.findViewById(R.id.txtStudentID);
            image_gender = itemView.findViewById(R.id.image_gender);
            imageReportbtn = itemView.findViewById(R.id.imageReportbtn);
            imagePlay = itemView.findViewById(R.id.imagePlay);
            card_view = itemView.findViewById(R.id.card_view);
        }
    }

    public void checkData(ArrayList<StudentTable> stsId, int position) {


        ArrayList<pojoReportData> data = a3dsapiobj.getAllStudentsForReports(EASYASEESS_QUESTIONSETID + "", stsId);
        Collections.sort(data);

        ArrayList<String> titles = getAllQuestionSetTitle(EASYASEESS_QUESTIONSETID);

        AppStatus.data = data.get(0);
        AppStatus.questionTables = getAllQuestions(EASYASEESS_QUESTIONSETID);
        AppStatus.titles = titles;
        Intent intent = new Intent(activity, TelemetryReportIndetail.class);
        intent.putExtra("name", stsId.get(0).getFirstName());
        intent.putExtra("fatherName", stsId.get(0).getMiddleName());
        intent.putExtra("stsId", stsId.get(0).getUid());
        activity.startActivity(intent);
       /* adapter = new TelemetryReportAdapter(this, data, getAllQuestions(EASYASEESS_QUESTIONSETID),titles);
        reportRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();*/
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
