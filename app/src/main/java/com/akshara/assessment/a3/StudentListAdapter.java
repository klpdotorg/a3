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

import com.akshara.assessment.a3.UtilsPackage.DailogUtill;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;

import java.util.ArrayList;

class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentViewHolder>{

    StudentListMainActivity activity;
    ArrayList<StudentPojo> students;
    String A3APP_GRADESTRING;
    long A3APP_INSTITUTIONID;
    int A3APP_GRADEID;
    String A3APP_LANGUAGE;
    int EASYASSESS_QUESTIONSETID;

    public StudentListAdapter(StudentListMainActivity activity, ArrayList<StudentPojo> students) {

        this.activity=activity;
        this.students=students;
        A3APP_GRADESTRING = activity.getIntent().getStringExtra("A3APP_GRADESTRING");
        A3APP_INSTITUTIONID = activity.getIntent().getLongExtra("A3APP_INSTITUTIONID",0L);
        A3APP_GRADEID = activity.getIntent().getIntExtra("A3APP_GRADEID",0);
        EASYASSESS_QUESTIONSETID = activity.getIntent().getIntExtra("EASYASSESS_QUESTIONSETID",0);
        A3APP_LANGUAGE =  activity.getIntent().getStringExtra("A3APP_LANGUAGE");

        Log.d("shri","-------A3 SEND DATA------");
        Log.d("Shri","GradeString:"+A3APP_GRADESTRING);
        Log.d("Shri","Institution Id:"+A3APP_INSTITUTIONID);
        Log.d("Shri","Grade Id:"+A3APP_GRADEID);
        Log.d("Shri","Q set Id:"+EASYASSESS_QUESTIONSETID);
        Log.d("Shri","App Language:"+A3APP_LANGUAGE);
        Log.d("shri","-------------");
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.student_text,parent,false);
       return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentViewHolder holder, final int position) {

       holder.txtStudent.setText(students.get(position).name+" "+students.get(position).lastName);
       // Log.d("shri",students.get(position).stsid+":"+students.get(position).name);
       holder.txtStudentID.setText("STS ID: "
               +(students.get(position).uid!=null &&
               !students.get(position).equals("")?students.get(position).uid:"NA"));

       if(students.get(position).gender.equalsIgnoreCase("male"))
       {
           holder.image_gender.setImageResource(R.drawable.boy);
       }else {
           holder.image_gender.setImageResource(R.drawable.girl);
       }



       holder.imageReportbtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
           //    Toast.makeText(activity,"Coming soon",Toast.LENGTH_SHORT).show();
               DailogUtill.showDialog("Coming soon",activity.getSupportFragmentManager(),activity);
           }
       });

        holder.imagePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, com.gka.akshara.assesseasy.assessment_manager.class);
                Bundle bundle = new Bundle();
                bundle.putString("EASYASSESS_FROMACTIVITY", "com.akshara.assessment.a3.AssessmentSelectorActivity");
                bundle.putInt("EASYASSESS_QUESTIONSETID", EASYASSESS_QUESTIONSETID);
                bundle.putBoolean("EASYASSESS_CLICKEDBACKARROW", false);


                bundle.putLong("A3APP_INSTITUTIONID", A3APP_INSTITUTIONID);
                bundle.putInt("A3APP_GRADEID", A3APP_GRADEID);
                bundle.putString("A3APP_GRADESTRING", A3APP_GRADESTRING);
                bundle.putString("A3APP_CHILDID", students.get(position).stsid+"");
                bundle.putString("A3APP_LANGUAGE", A3APP_LANGUAGE);

                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });


        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

           /*     Intent intent=new Intent(activity,AssessmentSelectorActivity.class);
                intent  .putExtra("A3APP_GRADESTRING",gradeString);
                intent  .putExtra("A3APP_INSTITUTIONID",institution);
                intent  .putExtra("A3APP_GRADEID",grade);
                intent.putExtra("A3APP_CHILDID",students.get(position).stsid);

                activity.startActivity(intent);*/

                Intent intent = new Intent(activity, com.gka.akshara.assesseasy.assessment_manager.class);
                Bundle bundle = new Bundle();
                bundle.putString("EASYASSESS_FROMACTIVITY", "com.akshara.assessment.a3.AssessmentSelectorActivity");
                bundle.putInt("EASYASSESS_QUESTIONSETID", EASYASSESS_QUESTIONSETID);
                bundle.putBoolean("EASYASSESS_CLICKEDBACKARROW", false);


                bundle.putLong("A3APP_INSTITUTIONID", A3APP_INSTITUTIONID);
                bundle.putInt("A3APP_GRADEID", A3APP_GRADEID);
                bundle.putString("A3APP_GRADESTRING", A3APP_GRADESTRING);
                 bundle.putString("A3APP_CHILDID", students.get(position).stsid+"");
                bundle.putString("A3APP_LANGUAGE", A3APP_LANGUAGE);

                intent.putExtras(bundle);
                activity.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder
    {
            TextView txtStudent,txtStudentID;
            ImageView image_gender,imageReportbtn,imagePlay;
            CardView card_view;
        public StudentViewHolder(View itemView) {
            super(itemView);
            txtStudent=itemView.findViewById(R.id.txtStudent);
            txtStudentID=itemView.findViewById(R.id.txtStudentID);
            image_gender=itemView.findViewById(R.id.image_gender);
            imageReportbtn=itemView.findViewById(R.id.imageReportbtn);
            imagePlay=itemView.findViewById(R.id.imagePlay);
            card_view=itemView.findViewById(R.id.card_view);
        }
    }
}
