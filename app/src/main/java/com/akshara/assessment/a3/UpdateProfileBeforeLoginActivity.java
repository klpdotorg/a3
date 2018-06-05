package com.akshara.assessment.a3;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akshara.assessment.a3.NetworkRetrofitPackage.A3NetWorkCalls;
import com.akshara.assessment.a3.NetworkRetrofitPackage.CurrentStateInterface;
import com.akshara.assessment.a3.UtilsPackage.DailogUtill;
import com.akshara.assessment.a3.UtilsPackage.RolesUtils;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.UtilsPackage.SignUpResultDialogFragment;
import com.akshara.assessment.a3.db.KontactDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * Created by shridhars on 06/04/2018.
 */

public class UpdateProfileBeforeLoginActivity extends BaseActivity {

    ImageView calBtn;
    ProgressDialog progressDialog;
    EditText edtFirstName, edtLastName, edtDob, edtEmail;
    int cyear, cmonth, cdate;

    private LinkedHashMap<String, String> userType;
    private KontactDatabase db;
    SessionManager sessionManager;
    String emailValue;
    Button btnUpdate;
    private Spinner spnRespondantType;
    String token;
    int flag = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);
        this.setTitle(getResources().getString(R.string.updateProfile));

        calBtn = findViewById(R.id.calBtn);
        edtDob = findViewById(R.id.edtDob);
        edtFirstName = findViewById(R.id.edtFirstName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        spnRespondantType = findViewById(R.id.spnRespondantType);
        sessionManager = new SessionManager(getApplicationContext());
        String stateKey = sessionManager.getStateSelection();
        btnUpdate = findViewById(R.id.btnUpdate);

        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        token = getIntent().getStringExtra("token");
        //  token = "9a5817a691d1e6d1b6fabc5b0400650e505e8b36";
        String email = getIntent().getStringExtra("email");
        if (email != null && !email.equalsIgnoreCase("null")) {
            edtEmail.setText(email);
        }

        if (firstName != null && !firstName.equalsIgnoreCase("null")) {
            edtFirstName.setText(firstName);
        }

        if (lastName != null && !lastName.equalsIgnoreCase("null")) {
            edtLastName.setText(lastName);
        }


        // edtDob.setText(getDDMMYYdate(sessionManager.getDOB()));
        cyear = 1988;
        cdate = 1;
        cmonth = 0;

        spnRespondantType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard(UpdateProfileBeforeLoginActivity.this);
                return false;
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validation();
            }
        });

        calBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setdate(null, cyear, cmonth, cdate);
            }
        });

        db = ((A3Application) getApplicationContext()).getDb();

        userType = new LinkedHashMap<String, String>();
        userType.put(getString(R.string.pleaseSelectrespondanttype), getString(R.string.pleaseSelectrespondanttype));
        userType.putAll(RolesUtils.getUserRoles(getApplicationContext(), db, stateKey));


        if (userType != null && userType.size() > 0) {
            List<String> userTypeNames = new ArrayList<>();
            userTypeNames.addAll(userType.keySet());
            ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<String>(UpdateProfileBeforeLoginActivity.this, R.layout.regspinner, userTypeNames);
            spnRespondantType.setAdapter(userTypeAdapter);


        }


    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            emailValue = "";
            return true;
        } else {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {   //its an email id
                return true;
            } else {

                // Generic Message
                edtEmail.setError(getResources().getString(R.string.enter_valid_email));
                return false;
                //Please provide valid Email or phone number

            }
        }
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

    private void setdate(DatePicker view, int y, int m, int d) {
        DatePickerDialog dpd;

        dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edtDob.setText(String.format("%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year));
                cyear = year;
                cdate = dayOfMonth;
                cmonth = monthOfYear;
                //   ReqDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                edtDob.setError(null);
            }
        }, y, m, d);
        try {
            // dpd.getDatePicker().setMaxDate(new Date().getTime());
            Calendar maxCal = Calendar.getInstance();
            maxCal.set(Calendar.YEAR, maxCal.get(Calendar.YEAR));
            dpd.getDatePicker().setMaxDate(maxCal.getTimeInMillis() - 1000);
            // dpd.getDatePicker().setMinDate();
        } catch (Exception e) {
        }
        dpd.show();
    }


    public void validation() {
        View focusView = null;
        boolean cancel = false;
        String dateofBirth = edtDob.getText().toString().trim();

        if (TextUtils.isEmpty(edtFirstName.getText().toString().trim())) {
            edtFirstName.setError(getResources().getString(R.string.error_field_required));
            focusView = edtFirstName;
            cancel = true;
        } else if (TextUtils.isEmpty(edtLastName.getText().toString().trim())) {
            edtLastName.setError(getResources().getString(R.string.error_field_required));
            focusView = edtLastName;
            cancel = true;
        } else if (spnRespondantType.getSelectedItemPosition() == 0) {
            // spnRespondantType.setError(getResources().getString(R.string.pleaseSelectrespondanttype));
            TextView errorText = (TextView) spnRespondantType.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(getResources().getString(R.string.pleaseSelectrespondanttype));
            focusView = spnRespondantType;
            cancel = true;
        } else if (TextUtils.isEmpty(dateofBirth)) {
            edtDob.setError(getResources().getString(R.string.error_field_required));
            focusView = edtDob;
            cancel = true;
        } else if (!isValidFormat(dateofBirth)) {
            edtDob.setError(getResources().getString(R.string.enterValidDate));
            focusView = edtDob;
            cancel = true;
        } else if (checkCalendarDate(dateofBirth)) {
            edtDob.setError("You cannot enter a date in the future");
            focusView = edtDob;
            cancel = true;
        } else if (!isEmailValid(edtEmail.getText().toString().trim())) {
            //emailWidget.setError("This email address is invalid");
            focusView = edtEmail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();

        } else {

            showProgress(true, getResources().getString(R.string.updating_profile));
            new A3NetWorkCalls(getApplicationContext()).setProfileUpdateAction(edtFirstName.getText().toString().trim(),
                    edtLastName.getText().toString().trim(), edtEmail.getText().toString().trim(), getRevDate(edtDob.getText().toString().trim()),
                    userType.get(spnRespondantType.getSelectedItem().toString()), "Token " + token, sessionManager.getStateSelection(), new CurrentStateInterface() {
                        @Override
                        public void setSuccess(final String message) {
                            showProgress(false, "");
                            //showSignupResultDialog(getString(R.string.app_name),,getResources().getString(R.string.Ok));
                            storeInSession(message, sessionManager.getStateSelection(), "Token " + token);
                            // DailogUtill.showDialog(getResources().getString(R.string.profileUpdatedSuccessfully) + "\n" + getString(R.string.pleaseLoginandcontue), getSupportFragmentManager(), getApplicationContext());
                            android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(UpdateProfileBeforeLoginActivity.this).create();
                            alertDialog.setCancelable(false);
                            alertDialog.setMessage(getResources().getString(R.string.profileUpdatedSuccessfully));
                            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.response_neutral),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent intent = new Intent(UpdateProfileBeforeLoginActivity.this, BoundaryLoaderActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("LOGIN", true);

                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                            finish();

                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();


                            //parse the userInfo String

                            //downloadSurveyInfo(message, "");


                        }

                        @Override
                        public void setFailed(String message) {
                            showProgress(false, "");
                            //showSignupResultDialog(getString(R.string.app_name),message,getResources().getString(R.string.Ok));
                            DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());


                        }
                    });
        }
    }

    private void storeInSession(final String userInfo, String stateKey, final String tokenlo) {
        try {
            JSONObject userLoginInfo = new JSONObject(userInfo);

            // create session
            String users = "PR";
            if (userLoginInfo.getString("user_type") != null &&
                    !userLoginInfo.getString("user_type").trim().equalsIgnoreCase("null")) {
                users = userLoginInfo.getString("user_type").toUpperCase();
            }

            sessionManager.createLoginSession(
                    userLoginInfo.getString("first_name"),
                    userLoginInfo.getString("id"),
                    token,
                    userLoginInfo.getString("last_name"),
                    userLoginInfo.getString("email"),
                    userLoginInfo.getString("mobile_no"),
                    userLoginInfo.getString("dob"),
                    users);

            showProgress(false, "");


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Login to continue", Toast.LENGTH_SHORT).show();
            finish();
            showProgress(false, "");
        }
    }


    public String getRevDate(String strDate) {
        String newstring = "1980-01-01";
        try {
            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(strDate);


            newstring = new SimpleDateFormat("yyyy-MM-dd").format(date1);
        } catch (Exception e) {

        }

        return newstring;
    }

    public static boolean isValidFormat(String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
                //  Log.d("Sriii","if");
                sdf = new SimpleDateFormat("d-M-yyyy");
                date = sdf.parse(value);
                if (!value.equals(sdf.format(date))) {
                    date = null;
                }
            }

        } catch (Exception ex) {
            //  Log.d("Sriii","exccee");
            ex.printStackTrace();
        }
        return date != null;
    }


    public boolean checkCalendarDate(String strDate) {
        boolean flag = false;
        try {
            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(strDate);
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date1);
            flag = !cal1.after(cal2);

        } catch (Exception e) {
//Toast.makeText(getApplicationContext(),"Exception",Toast.LENGTH_SHORT).show();
        }
        return flag;

    }

    public String getDDMMYYdate(String date) {

        String newstring = "01-01-1998";
        try {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);


            newstring = new SimpleDateFormat("dd-MM-yyyy").format(date1);
        } catch (Exception e) {

        }

        return newstring;
    }


    private void showProgress(final boolean show, String message) {
        if (show) {
            progressDialog = new ProgressDialog(UpdateProfileBeforeLoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(message);
            progressDialog.show();
            progressDialog.setCancelable(false);
        } else {
            if (progressDialog != null) progressDialog.cancel();
        }
    }


}
