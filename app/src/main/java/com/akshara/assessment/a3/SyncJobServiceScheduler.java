package com.akshara.assessment.a3;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;


import com.akshara.assessment.a3.UtilsPackage.AppStatus;
import com.gka.akshara.assesseasy.deviceDatastoreMgr;
import com.gka.akshara.assesseasy.globalvault;


public class SyncJobServiceScheduler extends JobService {


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        //   Log.d("Shri", "onstartJob");
        ShdeuleJob(jobParameters);
        return true;
    }

    public void ShdeuleJob(JobParameters jobParameters) {


        try {


            startSyncData();

        } catch (IllegalStateException exception) {

        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        return false;
    }


    public void startSyncData() {

               /* if (AppStatus.isConnected(getApplicationContext())) {
                   deviceDatastoreMgr dsmgr = new deviceDatastoreMgr();
                    dsmgr.initializeDS(getApplicationContext());

                    if(dsmgr.checkSysncedData() ){
                        // Log.d("shri","=================="+b);
                        //  Toast.makeText(getApplicationContext(),"syching5",Toast.LENGTH_SHORT).show();
                        dsmgr.syncTelemetry(globalvault.a3_telemetryapi_baseurl);
                    }

                }*/
            }




}
