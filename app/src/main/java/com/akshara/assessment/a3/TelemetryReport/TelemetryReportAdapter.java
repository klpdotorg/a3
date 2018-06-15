package com.akshara.assessment.a3.TelemetryReport;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akshara.assessment.a3.AsssessmentSelectorAdapter;
import com.akshara.assessment.a3.R;
import com.akshara.assessment.a3.db.StudentTable;

import java.util.ArrayList;
import java.util.Map;

public class TelemetryReportAdapter extends RecyclerView.Adapter<TelemetryReportAdapter.TelemetryViewHolder> {
    TelemetryRreportActivity telemetryRreportActivity;
    ArrayList<pojoReportData> data;
    int size;

    public TelemetryReportAdapter(TelemetryRreportActivity telemetryRreportActivity, ArrayList<pojoReportData> data,int size) {
        this.telemetryRreportActivity = telemetryRreportActivity;
        this.data = data;
        this.size=size;
    }

    @Override
    public TelemetryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tel_adaptet_view, parent, false);

        return new TelemetryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TelemetryViewHolder holder, int position) {
        StudentTable studentTable=data.get(position).getTable();
        holder.tvStudentName.setText(studentTable.getFirstName());
        holder.stsid.setText(studentTable.getUid());
        Map<Long,ArrayList<CombinePojo>> listData=data.get(position).getDetailReportsMap();
        if(listData.get(studentTable.getId())!=null&&listData.get(studentTable.getId()).size()>0)
        {
            ArrayList<CombinePojo> combinePojos=listData.get(studentTable.getId());
            if(combinePojos.size()>1)
            {
                holder.tvMarks.setText(combinePojos.get(listData.size()).getPojoAssessment().getScore()+"/"+size);

            }else {
                holder.tvMarks.setText(combinePojos.get(0).getPojoAssessment().getScore()+"/"+size);
            }

        }
        else
        {
            holder.tvMarks.setText("-");
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class TelemetryViewHolder extends RecyclerView.ViewHolder {

        TextView tvStudentName, tvMarks,stsid;

        public TelemetryViewHolder(View itemView) {
            super(itemView);
            tvMarks = itemView.findViewById(R.id.tvMarks);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            stsid = itemView.findViewById(R.id.stsid);
        }
    }
}
