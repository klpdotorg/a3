package com.akshara.assessment.a3.TelemetryReport;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.akshara.assessment.a3.A3Application;
import com.akshara.assessment.a3.BaseActivity;
import com.akshara.assessment.a3.BuildConfig;
import com.akshara.assessment.a3.NetworkRetrofitPackage.A3NetWorkCalls;
import com.akshara.assessment.a3.NetworkRetrofitPackage.A3Services;
import com.akshara.assessment.a3.NetworkRetrofitPackage.CurrentStateInterface;
import com.akshara.assessment.a3.Pojo.TelemetryPojo;
import com.akshara.assessment.a3.R;
import com.akshara.assessment.a3.UtilsPackage.AppStatus;
import com.akshara.assessment.a3.UtilsPackage.ConstantsA3;
import com.akshara.assessment.a3.UtilsPackage.DailogUtill;
import com.akshara.assessment.a3.UtilsPackage.SchoolStateInterface;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.WebViewActivity;
import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.QuestionSetDetailTable;
import com.akshara.assessment.a3.db.QuestionTable;
import com.akshara.assessment.a3.db.StudentTable;
import com.crashlytics.android.Crashlytics;
import com.gka.akshara.assesseasy.deviceDatastoreMgr;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;


import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TelemetryRreportActivity extends BaseActivity {

    KontactDatabase db;
    long A3APP_INSTITUTIONID;
    int EASYASSESS_QUESTIONSETID;
    int A3APP_GRADEID;
    private deviceDatastoreMgr a3dsapiobj;
    RecyclerView reportRecyclerView;
    TelemetryReportAdapter adapter;
    private File pdfFile;

    ArrayList<QuestionTable> questionTables;
    ArrayList<pojoReportData> dataInternal;
    ArrayList<String> mConceptList;

    ArrayList<StudentTable> studentIds;
    SessionManager sessionManager;
    String gradeS = "";
    //  private static final int STORAGE_PERMISSION_CODE = 123;
    ProgressDialog progressDialog;
    ArrayList<String> titles = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telemetry_rreport);
        try {
            db = ((A3Application) getApplicationContext()).getDb();
            a3dsapiobj = new deviceDatastoreMgr();
            a3dsapiobj.initializeDS(this);


            mConceptList = new ArrayList<>();
            //  mConceptList2nd = new ArrayList<>();
            reportRecyclerView = findViewById(R.id.reportRecyclerView);
            A3APP_INSTITUTIONID = getIntent().getLongExtra("A3APP_INSTITUTIONID", 0L);
            EASYASSESS_QUESTIONSETID = getIntent().getIntExtra("EASYASSESS_QUESTIONSETID", 0);
            A3APP_GRADEID = getIntent().getIntExtra("A3APP_GRADEID", 0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.studentScore));
            sessionManager = new SessionManager(getApplicationContext());
