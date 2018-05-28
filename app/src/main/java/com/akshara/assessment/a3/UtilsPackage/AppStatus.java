package com.akshara.assessment.a3.UtilsPackage;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class AppStatus {



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


