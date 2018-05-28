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

            // create array of 'Id's of the successfully synced records
            JSONArray arrJsonResp = new JSONArray(sb.toString());
            int resplength = arrJsonResp.length();
            StringBuilder syncedids = new StringBuilder();

            for(int i =0; i < resplength; i++) {

                JSONObject respObj = arrJsonResp.getJSONObject(i);
                if(respObj.getString("status").equals("success")) {
                    syncedids.append(respObj.getString("objid"));
                    if(i < (resplength-1))
                        syncedids.append(",");
                }
            }

            if(debugalerts) {
                Log.d("EASYASSESS", "RESTAPIsyncMgr.run: success. Rxd Response. Synced Ids: " + syncedids.toString());
            }

            // Delete the synced records
            deleteSyncedRecords(syncedids.toString());
        }
        catch(Exception e) {
            Log.e("EASYASSESS","RESTAPIsyncMgr.run: Exception: "+e.toString());
            if(apiConnection != null)
                apiConnection.disconnect();
        }
    }

    public void deleteSyncedRecords(String ids) {

        if(globalvault.deleterecordsaftersync)
            a3dsapiobj.deleteSyncedTelemetryRecords();
     }
}
