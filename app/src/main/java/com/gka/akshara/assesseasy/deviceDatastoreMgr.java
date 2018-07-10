package com.gka.akshara.assesseasy;

/**
 * Android Datastore Java API Library to save data in the local SQLite database
 * Configuration/Settings required:
 *      Add android.permission.INTERNET in the AndroidManifest.xml file of the app
 *      <uses-permission android:name="android.permission.INTERNET" />
 *
 * @Author: sureshkodoor@gmail.com
 */


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.akshara.assessment.a3.A3Application;
import com.akshara.assessment.a3.db.KontactDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

    public class deviceDatastoreMgr {

        private String a3dbname = com.akshara.assessment.a3.db.DB_CONSTANTS.DB_NAME;
        private Context appcontext;
        private SQLiteDatabase a3appdb = null;
        private String providerCode = "GWL";
        private boolean erroralerts = true;
        KontactDatabase db;

        public void initializeDS(Context context) {

            appcontext = context;
            db = ((A3Application) context.getApplicationContext()).getDb();
            try {
                String a3appdbfilepath = "";

                a3appdbfilepath = "/data/data/" + context.getPackageName() + "/databases/";

                a3appdbfilepath = a3appdbfilepath + a3dbname;
                if (MainActivity.debugalerts)
                    Log.d("EASYASSESS", "a3dbfilepath: " + a3appdbfilepath);

                a3appdb = SQLiteDatabase.openOrCreateDatabase(a3appdbfilepath, null);
                a3appdb.setVersion(com.akshara.assessment.a3.db.DB_CONSTANTS.DB_VERSION);

                if(a3appdb != null) {
                    if(MainActivity.debugalerts)
                        Log.d("EASYASSESS", "EASYASSESS.initializeDS: openOrCreateDatabase success. ");
                    this.createTables();

                }
                else {
                    if(erroralerts)
                        Log.e("EASYASSESS","EASYASSESS.initializeDS: openOrCreateDatabase failed. Returned NULL SQLiteDatabase handler");
                }
            }
            catch(Exception e) {
                Log.e("EASYASSESS","EASYASSESS.initializeDS: Error: "+e.toString());
            }
        }

        public boolean createTables() {

            // create / open (if already exists) the Tables
            String query_createassessmenttbl = "CREATE TABLE IF NOT EXISTS a3app_assessment_tbl ( \n" +
                    "id integer primary key autoincrement, \n" +
                    "id_assessment text, \n" +
                    "id_child text, \n" +
                    "id_questionset integer, \n" +
                    "score integer, datetime_start text, datetime_submission text, \n" +
                    "synced integer not null default 0)";

            String query_createassessmentdetailtbl = "CREATE TABLE IF NOT EXISTS a3app_assessmentdetail_tbl ( \n" +
                    "id integer primary key autoincrement, \n" +
                    "id_assessment text, id_question text, answer_given text, \n"+
                    "pass text, synced integer not null default 0)";

            String query_createquestionsettbl = "CREATE TABLE IF NOT EXISTS a3app_questionset_tbl ( \n" +
                    "id integer primary key autoincrement,  id_questionset integer, \n"+
                    "qset_title text, qset_name text,language_name text, \n" +
                    "subject_name text,  grade_name text, program_name text, assesstype_name text)";

            String query_createquestionsetdetailtbl = "CREATE TABLE IF NOT EXISTS a3app_questionsetdetail_tbl ( \n" +
                    "id integer primary key autoincrement,  id_questionset integer, id_question text)";

            String query_createquestiontbl = "CREATE TABLE IF NOT EXISTS a3app_question_tbl ( \n" +
                    "id integer primary key autoincrement,  id_question text, \n"+
                    "question_title text, question_text text,correct_answer text, language_name text, \n" +
                    "subject_name text,  grade_name text, level_name text, questiontype_name text, \n"+
                    "questiontempltype_name text, concept_name text, mconcept_name txt)";

            String query_createquestiondatatbl = "CREATE TABLE IF NOT EXISTS a3app_questiondata_tbl ( \n" +
                    "id integer primary key autoincrement,  id_question text, \n"+
                    "name text, label text, datatype text, role text, position text, val text, filecontent_base64 text)" ;

            if(MainActivity.debugalerts)
                Log.d("EASYASSESS","Enter EASYASSESS.createTables");

            try {
                a3appdb.execSQL(query_createassessmenttbl);
                a3appdb.execSQL(query_createassessmentdetailtbl);
                a3appdb.execSQL(query_createquestionsettbl);
                a3appdb.execSQL(query_createquestionsetdetailtbl);
                a3appdb.execSQL(query_createquestiontbl);
                a3appdb.execSQL(query_createquestiondatatbl);

                if(MainActivity.debugalerts)
                    Log.d("EASYASSESS","createTables: success");
            }
            catch(Exception e) {
                Log.e("EASYASSESS","createTables: failed. Error: "+e.toString());
            }

            return true;
        }

        public boolean dropTables() {

            // create / open (if already exists) the Tables
            String query_dropassessmenttbl = "DROP TABLE a3app_assessment_tbl";
            String query_dropassessmentdetailtbl = "DROP TABLE a3app_assessmentdetail_tbl";
            String query_dropquestionsettbl = "DROP TABLE a3app_questionset_tbl";
            String query_dropquestionsetdetailtbl = "DROP TABLE a3app_questionsetdetail_tbl";
            String query_dropquestiontbl = "DROP TABLE a3app_question_tbl" ;
            String query_dropquestiondatatbl = "DROP TABLE a3app_questiondata_tbl" ;

            try {
                a3appdb.execSQL(query_dropassessmenttbl);
                a3appdb.execSQL(query_dropassessmentdetailtbl);
                a3appdb.execSQL(query_dropquestionsettbl);
                a3appdb.execSQL(query_dropquestionsetdetailtbl);
                a3appdb.execSQL(query_dropquestiontbl);
                a3appdb.execSQL(query_dropquestiondatatbl);

                if(MainActivity.debugalerts)
                    Log.d("EASYASSESS","dropTables: success");
            }
            catch(Exception e) {
                Log.e("EASYASSESS","dropTables: failed. Error: "+e.toString());
            }

            return true;
        }

        public String saveAssessment(String[] arrData) {

            // Create a unique identifier for id_assessment (A 16 char unique string is generated as the Id).

            String randomstr = UUID.randomUUID().toString(); // format 8-4-4-4-12 total 36 chars
            String id_assessment = randomstr.substring(2, 5)+  randomstr.substring(14,17)+randomstr.substring(24,32); // provideCode + substring of 12 chars

            if(MainActivity.debugalerts)
                Log.d("EASYASSESS","Enter EASYASSESS.saveAssessment.  id_assessment: "+id_assessment);

            String query = "INSERT INTO a3app_assessment_tbl (id_assessment, id_child, id_questionset, score, datetime_start, datetime_submission) \n"+
                    " VALUES (?,?,?,?,?,?)";

            try {
                a3appdb.execSQL(query, new String[] {id_assessment,arrData[0],arrData[1],arrData[2], arrData[3], arrData[4]});
                if(MainActivity.debugalerts)
                    Log.d("EASYASSESS","EASYASSESS.saveAssessment: success  ");
                return id_assessment;
            }
            catch(Exception e) {
                Log.e("EASYASSESS","EASYASSESS.saveAssessment: failed. Error: "+e.toString());
                return null;
            }
        }

        public void saveAssessmentDetail(String[] arrData) {

            if(MainActivity.debugalerts)
                Log.d("EASYASSESS","Enter EASYASSESS.saveAssessment.");

            String query = "INSERT INTO a3app_assessmentdetail_tbl (id_assessment, id_question, answer_given, pass) \n"+
                    " VALUES (?,?,?,?)";

            try {
                a3appdb.execSQL(query, new String[] {arrData[0],arrData[1],arrData[2], arrData[3]});
                if(MainActivity.debugalerts)
                    Log.d("EASYASSESS","EASYASSESS.saveAssessmentDetail: success  ");
            }
            catch(Exception e) {
                Log.e("EASYASSESS","EASYASSESS.saveAssessmentDetail: failed. Error: "+e.toString());
            }
        }

        // Fetch unsynced Assessment telemetry records
        // Return values: JSONArray (array of JSONObject containing unsynced 'assessment' records)
        public JSONArray fetchUnsyncedAssessmentRecords() {

            String query = "SELECT id AS objid, id_assessment, id_child, id_questionset, score, datetime_start, datetime_submission FROM a3app_assessment_tbl WHERE synced = 0";
            JSONArray  arrRecords = new JSONArray();


            try {
                Cursor curs = a3appdb.rawQuery(query, null);

                if(curs.moveToFirst()){
                    do {
                        JSONObject record = new JSONObject();

                        int objid = curs.getInt(curs.getColumnIndex("objid"));
                        record.put("objid", objid);
                        String id_assessment = curs.getString(curs.getColumnIndex("id_assessment"));
                        record.put("id_assessment", id_assessment);
                        String id_child = curs.getString(curs.getColumnIndex("id_child"));
                        record.put("id_child", id_child);
                        String id_questionset = curs.getString(curs.getColumnIndex("id_questionset"));
                        record.put("id_questionset",id_questionset);
                        String score = curs.getString(curs.getColumnIndex("score"));
                        record.put("score",score);
                        String datetime_start = curs.getString(curs.getColumnIndex("datetime_start"));
                        record.put("datetime_start", datetime_start);
                        String datetime_submission = curs.getString(curs.getColumnIndex("datetime_submission"));
                        record.put("datetime_submission", datetime_submission);

                        arrRecords.put(record);
                    } while(curs.moveToNext());
                }
                if(MainActivity.debugalerts)
                    Log.d("EASYASSESS","EASYASSESS.fetchUnsyncedAssessmentRecords: success. arrRecords "+arrRecords.toString());
            }
            catch(Exception e) {
                Log.e("EASYASSESS","EASYASSESS.fetchUnsyncedAssessmentRecords: failed. Error: "+e.toString());
            }

            return arrRecords;
        }

        // Fetch unsynced AssessmentDetail telemetry records
        // Return values: JSONArray (array of JSONObject containing unsynced 'assessmentdetail' records)
        public JSONArray fetchUnsyncedAssessmentDetailRecords() {

            String query = "SELECT id AS objid, id_assessment, id_question, answer_given, pass FROM a3app_assessmentdetail_tbl WHERE synced = 0";
            JSONArray  arrRecords = new JSONArray();


            try {
                Cursor curs = a3appdb.rawQuery(query, null);

                if(curs.moveToFirst()){
                    do {
                        JSONObject record = new JSONObject();

                        int objid = curs.getInt(curs.getColumnIndex("objid"));
                        record.put("objid", objid);
                        String id_assessment = curs.getString(curs.getColumnIndex("id_assessment"));
                        record.put("id_assessment",id_assessment);
                        String id_question = curs.getString(curs.getColumnIndex("id_question"));
                        record.put("id_question",id_question);
                        String answer_given = curs.getString(curs.getColumnIndex("answer_given"));
                        record.put("answer_given",answer_given);
                        String pass = curs.getString(curs.getColumnIndex("pass"));
                        record.put("pass", pass);

                        arrRecords.put(record);
                    } while(curs.moveToNext());
                }
                if(MainActivity.debugalerts)
                    Log.d("EASYASSESS","EASYASSESS.fetchUnsyncedAssessmentDetailRecords: success. arrRecords "+arrRecords.toString());
            }
            catch(Exception e) {
                Log.e("EASYASSESS","EASYASSESS.fetchUnsyncedAssessmentDetailRecords: failed. Error: "+e.toString());
            }

            return arrRecords;
        }


        // Sync the telemetry data to the A3 Server invoking A3 REST APIs
        // Input parameters:
        // Return values:
        public void syncTelemetry(String apibaseurl) {

            // Fetch the saved Unsynced Assessment records
            JSONArray jsondata_assessment = fetchUnsyncedAssessmentRecords();
            // Invoke the txa3assessment A3 REST API to sync the telemetry data
            if(jsondata_assessment.length() > 0)
                invokeRESTAPI(apibaseurl, "txa3assessment", jsondata_assessment);

            // Fetch the saved Unsynced Assessment records
            JSONArray jsondata_assessmentdetail = fetchUnsyncedAssessmentDetailRecords();
            // Invoke the txa3assessmentdetail A3 REST API to sync the telemetry data
            if(jsondata_assessment.length() > 0)
                invokeRESTAPI(apibaseurl, "txa3assessmentdetail", jsondata_assessmentdetail);

        }

        // Mark records successfully synced as 'synced'
        public void markRecordsAsSynced(String table, String syncedids) {

            if(!TextUtils.isEmpty(syncedids)) {

                String query_updatesynced = "UPDATE "+table+" SET synced = 1 WHERE id IN ("+syncedids+")";
                try {
                    a3appdb.execSQL(query_updatesynced);
                    if(MainActivity.debugalerts)
                        Log.d("EASYASSESS","EASYASSESS.markRecordsAsSynced: table:"+table+" success  ");
                }
                catch(Exception e) {
                    Log.e("EASYASSESS","EASYASSESS.markRecordsAsSynced: table:"+table+"failed. Error: "+e.toString());
                }
            }
        }

        // Delete all the telemetry records that are synced successfuly
        public void deleteSyncedTelemetryRecords() {

            String query_deleteassessment = "DELETE FROM a3app_assessment_tbl  WHERE synced = 1";
            String query_deleteassessmentdetail = "DELETE FROM a3app_assessmentdetail_tbl  WHERE synced = 1";


            if(MainActivity.debugalerts)
                Log.d("EASYASSESS","In EASYASSESS.deleteSyncedTelemetryRecords:");

            try {
                a3appdb.execSQL(query_deleteassessment);
                a3appdb.execSQL(query_deleteassessmentdetail);
                if(MainActivity.debugalerts)
                    Log.d("EASYASSESS","EASYASSESS.deleteSyncedTelemetryRecords: success  ");
            }
            catch(Exception e) {
                Log.e("EASYASSESS","EASYASSESS.deleteSyncedTelemetryRecords: failed. Error: "+e.toString());
            }
        }

        // Fetch Questions for the given id_questionset.
        // The Questions are copied to globalvault.questions. Returns 'true' if success, 'false' if failed

        public boolean readQuestions(int id_questionset) {

            try {

                // Read the IDs of the Questions contained in the given QuestionSet
                String query = "SELECT id_question FROM a3app_questionsetdetail_tbl WHERE id_questionset='"+id_questionset+"'";

                Cursor curs = a3appdb.rawQuery(query, null);
                int totalrecordscount = curs.getCount();

                if(totalrecordscount == 0) {
                    if(MainActivity.debugalerts)
                        Log.d("EASYASSESS","deviceDatastoreMgr.readQuestions: Retrieved 0 records from a3app_questionsetdetail_tbl.");
                    return false;
                }

                String questionids = "";
                if(curs.moveToFirst()){
                    do {
                        String questionid = curs.getString(curs.getColumnIndex("id_question"));
                        questionids += "'"+questionid+"',";
                    } while(curs.moveToNext());
                    questionids = questionids.substring(0, questionids.length() - 1);

                }
                curs.close();
                if(MainActivity.debugalerts)
                    Log.d("EASYASSESS","deviceDatastoreMgr.readQuestions: get all question IDs - success");

                // Read the Questions from the question_tbl

                String query1 = "SELECT * FROM a3app_question_tbl WHERE id_question IN ("+questionids+")";

                Cursor curs1 = a3appdb.rawQuery(query1, null);
                int totalquestions = curs1.getCount();

                globalvault.questions = new assessquestion[totalquestions];

                if(totalquestions == 0) {
                    if(MainActivity.debugalerts)
                        Log.d("EASYASSESS","deviceDatastoreMgr.readQuestions: Retrieved 0 records from a3app_question_tbl.");
                    return false;
                }

                int n = 0;
                if(curs1.moveToFirst()){
                    do {

                        if(MainActivity.debugalerts)
                            Log.d("EASYASSESS","deviceDatastoreMgr:readQuestions: extracting Question details. n="+n);
                        assessquestion question = new assessquestion();
                        String questionid = curs1.getString(curs1.getColumnIndex("id_question"));
                        question.setQuestionID(questionid);
                        String question_text = curs1.getString(curs1.getColumnIndex("question_text"));
                        question.setQuestionText(question_text);
                        String questiontype_name = curs1.getString(curs1.getColumnIndex("questiontype_name"));
                        question.setQuestionType(questiontype_name);
                        String questiontempltype_name = curs1.getString(curs1.getColumnIndex("questiontempltype_name"));
                        question.setQuestionTemplType(questiontempltype_name);
                        String correct_answer = curs1.getString(curs1.getColumnIndex("correct_answer"));
                        question.setAnswerCorrect(correct_answer);

                        globalvault.questions[n] = new assessquestion();
                        globalvault.questions[n] = question;

                        n++;
                    } while(curs1.moveToNext());
                }
                curs1.close();
                if(MainActivity.debugalerts)
                    Log.d("EASYASSESS","deviceDatastoreMgr:readQuestions: All Questions read. Total #of Questions="+globalvault.questions.length);


                // Read the QuestionData for each of the Questions

                for(int i=0; i < totalquestions; i++) {

                    String qid = globalvault.questions[i].getQustionID();

                    String query2 = "SELECT * FROM a3app_questiondata_tbl WHERE id_question='"+qid+"'";

                    Cursor curs2 = a3appdb.rawQuery(query2, null);
                    int totalquestiondata = curs2.getCount();

                    if(totalquestiondata != 0) {

                        if (curs2.moveToFirst()) {
                            do {

                                assessquestiondata questiondata = new assessquestiondata();
                                questiondata.name = curs2.getString(curs2.getColumnIndex("name"));
                                questiondata.label = curs2.getString(curs2.getColumnIndex("label"));
                                questiondata.datatype = curs2.getString(curs2.getColumnIndex("datatype"));
                                questiondata.role = curs2.getString(curs2.getColumnIndex("role"));
                                questiondata.position = curs2.getString(curs2.getColumnIndex("position"));
                                questiondata.value = curs2.getString(curs2.getColumnIndex("val"));
                                questiondata.filecontent_base64 = curs2.getString(curs2.getColumnIndex("filecontent_base64"));

                                globalvault.questions[i].addQuestionData(questiondata);
                            } while (curs2.moveToNext());
                        }
                    }
                    curs2.close();
                }
                if(MainActivity.debugalerts)
                    Log.d("EASYASSESS","deviceDatastoreMgr:readQuestions: read questiondata : success");

                return true;

            }
            catch(Exception e) {
                Log.e("EASYASSESS","deviceDatastoreMgr.readQuestions: failed. Error: "+e.toString());
                return false;
            }
        }


        // Invoke the ABS REST API
        // Input parameters:
        // @param apibaseurl  - String - Base URL for the REST API (e.g http://www.kodvin.com/abs/)
        // @param apiname - String (name of the api. e.g txabsgameplay
        // @param jsondata - JSONArray - array of JSONObjects
        // Return values:
        public void invokeRESTAPI(String apibaseurl, String apiname, JSONArray jsondata) {

            AsyncTask.execute(new RESTAPIsyncMgr(apibaseurl,apiname,jsondata,this));
        }

        // ONLY for Testing purpose
        // Used for adding Questions in the Database for Testing the app
        public void addQuestionSet(int qsetid) {

            String query = "INSERT INTO a3app_questionset_tbl (id_questionset) VALUES (?)";
            a3appdb.execSQL(query, new String[] {Integer.toString(qsetid)});
        }
        // ONLY for Testing purpose
        // Used for adding Questions in the Database for Testing the app
        public void addQuestion(assessquestion question, int qsetid) {

            try {

                String query1 = "INSERT INTO a3app_questionsetdetail_tbl (id_questionset, id_question) VALUES (?,?)";

                a3appdb.execSQL(query1, new String[]{Integer.toString(qsetid), question.getQustionID()});

                String query2 = "INSERT INTO a3app_question_tbl ( \n" +
                        "id_question, question_text, correct_answer, \n" +
                        "questiontempltype_name) VALUES (?,?,?,?) ";

                a3appdb.execSQL(query2, new String[]{question.getQustionID(), question.getQuestionText(), question.getAnswerCorrect(), question.getQuestionTemplType()});

                ArrayList<assessquestiondata> arrquestiondata = question.getQuestionDataList();

                String id_question = question.getQustionID();

                for (int i = 0; i < arrquestiondata.size(); i++) {

                    assessquestiondata questiondata = arrquestiondata.get(i);

                    String query3 = "INSERT INTO a3app_questiondata_tbl ( \n" +
                            "id_question, name, label, datatype, role, position, \n" +
                            " val, filecontent_base64) \n" +
                            " VALUES (?,?,?,?,?,?,?,?) ";


                    a3appdb.execSQL(query3, new String[]{id_question, questiondata.name, questiondata.label, questiondata.datatype, questiondata.role, questiondata.position, questiondata.value, questiondata.filecontent_base64});

                }
            }
            catch(Exception e) {
                Log.d("EASYASSESS", "deviceDatatoreMgr:addTestQuestionsToDatabase. exception:" + e.toString());


            }
        }



        public ArrayList<com.akshara.assessment.a3.TelemetryReport.pojoReportData> getAllStudentsForReports(String questionsetId, ArrayList<com.akshara.assessment.a3.db.StudentTable> studentIds) {
            String tableName = "a3app_assessment_tbl";
            ArrayList<com.akshara.assessment.a3.TelemetryReport.pojoReportData> reportDataWithStudentInfo = new ArrayList<>();
            //String query="select id_assessment,id_child,score,datetime_submission from "+tableName;
            for (com.akshara.assessment.a3.db.StudentTable sID : studentIds) {
                Cursor cs1 = a3appdb.query(tableName, new String[]{"id", "id_assessment", "id_child",
                        "id_questionset", "score", "datetime_start", "datetime_submission", "synced"}, "id_questionset=? AND id_child=?", new String[]{
                        questionsetId, sID.getId() + ""}, null, null, null);
                //  Log.d("shri","studentId:"+sID.getId()+":"+sID.getUid());
                com.akshara.assessment.a3.TelemetryReport.pojoReportData pojoReportData = new com.akshara.assessment.a3.TelemetryReport.pojoReportData();

                if (cs1.getCount() > 0) {
                    ArrayList<com.akshara.assessment.a3.TelemetryReport.CombinePojo> combinePojosList = new ArrayList<>();
                    com.akshara.assessment.a3.TelemetryReport.pojoAssessment pojoAssessment = null;
                    //    Log.d("shri", "found" + sID + ":" + cs1.getCount());
                    while (cs1.moveToNext()) {
                        pojoAssessment = new com.akshara.assessment.a3.TelemetryReport.pojoAssessment();
                        int objid = cs1.getInt(cs1.getColumnIndex("id"));
                        pojoAssessment.setId(objid);
                        String id_assessment = cs1.getString(cs1.getColumnIndex("id_assessment"));
                        pojoAssessment.setId_assessment(id_assessment);
                        String id_child = cs1.getString(cs1.getColumnIndex("id_child"));
                        pojoAssessment.setId_child(id_child);
                        int id_questionset = cs1.getInt(cs1.getColumnIndex("id_questionset"));
                        pojoAssessment.setId_questionset(id_questionset);
                        int score = cs1.getInt(cs1.getColumnIndex("score"));
                        pojoAssessment.setScore(score);
                        String datetime_start = cs1.getString(cs1.getColumnIndex("datetime_start"));
                        pojoAssessment.setDatetime_start(datetime_start);
                        String datetime_submission = cs1.getString(cs1.getColumnIndex("datetime_submission"));
                        pojoAssessment.setDatetime_submission(datetime_submission);
                        int synced = cs1.getInt(cs1.getColumnIndex("synced"));
                        pojoAssessment.setSynced(synced);

                        //fetching assessment detail
                        String tablename2 = "a3app_assessmentdetail_tbl";
                        Cursor assessmentDetailCursor = a3appdb.query(tablename2, new String[]{"id", "id_assessment", "id_question",
                                "answer_given", "pass", "synced"}, "id_assessment=?", new String[]{

                                id_assessment + ""}, null, null, null);
                        if (assessmentDetailCursor != null && assessmentDetailCursor.getCount() > 0) {
                            ArrayList<com.akshara.assessment.a3.TelemetryReport.pojoAssessmentDetail> pojoAssessmentDetailArrayList = new ArrayList<>();
                            while (assessmentDetailCursor.moveToNext()) {
                                com.akshara.assessment.a3.TelemetryReport.pojoAssessmentDetail pojoAssessmentDetail = new com.akshara.assessment.a3.TelemetryReport.pojoAssessmentDetail();

                                int id = assessmentDetailCursor.getInt(assessmentDetailCursor.getColumnIndex("id"));
                                pojoAssessmentDetail.setId(id);
                                String id_assessmentD = assessmentDetailCursor.getString(assessmentDetailCursor.getColumnIndex("id_assessment"));
                                pojoAssessmentDetail.setId_assessment(id_assessmentD);
                                String id_question = assessmentDetailCursor.getString(assessmentDetailCursor.getColumnIndex("id_question"));

                                pojoAssessmentDetail.setId_question(id_question);
                                String answer_given = assessmentDetailCursor.getString(assessmentDetailCursor.getColumnIndex("answer_given"));
                                pojoAssessmentDetail.setAnswer_given(answer_given);
                                String pass = assessmentDetailCursor.getString(assessmentDetailCursor.getColumnIndex("pass"));
                                pojoAssessmentDetail.setPass(pass);
                                int syncedD = assessmentDetailCursor.getInt(assessmentDetailCursor.getColumnIndex("synced"));
                                pojoAssessmentDetail.setSynced(syncedD);

                                pojoAssessmentDetailArrayList.add(pojoAssessmentDetail);
                                //  Log.d("shri","===id_assessmentD:"+id_assessmentD+"----id_question:"+id_question+"----pass:"+pass);

                            }
                            com.akshara.assessment.a3.TelemetryReport.CombinePojo combinePojo = new com.akshara.assessment.a3.TelemetryReport.CombinePojo();
                            combinePojo.setPojoAssessment(pojoAssessment);
                            combinePojo.setPojoAssessmentDetail(pojoAssessmentDetailArrayList);

                            combinePojosList.add(combinePojo);
                        }

                    }

                    java.util.Map<Long, ArrayList<com.akshara.assessment.a3.TelemetryReport.CombinePojo>> detailReportsMap = new java.util.HashMap<>();
                    detailReportsMap.put(sID.getId(), combinePojosList);
                    pojoReportData.setDetailReportsMap(detailReportsMap);
                    pojoReportData.setTable(sID);
                    pojoReportData.setScore(pojoAssessment.getScore());
                    // Log.d("shri", "------------" + sID.getId());
                    reportDataWithStudentInfo.add(pojoReportData);









                } else {
                    java.util.Map<Long, ArrayList<com.akshara.assessment.a3.TelemetryReport.CombinePojo>> detailReportsMap = new java.util.HashMap<>();
                    com.akshara.assessment.a3.TelemetryReport.CombinePojo combinePojo = new com.akshara.assessment.a3.TelemetryReport.CombinePojo();
                    combinePojo.setPojoAssessment(new com.akshara.assessment.a3.TelemetryReport.pojoAssessment());
                    combinePojo.setPojoAssessmentDetail(new ArrayList<com.akshara.assessment.a3.TelemetryReport.pojoAssessmentDetail>());
                    detailReportsMap.put(sID.getId(), null);

                    pojoReportData.setDetailReportsMap(detailReportsMap);
                    pojoReportData.setTable(sID);
                    pojoReportData.setScore(-1);
                    reportDataWithStudentInfo.add(pojoReportData);
                    //  Log.d("shri", "------no found-----" + sID.getId());

                }


            }
            //  Log.d("shri", reportDataWithStudentInfo.size() + "--END----");
            return reportDataWithStudentInfo;

        }
}
