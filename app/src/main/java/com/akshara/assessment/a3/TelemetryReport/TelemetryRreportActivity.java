package com.akshara.assessment.a3.TelemetryReport;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.text.BidiFormatter;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Property;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.akshara.assessment.a3.A3Application;
import com.akshara.assessment.a3.BaseActivity;
import com.akshara.assessment.a3.BuildConfig;
import com.akshara.assessment.a3.R;
import com.akshara.assessment.a3.UtilsPackage.ConstantsA3;
import com.akshara.assessment.a3.UtilsPackage.DailogUtill;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.QuestionSetDetailTable;
import com.akshara.assessment.a3.db.QuestionTable;
import com.akshara.assessment.a3.db.StudentTable;
import com.crashlytics.android.Crashlytics;
import com.gka.akshara.assesseasy.deviceDatastoreMgr;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.FontProvider;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

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
    ArrayList<pojoReportData> data;
    ArrayList<String> mConceptList;
    ArrayList<StudentTable> studentIds;
    SessionManager sessionManager;
    String gradeS = "";
    //  private static final int STORAGE_PERMISSION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telemetry_rreport);
        try {
            db = ((A3Application) getApplicationContext()).getDb();
            a3dsapiobj = new deviceDatastoreMgr();
            a3dsapiobj.initializeDS(this);
            mConceptList = new ArrayList<>();
            reportRecyclerView = findViewById(R.id.reportRecyclerView);
            A3APP_INSTITUTIONID = getIntent().getLongExtra("A3APP_INSTITUTIONID", 0L);
            EASYASSESS_QUESTIONSETID = getIntent().getIntExtra("EASYASSESS_QUESTIONSETID", 0);
            A3APP_GRADEID = getIntent().getIntExtra("A3APP_GRADEID", 0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.studentScore));
            sessionManager = new SessionManager(getApplicationContext());
