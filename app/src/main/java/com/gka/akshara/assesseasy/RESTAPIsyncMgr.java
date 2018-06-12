package com.gka.akshara.assesseasy;

/**
 * Class to invoke REST APIs
 * This class implements 'runnable'. The 'run' methods fetch all the telemetry records and
 * This class is invoked by the 'invokeRESTAPI' method from the deviceDatastoreMgr class
 *
 * @Author: sureshkodoor@gmail.com
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RESTAPIsyncMgr implements Runnable {

    private String a3restapiurl;
    private String a3apiname;
    private JSONArray jsondata;
    private deviceDatastoreMgr a3dsapiobj;

    private boolean debugalerts = false;

    public RESTAPIsyncMgr(String apibaseurl, String apiname, JSONArray data, deviceDatastoreMgr dsapiobj) {

        a3apiname = apiname;
        String baseurl = apibaseurl;
        if((baseurl.charAt(baseurl.length() - 1)) != '/')
            baseurl = baseurl+"/";
        a3restapiurl = baseurl+apiname;

        jsondata = data;
        a3dsapiobj = dsapiobj;
    }

    @Override
    public void run() {

        HttpURLConnection apiConnection = null;

        try {
            URL apiurl = new URL(a3restapiurl);

            // Create connection
            apiConnection =  (HttpURLConnection) apiurl.openConnection();
            apiConnection.setRequestProperty("Accept","application/json");
            apiConnection.setRequestMethod("POST");

            // send Data
            apiConnection.setDoOutput(true);
            OutputStream outstream = apiConnection.getOutputStream();
            outstream.write(jsondata.toString().getBytes());
            if(debugalerts) {
                Log.d("EASYASSESS", "RESTAPIsyncMgr.run. send data: success. jsondata: " + jsondata.toString());
            }
            // read Response
            InputStream instream = apiConnection.getInputStream();
            InputStreamReader instreamreader = new InputStreamReader(instream,"UTF-8");

            BufferedReader inreader = new BufferedReader(instreamreader);
            StringBuilder sb = new StringBuilder();
            String line = "";
            while((line = inreader.readLine()) != null) {
                sb.append(line);
                break;
            }

            // Close the connection
            inreader.close();
            apiConnection.disconnect();

            JSONObject jsonResp = new JSONObject(sb.toString());

            String failedids = jsonResp.getString("failedids");
            String syncedids = jsonResp.getString("syncedids");

            if(jsonResp.getString("status").equals("success")) {

                if(debugalerts) {
                    Log.d("EASYASSESS", "RESTAPIsyncMgr.run: success.");
                }

                // Mark the sucessfully synced record as 'synced'
                markRecordsAsSynced(syncedids);

                // Delete the synced records
                if(globalvault.deleterecordsaftersync)
                    deleteSyncedRecords();
            }
            else if(jsonResp.getString("status").equals("partialsuccess")) {

                if(debugalerts) {
                    Log.d("EASYASSESS", "RESTAPIsyncMgr.run: partialsuccess. Failed to add some of the records. Failed Ids: "+failedids);
                }

                // Mark the sucessfully synced record as 'synced'
                markRecordsAsSynced(syncedids);

                // Delete the synced records
                if(globalvault.deleterecordsaftersync)
                    deleteSyncedRecords();

            }
            else {

                String errmsg = jsonResp.getString("message");
                if(debugalerts) {
                    Log.d("EASYASSESS", "RESTAPIsyncMgr.run: Failed to sync. Error msg: "+errmsg);
                }
            }
        }
        catch(Exception e) {
            Log.e("EASYASSESS","RESTAPIsyncMgr.run: Exception: "+e.toString());
            if(apiConnection != null)
                apiConnection.disconnect();
        }
    }

    public void deleteSyncedRecords() {

        a3dsapiobj.deleteSyncedTelemetryRecords();
    }

    public void markRecordsAsSynced(String ids) {

        if(this.a3apiname.equals("txa3assessment"))
            a3dsapiobj.markRecordsAsSynced("a3app_assessment_tbl", ids);
        else if(this.a3apiname.equals("txa3assessmentdetail"))
            a3dsapiobj.markRecordsAsSynced("a3app_assessmentdetail_tbl", ids);
        else;

    }
}
