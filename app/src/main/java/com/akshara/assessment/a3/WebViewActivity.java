package com.akshara.assessment.a3;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.LocaleList;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.content.FileProvider;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.akshara.assessment.a3.TelemetryReport.TelemetryRreportActivity;
import com.akshara.assessment.a3.UtilsPackage.ConstantsA3;
import com.akshara.assessment.a3.UtilsPackage.DailogUtill;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WebViewActivity extends BaseActivity {

    WebView webView;
    String data;


   /* @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(A3Application.updateLanguage(newBase));
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView = findViewById(R.id.myWebView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.studentScore));

        data = getIntent().getStringExtra("data");
        //     webView.loadData(data, "text/html", "UTF-8");

        webView.loadDataWithBaseURL(null, data, "text/HTML", "UTF-8", null);
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                downloadhtmlToPdf();
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:


                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
  /*  public void genPdfFile() {
        String currentDateandTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());


        PrintHelper printHelper = new PrintHelper(WebViewActivity.this);
        printHelper.name(getString(R.string.app_name) + "_" + currentDateandTime);
        printHelper.html(data);
        printHelper.print(new PrintHelper.CallBack() {
            @Override
            public void onFinish(PrintHelper.RESULT result) {
                Toast.makeText(getApplicationContext(), result.name(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    public void downloadhtmlToPdf() {


        String jobName = getString(R.string.app_name) + " Document";
   /*     PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();
*/

        final PrintAttributes attributes = new PrintAttributes.Builder()
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .setMediaSize(PrintAttributes.MediaSize.ISO_A3.asLandscape())
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)

                .setResolution(new PrintAttributes.Resolution("1", "label", 300, 300))
                .build();

        File path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        android.print.PdfPrint pdfPrint = new android.print.PdfPrint(attributes);
        String currentDateandTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

        String fileName = ConstantsA3.pdftitle+"_" + currentDateandTime + ".pdf";

        pdfPrint.print(webView.createPrintDocumentAdapter(jobName), path, fileName);

        try {
            File file = new File(path + "/" + fileName);
            Intent target = new Intent(Intent.ACTION_VIEW);
              target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(WebViewActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
            // I am opening a PDF file so I give it a valid MIME type
            target.setDataAndType(uri, "application/pdf");
            // target.setDataAndType(uri, "application/pdf");

            Intent intent = Intent.createChooser(target, "Open File");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getString(R.string.oops), Toast.LENGTH_SHORT).show();
        }


    }

/*
    private void createWebPrintJob(WebView webView) {
        try {
            //creae object of print manager in your device
            String currentDateandTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());


            final PrintAttributes attrs = new PrintAttributes.Builder()
                    .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A3.asLandscape())
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)

                    .setResolution(new PrintAttributes.Resolution("1", "label", 300, 300))
                    .build();
            PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

            //create object of print adapter
            PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(getResources().getString(R.string.app_name) + "_" + currentDateandTime);

            //provide name to your newly generated pdf file
            String jobName = getString(R.string.app_name) + " Print Test";

            //open print dialog
            //  printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
            PrintJob job = printManager.print(jobName, printAdapter, attrs);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
*/

    //perform click pdf creation operation on click of print button click
    public void printPDF(View view) {

        downloadhtmlToPdf();

    }
}
