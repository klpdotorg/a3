package com.akshara.assessment.a3.TelemetryReport;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akshara.assessment.a3.AsssessmentSelectorAdapter;
import com.akshara.assessment.a3.R;
import com.akshara.assessment.a3.UtilsPackage.AppStatus;
import com.akshara.assessment.a3.db.QuestionTable;
import com.akshara.assessment.a3.db.StudentTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class TelemetryReportAdapter extends RecyclerView.Adapter<TelemetryReportAdapter.TelemetryViewHolder> {
    TelemetryRreportActivity telemetryRreportActivity;
    ArrayList<pojoReportData> data;
    ArrayList<QuestionTable> questionTables;
    ArrayList<String> titles;

    public TelemetryReportAdapter(TelemetryRreportActivity telemetryRreportActivity, ArrayList<pojoReportData> data,
                                  ArrayList<QuestionTable> questionTables, ArrayList<String> titles) {
        this.telemetryRreportActivity = telemetryRreportActivity;
        this.data = data;
        this.titles = titles;
        this.questionTables = questionTables;

    }

    @Override
    public TelemetryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tel_adaptet_view, parent, false);

        return new TelemetryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TelemetryViewHolder holder, final int position) {
        final StudentTable studentTable = data.get(position).getTable();
        holder.tvStudentName.setText(studentTable.getFirstName());
        holder.stsid.setText(studentTable.getUid());
        Map<Long, ArrayList<CombinePojo>> listData = data.get(position).getDetailReportsMap();
        if (listData.get(studentTable.getId()) != null && listData.get(studentTable.getId()).size() > 0) {
            ArrayList<CombinePojo> combinePojos = listData.get(studentTable.getId());
            for(CombinePojo cd:listData.get(studentTable.getId()))
            {
                //Log.d("shri",cd.getPojoAssessment().getScore()+"!!!!");
            }
            if (combinePojos.size() > 1) {
                int size=combinePojos.size();
                holder.tvMarks.setText(combinePojos.get((size-1)).getPojoAssessment().getScore() + "/" + questionTables.size());

            } else {
                holder.tvMarks.setText(combinePojos.get(0).getPojoAssessment().getScore() + "/" + questionTables.size());
            }

        } else {
            holder.tvMarks.setText("-");
        }


        holder.linLayRepo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppStatus.data = data.get(position);
                AppStatus.questionTables = questionTables;
                AppStatus.titles = titles;
                Intent intent = new Intent(telemetryRreportActivity, TelemetryReportIndetail.class);
                intent.putExtra("name",studentTable.getFirstName());
                intent.putExtra("fatherName",studentTable.getMiddleName());
                intent.putExtra("stsId",studentTable.getUid());
                telemetryRreportActivity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class TelemetryViewHolder extends RecyclerView.ViewHolder {

        TextView tvStudentName, tvMarks, stsid;
        LinearLayout linLayRepo;

        public TelemetryViewHolder(View itemView) {
            super(itemView);
            tvMarks = itemView.findViewById(R.id.tvMarks);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            stsid = itemView.findViewById(R.id.stsid);
            linLayRepo = itemView.findViewById(R.id.linLayRepo);
        }
    }
}