//        ArrayList<QuestionTable> QuestionTitles = getAllQuestionSetTitle(EASYASSESS_QUESTIONSETID);
            reportRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            Log.d("shri","----"+EASYASSESS_QUESTIONSETID);
            reportRecyclerView.setItemAnimator(new DefaultItemAnimator());
            studentIds = getStudentIds(A3APP_INSTITUTIONID, A3APP_GRADEID);
            gradeS = getResources().getStringArray(R.array.array_grade)[A3APP_GRADEID - 1];
            dataInternal = a3dsapiobj.getAllStudentsForReports(EASYASSESS_QUESTIONSETID + "", studentIds, true, ConstantsA3.assessmenttype, true);
            Collections.sort(dataInternal);

            titles = getAllQuestionSetTitle(EASYASSESS_QUESTIONSETID);
            questionTables = getAllQuestions(EASYASSESS_QUESTIONSETID);
            adapter = new TelemetryReportAdapter(this, dataInternal, questionTables, titles);
            reportRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Crashlytics.log("TelemetryRreportActivity report crash:");
            //     DailogUtill.showDialog("Oops some thing went wrong",getSupportFragmentManager(),getApplicationContext());
            DailogUtill.showDialog(getResources().getString(R.string.oops), getSupportFragmentManager(), TelemetryRreportActivity.this);

        }
        //  saveExcelFile(getApplicationContext(),"shreee.xls");

    }

    private void initPorgresssDialogForSchool() {
        progressDialog = new ProgressDialog(TelemetryRreportActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.loadingStudent));
        //  progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.download_menu, menu);

        return true;
    }

    public void downloadStudents(MenuItem item) {
        if (item.getItemId() == R.id.action_download) {
            try {
                // DailogUtill.showDialog("Error while generating report",getSupportFragmentManager(),TelemetryRreportActivity.this);

                //   requestStoragePermission();
                //  generatePDFData();
                if (AppStatus.isConnected(getApplicationContext())) {
                     downloadStudentsFirst();

                } else {
                    DailogUtill.showDialog(getResources().getString(R.string.netWorkError), getSupportFragmentManager(), TelemetryRreportActivity.this);

                }

            } catch (Exception e) {

                try {
                    Crashlytics.log("Error while generating report");
                    Crashlytics.log("Institution Id:" + A3APP_INSTITUTIONID);
                } catch (Exception e1) {
                    Crashlytics.log("Error while generating report inner try catch block:" + e.getMessage());
                }
                DailogUtill.showDialog("Error while generating report.Please try again", getSupportFragmentManager(), TelemetryRreportActivity.this);

                //Toast.makeText(getApplicationContext(), "Error while generating report", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void downloadStudentsFirst() {
        initPorgresssDialogForSchool();
        String URL = BuildConfig.HOST + "/api/v1/institutions/" + A3APP_INSTITUTIONID + "/students/";
        //   String URL =  BuildConfig.HOST +"/api/v1/institutestudents/?institution_id="+schoolId;
        new A3NetWorkCalls(TelemetryRreportActivity.this).downloadStudent(URL, A3APP_INSTITUTIONID, A3APP_GRADEID,sessionManager.getToken(), new SchoolStateInterface() {
            @Override
            public void success(String message) {
                //  finishProgress();
                studentIds = getStudentIds(A3APP_INSTITUTIONID, A3APP_GRADEID);
                ArrayList<String> childIds = new ArrayList<>();
                for (StudentTable child : studentIds) {
                    childIds.add(child.getId() + "");
                }

                if (childIds.size() > 0) {
                    //   ArrayList<String> temp = new ArrayList<>();
                    //  temp.add("5455736");

                    TelemetryPojo pojo = new TelemetryPojo(sessionManager.getLanguage(), ConstantsA3.subject, gradeS, sessionManager.getProgramFromSession(), ConstantsA3.assessmenttype, "", "", A3Services.AUTH_KEY, childIds);
                    //TelemetryPojo pojo = new TelemetryPojo(sessionManager.getLanguage(), ConstantsA3.subject, gradeS, sessionManager.getProgramFromSession(), ConstantsA3.assessmenttype, "2017:08:01", "2018:08:16", A3Services.AUTH_KEY, temp);
                    new A3NetWorkCalls(TelemetryRreportActivity.this).getTelmetryData(pojo, new CurrentStateInterface() {
                        @Override
                        public void setSuccess(String message) {
                            finishProgress();
                            //  a3dsapiobj.a3appdb.close();
                            // a3dsapiobj = new deviceDatastoreMgr();
                            //a3dsapiobj.initializeDS(TelemetryRreportActivity.this);
                            //   Log.d("shri",dataInternal.size()+"Internal");
                            dataInternal = a3dsapiobj.getAllStudentsForReports(EASYASSESS_QUESTIONSETID + "", studentIds, true, ConstantsA3.assessmenttype, true);
                            Log.d("shri",EASYASSESS_QUESTIONSETID+"---"+studentIds.toString()+"----" +ConstantsA3.assessmenttype);
                            Collections.sort(dataInternal);
                            GenerateHtmlString();
                            //    final ArrayList<pojoReportData> data2=data;
                            //    Log.d("shri",data2.size()+"p");
                            try {

                                try {
                                    //  generatePDFData();
                                    //  GenerateHtmlString();
                                } catch (Exception e) {
                                    DailogUtill.showDialog("Error while generating report.Please try again", getSupportFragmentManager(), TelemetryRreportActivity.this);

                                }


                            } catch (Exception e) {
                                Toast.makeText(TelemetryRreportActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            //  dataInternal = a3dsapiobj.getAllStudentsForReports(EASYASSESS_QUESTIONSETID + "", studentIds, true, ConstantsA3.assessmenttype, true);
                            // Collections.sort(dataInternal);

                            adapter = new TelemetryReportAdapter(TelemetryRreportActivity.this, dataInternal, questionTables, titles);
                            reportRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            //DailogUtill.showDialog(message, getSupportFragmentManager(), TelemetryRreportActivity.this);

                        }

                        @Override
                        public void setFailed(String message) {
                            finishProgress();
                            DailogUtill.showDialog(message, getSupportFragmentManager(), TelemetryRreportActivity.this);

                        }
                    });


                } else {
                    Toast.makeText(getApplicationContext(), "No Childrens info found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failed(String message) {
                finishProgress();
                DailogUtill.showDialog(message, getSupportFragmentManager(), TelemetryRreportActivity.this);

            }

            @Override
            public void update(int message) {

            }
        });


    }

    public void GenerateHtmlString() {
        int totalAssessmentAttemptedStu=0;
        int totalStudents=0;
        Map<Integer, Integer> freq = new HashMap<Integer, Integer>();


        final String thStyle=" ";
        final String dataTillBodyTagOpen = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<meta charset=\"UTF-8\"/>\n" +
                "<head>\n" +
                "<style>\n" +
                "table, th, td {\n" +
                "    border: 1px solid black;\n" +
                "    border-collapse: collapse;\n" +
                "}\n" +
                "th, td {\n" +
                "    padding: 5px;\n" +
                "    text-align: left;   style=\"width:100%\"  \n" +
                "}\n" +
                "th span \n" +
                "{\n" +
                "  -ms-writing-mode: tb-rl;\n" +
                "  -webkit-writing-mode: vertical-rl;\n" +
                "  writing-mode: vertical-rl;\n" +
                "  transform: rotate(180deg);\n" +
                "  white-space: nowrap;\n" +
                "}</style>\n" +
                "</head>\n" +
                "<body>\n";

        final String bodyEnd = "</body></html>";

        String title = ConstantsA3.subject + " " + "Assessment for " + ConstantsA3.schoolName;
        String assessmenttype = getResources().getString(R.string.assessment_type_rep)+" "+ ConstantsA3.assessmenttype+" , "+gradeS;
        String userName = sessionManager.getUserType() + ": " + sessionManager.getFirstName();
       // String language = "Language: " + sessionManager.getLanguage();
        String subject = getResources().getString(R.string.subject_rep) +" "+ ConstantsA3.subject;
        //String grade = "Grade: " +gradeS ;

        String headers1 = "<h2><center>" + title + "</center></h2>";
        String headers2 = "<h2><center>" + assessmenttype + "</center></h2>";
        String Print1 = "<h3>" + userName + "</h3>";
       // String Print2 = "<h3>" + grade + "</h3>";
       // String Print3 = "<h3>" + language + "</h3>";
        String Print4 = "<h3>" + subject + "</h3>";



        StringBuilder firstTable =new StringBuilder();

        firstTable.append("<table style=\\\"width:100%\\\"> <tr>");

        for (int i = 0; i < mConceptList.size(); i++) {

            if (i == 0) {
                String tableheader1 =" <th style='text-align:center;vertical-align:middle;font-weight:bold;background-color:#cccccc';width=\"20%\">"+getResources().getString(R.string.slno)+"</th><th style='font-weight:bold;background-color:#cccccc';width=\\\"80%\\\">"+getResources().getString(R.string.micro)+"</th></tr>";
                firstTable.append(tableheader1);
            }


             String mconcept= mConceptList.get(i).split("@@")[0];
             String tableheader1 ="<tr><th style='text-align:center;vertical-align:middle'; width=\"20%\">"+(i+1)+"</th><th width=\"80%\">"+mconcept+"</th></tr>";
             firstTable.append(tableheader1);



            if (i == mConceptList.size() - 1) {
             //   document.add(pdfPTableforContent);
              //  document.newPage();
                firstTable.append("</table><p style=\"page-break-before: always\">");
            }

        }




        final String tableStart="<table style=\"width:100%\";page-break-before: always> <tr>";
        //  StringBuilder htmlData = new StringBuilder(dataTillBodyTagOpen+ headers1 + headers2 + Print1  + Print4 +firstTable+tableStart);
         StringBuilder htmlData = new StringBuilder();
         //StringBuilder htmlData = new StringBuilder(dataTillBodyTagOpen + headers1 + headers2 + Print1  + Print4 +firstTable+tableStart);
        String tempSize = (70 / mConceptList.size())+"%";
        for (int m = 0; m < mConceptList.size(); m++) {

            if (m == 0) {
                String tableheader1 =" <th style='font-weight:bold;background-color:#cccccc';width=\"15%\">"+ getResources().getString(R.string.studntName)+"</th>";
                String tableheader2 =" <th style='font-weight:bold;background-color:#cccccc';width=\"5%\">"+getResources().getString(R.string.sln)+"</th>";
                htmlData.append(tableheader2);
                htmlData.append(tableheader1);

            }
            //0 index mconcept,1 index will have question id,2 will have qtitle& 3 concept
            //0 index mconcept,1 index will have question id,2 will have qtitle& 3 concept

            //
            String concept=mConceptList.get(m).split("@@")[3];
            try {
                concept = concept.split("-", 2)[1];
            }catch (Exception e)
            {

            }
           // String tablemconceptsheader="<th style='text-align:center;vertical-align:middle';width="+tempSize+">"+String.valueOf(m + 1)+"</th>";
            String tablemconceptsheader="<th style='text-align:center;vertical-align:middle;font-weight:bold;background-color:#cccccc';width="+tempSize+">("+(m+1)+")"+concept+"</th>";
            htmlData.append(tablemconceptsheader);
            //Phrase phrase = new Phrase(String.valueOf(mConceptList.get(m).split("@@")[0]),getFont());

            if (m == (mConceptList.size() - 1)) {
                String sizetemp="10%";
                htmlData.append("<th style='text-align:center;vertical-align:middle;font-weight:bold;background-color:#cccccc' width="+sizetemp+">"+getResources().getString(R.string.stscore)+"</th> </tr>");
            }
        }
         //first header completed


        for (int j = 0; j < dataInternal.size(); j++) {
            StudentTable table = dataInternal.get(j).getTable();
            String name = " "+table.getFirstName().toLowerCase();
            Log.d("shri",table.getFirstName()+"--"+table.getId());
            try {
                if (table.getMiddleName() != null && !table.getMiddleName().equalsIgnoreCase("")) {
                    name = name + " " + (table.getMiddleName().substring(0, 1)).toLowerCase();
                }

            } catch (Exception e) {

            }


            //pdfPTable.addCell(new PdfPCell(new Phrase(name)));
            String startDataH = "<tr><th style='text-align:center;vertical-align:middle'>"+(j+1)+"</th><th>" + WordUtils.capitalize(name) + "</th>";
            htmlData.append(startDataH);

            for (int k = 0; k < mConceptList.size(); k++) {
                if (dataInternal.get(j).getDetailReportsMap().get(table.getId()) != null) {
                    int size = dataInternal.get(j).getDetailReportsMap().get(table.getId()).size();
                    Log.d("shri",size+"---------------------");
                    CombinePojo pojo = dataInternal.get(j).getDetailReportsMap().get(table.getId()).get(size - 1);
                    int marks = getAnswer(mConceptList.get(k), pojo, j, table.getId(), (size - 1));
                    htmlData.append("<th style='text-align:center;vertical-align:middle'>" + marks + "</th>");

                    // getAnswer(mConceptList.get(k),pojo);
                   // if(marks!=0) {
                        Integer count = freq.get(k);
                        if (count == null) {
                            freq.put(k, marks);
                        } else {
                            freq.put(k, count + marks);
                        }
                   // }



                } else {
                    String nodata = "-";

                    htmlData.append("<th style='text-align:center;vertical-align:middle'>" + nodata + "</th>");


                }


                if (k == mConceptList.size() - 1) {
                    if (dataInternal.get(j).getDetailReportsMap().get(table.getId()) != null) {
                        totalAssessmentAttemptedStu++;
                        totalStudents++;
                        int size = dataInternal.get(j).getDetailReportsMap().get(table.getId()).size();
                        int score = dataInternal.get(j).getDetailReportsMap().get(table.getId()).get(size - 1).getPojoAssessment().getScore();
                        htmlData.append("<th style='text-align:center;vertical-align:middle'>" + score + "</th></tr>");

                        //end score
                    } else {
                        String score = "-";
                        totalStudents++;
                        htmlData.append("<th style='text-align:center;vertical-align:middle'>" + score + "</th></tr>");

                    }

                }

            }



            if (j == dataInternal.size() - 1) {
                String Print5 = "<h3>" + getResources().getString(R.string.attemptedStudents) +""+totalAssessmentAttemptedStu+ "</h3>";
                String Print6 = "<h3>" + getResources().getString(R.string.totalStudents) +""+totalStudents+ "</h3>";

                StringBuilder htmlData2 = new StringBuilder(dataTillBodyTagOpen+ headers1 + headers2 + Print1  + Print4 +Print6+Print5+firstTable+tableStart);
                htmlData2.append(htmlData);

                if(freq!=null&&freq.size()>0) {
                    String lastTotlalString = "<tr><th style='text-align:center;vertical-align:middle;font-weight:bold;background-color:#cccccc'>  </th><th style='text-align:center;vertical-align:middle;font-weight:bold;background-color:#cccccc'>"+ getResources().getString(R.string.coorect_answers)+"</th>";
                    StringBuilder builder = new StringBuilder(lastTotlalString);
                    for (Integer val : freq.values()) {
                       // if (val <= 4) {
                      //      builder.append("<th style='text-align:center;vertical-align:middle;font-weight:bold;background-color:#F00'><font color=\"#FFDF00\">" + val + "</font></th>");

                      //  } else {
                            builder.append("<th style='text-align:center;vertical-align:middle;font-weight:bold;background-color:#cccccc'>" + val + "</th>");
                      //  }
                    }
                    builder.append("<th style='text-align:center;vertical-align:middle;font-weight:bold;background-color:#cccccc'> </th></tr>");


                    htmlData2.append(builder);
                    String s="</table><h4><left>" + getResources().getString(R.string.kitQ) + "</left></h4>";
                    htmlData2.append(s);
                }
                htmlData2.append(bodyEnd);



            // Log.d("shri",freq.toString());
              //  genPdfFile(htmlData.toString());
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra("data", htmlData2.toString());
                startActivity(intent);



            }
        }



    }






















/*


   private void generatePDFData() throws DocumentException {


        int tableSize = mConceptList.size() + 2;
        int[] bytes = new int[tableSize];
        int temp = (75 / mConceptList.size());
        for (int i = 0; i < tableSize; i++) {
            if (i == 0) {
                bytes[i] = 15;
                continue;

            }
            bytes[i] = temp;


            if (i == tableSize - 1) {
                bytes[i] = 10;
            }
        }
        PdfPTable pdfPTable = new PdfPTable(tableSize);
        pdfPTable.setWidths(bytes);
        pdfPTable.setWidthPercentage(100);
        try {
         */
/*   File root = Environment.getExternalStorageDirectory();
            File dir = new File (root.getAbsolutePath() + "/downloadAK");
           if(!dir.exists())
            dir.mkdirs();*//*


            Calendar calendar = Calendar.getInstance();
            String fileName = getResources().getString(R.string.app_name) + calendar.getTimeInMillis();

            // File file = new File(dir, fileName+".pdf");

            //  File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
           */
/* File file = File.createTempFile(
                    fileName,
                    ".pdf",
                    storageDir
            );*//*

            File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File file = File.createTempFile(
                    fileName,  */
/* prefix *//*

                    ".pdf",         */
/* suffix *//*

                    storageDir      */
/* directory *//*

            );

           */
/* File file = File.createTempFile(
                    fileName,  *//*
*/
/* prefix *//*
*/
/*
                    ".pdf",         *//*
*/
/* suffix *//*
*/
/*
                    dir      *//*
*/
/* directory *//*
*/
/*
            );*//*



            //   String  mCurrentPhotoPath = "file:" + file.getAbsolutePath();
            String imageFilePath = file.getAbsolutePath();
            pdfFile = file;
            OutputStream output = new FileOutputStream(pdfFile);
            Document document = new Document(PageSize.A3.rotate());


            PdfWriter.getInstance(document, output);
            document.open();


            Font font = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD);
            Font font2 = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            String title = ConstantsA3.subject + " " + "Assessment for " + ConstantsA3.schoolName;
            Paragraph paragraphappName = new Paragraph(title, font);

            paragraphappName.setAlignment(Element.ALIGN_CENTER);
            paragraphappName.setLeading(0, 1);
            paragraphappName.setSpacingAfter(20);
            document.add(paragraphappName);

            //  String FONT = "assets/sample1.ttf";

            //  Font f = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            Paragraph paragraphappName2 = new Paragraph(getResources().getString(R.string.assessment_type_rep) + ConstantsA3.assessmenttype, font);
            paragraphappName2.setAlignment(Element.ALIGN_CENTER);
            paragraphappName2.setLeading(0, 1);
            paragraphappName2.setSpacingAfter(30);
            document.add(paragraphappName2);


            Paragraph paragraphUserName = new Paragraph(sessionManager.getUserType() + ": " + sessionManager.getFirstName(), font2);
            Paragraph gradeParagraph = new Paragraph("Grade: " + gradeS, font2);
            //  Paragraph paraAsstypetitle = new Paragraph("Assessment type title: " + ConstantsA3.surveyTitle, font2);
            //  Paragraph paraAsstype = new Paragraph("ASSESSMENT TYPE: " + ConstantsA3.assessmenttype, font2);
         //   Paragraph paralang = new Paragraph("Language: " + sessionManager.getLanguage(), font2);
            Paragraph paraSubjecttype = new Paragraph(getResources().getString(R.string.subject_rep) + ConstantsA3.subject, font2);


            paraSubjecttype.setSpacingAfter(15);
            document.add(paragraphUserName);
            //  document.add(paraAsstypetitle);
            //  document.add(paraAsstype);
            document.add(gradeParagraph);

          //  document.add(paralang);
            document.add(paraSubjecttype);
            // BidiFormatter myBidiFormatter = BidiFormatter.getInstance();

            PdfPTable pdfPTableforContent = new PdfPTable(2);
            pdfPTableforContent.setWidths(new float[]{15, 85});
            pdfPTableforContent.setWidthPercentage(70);

            for (int i = 0; i < mConceptList.size(); i++) {
                PdfPCell pdfPCell;
                if (i == 0) {
                    //set headers
                    pdfPCell = new PdfPCell(new Phrase("Sl.NO"));
                    pdfPCell.setBackgroundColor(BaseColor.GRAY);
                    pdfCellStyles(pdfPCell);

                    // pdfPCell.setColspan(1);
                    pdfPTableforContent.addCell(pdfPCell);
                    pdfPCell = new PdfPCell(new Phrase("Concept name"));
                    pdfPCell.setBackgroundColor(BaseColor.GRAY);
                    pdfCellStyles1(pdfPCell);


                    pdfPTableforContent.addCell(pdfPCell);
                }

                pdfPCell = new PdfPCell(new Phrase(String.valueOf(i + 1)));
                // pdfPCell.setColspan(1);
                pdfCellStyles(pdfPCell);
                pdfPTableforContent.addCell(pdfPCell);

                //pdfPCell = new PdfPCell(new Phrase(mConceptList.get(i)));
                // pdfPCell = new PdfPCell(new Phrase(mConceptList.get(i).split("@@")[2] + "   -  " + mConceptList.get(i).split("@@")[0]));
                pdfPCell = new PdfPCell(new Phrase(" " + mConceptList.get(i).split("@@")[0]));
                // Phrase p=new Phrase("");
                pdfPTableforContent.addCell(pdfPCell);
                //  pdfCellStyles2(pdfPCell)


                if (i == mConceptList.size() - 1) {
                    document.add(pdfPTableforContent);
                    document.newPage();
                }

            }


            for (int m = 0; m < mConceptList.size(); m++) {

                PdfPCell pdfPCell;
                if (m == 0) {
                    Phrase phrase = new Phrase("Concept type/ \nStudent Name");
                    pdfPCell = new PdfPCell(phrase);
                    //  pdfPCell.setNoWrap(false);
                    pdfPCell.setBackgroundColor(BaseColor.GRAY);

                    //  pdfPCell.setColspan(1);
                    pdfCellStylesInside(pdfPCell);
                    pdfPTable.addCell(pdfPCell);

                }
                Phrase phrase = new Phrase(String.valueOf(m + 1));
                //Phrase phrase = new Phrase(String.valueOf(mConceptList.get(m).split("@@")[0]),getFont());
                pdfPCell = new PdfPCell(phrase);
                //  pdfPCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                //  pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                //  pdfPCell.setRotation(90);

                pdfCellStyles(pdfPCell);

                pdfPCell.setBackgroundColor(BaseColor.GRAY);

                pdfPTable.addCell(pdfPCell);


                if (m == (mConceptList.size() - 1)) {
                    Phrase phrase1 = new Phrase("Score");
                    pdfPCell = new PdfPCell(phrase1);
                    pdfCellStyles(pdfPCell);
                    pdfPCell.setBackgroundColor(BaseColor.GRAY);

                    pdfPTable.addCell(pdfPCell);

                }
            }

            for (int j = 0; j < dataInternal.size(); j++) {
                StudentTable table = dataInternal.get(j).getTable();
                String name = table.getFirstName();
                try {
                    if (table.getMiddleName() != null && !table.getMiddleName().equalsIgnoreCase("")) {
                        name = name + " " + (table.getMiddleName().substring(0, 1));
                    }

                 */
/*   if (table.getLastName() != null && !table.getLastName().equalsIgnoreCase("")) {
                        name = name + " " + (table.getLastName().substring(0, 1));
                    }*//*

                } catch (Exception e) {

                }
                pdfPTable.addCell(new PdfPCell(new Phrase(name)));

                for (int k = 0; k < mConceptList.size(); k++) {
                    if (dataInternal.get(j).getDetailReportsMap().get(table.getId()) != null) {
                        int size = dataInternal.get(j).getDetailReportsMap().get(table.getId()).size();
                        CombinePojo pojo = dataInternal.get(j).getDetailReportsMap().get(table.getId()).get(size - 1);
                        PdfPCell pdfPCell = new PdfPCell(new PdfPCell(new Phrase(getAnswer(mConceptList.get(k), pojo, j, table.getId(), (size - 1)) + "")));
                        pdfCellStyles(pdfPCell);
                        pdfPTable.addCell(pdfPCell);
                        // getAnswer(mConceptList.get(k),pojo);


                    } else {
                        PdfPCell pdfPCell = new PdfPCell(new Phrase("-"));
                        pdfCellStyles(pdfPCell);
                        pdfPTable.addCell(pdfPCell);


                    }


                    if (k == mConceptList.size() - 1) {
                        if (dataInternal.get(j).getDetailReportsMap().get(table.getId()) != null) {
                            int size = dataInternal.get(j).getDetailReportsMap().get(table.getId()).size();
                            int score = dataInternal.get(j).getDetailReportsMap().get(table.getId()).get(size - 1).getPojoAssessment().getScore();
                            PdfPCell pdfPCell = new PdfPCell(new PdfPCell(new Phrase(String.valueOf(score))));
                            pdfCellStyles(pdfPCell);
                            pdfPTable.addCell(pdfPCell);
                        } else {
                            PdfPCell pdfPCell = new PdfPCell(new Phrase("-"));
                            pdfCellStyles(pdfPCell);
                            pdfPTable.addCell(pdfPCell);
                        }
                    }

                }

                if (j == dataInternal.size() - 1) {
                    document.add(pdfPTable);
                    pdfPTable.setHeaderRows(1);
                    document.close();
                    //  output.close();


                    Intent intent = new Intent(Intent.ACTION_VIEW);

// set flag to give temporary permission to external app to use your FileProvider
                   // intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);


// generate URI, I defined authority as the application ID in the Manifest, the last param is file I want to open
                    //  Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, pdfFile);

                    Uri uri = FileProvider.getUriForFile(TelemetryRreportActivity.this, BuildConfig.APPLICATION_ID + ".provider", pdfFile);
// I am opening a PDF file so I give it a valid MIME type
                    intent.setDataAndType(uri, "application/pdf");

// validate that the device can open your File!
                    PackageManager pm = getApplicationContext().getPackageManager();
                    if (intent.resolveActivity(pm) != null) {
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            try {
                                Crashlytics.log("Institution Id:" + A3APP_INSTITUTIONID);
                                Crashlytics.log("PDF reader not found");
                            } catch (Exception e1) {
                                Crashlytics.log("PDF reader not found inner catch");
                            }

                            DailogUtill.showDialog("PDF reader not found", getSupportFragmentManager(), getApplicationContext());

                            //  Toast.makeText(getApplicationContext(), "PDF reader not found", Toast.LENGTH_SHORT).show();
                        }
                    }


                }
            }


        } catch (Exception e) {
            Crashlytics.log("Error-" + e.getMessage());
            DailogUtill.showDialog(getResources().getString(R.string.oops), getSupportFragmentManager(), TelemetryRreportActivity.this);

            // Toast.makeText(getApplicationContext(), "Error-" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }



    }*/


    private void finishProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

   /* private void pdfCellStyles(PdfPCell pdfPCell) {
        pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    }

    private void pdfCellStyles1(PdfPCell pdfPCell) {
        //  pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    }

    private void pdfCellStylesInside(PdfPCell pdfPCell) {
        //pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    }*/


    public int getAnswer(String mconceptName1, CombinePojo pojo, int j, long stuid, int i) {
        int answerCount = 0;


        // Log.d("shri","-----------------start ---:"+stuid);


        for (int m = 0; m < pojo.getPojoAssessmentDetail().size(); m++) {

            pojoAssessmentDetail detail = pojo.getPojoAssessmentDetail().get(m);
           /* if(detail.getId_assessment().equalsIgnoreCase("2c54c984e21cc4"))
            {
                Log.d("shri",detail.toString());
            }*/

            if (detail != null) {
                //   Log.d("shri",de)
                String mConceptName = getMConceptName(detail.getId_question());
                //   Log.d("shri",concept+"------"+conceptName+":"+detail.getPass()+"qidAss"+detail.getId_assessment()+"-QID"+detail.getId_question()+"--id--"+detail.getId());
                //   pojo.getPojoAssessment().id_questionset;
                //   if (mconceptName1.split("@@")[1].equalsIgnoreCase(detail.getId_question()) && detail.getPass() != null && detail.getPass().equalsIgnoreCase("P")) {

                if (mconceptName1.split("@@")[0].equalsIgnoreCase(mConceptName) && detail.getPass() != null
                        && detail.getPass().equalsIgnoreCase("P") && detail.isFlag() == false) {
                    //answerCount = answerCount + 1;
                    answerCount = 1;

                    //    dataInternal.get(j).getDetailReportsMap().get(stuid).get(i).getPojoAssessmentDetail().remove(m);
                    //   pojo.getPojoAssessmentDetail().remove(m);
                    dataInternal.get(j).getDetailReportsMap().get(stuid).get(i).getPojoAssessmentDetail().get(m).setFlag(true);
                    return answerCount;
                    //  m=m-1;
                } else if (mconceptName1.split("@@")[0].equalsIgnoreCase(mConceptName) && detail.isFlag() == false) {

                    dataInternal.get(j).getDetailReportsMap().get(stuid).get(i).getPojoAssessmentDetail().get(m).setFlag(true);
                    // pojo.getPojoAssessmentDetail().remove(m);
                    // m=m-1;
                    return answerCount;
                }


            }


        }


        return answerCount;

    }


    public String getMConceptName(String questionId) {
        Query question = Query.select().from(QuestionTable.TABLE)
                .where(QuestionTable.ID_QUESTION.eq(questionId));
        SquidCursor<QuestionTable> studentCursor = db.query(QuestionTable.class, question);
        while (studentCursor.moveToNext()) {
            String mconceptName = new QuestionTable(studentCursor).getMconceptName();
            if (studentCursor != null) {
                studentCursor.close();
            }
            return mconceptName;
        }
        return "";

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

    public void navigateBack() {

        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        finish();

    }

    public ArrayList<StudentTable> getStudentIds(long institution, int gradeId) {
        ArrayList<StudentTable> studentIds = new ArrayList<>();
        Query studentQuery = Query.select().from(StudentTable.TABLE)
                .where(StudentTable.INSTITUTION.eq(institution).and(StudentTable.STUDENT_GRADE.eq(gradeId))).orderBy(StudentTable.FIRST_NAME.asc());
        SquidCursor<StudentTable> studentCursor = db.query(StudentTable.class, studentQuery);
        if (studentCursor != null && studentCursor.getCount() > 0) {
            while (studentCursor.moveToNext()) {
                StudentTable studentTable = new StudentTable(studentCursor);
                studentIds.add(studentTable);

            }
        }
        //Log.d("shri","Student size"+studentIds.size());
        return studentIds;


    }


    public ArrayList<String> getAllQuestioId(int questionsetId) {
        ArrayList<String> listQId = new ArrayList<>();
        Query QuestionsetQuery = Query.select().from(QuestionSetDetailTable.TABLE)
                .where(QuestionSetDetailTable.ID_QUESTIONSET.eq(questionsetId));
        SquidCursor<QuestionSetDetailTable> questionsetDetailCursor = db.query(QuestionSetDetailTable.class, QuestionsetQuery);
        if (questionsetDetailCursor != null && questionsetDetailCursor.getCount() > 0) {
            while (questionsetDetailCursor.moveToNext()) {
                QuestionSetDetailTable questionSetDetailTable = new QuestionSetDetailTable(questionsetDetailCursor);
                listQId.add(questionSetDetailTable.getIdQuestion());


            }
        }
        return listQId;
    }

    public ArrayList<QuestionTable> getAllQuestions(int questionsetId) {
        ArrayList<String> qIDtemp = new ArrayList<>();

        mConceptList = new ArrayList<>();
        //  mConceptList2nd = new ArrayList<>();
        ArrayList<QuestionTable> listAllQuestions = new ArrayList<>();
        Query QuestionsetQuery = Query.select().from(QuestionSetDetailTable.TABLE)
                .where(QuestionSetDetailTable.ID_QUESTIONSET.eq(questionsetId));
        SquidCursor<QuestionSetDetailTable> questionsetDetailCursor = db.query(QuestionSetDetailTable.class, QuestionsetQuery);
        if (questionsetDetailCursor != null && questionsetDetailCursor.getCount() > 0) {
            while (questionsetDetailCursor.moveToNext()) {
                QuestionSetDetailTable questionSetDetailTable = new QuestionSetDetailTable(questionsetDetailCursor);

                Query QuestionQuery = Query.select().from(QuestionTable.TABLE)
                        .where(QuestionTable.ID_QUESTION.eq(questionSetDetailTable.getIdQuestion()));
                SquidCursor<QuestionTable> questionCursoe = db.query(QuestionTable.class, QuestionQuery);

                while (questionCursoe.moveToNext()) {
                    QuestionTable questionTable = new QuestionTable(questionCursoe);

                    if (!qIDtemp.contains(questionTable.getIdQuestion())) {
                        qIDtemp.add(questionTable.getIdQuestion());
                        //if (!mConceptList.contains(questionTable.getMconceptName())) {
                        mConceptList.add(questionTable.getMconceptName() + "@@" + questionTable.getIdQuestion() + "@@" + questionTable.getQuestionTitle()+"@@"+questionTable.getConceptName());
                        // mConceptList2nd.add(questionTable.getIdQuestion() );
                        //  }
                        listAllQuestions.add(questionTable);
                    }

                }

            }
        }
        return listAllQuestions;
    }

    public ArrayList<String> getAllQuestionSetTitle(int questionsetId) {
        ArrayList<String> qIDtemp = new ArrayList<>();

        ArrayList<String> listQuestionTitle = new ArrayList<>();
        Query QuestionsetQuery = Query.select().from(QuestionSetDetailTable.TABLE)
                .where(QuestionSetDetailTable.ID_QUESTIONSET.eq(questionsetId));
        SquidCursor<QuestionSetDetailTable> questionsetDetailCursor = db.query(QuestionSetDetailTable.class, QuestionsetQuery);
        if (questionsetDetailCursor != null && questionsetDetailCursor.getCount() > 0) {
            while (questionsetDetailCursor.moveToNext()) {
                QuestionSetDetailTable questionSetDetailTable = new QuestionSetDetailTable(questionsetDetailCursor);

                Query QuestionQuery = Query.select().from(QuestionTable.TABLE)
                        .where(QuestionTable.ID_QUESTION.eq(questionSetDetailTable.getIdQuestion()));
                SquidCursor<QuestionTable> questionCursoe = db.query(QuestionTable.class, QuestionQuery);

                while (questionCursoe.moveToNext()) {
                    QuestionTable questionTable = new QuestionTable(questionCursoe);
                    if (!qIDtemp.contains(questionTable.getIdQuestion())) {
                        qIDtemp.add(questionTable.getIdQuestion());
                        if (!listQuestionTitle.contains(questionTable.getConceptName())) {
                            listQuestionTitle.add(questionTable.getConceptName());
                        }
                    }
                }

            }
        }
        return listQuestionTitle;
    }
}
