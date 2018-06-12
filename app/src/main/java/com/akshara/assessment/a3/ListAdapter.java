package com.akshara.assessment.a3;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akshara.assessment.a3.Pojo.StudentDetailGrade;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    Activity activity;
    ArrayList<StudentDetailGrade> listData;
    public ListAdapter(Activity activity,ArrayList<StudentDetailGrade> listData)
    {
        this.listData=listData;
        this.activity=activity;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.students_badge_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

      holder.tv_grade.setText(listData.get(position).getGradeString());
      holder.tv_boys.setText(listData.get(position).getBoys()+"");
      holder.tv_girls.setText(listData.get(position).getGirls()+"");
      holder.tv_total.setText(listData.get(position).getTotal()+"");

      holder.lin.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              long  SchoolId= activity.getIntent().getLongExtra("A3APP_INSTITUTIONID",0l);
              Intent intent  = new Intent(activity,StudentListMainActivity.class) ;
              intent .putExtra("A3APP_INSTITUTIONID",SchoolId);
              intent  .putExtra("A3APP_GRADEID",listData.get(position).getGrade());
              intent  .putExtra("A3APP_GRADESTRING",listData.get(position).getGradeString());
              activity.startActivity(intent);
              activity. overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

          }
      });

    }

    @Override
    public int getItemCount() {
      return   listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout lin;
        TextView tv_grade,tv_boys,tv_girls,tv_total;

        public ViewHolder(View itemView) {
            super(itemView);
            lin=itemView.findViewById(R.id.lin);
            tv_grade=itemView.findViewById(R.id.tv_grade);
            tv_boys=itemView.findViewById(R.id.tv_boys);
            tv_girls=itemView.findViewById(R.id.tv_girls);
            tv_total=itemView.findViewById(R.id.tv_total);
        }
    }
}
