package com.akshara.assessment.a3;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_layout);
        tvMobileNumber = findViewById(R.id.tvMobileNumber);
        edtOTPNumber = findViewById(R.id.edtOTPNumber);
        mobile = getIntent().getStringExtra("mobile");
        tvMobileNumber.setText(mobile);
        sessionManager=new SessionManager(getApplicationContext());
        Button btnOK = findViewById(R.id.btnOK);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation(edtOTPNumber.getText().toString().trim())) {
                    showProgress(true);

                    new A3NetWorkCalls(getApplicationContext()).varifyOTPAfterSignup(mobile, edtOTPNumber.getText().toString().trim(),sessionManager.getStateSelection(), new CurrentStateInterface() {
                        @Override
                        public void setSuccess(String message) {
                            showProgress(false);
                            showSignupResultDialog(
                                    getResources().getString(R.string.app_name),
                                    getResources().getString(R.string.signupsuccess),
                                    getResources().getString(R.string.login));
                        }

                        @Override
                        public void setFailed(String message) {
                            showProgress(false);
                            DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
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

    private void showProgress(final boolean show) {
        if (show) {
            progressDialog = new ProgressDialog(OTP_VarifyActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);

            progressDialog.setMessage(getResources().getString(R.string.authenticating));
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
    }
}
