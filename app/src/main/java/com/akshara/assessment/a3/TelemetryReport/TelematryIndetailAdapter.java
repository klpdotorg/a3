package com.akshara.assessment.a3.TelemetryReport;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akshara.assessment.a3.A3Application;
import com.akshara.assessment.a3.R;
import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.QuestionTable;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;

public class TelematryIndetailAdapter extends RecyclerView.Adapter<TelematryIndetailAdapter.TelematryIndiViewHolder> {

    TelemetryReportIndetail telemetryReportIndetail;
    pojoReportData data;
    ArrayList<String> titles;
    ArrayList<QuestionTable> questionTables;
    KontactDatabase db;
    Context context;
    int totalanswerCount = 0;

    public TelematryIndetailAdapter(TelemetryReportIndetail telemetryReportIndetail,
                                    pojoReportData data, ArrayList<String> titles,
                                    ArrayList<QuestionTable> questionTables, Context context) {
        this.telemetryReportIndetail = telemetryReportIndetail;
        this.data = data;
        this.titles = titles;
        this.questionTables = questionTables;
        notifyDataSetChanged();
        this.context = context;
        db = ((A3Application) this.context.getApplicationContext()).getDb();
        int totalanswerCount = 0;

    }

    @Override
    public TelematryIndiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tel_rep_indi_adaptet_view, parent, false);

        return new TelematryIndiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TelematryIndiViewHolder holder, int position) {


        holder.text_Repo.setText(titles.get(position));
        holder.text_score.setText(getAnswer(titles.get(position)) + "/" + getCount(position));
      //  telemetryReportIndetail.setScore(telemetryReportIndetail.getResources().getString(R.string.totalScore) + totalanswerCount + "");

       telemetryReportIndetail.setScore(totalanswerCount+"/"+questionTables.size());
    }


    public int getCount(int position) {
        int number = 0;
        for (QuestionTable table : questionTables) {
            if (table.getConceptName().equalsIgnoreCase(titles.get(position))) {
                number = number + 1;
            }
        }
        return number;

    }


    public int getAnswer(String conceptName) {
        int answerCount = 0;
        if (data.getDetailReportsMap().get(data.getTable().getId()) != null) {

            int size = data.getDetailReportsMap().get(data.getTable().getId()).size();
            // int size2=data.getDetailReportsMap().get(data.getTable().getId()).get((size-1)).getPojoAssessmentDetail().size();
            //pojoAssessmentDetail det=data.getDetailReportsMap().get(data.getTable().getId()).get((size-1)).getPojoAssessmentDetail().get((size2-1));

            // Log.d("shri","////"+det.getId()+":"+det.getPass());


            //reportDataWithStudentInfo.get(0).getDetailReportsMap().get(18).get(3).getPojoAssessmentDetail().
            for (pojoAssessmentDetail detail : data.getDetailReportsMap().get(data.getTable().getId()).get((size - 1)).getPojoAssessmentDetail()) {

                if (detail != null) {
                    String concept = getConceptName(detail.getId_question());
                   Log.d("shri",concept+"------"+conceptName+":"+detail.getPass()+"qidAss"+detail.getId_assessment()+"-QID"+detail.getId_question()+"--id--"+detail.getId());
                    if (conceptName.equalsIgnoreCase(concept) && detail.getPass().equalsIgnoreCase("P")) {
                        answerCount = answerCount + 1;
                    totalanswerCount = totalanswerCount + 1;
                        Log.d("shri",totalanswerCount+"-----------------");
                    }
                }


            }
        }
        return answerCount;

    }


    public String getConceptName(String questionId) {
        Query question = Query.select().from(QuestionTable.TABLE)
                .where(QuestionTable.ID_QUESTION.eq(questionId));
        SquidCursor<QuestionTable> studentCursor = db.query(QuestionTable.class, question);
        while (studentCursor.moveToNext()) {
            String conceptName = new QuestionTable(studentCursor).getConceptName();
            if(studentCursor!=null)
            {
                studentCursor.close();
            }
            return conceptName;
        }
        return "";

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    class TelematryIndiViewHolder extends RecyclerView.ViewHolder {
        TextView text_Repo,text_score;

        public TelematryIndiViewHolder(View itemView) {

            super(itemView);
            text_Repo = (itemView).findViewById(R.id.text_Repo);
            text_score = (itemView).findViewById(R.id.text_score);
        }
    }


}
