package com.akshara.assessment.a3.NetworkRetrofitPackage;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.akshara.assessment.a3.A3Application;
import com.akshara.assessment.a3.AssessmentPojoPack.AssessmentPojoRes;
import com.akshara.assessment.a3.BlocksPojo.BlockDetailPojo;
import com.akshara.assessment.a3.ClusterPojos.ClusterDetailPojo;
import com.akshara.assessment.a3.DistrictPojos.DistrictPojos;
import com.akshara.assessment.a3.ErrorHandlingPack.A3Errors;
import com.akshara.assessment.a3.ErrorPojoPackage.ForgotOTPError;
import com.akshara.assessment.a3.ErrorPojoPackage.InvalidOTp;
import com.akshara.assessment.a3.ErrorPojoPackage.LoginErrorPojo;
import com.akshara.assessment.a3.Pojo.AuthKeyPojo;
import com.akshara.assessment.a3.Pojo.ForgotPassswordOtpPojo;

import com.akshara.assessment.a3.Pojo.Program;
import com.akshara.assessment.a3.Pojo.ProgramsResPojo;
import com.akshara.assessment.a3.Pojo.QuestionSetPojo;
import com.akshara.assessment.a3.Pojo.RegisterStudentPojo;
import com.akshara.assessment.a3.Pojo.ResetPasswordPojo;
import com.akshara.assessment.a3.Pojo.StudentExistsPojo;
import com.akshara.assessment.a3.Pojo.Subject;
import com.akshara.assessment.a3.QuestionSetPojos.Question;
import com.akshara.assessment.a3.QuestionSetPojos.QuestionSetPojos;
import com.akshara.assessment.a3.QuestionSetPojos.Questiondatum;
import com.akshara.assessment.a3.QuestionSetPojos.Questionset;
import com.akshara.assessment.a3.R;
import com.akshara.assessment.a3.SchoolDataPojo.Grade;
import com.akshara.assessment.a3.SchoolDataPojo.SchoolDataPojo;
import com.akshara.assessment.a3.StudentPojopack.Result;
import com.akshara.assessment.a3.StudentPojopack.SchoolStudentPojo;
import com.akshara.assessment.a3.UserRolePack.UserRolesPojos;
import com.akshara.assessment.a3.UtilsPackage.SchoolStateInterface;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.db.AssessmentTypeTable;
import com.akshara.assessment.a3.db.Boundary;
import com.akshara.assessment.a3.db.InstititeGradeIdTable;
import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.ProgramTable;
import com.akshara.assessment.a3.db.QuestionDataTable;
import com.akshara.assessment.a3.db.QuestionSetDetailTable;
import com.akshara.assessment.a3.db.QuestionSetTable;
import com.akshara.assessment.a3.db.QuestionTable;
import com.akshara.assessment.a3.db.Respondent;
import com.akshara.assessment.a3.db.School;
import com.akshara.assessment.a3.db.State;
import com.akshara.assessment.a3.db.StudentTable;
import com.akshara.assessment.a3.db.SubjectTable;
import com.akshara.assessment.a3.regstdrespPojo.RegisterStdPojoResp;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;
import com.yahoo.squidb.sql.Update;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class A3NetWorkCalls {


    Context context;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;
    private KontactDatabase db;
    double schoolCountp = 0;

    public A3NetWorkCalls(Context context) {
        this.context = context;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        db = ((A3Application) context.getApplicationContext()).getDb();
        double schoolCountp = 0;
    }


    public void login(String mobile, String password, String stateKey, final CurrentStateInterface currentStateInterface) {

        apiInterface.userLoginService(mobile, password, stateKey, "a3").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        currentStateInterface.setSuccess(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                } else if (response.code() == 401) {

                    currentStateInterface.setFailed(A3Errors.loginErrorHandle(response.errorBody()).getDetail());
                } else if (response.code() == 400) {
                    //email or password invalid
                    currentStateInterface.setFailed(A3Errors.loginErrorHandle(response.errorBody()).getNonFieldErrors().get(0));
                } else if (response.code() == 500) {
                    //email or password invalid
                    currentStateInterface.setFailed(funInternalServerError());
                } else {
                    //email or password invalid
                    currentStateInterface.setFailed(context.getResources().getString(R.string.oops));
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                currentStateInterface.setFailed(getFailureMessage(t));
            }
        });


    }


    public void forgotPasswordGenerateOtp(String mobilenumber, String stateKey, final CurrentStateInterface stateInterface) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.generateOtpForForgotPassword(mobilenumber, stateKey).enqueue(new Callback<ForgotPassswordOtpPojo>() {
            @Override
            public void onResponse(Call<ForgotPassswordOtpPojo> call, Response<ForgotPassswordOtpPojo> response) {
                if (response.isSuccessful()) {
                    stateInterface.setSuccess(response.body().getSuccess());
                } else {
                    if (response.code() == 200) {
                        stateInterface.setSuccess("Sent OTP");
                    } else if (response.code() == 404) {
                        //user not registered in ilp
                        Gson gson = new Gson();
                        ForgotOTPError forgotpassword = gson.fromJson(response.errorBody().charStream(), ForgotOTPError.class);
                        if (forgotpassword != null && forgotpassword.getDetail() != null) {
                            stateInterface.setFailed(forgotpassword.getDetail());
                        } else {
                            stateInterface.setFailed(context.getResources().getString(R.string.oops));
                        }

                    } else if (response.code() == 500) {
                        //email or password invalid
                        stateInterface.setFailed(funInternalServerError());
                    } else {
                        stateInterface.setFailed(context.getResources().getString(R.string.oops));
                    }
                }
            }

            @Override
            public void onFailure(Call<ForgotPassswordOtpPojo> call, Throwable t) {

                stateInterface.setFailed(getFailureMessage(t));

            }
        });

    }


    public void forgotPasswordResetWithOTP(String mobile, String otp, String newPassword, String stateKey, final CurrentStateInterface stateInterface) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.forgotPasswordResetWithOTP(mobile, otp, newPassword, stateKey).enqueue(new Callback<ResetPasswordPojo>() {
            @Override
            public void onResponse(Call<ResetPasswordPojo> call, Response<ResetPasswordPojo> response) {

                if (response.isSuccessful() && response.code() == 200) {
                    stateInterface.setSuccess(response.body().getSuccess());

                } else if (response.code() == 404) {
                    Gson gson = new Gson();
                    ForgotOTPError forgotpassword = gson.fromJson(response.errorBody().charStream(), ForgotOTPError.class);
                    if (forgotpassword != null && forgotpassword.getDetail() != null) {
                        stateInterface.setFailed(forgotpassword.getDetail());
                    } else {
                        stateInterface.setFailed(context.getResources().getString(R.string.oops));
                    }

                } else if (response.code() == 500) {
                    //email or password invalid
                    stateInterface.setFailed(funInternalServerError());
                } else {
                    stateInterface.setFailed(context.getResources().getString(R.string.oops));
                }


            }

            @Override
            public void onFailure(Call<ResetPasswordPojo> call, Throwable t) {

                stateInterface.setFailed(getFailureMessage(t));

            }
        });

    }

    public void varifyOTPAfterSignup(String mobile, String otp, String stateKey, final CurrentStateInterface stateInterface) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.otpSignUp(mobile, otp, stateKey).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String message = context.getResources().getString(R.string.invalidOTP);
                if (response.isSuccessful()) {
                    stateInterface.setSuccess("success");
                } else if (response.code() == 404) {
                    //invalid otp
                    Gson gson = new Gson();
                    InvalidOTp invalidOTp = gson.fromJson(response.errorBody().charStream(), InvalidOTp.class);
                    if (invalidOTp != null) {
                        message = invalidOTp.getDetail();
                    }
                    stateInterface.setFailed(message);


                } else if (response.code() == 500) {
                    //email or password invalid
                    stateInterface.setFailed(funInternalServerError());
                } else {
                    stateInterface.setFailed(context.getResources().getString(R.string.oops) + "");
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                stateInterface.setFailed(getFailureMessage(t));
            }
        });


    }


    public void getStateAndUserDeail(final CurrentStateInterface stateInterface) {

        apiInterface.getStateDeailFromNetwork().enqueue(new Callback<UserRolesPojos>() {
            @Override
            public void onResponse(Call<UserRolesPojos> call, Response<UserRolesPojos> response) {

                JSONObject jsonObject;
                String stringJsonData;
                if (response.code() == 200 && response.isSuccessful()) {


                    parseStateDeailIntoDb(response);
                    stateInterface.setSuccess("success");


                } else if (response.code() == 500) {
                    //email or password invalid
                    stateInterface.setFailed(funInternalServerError());
                } else {

                    stateInterface.setFailed(context.getResources().getString(R.string.stateLoadingFailed));
                }


            }

            @Override
            public void onFailure(Call<UserRolesPojos> call, Throwable t) {
                stateInterface.setFailed(getFailureMessage(t));
            }
        });


    }


    public void parseStateDeailIntoDb(Response<UserRolesPojos> jsonObject) {

        if (jsonObject != null & jsonObject.body() != null && jsonObject.body().getResults() != null) {

            db.deleteAll(State.class);
            db.deleteAll(Respondent.class);
        }

        for (int i = 0; i < jsonObject.body().getResults().size(); i++) {
            //then it will be state detail


            String stateCode = jsonObject.body().getResults().get(i).getStateCode();
            if (!stateCode.equalsIgnoreCase("ilp")) {
                String stateLongForm = jsonObject.body().getResults().get(i).getLongForm();
                State state = new State();
                state.setState(jsonObject.body().getResults().get(i).getName());
                state.setStateLocText(jsonObject.body().getResults().get(i).getName());
                state.setStatekey(stateCode);
                state.setLangName(jsonObject.body().getResults().get(i).getLangName());
                state.setLangKey(jsonObject.body().getResults().get(i).getLangKey());
                db.insertNew(state);

                //loading user roles
                Respondent respondent = new Respondent();

                for (int j = 0; j < jsonObject.body().getResults().get(i).getRespondentTypes().size(); j++) {
                    respondent = new Respondent();
                    String key = jsonObject.body().getResults().get(i).getRespondentTypes().get(j).getCharId();
                    if (!key.equalsIgnoreCase("UK")) {
                        respondent.setRoleKEY(key);
                        respondent.setName(jsonObject.body().getResults().get(i).getRespondentTypes().get(j).getName());
                        respondent.setNamLoc(jsonObject.body().getResults().get(i).getRespondentTypes().get(j).getName());
                        respondent.setStateKey(stateCode);
                        boolean b = db.insertWithId(respondent);
                    }
                }


            }

        }


    }


    public void downloadDistrictForState(String url, final String stateKey, final CurrentStateInterface stateInterface) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        apiInterface.getAllDistrictData(url).enqueue(new Callback<DistrictPojos>() {
            @Override
            public void onResponse(Call<DistrictPojos> call, Response<DistrictPojos> response) {

                if (response.isSuccessful()) {
                    AddDataToDistrcit(response, stateKey, stateInterface);

                } else if (response.code() == 500) {
                    //email or password invalid
                    stateInterface.setFailed(funInternalServerError());
                } else {
                    stateInterface.setFailed(context.getResources().getString(R.string.districtDataLoadingFailed));

                }

            }

            @Override
            public void onFailure(Call<DistrictPojos> call, Throwable t) {
                stateInterface.setFailed(getFailureMessage(t));
            }
        });


    }

    public String getFailureMessage(Throwable t) {
        if (t instanceof IOException) {

            return context.getResources().getString(R.string.netWorkError);
            // logging probably not necessary
        } else {
            return context.getResources().getString(R.string.oops);
        }


    }

    public void AddDataToDistrcit(Response<DistrictPojos> response, String stateKey, CurrentStateInterface stateInterface) {

        for (int i = 0; i < response.body().getResults().size(); i++) {

            Boundary boundary = new Boundary();
            if (response.body().getResults().get(i).getType().equalsIgnoreCase("primary")) {
                boundary.setId(response.body().getResults().get(i).getId());
                boundary.setParentId(1L);
                boundary.setName(response.body().getResults().get(i).getName());
                boundary.setHierarchy("district");
                boundary.setType("primaryschool");
                boundary.setStateKey(stateKey);
                boundary.setIsFlag(false);
                boundary.setIsFlagCB(false);
                String locName = response.body().getResults().get(i).getLangName();
                if (locName == null)
                    boundary.setLocName(response.body().getResults().get(i).getName());
                else
                    boundary.setLocName(locName);

                try {
                    db.insertNew(boundary);
                } catch (Exception e) {
                    db.persist(boundary);
                }
            }
            // Log.d("w", i + "");
        }
        if (response.body().getNext() != null) {
            downloadDistrictForState(response.body().getNext().toString(), stateKey, stateInterface);
            //Toast.makeText(getApplicationContext(), "next", Toast.LENGTH_SHORT).show();

        } else {

            stateInterface.setSuccess(getDistrictCount(stateKey) + ":" + context.getResources().getString(R.string.districtDownloaded));

        }

    }

    public int getDistrictCount(String stateKey) {
        Query listDistQuery = Query.select().from(Boundary.TABLE).where(Boundary.STATE_KEY.eqCaseInsensitive(stateKey).and(Boundary.PARENT_ID.eq(1)));
        SquidCursor<Boundary> distCursor = db.query(Boundary.class, listDistQuery);

        if (distCursor != null) {
            return distCursor.getCount();
        }
        return 0;


    }


    public void DownloadSchoolData(String url, final long clusterId, final long distId, final String token, final long blockId, final SchoolStateInterface stateInterface) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        apiInterface.getAllSchoolsData(url, token).enqueue(new Callback<SchoolDataPojo>() {
            @Override
            public void onResponse(Call<SchoolDataPojo> call, Response<SchoolDataPojo> response) {

                if (response.isSuccessful()) {

                    if (response.body().getCount() > 0) {
                        try {
                            double totalRecordsCount = response.body().getCount();
                            double resposeSingle = response.body().getFeatures().size();
                            double temp = Double.parseDouble((resposeSingle / totalRecordsCount) + "");
                            double onepercent = Double.parseDouble(((100 * temp)) + "");
                            schoolCountp = schoolCountp + onepercent;
                            if (schoolCountp > 0 && schoolCountp <= 100) {
                                stateInterface.update((int) schoolCountp);
                            }
                        } catch (Exception e) {
                            //execption
                        }


                        parseSchoolData(response, clusterId, distId, stateInterface, token, blockId);
                    } else {
                        stateInterface.failed(context.getResources().getString(R.string.schoolsnotfoundforcluster));

                    }
                } else if (response.code() == 500) {
                    //email or password invalid
                    stateInterface.failed(funInternalServerError());
                } else {

                    stateInterface.failed(context.getResources().getString(R.string.schoolloadingfailed));

                }


            }

            @Override
            public void onFailure(Call<SchoolDataPojo> call, Throwable t) {

                stateInterface.failed(getFailureMessage(t) + t.getMessage());

            }
        });

    }


    private void parseSchoolData(Response<SchoolDataPojo> response, long clusterId, long distId, SchoolStateInterface stateInterface, String token, long blockId) {

        for (int i = 0; i < response.body().getFeatures().size(); i++) {

            // Log.d("shri",i+"");
            School schol = new School();
            schol.setId(response.body().getFeatures().get(i).getProperties().getId());
            schol.setName(response.body().getFeatures().get(i).getProperties().getName());
            schol.setBoundaryId(response.body().getFeatures().get(i).getProperties().getBoundary().getId());
            schol.setDise(response.body().getFeatures().get(i).getProperties().getDiseCode() + "");

            // Log.d("shri",response.body().getFeatures().get(i).getProperties().getGrades().size()+"===="+clusterId);
            try {
                for (Grade grade : response.body().getFeatures().get(i).getProperties().getGrades()) {
                    InstititeGradeIdTable gradeIdTable = new InstititeGradeIdTable();
                    gradeIdTable.setId(grade.getGrpid());
                    gradeIdTable.setSchoolId(response.body().getFeatures().get(i).getProperties().getId());
                    gradeIdTable.setGradeName(grade.getGrpname());
                    db.insertNew(gradeIdTable);

                }
            } catch (Exception e) {
                try {
                    Crashlytics.log("blockid:" + blockId);
                    Crashlytics.log("clusterId:" + clusterId);
                    Crashlytics.log("school Id:" + response.body().getFeatures().get(i).getProperties().getId());
                } catch (Exception e1) {
                    Crashlytics.log("Inner catch Exception in parse school");
                }
            }

            try {
                Boolean b = db.insertNew(schol);
            } catch (Exception e) {
                Boolean b = db.persist(schol);
            }
        }
        if (response.body().getNext() != null) {
            DownloadSchoolData(response.body().getNext(), clusterId, distId, token, blockId, stateInterface);
            // stateInterface.success("success");//remove this

        } else {
            Boundary boundary = new Boundary();
            boundary.setIsFlag(true);
            Update update = Update.table(Boundary.TABLE).where(Boundary.ID.eq(distId).or(Boundary.ID.eq(clusterId)));
            Update boundaryupdate = update.fromTemplate(boundary);
            db.update(boundaryupdate);


            Boundary blockboundary = new Boundary();
            blockboundary.setIsFlag(true);
            Update updateblck = Update.table(Boundary.TABLE).where(Boundary.ID.eq(distId).or(Boundary.ID.eq(blockId)));
            Update boundaryupdateblock = updateblck.fromTemplate(blockboundary);
            db.update(boundaryupdateblock);


            // stateInterface.success("success");

            stateInterface.success(context.getResources().getString(R.string.schools_loaded_success));

        }


    }

    public void downloadStudent(String url, final long schoolId, final int flag, final SchoolStateInterface stateInterface) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.getStudentAtClusterLevel(url).enqueue(new Callback<SchoolStudentPojo>() {
            @Override
            public void onResponse(Call<SchoolStudentPojo> call, Response<SchoolStudentPojo> response) {

                if (response.isSuccessful()) {
                    parseStudent(response.body(), schoolId, stateInterface, flag);

                } else if (response.code() == 500) {
                    //email or password invalid
                    stateInterface.failed(funInternalServerError());
                } else {
                    stateInterface.failed(context.getResources().getString(R.string.stu_downloading_failed));
                }

            }

            @Override
            public void onFailure(Call<SchoolStudentPojo> call, Throwable t) {
                stateInterface.failed(context.getResources().getString(R.string.stu_downloading_failed));
            }
        });
    }

    public void parseStudent(SchoolStudentPojo response, long schoolId, SchoolStateInterface stateInterface, int flag) {

        int studentCount = 0;
        int particularCOunt = 0;
      //  Log.d("shri", schoolId + "=============");
        for (int i = 0; i < response.getResults().size(); i++) {
            int gradeInt = 0;
            try {


                String garde = response.getResults().get(i).getClasses().get(0).getName();
                try {
                    gradeInt = Integer.parseInt(garde);
                } catch (Exception e) {
                    //  Log.d("shri", i + "=============");
                    continue;
                }
            } catch (Exception e) {
                //  Log.d("shri",   response.getResults().get(i).getId()+"----");
                continue;
            }

            if (gradeInt > 0 && gradeInt <= 5) {
                Result result = response.getResults().get(i);
                StudentTable table = new StudentTable();
                table.setId(result.getId());
                table.setFirstName(result.getFirstName());
                table.setLastName(result.getLastName() != null ? result.getLastName().trim() : "");
                table.setDob(result.getDob());
                table.setGender(result.getGender());
                table.setStatus(result.getStatus());
                table.setMt(result.getMt());
                table.setStudentGrade(gradeInt);
                table.setUid(result.getUid());
                table.setInstitution(result.getInstitution());
                table.setMiddleName(result.getFather_name() != null ? result.getFather_name().trim() : "");
                if (flag != 0 && flag == gradeInt) {
                    particularCOunt++;
                }
                studentCount++;
                db.insertNew(table);
            }

        }


        if (response.getNext() != null) {
            downloadStudent(response.getNext().toString(), schoolId, flag, stateInterface);
            //Toast.makeText(getApplicationContext(), "next", Toast.LENGTH_SHORT).show();

        } else {

            if (response.getCount() > 0) {
                if (flag != 0) {
                    stateInterface.success(String.format(context.getResources().getString(R.string.students_downloaded), particularCOunt + ""));

                } else {
                    stateInterface.success(String.format(context.getResources().getString(R.string.students_downloaded), studentCount + ""));

                }
                //UPDATE
                Update updateSchool = Update.table(School.TABLE).where(School.ID.eq(schoolId));
                Update schoolUpdate = updateSchool.fromTemplate(new School().setStudentCount(response.getCount()));
                db.update(schoolUpdate);

            } else {
                Update updateSchool = Update.table(School.TABLE).where(School.ID.eq(schoolId));
                Update schoolUpdate = updateSchool.fromTemplate(new School().setStudentCount(0));
                db.update(schoolUpdate);
                stateInterface.success(context.getResources().getString(R.string.schools_not_contains_reg_stu));

            }

        }


    }


    public void DownloadBlocksData(String url, final String statekey, final boolean isDataAlreadyDownloaded, final String token, final SchoolStateInterface stateInterface) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.getAllBlocksData(url, token).enqueue(new Callback<BlockDetailPojo>() {
            @Override
            public void onResponse(Call<BlockDetailPojo> call, Response<BlockDetailPojo> response) {

                if (response.isSuccessful()) {
                    if (response.body().getCount() > 0) {
                        try {
                            double totalRecordsCount = response.body().getCount();
                            double resposeSingle = response.body().getResults().size();
                            double temp = Double.parseDouble((resposeSingle / totalRecordsCount) + "");
                            double onepercent = Double.parseDouble(((100 * temp)) + "");
                            schoolCountp = schoolCountp + onepercent;

                            if (schoolCountp > 0 && schoolCountp <= 100) {
                                stateInterface.update((int) schoolCountp);

                            }
                        } catch (Exception e) {
                            //execption
                        }
                        parseBlockDataToDb(response, stateInterface, statekey, isDataAlreadyDownloaded, token);
                    } else {
                        stateInterface.failed(context.getResources().getString(R.string.registeredblocksnotfound));

                    }
                } else if (response.code() == 500) {
                    //email or password invalid
                    stateInterface.failed(funInternalServerError());
                } else {

                    //Exception
                    stateInterface.failed(context.getResources().getString(R.string.blocksDataLoadingFailed));
                }

            }

            @Override
            public void onFailure(Call<BlockDetailPojo> call, Throwable t) {
                stateInterface.failed(getFailureMessage(t));
            }
        });


    }


    private void parseBlockDataToDb(Response<BlockDetailPojo> response, SchoolStateInterface stateInterface, String stateKey, boolean isDataAlreadyDownloaded, String token) {

        if (response.body().getCount() != null && response.body().getCount() > 0) {
            if (response.body().getResults() != null && response.body().getResults().size() > 0) {
                for (int i = 0; i < response.body().getResults().size(); i++) {


                    Boundary boundary = new Boundary();
                    if (response.body().getResults().get(i).getType().equalsIgnoreCase("primary")) {
                        boundary.setId(response.body().getResults().get(i).getId());
                        boundary.setParentId(response.body().getResults().get(i).getParentBoundary().getId());
                        boundary.setName(response.body().getResults().get(i).getName());
                        boundary.setHierarchy("block");
                        boundary.setType("primaryschool");
                        if (isDataAlreadyDownloaded == false) {
                            boundary.setIsFlag(false);
                            boundary.setIsFlagCB(false);
                        }
                        boundary.setStateKey(stateKey);
                        String locName = response.body().getResults().get(i).getLangName();
                        if (locName == null)
                            boundary.setLocName(response.body().getResults().get(i).getName());
                        else
                            boundary.setLocName(locName);
                        try {
                            db.insertNew(boundary);
                        } catch (Exception e) {

                            db.persist(boundary);
                        }
                    }
                }


                if (response.body().getNext() != null) {
                    DownloadBlocksData(response.body().getNext().toString(), stateKey, isDataAlreadyDownloaded, token, stateInterface);
                } else {
                    stateInterface.success("success");
                }
            } else {
                stateInterface.failed(context.getResources().getString(R.string.noblock));
            }

        } else {
            stateInterface.failed(context.getResources().getString(R.string.noblock));

        }
    }

    public void DownloadClusterData(String url, final long distId, final String stateKey, final boolean isDataAlreadyDownloaded, final String token, final SchoolStateInterface stateInterface) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.getAllClusterData(url, token).enqueue(new Callback<ClusterDetailPojo>() {
            @Override
            public void onResponse(Call<ClusterDetailPojo> call, Response<ClusterDetailPojo> response) {

                if (response.isSuccessful()) {
                    try {
                        double totalRecordsCount = response.body().getCount();
                        double resposeSingle = response.body().getResults().size();
                        double temp = Double.parseDouble((resposeSingle / totalRecordsCount) + "");
                        double onepercent = Double.parseDouble(((100 * temp)) + "");
                        schoolCountp = schoolCountp + onepercent;

                        if (schoolCountp > 0 && schoolCountp <= 100) {
                            stateInterface.update((int) schoolCountp);
                        }
                    } catch (Exception e) {
                        //execption
                    }


                    parseClusterDataToDb(response, distId, stateInterface, stateKey, isDataAlreadyDownloaded, token);
                } else if (response.code() == 500) {
                    //email or password invalid
                    stateInterface.failed(funInternalServerError());
                } else {

                    stateInterface.failed(context.getResources().getString(R.string.clusterDataLoadingFailed));
                    //   Toast.makeText(getApplicationContext(), "Ex", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ClusterDetailPojo> call, Throwable t) {
                //  Toast.makeText(getApplicationContext(), "Ex1", Toast.LENGTH_SHORT).show();
                // );
                stateInterface.failed(getFailureMessage(t));
            }
        });


    }

    private void parseClusterDataToDb(Response<ClusterDetailPojo> response, long distId, SchoolStateInterface stateInterface, String stateKey, boolean isDataAlreadyDownloaded, String token) {


        for (int i = 0; i < response.body().getResults().size(); i++) {
            Boundary boundary = new Boundary();
            if (response.body().getResults().get(i).getType().equalsIgnoreCase("primary")) {
                boundary.setId(response.body().getResults().get(i).getId());
                boundary.setParentId(response.body().getResults().get(i).getParentBoundary().getId());
                boundary.setName(response.body().getResults().get(i).getName());
                boundary.setHierarchy("cluster");
                if (isDataAlreadyDownloaded == false) {
                    boundary.setIsFlag(false);
                    boundary.setIsFlagCB(false);
                }
                boundary.setType("primaryschool");
                boundary.setStateKey(stateKey);
                String locName = response.body().getResults().get(i).getLangName();
                if (locName == null)
                    boundary.setLocName(response.body().getResults().get(i).getName());
                else
                    boundary.setLocName(locName);

                try {
                    db.insertNew(boundary);
                } catch (Exception e) {
                    db.persist(boundary);
                }
            }
            // Log.d("w", i + "");
        }

        if (response.body().getNext() != null) {
            DownloadClusterData(response.body().getNext(), distId, stateKey, isDataAlreadyDownloaded, token, stateInterface);
            //Toast.makeText(getApplicationContext(), "next", Toast.LENGTH_SHORT).show();
            //  Log.d("Sreee", "------------------------NEXT----");
        } else {
            Boundary boundary = new Boundary();
            boundary.setIsFlagCB(true);
            Update update = Update.table(Boundary.TABLE).where(Boundary.ID.eq(distId));
            Update boundaryupdate = update.fromTemplate(boundary);
            db.update(boundaryupdate);
            stateInterface.success("success");
            //  Log.d("Sreee", "------------------------FINISH----");
        }


    }

    public void setProfileUpdateAction(String firstName, String lastName, final String email, String dob, String usertype, String headertoken, String stateKey, final CurrentStateInterface stateInterface) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> api = null;

        api = apiInterface.setUpdateProfile(firstName, lastName, usertype, dob, email, headertoken, stateKey);
        /*if (TextUtils.isEmpty(email)) {
            api = apiInterface.setUpdateProfileWithoutEmail(firstName, lastName, usertype, dob, headertoken, stateKey);

        } else {
            api = apiInterface.setUpdateProfile(firstName, lastName, usertype, dob, email, headertoken, stateKey);
        }*/

        api.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (response != null && response.code() == 200 && response.isSuccessful()) {
                    try {

                        String data = response.body().string();
                        // Log.d("test", data);
                        stateInterface.setSuccess(data);
                        updateSessionData(data);

                    } catch (IOException e) {
                        e.printStackTrace();
                        stateInterface.setFailed(context.getResources().getString(R.string.profileUpdationFailed));
                    }

                } else if (response.code() == 500) {
                    //email or password invalid
                    stateInterface.setFailed(funInternalServerError());
                } else {

                    stateInterface.setFailed(context.getResources().getString(R.string.profileUpdationFailed));

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                stateInterface.setFailed(getFailureMessage(t));

            }
        });

    }

    private void updateSessionData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String firstName = jsonObject.getString("first_name");
            String lastName = jsonObject.getString("last_name");
            String dob = jsonObject.getString("dob");
            String email = jsonObject.getString("email");
            String usertype = jsonObject.getString("user_type");

            SessionManager sessionManager = new SessionManager(context);
            sessionManager.updateSession(firstName, lastName, dob, email, usertype);
        } catch (Exception e) {

        }
    }

    public void downloadQuestionset(String url, QuestionSetPojo qestionPojo, final CurrentStateInterface currentStateInterface) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<QuestionSetPojos> api = apiInterface.getQuestionset(url, qestionPojo);
        api.enqueue(new Callback<QuestionSetPojos>() {
            @Override
            public void onResponse(Call<QuestionSetPojos> call, Response<QuestionSetPojos> response) {

                if (response.isSuccessful() && response.code() == 200 && response.body().getStatus().equalsIgnoreCase("success")) {
                    parseQuestionSet(response.body());
                    currentStateInterface.setSuccess(context.getResources().getString(R.string.successfuly_Questionset));
                } else if (response.code() == 500) {
                    //email or password invalid
                    currentStateInterface.setFailed(funInternalServerError());
                } else {
                    if (response.body().getDescription() != null && !response.body().getDescription().equalsIgnoreCase("")) {
                        currentStateInterface.setFailed(response.body().getDescription());
                    } else {
                        currentStateInterface.setFailed(context.getResources().getString(R.string.oops));
                    }
                }
            }

            @Override
            public void onFailure(Call<QuestionSetPojos> call, Throwable t) {
                currentStateInterface.setFailed(getFailureMessage(t));
            }
        });


    }

    public void getAssessmentList(AuthKeyPojo authKeyPojo, final CurrentStateInterface currentStateInterface) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String Url = A3Services.ASSESSMENT_URL;
        Call<AssessmentPojoRes> api = apiInterface.getAssessmentFromService(authKeyPojo, Url);

        api.enqueue(new Callback<AssessmentPojoRes>() {
            @Override
            public void onResponse(Call<AssessmentPojoRes> call, Response<AssessmentPojoRes> response) {

                if (response.isSuccessful()) {
                    parseAssessmentData(response.body(), currentStateInterface);
                } else {
                    currentStateInterface.setFailed(context.getResources().getString(R.string.assment_loading_failed));
                }
            }

            @Override
            public void onFailure(Call<AssessmentPojoRes> call, Throwable t) {
                currentStateInterface.setFailed(getFailureMessage(t));
            }
        });


    }

    private void parseAssessmentData(AssessmentPojoRes body, CurrentStateInterface currentStateInterface) {

        if (body != null && body.getStatus().equalsIgnoreCase("success") && body.getPrograms() != null && body.getPrograms().size() > 0) {
            try {
                db.deleteAll(AssessmentTypeTable.class);
            } catch (Exception e) {

            }


            for (com.akshara.assessment.a3.AssessmentPojoPack.Program program : body.getPrograms()) {

                //  Log.d("shri", body.getPrograms().size() + "---------");

                AssessmentTypeTable typeTable = new AssessmentTypeTable();
                typeTable.setId(Long.parseLong(program.getIdAssesstype()));
                typeTable.setAssesstypeName(program.getAssesstypeName());
                typeTable.setAssesstypeDescr(program.getAssesstypeDescr());
                typeTable.setIdProgram(Long.parseLong(program.getIdProgram()));
                typeTable.setProgramName(program.getProgramName());
                typeTable.setStartMonth(program.getStartMonth());
                typeTable.setEndMonth(program.getEndMonth());
                db.insertNew(typeTable);

            }

            currentStateInterface.setSuccess(context.getResources().getString(R.string.assessment_loaded));


        } else {
            currentStateInterface.setFailed(context.getResources().getString(R.string.assment_loading_failed));
        }

    }

    public void getProgramsList(AuthKeyPojo authKeyPojo, final CurrentStateInterface currentStateInterface) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String Url = A3Services.PROGRAMSURL;

        Call<ProgramsResPojo> api = apiInterface.getProgramsFromService(authKeyPojo, Url);
        api.enqueue(new Callback<ProgramsResPojo>() {
            @Override
            public void onResponse(Call<ProgramsResPojo> call, Response<ProgramsResPojo> response) {

                if (response.isSuccessful() && response.body().getStatus().equalsIgnoreCase("success")) {
                    parseResponse(response.body(), currentStateInterface);
                } else {
                    //Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                    currentStateInterface.setFailed(context.getResources().getString(R.string.program_loading_failed));
                }

            }

            @Override
            public void onFailure(Call<ProgramsResPojo> call, Throwable t) {
                currentStateInterface.setFailed(getFailureMessage(t));
            }
        });


    }

    private void parseResponse(ProgramsResPojo body, CurrentStateInterface currentStateInterface) {

        if (body != null && body.getPrograms() != null && body.getPrograms().size() > 0) {
            try {
                db.deleteAll(ProgramTable.class);
                db.deleteAll(SubjectTable.class);
            } catch (Exception e) {

            }

            for (Program program : body.getPrograms()) {
                try {
                    long id = Long.parseLong(program.getIdProgram());
                    String program_name = program.getProgramName();
                    String program_descr = program.getProgramDescr();
                    ProgramTable programTable = new ProgramTable();
                    programTable.setId(id);
                    programTable.setProgramName(program_name);
                    programTable.setProgramDescr(program_descr);
                    db.insertNew(programTable);

                    if (program.getSubjects() != null && program.getSubjects().size() > 0) {
                        for (Subject subject : program.getSubjects()) {
                            long subjectId = 0l;
                            try {
                                subjectId = Long.parseLong(subject.getIdSubject());
                            } catch (Exception e) {
                                continue;
                            }
                            SubjectTable subjectTable = new SubjectTable();
                            subjectTable.setIdSubject(subjectId);
                            subjectTable.setSubjectName(subject.getSubjectName());
                            subjectTable.setIdProgram(id);
                            subjectTable.setProgramName(program_name);
                            db.insertNew(subjectTable);


                        }

                    }


                } catch (Exception e) {
                    currentStateInterface.setFailed(context.getResources().getString(R.string.program_loading_failed));
                }
            }
            currentStateInterface.setSuccess(context.getResources().getString(R.string.all_programs_loaded));

        } else {
            currentStateInterface.setFailed(context.getResources().getString(R.string.no_program_found));
        }

    }


    public void registerStudentservice(long groupId, final int grade, String authorization, ArrayList<RegisterStudentPojo> pojo, final CurrentStateInterface currentStateInterface) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterStdPojoResp> api = apiInterface.registerStudent(groupId, authorization, pojo);
        api.enqueue(new Callback<RegisterStdPojoResp>() {
            @Override
            public void onResponse(Call<RegisterStdPojoResp> call, Response<RegisterStdPojoResp> response) {
                //Toast.makeText(context, response.code() + "", Toast.LENGTH_SHORT).show();
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getCount() > 0) {
                        storeregistredStudent(response.body(),grade);
                        currentStateInterface.setSuccess(context.getResources().getString(R.string.student_registration_success));
                    } else {
                        currentStateInterface.setFailed(context.getResources().getString(R.string.student_registration_failed));

                    }
                } else if (response.code() == 400) {
                    Gson gson = new Gson();
                  /*  try {
                        currentStateInterface.setFailed(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                        currentStateInterface.setFailed("STS ID already exists");

                    }*/
                    try {
                        StudentExistsPojo messageObject = gson.fromJson(response.errorBody().charStream(), StudentExistsPojo.class);
                        currentStateInterface.setFailed(getDataFromList(messageObject.getResults().get(0).getUid()));
                    } catch (Exception e) {
                        try {
                            currentStateInterface.setFailed(response.errorBody().string());
                        } catch (Exception e1) {
                            currentStateInterface.setFailed("STS ID already exists");
                        }

                    }
                } else if (response.code() == 500) {
                    //email or password invalid
                    currentStateInterface.setFailed(funInternalServerError());
                } else {
                    try {
                        Gson gson = new Gson();
                        ForgotOTPError messageObject = gson.fromJson(response.errorBody().charStream(), ForgotOTPError.class);
                        currentStateInterface.setFailed(messageObject.getDetail());

                    } catch (Exception e) {

                        currentStateInterface.setFailed(context.getResources().getString(R.string.student_registration_failed));

                    }
                }

            }

            @Override
            public void onFailure(Call<RegisterStdPojoResp> call, Throwable t) {
                currentStateInterface.setFailed(getFailureMessage(t));

            }
        });

    }


    public String getDataFromList(List<String> listData) {
        String msg = "";
        if (listData != null && listData.size() > 0) {
            for (String msgData : listData) {
                msg = msg + msgData + "\n";
            }
        }

        return msg.trim();


    }

    private void storeregistredStudent(RegisterStdPojoResp response,int grade) {
       // Log.d("shri",response.toString());

        for (int i = 0; i < response.getResults().size(); i++) {
            try {
             //   Log.d("shri","1");
               // String garde = response.getResults().get(i).getClasses().get(0).getName();
               // int gradeInt = Integer.parseInt(garde);
                int gradeInt = grade;


                com.akshara.assessment.a3.regstdrespPojo.Result result = response.getResults().get(i);

                StudentTable table = new StudentTable();
                table.setId(result.getId());
                table.setFirstName(result.getFirstName());
                table.setLastName(result.getLastName() != null ? result.getLastName().trim() : "");
                table.setDob(result.getDob());
                table.setGender(result.getGender());
                table.setStatus(result.getStatus());
                table.setMt(result.getMt());
                table.setStudentGrade(gradeInt);
                table.setUid(result.getUid());
                table.setInstitution(result.getInstitution());
                table.setMiddleName(result.getMiddleName() != null ? result.getMiddleName().trim() : "");



                db.insertNew(table);
            } catch (Exception e) {
                Crashlytics.log("student register insert crash");
                Toast.makeText(context, "please download all students from menu", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void insertQuestionSetIntoDB(QuestionSetTable questionSetTable) {
        Query QuestionsetQuery = Query.select().from(QuestionSetTable.TABLE)
                .where(QuestionSetTable.ID_QUESTIONSET.eq(questionSetTable.getIdQuestionset()));
        SquidCursor<QuestionSetTable> questionsetCursor = db.query(QuestionSetTable.class, QuestionsetQuery);

        if (questionsetCursor != null && questionsetCursor.getCount() > 0) {
            //QUESTION SET FOUND
            //UPDATE
            Update questionsetUpdate = Update.table(QuestionSetTable.TABLE)
                    .fromTemplate(questionSetTable)


                    .where(QuestionSetTable.ID_QUESTIONSET.eq(questionSetTable.getIdQuestionset()));
            int val = db.update(questionsetUpdate);
            //  Log.d("shri", "Updated:" + val);
            if (val > 0) {


                Query QuestionListQuery = Query.select().from(QuestionSetDetailTable.TABLE)
                        .where(QuestionSetDetailTable.ID_QUESTIONSET.eq(questionSetTable.getIdQuestionset()));
                SquidCursor<QuestionSetDetailTable> questionsetListCursor = db.query(QuestionSetDetailTable.class, QuestionListQuery);
                ArrayList<String> listIds = new ArrayList<>();
                while (questionsetListCursor.moveToNext()) {
                    QuestionSetDetailTable qsT = new QuestionSetDetailTable(questionsetListCursor);
                    listIds.add(qsT.getIdQuestion());
                }

                // Log.d("shri", "Question size:=======" + listIds.size());

                //delete Question & data
                int questionint = db.deleteWhere(QuestionTable.class, QuestionTable.ID_QUESTION.in(listIds));
                //  Log.d("shri", "deleted Questions:" + questionint);
                int quesDataInt = db.deleteWhere(QuestionDataTable.class, QuestionDataTable.ID_QUESTION.in(listIds));
                // Log.d("shri", "deleted data:" + quesDataInt);

                int quesSetDetailInt = db.deleteWhere(QuestionSetDetailTable.class, QuestionSetDetailTable.ID_QUESTIONSET.eq(questionSetTable.getIdQuestionset()));
                //   Log.d("shri", "deleted QuestionsetDetail:" + quesSetDetailInt);
            }


        } else {
            //INSERT
            boolean b = db.insertNew(questionSetTable);
            //  Log.d("shri", "Insert:" + b);
        }
    }


    private void parseQuestionSet(QuestionSetPojos questionSetDataObj) {

        if (questionSetDataObj.getStatus().equalsIgnoreCase("success")) {

            //LOAD QUESTION SET
            if (questionSetDataObj.getQuestionsets() != null) {
                for (int i = 0; i < questionSetDataObj.getQuestionsets().size(); i++) {

                    QuestionSetTable questionSetTable = new QuestionSetTable();

                    Questionset questionset = questionSetDataObj.getQuestionsets().get(i);
                    questionSetTable.setIdQuestionset(Integer.parseInt(questionset.getIdQuestionset()));
                    questionSetTable.setQsetTitle(questionset.getTitle());
                    questionSetTable.setQsetName(questionset.getTitle());
                    questionSetTable.setLanguageName(questionset.getLanguage());
                    questionSetTable.setSubjectName(questionset.getSubject());
                    questionSetTable.setGradeName(questionset.getGrade());
                    questionSetTable.setProgramName(questionset.getProgram());
                    questionSetTable.setAssesstypeName(questionset.getAssessmenttype());

                    //INSERT QUESTION SET IF ID ALready EXIST DELETE FIRST
                    insertQuestionSetIntoDB(questionSetTable);
                    if (questionset.getQuestions() != null) {
                        //LOAD QUESTIONS
                        for (int j = 0; j < questionset.getQuestions().size(); j++) {
                            Question question = questionset.getQuestions().get(j);
                            QuestionTable questionTable = new QuestionTable();

                            questionTable.setIdQuestion(question.getIdQuestion());
                            //questionTable.setIdQuestionset(Integer.parseInt(questionset.getIdQuestionset()));
                            questionTable.setQuestionTitle(question.getTitle());
                            questionTable.setQuestionText(question.getText());
                            questionTable.setCorrectAnswer(question.getCorrect_answer());
                            questionTable.setLanguageName(question.getLanguage());
                            questionTable.setSubjectName(question.getSubject());
                            questionTable.setGradeName(question.getGrade());
                            questionTable.setLevelName(question.getLevel());
                            questionTable.setQuestiontypeName(question.getQuestiontype());
                            questionTable.setQuestiontempltypeName(question.getQuestiontempltype());
                            questionTable.setAnswerunitlabel(question.getAnswerunitlabel());
                            questionTable.setConceptName(question.getConcept());
                            questionTable.setMconceptName(question.getMicroconcept());

                            boolean b = db.persist(questionTable);

                            QuestionSetDetailTable questionSetDetailTable = new QuestionSetDetailTable();
                            questionSetDetailTable.setIdQuestionset(Integer.parseInt(questionset.getIdQuestionset()));
                            questionSetDetailTable.setIdQuestion(question.getIdQuestion());

                            db.persist(questionSetDetailTable);
                            //  Log.d("shri", "Insert Question:" + b);
                            //LOAD OPTIONS FOR THE QUESTION
                            if (question.getQuestiondata() != null) {
                                for (int k = 0; k < question.getQuestiondata().size(); k++) {
                                    Questiondatum questionData = question.getQuestiondata().get(k);
                                    QuestionDataTable questionDataTable = new QuestionDataTable();
                                    questionDataTable.setIdQuestion(questionData.getIdQuestion());
                                    //questionDataTable.setIdQuestionset(Integer.parseInt(questionset.getIdQuestionset()));

                                    questionDataTable.setName(questionData.getName());
                                    questionDataTable.setLabel(questionData.getLabel());
                                    questionDataTable.setDatatype(questionData.getDatatype());
                                    questionDataTable.setRole(questionData.getRole());
                                    questionDataTable.setPosition(questionData.getPosition());
                                    questionDataTable.setVal(questionData.getVal());
                                    questionDataTable.setFilecontentBase64(questionData.getFilecontentBase64());

                                    boolean b1 = db.persist(questionDataTable);
                                    //   Log.d("shri", "Insert Question data:" + b1);

                                }
                            }


                        }
                    }

                }
            }


        }
    }


    public String funInternalServerError() {
        return context.getResources().getString(R.string.internal_server_error);
    }
}
