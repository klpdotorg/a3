package com.akshara.assessment.a3.UtilsPackage;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.akshara.assessment.a3.TelemetryReport.pojoReportData;
import com.akshara.assessment.a3.db.QuestionTable;

import java.util.ArrayList;


public class AppStatus {


   public static pojoReportData data;
   public static ArrayList<QuestionTable> questionTables;
    public static  ArrayList<String> titles;


        public static boolean isConnected(Context context)
        {

            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null)
            {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        {
                            return true;
                        }

            }
            return false;
        }










}


