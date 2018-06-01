package com.akshara.assessment.a3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.db.QuestionSetTable;

import java.util.ArrayList;

public class AsssessmentSelectorAdapter extends RecyclerView.Adapter{

    AssessmentSelectorActivity activity;
    ArrayList<QuestionSetTable> questionSetTables;
    long A3APP_INSTITUTIONID;
    int A3APP_GRADEID;
    String A3APP_GRADESTRING;
    String A3APP_CHILDID;
    String A3APP_LANGUAGE;
   private final  int EMPTY=0;
    private final  int DATA=1;


    public AsssessmentSelectorAdapter(AssessmentSelectorActivity activity, ArrayList<QuestionSetTable> questionSetTables) {
        this.activity = activity;
        this.questionSetTables = questionSetTables;
        A3APP_GRADEID = activity.getIntent().getIntExtra("A3APP_GRADEID", 0);
        A3APP_INSTITUTIONID = activity.getIntent().getLongExtra("A3APP_INSTITUTIONID", 0l);
        A3APP_CHILDID = activity.getIntent().getLongExtra("A3APP_CHILDID", 0l) + "";
        A3APP_GRADESTRING = activity.getIntent().getStringExtra("A3APP_GRADESTRING");

        A3APP_LANGUAGE=this.activity.getLanguage();
      //  notifyDataSetChanged();
    }




    @Override
    public int getItemViewType(int position) {
        if(questionSetTables!=null&&questionSetTables.size()>0)
        {

        return DATA;
        }

        return EMPTY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case DATA:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assessment_adapter_lay, parent, false);

                return new AssessmentViewHolder(view);

            case EMPTY:
                View viewd = LayoutInflater.from(parent.getContext()).inflate(R.layout.assessment_adapter_lay_empty, parent, false);

                return new AssessmentViewHolderEmpty(viewd);


        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder mainHolder, final int position) {


        switch (mainHolder.getItemViewType())
        {
            case DATA:
                AssessmentViewHolder holder=   ((AssessmentViewHolder) mainHolder);
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


                break;
            case EMPTY:
                AssessmentViewHolderEmpty emptyHolder=((AssessmentViewHolderEmpty) mainHolder);
                emptyHolder.tv_message.setText( String.format(
                        activity.getString(R.string.noQuestionSet),A3APP_GRADESTRING

                ));

                emptyHolder.btnOKGo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(activity,DownloadQuestionSetActivity.class);
                        activity.startActivity(intent);
                    }
                });
                break;

        }


    }

    @Override
    public int getItemCount() {
        Log.d("shri",questionSetTables.size()+"s");
        if(questionSetTables.size()<=0)
        {
            return 1;
        }
        return questionSetTables.size();
    }

    public static class AssessmentViewHolder extends RecyclerView.ViewHolder{
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

    public static class AssessmentViewHolderEmpty extends RecyclerView.ViewHolder{

        Button btnOKGo;
        TextView tv_message;
        public AssessmentViewHolderEmpty(View itemView) {
            super(itemView);
            tv_message= itemView.findViewById(R.id.tv_message);
            btnOKGo= itemView.findViewById(R.id.btnOKGo);


        }
    }


}
