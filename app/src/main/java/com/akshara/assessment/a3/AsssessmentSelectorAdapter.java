package com.akshara.assessment.a3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.db.QuestionSetTable;

import java.util.ArrayList;

public class AsssessmentSelectorAdapter extends RecyclerView.Adapter<AsssessmentSelectorAdapter.AssessmentViewHolder> {

    AssessmentSelectorActivity activity;
    ArrayList<QuestionSetTable> questionSetTables;
    long A3APP_INSTITUTIONID;
    int A3APP_GRADEID;
    String A3APP_GRADESTRING;
    String A3APP_CHILDID;
    String A3APP_LANGUAGE;


    public AsssessmentSelectorAdapter(AssessmentSelectorActivity activity, ArrayList<QuestionSetTable> questionSetTables) {
        this.activity = activity;
        this.questionSetTables = questionSetTables;
        A3APP_GRADEID = activity.getIntent().getIntExtra("A3APP_GRADEID", 0);
        A3APP_INSTITUTIONID = activity.getIntent().getLongExtra("A3APP_INSTITUTIONID", 0l);
        A3APP_CHILDID = activity.getIntent().getLongExtra("A3APP_CHILDID", 0l) + "";
        A3APP_GRADESTRING = activity.getIntent().getStringExtra("A3APP_GRADESTRING");

        A3APP_LANGUAGE=this.activity.getLanguage();
        notifyDataSetChanged();
    }

    @Override
    public AssessmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(activity).inflate(R.layout.assessment_adapter_lay, parent, false);
        return new AssessmentViewHolder(view);

    }


    @Override
    public void onBindViewHolder(AssessmentViewHolder holder, final int position) {


        holder.tvAssessmentTitle.setText(questionSetTables.get(position).getQsetTitle());
        holder.tvLanguage.setText(questionSetTables.get(position).getLanguageName());
        holder.tvSubject.setText(questionSetTables.get(position).getSubjectName());
        holder.tvAssementtype.setText(questionSetTables.get(position).getAssesstypeName());


        holder.adapterLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, com.gka.akshara.assesseasy.assessment_manager.class);
                Bundle bundle = new Bundle();
                bundle.putString("EASYASSESS_FROMACTIVITY", "com.akshara.assessment.a3.AssessmentSelectorActivity");
                bundle.putInt("EASYASSESS_QUESTIONSETID", questionSetTables.get(position).getIdQuestionset());
                bundle.putBoolean("EASYASSESS_CLICKEDBACKARROW", false);


                bundle.putLong("A3APP_INSTITUTIONID", A3APP_INSTITUTIONID);
                bundle.putInt("A3APP_GRADEID", A3APP_GRADEID);
                bundle.putString("A3APP_GRADESTRING", A3APP_GRADESTRING);
                bundle.putString("A3APP_CHILDID", A3APP_CHILDID);
                bundle.putString("A3APP_LANGUAGE", A3APP_LANGUAGE);

                intent.putExtras(bundle);
                activity.startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return questionSetTables.size();
    }

    class AssessmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAssessmentTitle, tvLanguage, tvSubject, tvAssementtype;
        LinearLayout adapterLay;

        public AssessmentViewHolder(View itemView) {
            super(itemView);
            tvAssessmentTitle = itemView.findViewById(R.id.tvAssessmentTitle);
            tvLanguage = itemView.findViewById(R.id.tvLanguage);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvAssementtype = itemView.findViewById(R.id.tvAssementtype);
            adapterLay = itemView.findViewById(R.id.adapterLay);

        }
    }


}
