package com.akshara.assessment.a3.TelemetryReport;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.akshara.assessment.a3.BaseActivity;
import com.akshara.assessment.a3.R;
import com.akshara.assessment.a3.UtilsPackage.AppStatus;

public class TelemetryReportIndetail extends BaseActivity {

    RecyclerView recyReportIndi;
    TelematryIndetailAdapter adapter;
    TextView tvtotalScore,tv_stsId,tvFirstName,tv_fatherName;
    String name,fname,stsid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_inditail_lay);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.report));
        name=getIntent().getStringExtra("name");
        fname=getIntent().getStringExtra("fatherName");
        stsid=getIntent().getStringExtra("stsId");
        recyReportIndi= findViewById(R.id.recyReportIndi);
        tv_stsId= findViewById(R.id.tv_stsId);
        tvFirstName= findViewById(R.id.tvFirstName);
        tv_fatherName= findViewById(R.id.tv_fatherName);

        tv_stsId.setText(stsid!=null&&!stsid.equalsIgnoreCase("")?stsid:"NA");
        tv_fatherName.setText(fname);
        tvFirstName.setText(name);
        recyReportIndi.setLayoutManager(new LinearLayoutManager(this));
        tvtotalScore=findViewById(R.id.txtScore);
        recyReportIndi.setItemAnimator(new DefaultItemAnimator());
        adapter=new TelematryIndetailAdapter(
                TelemetryReportIndetail.this,
        AppStatus.data,AppStatus.titles,AppStatus.questionTables,getApplicationContext() );
        recyReportIndi.setAdapter(adapter);
        adapter.notifyDataSetChanged();




    }

    public void setScore(String score)
    {
        tvtotalScore.setText(score);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                navigateBack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateBack();
    }

    public void navigateBack()
    {

        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        finish();

    }
}
