package com.akshara.assessment.a3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akshara.assessment.a3.NetworkRetrofitPackage.A3NetWorkCalls;
import com.akshara.assessment.a3.NetworkRetrofitPackage.CurrentStateInterface;
import com.akshara.assessment.a3.Pojo.LanguagePojo;
import com.akshara.assessment.a3.Pojo.Program;
import com.akshara.assessment.a3.Pojo.ProgramPojo;
import com.akshara.assessment.a3.Pojo.StatePojo;
import com.akshara.assessment.a3.UtilsPackage.AnalyticsConstants;
import com.akshara.assessment.a3.UtilsPackage.ProgressUtil;
import com.akshara.assessment.a3.UtilsPackage.RolesUtils;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.UtilsPackage.SignUpResultDialogFragment;
import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.State;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;

/**
 * Created by shridhars on 2/2/2018.
 */

public class LanguageSelectionActivity extends BaseActivity {


    Button btnOK, btnNext;
    Spinner spnSelectStae, spnSelectLanguage, spnProgram;
    SessionManager sessionManager;

    private KontactDatabase db;
    private ArrayList<StatePojo> stateList;
    private ArrayList<LanguagePojo> languageList;
    ArrayAdapter statelistAdp;
    boolean flag = false;
    TextView tv1, tv2;
    boolean qFlag = false;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_setting_layout);
        btnNext = findViewById(R.id.btnNext);
        sessionManager = new SessionManager(getApplicationContext());
        spnSelectLanguage = findViewById(R.id.spnSelectLanguage);
        spnSelectStae = findViewById(R.id.spnSelectStae);
        db = ((A3Application) getApplicationContext().getApplicationContext()).getDb();
        spnProgram = findViewById(R.id.spnProgram);



        getSupportActionBar().setTitle(getResources().getString(R.string.string_select_language));
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv1.setText(getResources().getString(R.string.selectStateForLanguage));
        tv2.setText(getResources().getString(R.string.selectLanguageForLanguage));
        progressDialog = ProgressUtil.showProgress(LanguageSelectionActivity.this, getResources().getString(R.string.authenticating));
        stateList = new ArrayList<>();
        languageList = new ArrayList<>();


        if (sessionManager.getState().equalsIgnoreCase("no") || sessionManager.getLanguage().equalsIgnoreCase("no")
                || sessionManager.getLanguageKey().equalsIgnoreCase("no") || sessionManager.getStateKey().equalsIgnoreCase("")) {
            getState(false);
        } else {
            getState(true);


        }


        spnSelectStae.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {

                if (position != 0) {

                    if (flag == true) {
                        flag = false;
                    } else {
                        // Toast.makeText(getApplicationContext(),stateList.size()+"",Toast.LENGTH_SHORT).show();
                        getLanguage(stateList.get(spnSelectStae.getSelectedItemPosition()).getStateKey(),
                                stateList.get(spnSelectStae.getSelectedItemPosition()).getLanguage(),

                                stateList.get(spnSelectStae.getSelectedItemPosition()).getLanguage(),
                                stateList.get(spnSelectStae.getSelectedItemPosition()).getLangKey(),
                                false);
                    }

                } else {
                    statelistAdp = new ArrayAdapter(LanguageSelectionActivity.this, R.layout.spinnertextview, new ArrayList());
                    spnSelectLanguage.setAdapter(statelistAdp);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        btnOK = findViewById(R.id.btnOK);


        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (spnSelectLanguage.getSelectedItemPosition() > 0)

                {

                    if (spnProgram.getSelectedItemPosition() > 0) {
                        final String language = languageList.get(spnSelectLanguage.getSelectedItemPosition()).getLanguageEng();
                        final String languagekey = languageList.get(spnSelectLanguage.getSelectedItemPosition()).getKey();
                        final String state = stateList.get(spnSelectStae.getSelectedItemPosition()).getState();
                        final String stateKeyString = stateList.get(spnSelectStae.getSelectedItemPosition()).getStateKey();

                        String url = BuildConfig.HOST + "/api/v1/boundary/admin1s/?per_page=0&state=" + stateKeyString;
                        progressDialog.show();

                        new A3NetWorkCalls(LanguageSelectionActivity.this).downloadDistrictForState(url, stateKeyString, new CurrentStateInterface() {
                            @Override
                            public void setSuccess(String message) {


                                progressDialog.dismiss();
                                sessionManager.setLanguage(state, language, languagekey, stateKeyString);
                                sessionManager.setStateSelection(stateKeyString);
                                sessionManager.setStatePosition(spnSelectStae.getSelectedItemPosition());
                                sessionManager.setProgram(spnProgram.getSelectedItem().toString().trim());
                                sessionManager.setProgramId(((ProgramPojo) spnProgram.getSelectedItem()).getId());
                                // Toast.makeText(getApplicationContext(),stateKey+":"+languagekey,Toast.LENGTH_SHORT).show();
                                A3Application.setLanguage(getApplicationContext(), languagekey);

                                sessionManager.setLanguagePosition(spnSelectLanguage.getSelectedItemPosition());

                                try {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(AnalyticsConstants.State, sessionManager.getState());
                                    bundle.putString(AnalyticsConstants.Language, sessionManager.getLanguage());
                                    bundle.putString(AnalyticsConstants.Program, sessionManager.getProgramFromSession());
                                    A3Application.getAnalyticsObject().logEvent("LANGUAGE_SELECTION",bundle);
                                }
                                catch (Exception e)
                                {
                                    Crashlytics.log("Analytics exception in language Selection");
                                }




                                Intent intent = new Intent(new Intent(getApplicationContext(), LoginActivity.class));
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);



                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                  //  ActivityCompat.finishAffinity(LanguageSelectionActivity.this);
                                    LanguageSelectionActivity.this.finish();
                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);



                                } else {
                                    finish();
                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                }

                            }

                            @Override
                            public void setFailed(String message) {
                                progressDialog.dismiss();
                                showSignupResultDialog(getResources().getString(R.string.app_name), message, getResources().getString(R.string.Ok));
                            }
                        });


                    } else {
                        showSignupResultDialog(
                                getResources().getString(R.string.app_name),
                                getResources().getString(R.string.pleaseSelectProgram),
                                getResources().getString(R.string.Ok_));
                    }

                } else {


                    showSignupResultDialog(
                            getResources().getString(R.string.app_name),
                            getResources().getString(R.string.pleaseSelectsurveyLanguage),
                            getResources().getString(R.string.Ok_));

                }


            }
        });

    }


    @Override
    public void onBackPressed() {

        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            try {


                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                super.onBackPressed();
                startActivity(a);
                return;
            } catch (Exception e) {

            }
        } else {
            Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

    public void getState(boolean b) {
        Query listStateQuery = Query.select().from(State.TABLE).orderBy(State.STATE.asc());
        SquidCursor<State> stateCursor = db.query(State.class, listStateQuery);

        if (stateCursor.getCount() > 0) {
            // we have surveys in DB, get them
            try {
                while (stateCursor.moveToNext()) {
                    State state = new State(stateCursor);
                    StatePojo pojo = new StatePojo(state.getState(), state.getStateLocText(), state.getStatekey(), state.getLangKey(), state.getLangName());
                    stateList.add(pojo);
                }
                // Toast.makeText(getApplicationContext(),stateList.size()+":"+b,Toast.LENGTH_SHORT).show();
            } finally {
                if (stateCursor != null) {
                    stateCursor.close();
                }
            }
            StatePojo pojo = new StatePojo(getString(R.string.selectYourState), getString(R.string.selectYourState), "0", "", "");
            stateList.add(0, pojo);

            if (stateList != null && stateList.size() > 1) {
                statelistAdp = new ArrayAdapter(this, R.layout.spinnertextview, stateList);
                spnSelectStae.setAdapter(statelistAdp);


                if (b) {
                    for (int i = 0; i < stateList.size(); i++) {
                        if (sessionManager.getStateKey().equalsIgnoreCase(stateList.get(i).getStateKey())) {
                            spnSelectStae.setSelection(i);
                            getLanguage(stateList.get(spnSelectStae.getSelectedItemPosition()).getStateKey(),
                                    stateList.get(spnSelectStae.getSelectedItemPosition()).getLanguage(),

                                    stateList.get(spnSelectStae.getSelectedItemPosition()).getLanguage(),
                                    stateList.get(spnSelectStae.getSelectedItemPosition()).getLangKey(),
                                    true);
                            flag = true;

                            //   Toast.makeText(getApplicationContext(),stateList.get(i).getState(),Toast.LENGTH_SHORT).show();
                            break;
                        }


                    }


                }
            }

        }

    }
public void setProgramToSpinner()
{
    String stateKey=stateList.get(spnSelectStae.getSelectedItemPosition()).getStateKey();
    //Toast.makeText(getApplicationContext(),stateKey,Toast.LENGTH_SHORT).show();
    ArrayList<ProgramPojo> dataList = RolesUtils.getProgramData(db,stateKey);
    ProgramPojo pojo = new ProgramPojo();
    pojo.setId(0);
    pojo.setProgramName(getResources().getString(R.string.select_program));
    dataList.add(0, pojo);
    spnProgram.setAdapter(new ArrayAdapter(LanguageSelectionActivity.this, R.layout.spinnertextview, dataList));

}


    public void getLanguage(String stateKey, String langName, String langLocName, String langKey, boolean b) {

        languageList.clear();

        LanguagePojo pojo = new LanguagePojo(stateKey, langName, langLocName, langKey);
        languageList.add(pojo);


        pojo = new LanguagePojo("", getResources().getString(R.string.selectYourLanguage), getResources().getString(R.string.selectYourLanguage), "en");
        languageList.add(0, pojo);
        pojo = new LanguagePojo("", getResources().getString(R.string.english), getResources().getString(R.string.english), "en");
        languageList.add(1, pojo);
       /* pojo = new LanguagePojo("", getResources().getString(R.string.urdu), getResources().getString(R.string.urdu), "eng");
        languageList.add(2, pojo);
     */   spnSelectLanguage.setAdapter(new ArrayAdapter(this, R.layout.spinnertextview, languageList));
        setProgramToSpinner();

        if (b)

        {


            for (int i = 1; i < languageList.size(); i++) {

                if (sessionManager.getLanguageKey().equalsIgnoreCase(languageList.get(i).getKey())) {

                    // Toast.makeText(getApplicationContext(), i + "mm", Toast.LENGTH_SHORT).show();
                    spnSelectLanguage.setSelection(i);
                    A3Application.setLanguage(getApplicationContext(), sessionManager.getLanguageKey());

                    btnNext.setEnabled(true);
                    break;
                }
            }
        }


    }


    private void showSignupResultDialog(String title, String message, String buttonText) {
        Bundle signUpResult = new Bundle();
        signUpResult.putString("title", title);
        signUpResult.putString("result", message);
        signUpResult.putString("buttonText", buttonText);

        SignUpResultDialogFragment resultDialog = new SignUpResultDialogFragment();
        resultDialog.setArguments(signUpResult);
        resultDialog.setCancelable(false);
        resultDialog.show(getSupportFragmentManager(), "Registration result");
    }


}