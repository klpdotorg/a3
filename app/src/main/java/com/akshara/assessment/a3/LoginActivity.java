package com.akshara.assessment.a3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akshara.assessment.a3.NetworkRetrofitPackage.A3NetWorkCalls;
import com.akshara.assessment.a3.NetworkRetrofitPackage.CurrentStateInterface;
import com.akshara.assessment.a3.UtilsPackage.AnalyticsConstants;
import com.akshara.assessment.a3.UtilsPackage.DailogUtill;
import com.akshara.assessment.a3.UtilsPackage.RolesUtils;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;

import com.akshara.assessment.a3.db.KontactDatabase;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.FirebaseAnalyticsEvent;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends BaseActivity implements View.OnClickListener {


    EditText edtMobileNumber, edtPassword;
    Button btnLogin, btnRegister;
    private ProgressDialog progressDialog = null;
    SessionManager sessionManager;
    TextView btnForgotPassword;
    KontactDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        edtMobileNumber = findViewById(R.id.edtMobileNumber);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLoginid);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnRegister = findViewById(R.id.btnRegister);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());

     /*   edtMobileNumber.setText("9916712375");
        edtPassword.setText("test");*/

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnForgotPassword.setOnClickListener(this);
        edtMobileNumber.clearFocus();
        //DatabaseCopyHelper dbCopyHelper = new DatabaseCopyHelper(this);
        // SQLiteDatabase dbCopy = dbCopyHelper.getReadableDatabase();
        db = ((A3Application) getApplicationContext()).getDb();

    }


    public boolean validation() {
        String mobile = edtMobileNumber.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(password) || mobile.length() != 10) {
            if (TextUtils.isEmpty(mobile)) {
                edtMobileNumber.setError(getResources().getString(R.string.entermobilenumber));
                edtMobileNumber.requestFocus();
            }
            if (mobile.length() != 10) {
                edtMobileNumber.setError(getResources().getString(R.string.enter10digitmobilenumber));
                edtMobileNumber.requestFocus();
            }

            if (TextUtils.isEmpty(password)) {
                edtPassword.setError(getResources().getString(R.string.enterPassword));
                edtPassword.requestFocus();
            }

            return false;
        }
        return true;

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnLoginid:
                if (validation()) {
                    userlogin();
                }

                break;

            case R.id.btnRegister:
                startActivity(new Intent(getApplicationContext(), UserRegistrationActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
            case R.id.btnForgotPassword:

                startActivity(new Intent(getApplicationContext(), ForgotPasswordOTP.class));
                finish();
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;


        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), LanguageSelectionActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                Intent intent = new Intent(getApplicationContext(), LanguageSelectionActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showProgress(final boolean show) {
        if (show) {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);

            progressDialog.setMessage(getResources().getString(R.string.userLoging));
            progressDialog.show();
        } else {
            if (progressDialog != null) {
                if (!LoginActivity.this.isFinishing()) {
                    progressDialog.cancel();
                }
            }
        }
    }

    private void userlogin() {

        showProgress(true);
        String mobile = edtMobileNumber.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        new A3NetWorkCalls(getApplicationContext()).login(mobile, password, sessionManager.getStateKey(), new CurrentStateInterface() {
            @Override
            public void setSuccess(String userInfo) {
                showProgress(false);
                try {
                    JSONObject userLoginInfo = new JSONObject(userInfo);
                    if (userLoginInfo.has("token")) {
                        // create session

                        String users = "PR";
                        if (userLoginInfo.getString("user_type") != null &&
                                !userLoginInfo.getString("user_type").trim().equalsIgnoreCase("null")
                                && !userLoginInfo.getString("user_type").trim().equalsIgnoreCase("")) {

                            users = userLoginInfo.getString("user_type").toUpperCase();
                            sessionManager.createLoginSession(
                                    userLoginInfo.getString("first_name"),
                                    userLoginInfo.getString("id"),
                                    userLoginInfo.getString("token"),
                                    userLoginInfo.getString("last_name"),
                                    userLoginInfo.getString("email"),
                                    userLoginInfo.getString("mobile_no"),
                                    userLoginInfo.getString("dob"),
                                    users);
                            try {

                                try {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(AnalyticsConstants.App_Version, BuildConfig.VERSION_NAME);
                                    A3Application.getAnalyticsObject().logEvent("APP_VERSION", bundle);
                                } catch (Exception e) {
                                    Crashlytics.log("login");
                                }

                                // A3Application.getAnalyticsObject().logEvent(FirebaseAnalytics.Event.);

                                subscribetoTopicsForNotification(sessionManager.getState(), sessionManager.getUserType());
                            } catch (Exception e) {

                            }
                            Intent intent = new Intent(LoginActivity.this, BoundaryLoaderActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("LOGIN", true);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            finish();


                        } else {

                            try {
                                Bundle bundle = new Bundle();
                                bundle.putString(AnalyticsConstants.App_Version, BuildConfig.VERSION_NAME);
                                A3Application.getAnalyticsObject().logEvent("APP_VERSION", bundle);
                            } catch (Exception e) {
                                Crashlytics.log("Login update");
                            }
                            //update Profile
                            Intent intent = new Intent(getApplicationContext(), UpdateProfileBeforeLoginActivity.class);
                            intent.putExtra("firstName", userLoginInfo.getString("first_name"));
                            intent.putExtra("lastName", userLoginInfo.getString("last_name"));
                            intent.putExtra("mobile", userLoginInfo.getString("mobile_no"));
                            intent.putExtra("email", userLoginInfo.getString("email"));
                            intent.putExtra("token", userLoginInfo.getString("token"));

                            startActivity(intent);
                            finish();

                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void setFailed(String message) {
                showProgress(false);
                DailogUtill.showDialog(message, getSupportFragmentManager(), LoginActivity.this);
            }
        });

    }


    private void subscribetoTopicsForNotification(String state, String stateUserType) {

        try {
            FirebaseMessaging.getInstance().subscribeToTopic(state);
            FirebaseMessaging.getInstance().subscribeToTopic(state + "-" + RolesUtils.getUserRoleValueForFcmGroup(getApplicationContext(), db, stateUserType));
            FirebaseMessaging.getInstance().subscribeToTopic(state + "-" + sessionManager.getLanguage());
            FirebaseMessaging.getInstance().subscribeToTopic(state + "-" + sessionManager.getProgramFromSession().replaceAll("\\s+", ""));
            //   Toast.makeText(getApplicationContext(),state+"-"+state + ":" + RolesUtils.getUserRoleValueForFcmGroup(getApplicationContext(), db, stateUserType),Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //may be topic contains some special symbols
            Crashlytics.log("error in notification subscription in login screen");
        }
    }
}
