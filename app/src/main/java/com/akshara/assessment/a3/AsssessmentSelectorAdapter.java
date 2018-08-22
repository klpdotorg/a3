package com.akshara.assessment.a3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akshara.assessment.a3.TelemetryReport.TelemetryRreportActivity;
import com.akshara.assessment.a3.TelemetryReport.pojoReportData;
import com.akshara.assessment.a3.UtilsPackage.ConstantsA3;
import com.akshara.assessment.a3.UtilsPackage.DailogUtill;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.db.QuestionSetTable;

import java.util.ArrayList;

public class AsssessmentSelectorAdapter extends RecyclerView.Adapter {

    AssessmentSelectorActivity activity;
    ArrayList<QuestionSetTable> questionSetTables;
    long A3APP_INSTITUTIONID;
    int A3APP_GRADEID;
    String A3APP_GRADESTRING;
   // String A3APP_CHILDID;
    String A3APP_LANGUAGE;
    private final int EMPTY = 0;
    private final int DATA = 1;



    public AsssessmentSelectorAdapter(AssessmentSelectorActivity activity, ArrayList<QuestionSetTable> questionSetTables) {
        this.activity = activity;
        this.questionSetTables = questionSetTables;
        A3APP_GRADEID = activity.getIntent().getIntExtra("A3APP_GRADEID", 0);
        A3APP_INSTITUTIONID = activity.getIntent().getLongExtra("A3APP_INSTITUTIONID", 0l);
        A3APP_GRADESTRING = activity.getIntent().getStringExtra("A3APP_GRADESTRING");
         A3APP_LANGUAGE = this.activity.getLanguage();

        //  notifyDataSetChanged();
    }




    @Override
    public int getItemViewType(int position) {
        if (questionSetTables != null && questionSetTables.size() > 0) {

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

          switch (mainHolder.getItemViewType()) {
            case DATA:
                AssessmentViewHolder holder = ((AssessmentViewHolder) mainHolder);
                holder.tvAssessmentTitle.setText(questionSetTables.get(position).getQsetTitle());
                holder.tvLanguage.setText(questionSetTables.get(position).getLanguageName());
                holder.tvSubject.setText(questionSetTables.get(position).getSubjectName());
                holder.tvAssementtype.setText(questionSetTables.get(position).getAssesstypeName());

               holder.imageReport.setVisibility(View.GONE);

                holder.imageReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent=new Intent(activity,TelemetryRreportActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putLong("A3APP_INSTITUTIONID", A3APP_INSTITUTIONID);
                        bundle.putInt("EASYASSESS_QUESTIONSETID", questionSetTables.get(position).getIdQuestionset());
                        bundle.putInt("A3APP_GRADEID", A3APP_GRADEID);
                       // ConstantsA3.surveyTitle=questionSetTables.get(position).getQsetTitle();
                        ConstantsA3.subject=questionSetTables.get(position).getSubjectName();
                        ConstantsA3.assessmenttype=questionSetTables.get(position).getAssesstypeName();
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                        activity. overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                        // DailogUtill.showDialog("Coming soon",activity.getSupportFragmentManager(),activity);

                        // Toast.makeText(activity,"Coming soon",Toast.LENGTH_SHORT).show();

                    }
                });

                holder.imageNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(activity, StudentListMainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putLong("A3APP_INSTITUTIONID", A3APP_INSTITUTIONID);
                        bundle.putInt("A3APP_GRADEID", A3APP_GRADEID);
                        bundle.putInt("EASYASEESS_QUESTIONSETID", questionSetTables.get(position).getIdQuestionset());
                        bundle.putString("A3APP_GRADESTRING", A3APP_GRADESTRING);
                        //  bundle.putString("A3APP_CHILDID", A3APP_CHILDID);
                        bundle.putString("A3APP_LANGUAGE", A3APP_LANGUAGE);
                        String title=activity.getResources().getString(R.string.app_name)+">"+A3APP_GRADESTRING+">"+questionSetTables.get(position).getAssesstypeName()+">"+questionSetTables.get(position).getSubjectName();
                        bundle.putString(ConstantsA3.A3APP_TITLETEXT,title);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                        activity. overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                        activity.finish();


                    }
                });

                break;
            case EMPTY:
                AssessmentViewHolderEmpty emptyHolder = ((AssessmentViewHolderEmpty) mainHolder);
                emptyHolder.tv_message.setText(String.format(
                        activity.getString(R.string.noQuestionSet), A3APP_GRADESTRING

                ));

                emptyHolder.btnOKGo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, DownloadQuestionSetActivity.class);
                        intent.putExtra("A3APP_GRADEID",A3APP_GRADEID);
                        activity.startActivity(intent);
                    }
                });
                break;

        }


    }

    @Override
    public int getItemCount() {
       // Log.d("shri", questionSetTables.size() + "s");
        if (questionSetTables.size() <= 0) {
            return 1;
        }
        return questionSetTables.size();
    }

    public static class AssessmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAssessmentTitle, tvLanguage, tvSubject, tvAssementtype;
        LinearLayout adapterLay;
        ImageView imageNext,imageReport;
        public AssessmentViewHolder(View itemView) {
            super(itemView);
            tvAssessmentTitle = itemView.findViewById(R.id.tvAssessmentTitle);
            tvLanguage = itemView.findViewById(R.id.tvLanguage);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvAssementtype = itemView.findViewById(R.id.tvAssementtype);
            adapterLay = itemView.findViewById(R.id.adapterLay);
            imageNext = itemView.findViewById(R.id.imageNext);
            imageReport = itemView.findViewById(R.id.imageReport);

        }
    }

    public static class AssessmentViewHolderEmpty extends RecyclerView.ViewHolder {

        Button btnOKGo;
        TextView tv_message;

        public AssessmentViewHolderEmpty(View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.tv_message);
            btnOKGo = itemView.findViewById(R.id.btnOKGo);


        }
    }


}
