package com.akshara.assessment.a3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akshara.assessment.a3.NetworkRetrofitPackage.A3NetWorkCalls;
import com.akshara.assessment.a3.NetworkRetrofitPackage.CurrentStateInterface;
import com.akshara.assessment.a3.UtilsPackage.DailogUtill;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.UtilsPackage.SignUpResultDialogFragment;


/**
 * Created by shridhars on 2/1/2018.
 */

public class OTP_VarifyActivity extends BaseActivity {


    String mobile;
    TextView tvMobileNumber;
    EditText edtOTPNumber;
    private ProgressDialog progressDialog = null;
    SessionManager sessionManager;
    TextView tvResendOTP;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_layout);
        tvMobileNumber = findViewById(R.id.tvMobileNumber);
        edtOTPNumber = findViewById(R.id.edtOTPNumber);
        mobile = getIntent().getStringExtra("mobile");
        tvMobileNumber.setText(mobile);
        sessionManager = new SessionManager(getApplicationContext());
        Button btnOK = findViewById(R.id.btnOK);
        tvResendOTP = findViewById(R.id.tvResendOTP);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(OTP_VarifyActivity.this);
                builder.setMessage(getResources().getString(R.string.do_you_want_resend_otp));
                builder.setCancelable(false);
                builder.setPositiveButton(getResources().getText(R.string.resend), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                        showProgress(true, getResources().getString(R.string.resending_otp));
                        new A3NetWorkCalls(getApplicationContext()).forgotPasswordGenerateOtp(mobile, sessionManager.getStateSelection(), new CurrentStateInterface() {
                            @Override
                            public void setSuccess(String message) {

                                showProgress(false, getResources().getString(R.string.resending_otp));


                            }

                            @Override
                            public void setFailed(String message) {
                                showProgress(false, getResources().getString(R.string.resending_otp));

                                DailogUtill.showDialog(message, getSupportFragmentManager(), OTP_VarifyActivity.this);
                            }
                        });


                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });


               builder.create().show();
            }
        });


        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation(edtOTPNumber.getText().toString().trim())) {
                    showProgress(true, getResources().getString(R.string.authenticating));

                    new A3NetWorkCalls(getApplicationContext()).varifyOTPAfterSignup(mobile, edtOTPNumber.getText().toString().trim(), sessionManager.getStateSelection(), new CurrentStateInterface() {
                        @Override
                        public void setSuccess(String message) {
                            showProgress(false, getResources().getString(R.string.authenticating));
                            showSignupResultDialog(
                                    getResources().getString(R.string.app_name),
                                    getResources().getString(R.string.signupsuccess),
                                    getResources().getString(R.string.login));
                        }

                        @Override
                        public void setFailed(String message) {
                            showProgress(false, getResources().getString(R.string.authenticating));
                            DailogUtill.showDialog(message, getSupportFragmentManager(), OTP_VarifyActivity.this);
                        }
                    });

                }

            }
        });


    }


    private void showSignupResultDialog(String title, String message, String buttonText) {


        Bundle signUpResult = new Bundle();
        signUpResult.putString("title", title);
        signUpResult.putString("result", message);
        signUpResult.putString("buttonText", buttonText);

        SignUpResultDialogFragment resultDialog = new SignUpResultDialogFragment();
        resultDialog.setArguments(signUpResult);
        resultDialog.setCancelable(false);
        try {
            resultDialog.show(getSupportFragmentManager(), "Registration result");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }

    }

    public boolean validation(String edtmobile) {
        if (TextUtils.isEmpty(edtmobile.trim())) {

            edtOTPNumber.setError(getResources().getString(R.string.pleaseEnterValidOTP));
            edtOTPNumber.requestFocus();
            return false;

        } else
            return true;


    }

    private void showProgress(final boolean show, String msg) {
        if (show) {
            progressDialog = new ProgressDialog(OTP_VarifyActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);

            progressDialog.setMessage(msg);
            progressDialog.show();
        } else {
            if (progressDialog != null) {
                if (!OTP_VarifyActivity.this.isFinishing()) {
                    if (progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        alert();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:


                alert();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void alert() {
       android.support.v7.app.AlertDialog alertDailog = new android.support.v7.app.AlertDialog.Builder(OTP_VarifyActivity.this).create();

        alertDailog.setCancelable(false);
        alertDailog.setMessage(getResources().getString(R.string.do_you_want_to_go_back));
        alertDailog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.response_positive),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        finish();
                        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

                    }
                });
        alertDailog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.response_negative),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDailog.show();
    }
}
