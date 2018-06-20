package com.akshara.assessment.a3.TelemetryReport;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.akshara.assessment.a3.BaseActivity;
import com.akshara.assessment.a3.R;
import com.akshara.assessment.a3.UtilsPackage.AppStatus;

public class TelemetryReportIndetail extends BaseActivity {

    RecyclerView recyReportIndi;
    TelematryIndetailAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_inditail_lay);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyReportIndi= findViewById(R.id.recyReportIndi);

        recyReportIndi.setLayoutManager(new LinearLayoutManager(this));

        recyReportIndi.setItemAnimator(new DefaultItemAnimator());
        adapter=new TelematryIndetailAdapter(
                TelemetryReportIndetail.this,
                AppStatus.data,AppStatus.titles,AppStatus.questionTables,getApplicationContext() );
        recyReportIndi.setAdapter(adapter);
        adapter.notifyDataSetChanged();




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