//        ArrayList<QuestionTable> QuestionTitles = getAllQuestionSetTitle(EASYASSESS_QUESTIONSETID);
            reportRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            reportRecyclerView.setItemAnimator(new DefaultItemAnimator());
            studentIds = getStudentIds(A3APP_INSTITUTIONID, A3APP_GRADEID);
            gradeS = getResources().getStringArray(R.array.array_grade)[A3APP_GRADEID - 1];
            data = a3dsapiobj.getAllStudentsForReports(EASYASSESS_QUESTIONSETID + "", studentIds);
            Collections.sort(data);

            ArrayList<String> titles = getAllQuestionSetTitle(EASYASSESS_QUESTIONSETID);
            questionTables = getAllQuestions(EASYASSESS_QUESTIONSETID);
            adapter = new TelemetryReportAdapter(this, data, questionTables, titles);
            reportRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Crashlytics.log("TelemetryRreportActivity report crash:" + e.getMessage());
            //     DailogUtill.showDialog("Oops some thing went wrong",getSupportFragmentManager(),getApplicationContext());
            DailogUtill.showDialog(getResources().getString(R.string.oops), getSupportFragmentManager(), TelemetryRreportActivity.this);

        }
        //  saveExcelFile(getApplicationContext(),"shreee.xls");

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
                if (studentIds.size() > 0) {
                    generatePDFData();
                } else {
                    DailogUtill.showDialog("No Students found to generate report", getSupportFragmentManager(), TelemetryRreportActivity.this);

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

    private void pdfCellStyles(PdfPCell pdfPCell) {
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
    }


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

            Calendar calendar = Calendar.getInstance();
            String fileName = getResources().getString(R.string.app_name) + calendar.getTimeInMillis();
            File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File file = File.createTempFile(
                    fileName,
                    ".pdf",
                    storageDir
            );
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
            paragraphappName.setSpacingAfter(30);
            document.add(paragraphappName);


            Paragraph paragraphUserName = new Paragraph(sessionManager.getUserType() + ": " + sessionManager.getFirstName(), font2);
            Paragraph gradeParagraph = new Paragraph("Grade: " + gradeS, font2);
            Paragraph paraAsstypetitle = new Paragraph("Assessment type title: " + ConstantsA3.surveyTitle, font2);
            Paragraph paraAsstype = new Paragraph("Assessment type: " + ConstantsA3.assessmenttype, font2);
            Paragraph paralang = new Paragraph("Language: " + sessionManager.getLanguage(), font2);
            Paragraph paraSubjecttype = new Paragraph("Subject: " + ConstantsA3.subject, font2);
            paraSubjecttype.setSpacingAfter(15);
            document.add(paragraphUserName);
            document.add(paraAsstypetitle);
            document.add(paraAsstype);
            document.add(gradeParagraph);

            document.add(paralang);
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
                    pdfPCell = new PdfPCell(new Phrase("Question title & Concept name"));
                    pdfPCell.setBackgroundColor(BaseColor.GRAY);
                    pdfCellStyles1(pdfPCell);


                    pdfPTableforContent.addCell(pdfPCell);
                }

                pdfPCell = new PdfPCell(new Phrase(String.valueOf(i + 1)));
                // pdfPCell.setColspan(1);
                pdfCellStyles(pdfPCell);
                pdfPTableforContent.addCell(pdfPCell);

                //pdfPCell = new PdfPCell(new Phrase(mConceptList.get(i)));
                pdfPCell = new PdfPCell(new Phrase(mConceptList.get(i).split("@@")[2]+"   -  "+mConceptList.get(i).split("@@")[0]));
                pdfPTableforContent.addCell(pdfPCell);
                //pdfCellStyles(pdfPCell);


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
                pdfPCell = new PdfPCell(phrase);
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

            for (int j = 0; j < data.size(); j++) {
                StudentTable table = data.get(j).getTable();
                String name = table.getFirstName();
                try {
                    if (table.getMiddleName() != null && !table.getMiddleName().equalsIgnoreCase("")) {
                        name = name + " " + (table.getMiddleName().substring(0, 1));
                    }

                 /*   if (table.getLastName() != null && !table.getLastName().equalsIgnoreCase("")) {
                        name = name + " " + (table.getLastName().substring(0, 1));
                    }*/
                } catch (Exception e) {

                }
                pdfPTable.addCell(new PdfPCell(new Phrase(name)));

                for (int k = 0; k < mConceptList.size(); k++) {
                    if (data.get(j).getDetailReportsMap().get(table.getId()) != null) {
                        int size = data.get(j).getDetailReportsMap().get(table.getId()).size();
                        CombinePojo pojo = data.get(j).getDetailReportsMap().get(table.getId()).get(size - 1);
                        PdfPCell pdfPCell = new PdfPCell(new PdfPCell(new Phrase(getAnswer(mConceptList.get(k), pojo) + "")));
                        pdfCellStyles(pdfPCell);
                        pdfPTable.addCell(pdfPCell);
                        // getAnswer(mConceptList.get(k),pojo);


                    } else {
                        PdfPCell pdfPCell = new PdfPCell(new Phrase("-"));
                        pdfCellStyles(pdfPCell);
                        pdfPTable.addCell(pdfPCell);


                    }


                    if (k == mConceptList.size() - 1) {
                        if (data.get(j).getDetailReportsMap().get(table.getId()) != null) {
                            int size = data.get(j).getDetailReportsMap().get(table.getId()).size();
                            int score = data.get(j).getDetailReportsMap().get(table.getId()).get(size - 1).getPojoAssessment().getScore();
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

                if (j == data.size() - 1) {
                    document.add(pdfPTable);
                    pdfPTable.setHeaderRows(1);
                    document.close();
                    Toast.makeText(getApplicationContext(), "report created", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(Intent.ACTION_VIEW);

// set flag to give temporary permission to external app to use your FileProvider
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

// generate URI, I defined authority as the application ID in the Manifest, the last param is file I want to open
                    Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, pdfFile);

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


    }


    public int getAnswer(String mconceptName1, CombinePojo pojo) {
        int answerCount = 0;


        for (pojoAssessmentDetail detail : pojo.getPojoAssessmentDetail()) {

            if (detail != null) {
               // String mConceptName = getMConceptName(detail.getId_question());
                //   Log.d("shri",concept+"------"+conceptName+":"+detail.getPass()+"qidAss"+detail.getId_assessment()+"-QID"+detail.getId_question()+"--id--"+detail.getId());

                //0 index concept,1 index will have question id,2 will have qtitle
                if ( mconceptName1.split("@@")[1].equalsIgnoreCase(detail.getId_question()) && detail.getPass()!=null&&detail.getPass().equalsIgnoreCase("P")) {
                    answerCount = answerCount + 1;

                    //  Log.d("shri",totalanswerCount+"-----------------");
                }
            }


        }

        return answerCount;

    }


  /*  public String getMConceptName(String questionId) {
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

    }*/


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
                .where(StudentTable.INSTITUTION.eq(institution).and(StudentTable.STUDENT_GRADE.eq(gradeId))).orderBy(StudentTable.UID.asc());
        SquidCursor<StudentTable> studentCursor = db.query(StudentTable.class, studentQuery);
        if (studentCursor != null && studentCursor.getCount() > 0) {
            while (studentCursor.moveToNext()) {
                StudentTable studentTable = new StudentTable(studentCursor);
                studentIds.add(studentTable);

            }
        }
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

        mConceptList = new ArrayList<>();
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
                  //  if (!mConceptList.contains(questionTable.getMconceptName())) {
                        mConceptList.add(questionTable.getMconceptName()+"@@"+questionTable.getIdQuestion()+"@@"+questionTable.getQuestionTitle());
                    //}
                    listAllQuestions.add(questionTable);

                }

            }
        }
        return listAllQuestions;
    }

    public ArrayList<String> getAllQuestionSetTitle(int questionsetId) {


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
                    if (!listQuestionTitle.contains(questionTable.getConceptName())) {
                        listQuestionTitle.add(questionTable.getConceptName());
                    }
                }

            }
        }
        return listQuestionTitle;
    }
}
