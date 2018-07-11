package com.akshara.assessment.a3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.akshara.assessment.a3.NetworkRetrofitPackage.A3NetWorkCalls;
import com.akshara.assessment.a3.NetworkRetrofitPackage.CurrentStateInterface;
import com.akshara.assessment.a3.UtilsPackage.DailogUtill;
import com.akshara.assessment.a3.UtilsPackage.ProgressUtil;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.UtilsPackage.SignUpResultDialogFragment;

/**
 * Created by Shridhar on 01/02/2018.
 */
public class ForgotPasswordActivity extends BaseActivity {
    private ProgressDialog progressDialog;
    EditText new_password, edtOTPNumber;
    Button btnOK;
    TextView tvMobileNumber;
    SessionManager sessionManager;
    String mobile = "";
    TextView btnResendOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        initlization();
        sessionManager = new SessionManager(getApplicationContext());
        mobile = getIntent().getStringExtra("mobile");
        tvMobileNumber.setText(mobile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        btnResendOTP.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {



                final AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                builder.setMessage(getResources().getString(R.string.do_you_want_resend_otp));
                builder.setCancelable(false);
                builder.setPositiveButton(getResources().getText(R.string.resend), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                        progressDialog.setMessage(getResources().getString(R.string.resending_otp));
                        progressDialog.show();
                        new A3NetWorkCalls(getApplicationContext()).forgotPasswordGenerateOtp(mobile, sessionManager.getStateSelection(), new CurrentStateInterface() {
                            @Override
                            public void setSuccess(String message) {

                              progressDialog.dismiss();

                            }

                            @Override
                            public void setFailed(String message) {
                                progressDialog.dismiss();
                                DailogUtill.showDialog(message, getSupportFragmentManager(), ForgotPasswordActivity.this);
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


        btnOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validation(new_password.getText().toString().trim(), edtOTPNumber.getText().toString().trim())) {
                    progressDialog.show();
                    new A3NetWorkCalls(getApplicationContext()).forgotPasswordResetWithOTP(mobile.trim(), edtOTPNumber.getText().toString().trim(), new_password.getText().toString().trim(), sessionManager.getStateSelection(), new CurrentStateInterface() {
                        @Override
                        public void setSuccess(String message) {
                            progressDialog.dismiss();
                            showSignupResultDialog(
                                    getResources().getString(R.string.app_name),
                                    getResources().getString(R.string.passwordChanged),
                                    getResources().getString(R.string.login));


                        }

                        @Override
                        public void setFailed(String message) {
                            progressDialog.dismiss();
                            DailogUtill.showDialog(message, getSupportFragmentManager(), ForgotPasswordActivity.this);
                        }
                    });

                    //      Toast.makeText(getApplicationContext(),"Coming soon",Toast.LENGTH_SHORT).show();


                }


            }
        });


    }

    private void initlization() {

        tvMobileNumber = findViewById(R.id.tvMobileNumber);
        new_password = findViewById(R.id.new_password);
        edtOTPNumber = findViewById(R.id.edtOTPNumber);
        btnOK = findViewById(R.id.btnOK);
        btnResendOTP = findViewById(R.id.btnResendOTP);
        progressDialog = ProgressUtil.showProgress(ForgotPasswordActivity.this, getResources().getString(R.string.resettingpassword));

    }


    protected void finishReset() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }


    }


    public boolean validation(String PAssword, String otp) {


        if (TextUtils.isEmpty(otp.trim())) {
            edtOTPNumber.setError(getResources().getString(R.string.error_field_required));
            return false;
        }
        if (TextUtils.isEmpty(PAssword.trim())) {
            new_password.setError(getResources().getString(R.string.error_field_required));
            return false;
        }


        return true;

    }


    private void showSignupResultDialog(String title, String message, String buttonText) {
        Bundle signUpResult = new Bundle();
        signUpResult.putString("title", title);
        signUpResult.putString("result", message);
        signUpResult.putString("buttonText", buttonText);

        SignUpResultDialogFragment resultDialog = new SignUpResultDialogFragment();
        resultDialog.setArguments(signUpResult);
        resultDialog.setCancelable(false);
        resultDialog.show(getSupportFragmentManager(), "Forgot password result");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                Intent intent = new Intent(getApplicationContext(), ForgotPasswordOTP.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }


}
