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


    }

    @Override
    public TelematryIndiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tel_rep_indi_adaptet_view, parent, false);

        return new TelematryIndiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TelematryIndiViewHolder holder, int position) {


        holder.text_Repo.setText(titles.get(position) + ":" +getAnswer(titles.get(position))+"/"+ getCount(position));

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
         if(data.getDetailReportsMap().get(data.getTable().getId())!=null){
        for (pojoAssessmentDetail detail : data.getDetailReportsMap().get(data.getTable().getId()).get(data.getDetailReportsMap().size()-1).getPojoAssessmentDetail()) {
            Log.d("shri",conceptName+"---------00--------");

            if(detail!=null) {
                String concept = getConceptName(detail.getId_question());
                Log.d("shri",concept+"-----------------");
                if (conceptName.equalsIgnoreCase(concept)&&detail.getPass().equalsIgnoreCase("P")) {
                    answerCount = answerCount + 1;
                    Log.d("shri",answerCount+"-----------------");
                }
            }


        }}
        return answerCount;

    }


    public String getConceptName(String questionId) {
        Query question = Query.select().from(QuestionTable.TABLE)
                .where(QuestionTable.ID_QUESTION.eq(questionId));
        SquidCursor<QuestionTable> studentCursor = db.query(QuestionTable.class, question);
        while (studentCursor.moveToNext()) {
            String conceptName = new QuestionTable(studentCursor).getConceptName();
            return conceptName;
        }
        return "";

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    class TelematryIndiViewHolder extends RecyclerView.ViewHolder {
        TextView text_Repo;

        public TelematryIndiViewHolder(View itemView) {

            super(itemView);
            text_Repo = (itemView).findViewById(R.id.text_Repo);
        }
    }


}
