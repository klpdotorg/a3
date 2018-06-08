package com.akshara.assessment.a3;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akshara.assessment.a3.UtilsPackage.SessionManager;

import java.util.ArrayList;

class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentViewHolder>{

    StudentListMainActivity activity;
    ArrayList<StudentPojo> students;
    String gradeString;
    long institution;
    int grade;

    public StudentListAdapter(StudentListMainActivity activity, ArrayList<StudentPojo> students) {

        this.activity=activity;
        this.students=students;
        gradeString = activity.getIntent().getStringExtra("A3APP_GRADESTRING");
        institution = activity.getIntent().getLongExtra("A3APP_INSTITUTIONID",0L);
        grade = activity.getIntent().getIntExtra("A3APP_GRADEID",0);

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

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(activity,AssessmentSelectorActivity.class);
                intent  .putExtra("A3APP_GRADESTRING",gradeString);
                intent  .putExtra("A3APP_INSTITUTIONID",institution);
                intent  .putExtra("A3APP_GRADEID",grade);
                intent.putExtra("A3APP_CHILDID",students.get(position).stsid);

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
            ImageView image_gender;
            CardView card_view;
        public StudentViewHolder(View itemView) {
            super(itemView);
            txtStudent=itemView.findViewById(R.id.txtStudent);
            txtStudentID=itemView.findViewById(R.id.txtStudentID);
            image_gender=itemView.findViewById(R.id.image_gender);
            card_view=itemView.findViewById(R.id.card_view);
        }
    }
}
