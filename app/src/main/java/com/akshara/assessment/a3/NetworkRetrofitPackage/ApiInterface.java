package com.akshara.assessment.a3.NetworkRetrofitPackage;


import com.akshara.assessment.a3.AssessmentPojoPack.AssessmentPojoRes;
import com.akshara.assessment.a3.BlocksPojo.BlockDetailPojo;
import com.akshara.assessment.a3.ClusterPojos.ClusterDetailPojo;
import com.akshara.assessment.a3.DistrictPojos.DistrictPojos;
import com.akshara.assessment.a3.Pojo.AuthKeyPojo;
import com.akshara.assessment.a3.Pojo.ForgotPassswordOtpPojo;
import com.akshara.assessment.a3.Pojo.ProgramsResPojo;
import com.akshara.assessment.a3.Pojo.QuestionSetPojo;
import com.akshara.assessment.a3.Pojo.RegisterStudentPojo;
import com.akshara.assessment.a3.Pojo.RegstrationResponsePojo;
import com.akshara.assessment.a3.Pojo.ResetPasswordPojo;
import com.akshara.assessment.a3.Pojo.TelemetryPojo;
import com.akshara.assessment.a3.QuestionSetPojos.QuestionSetPojos;
import com.akshara.assessment.a3.SchoolDataPojo.SchoolDataPojo;
import com.akshara.assessment.a3.StudentPojopack.SchoolStudentPojo;
import com.akshara.assessment.a3.TelPojos.TelmetryResPojo;
import com.akshara.assessment.a3.UserRolePack.UserRolesPojos;
import com.akshara.assessment.a3.regstdrespPojo.RegisterStdPojoResp;


import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiInterface {




    @POST(A3Services.LOGIN_API)
    @FormUrlEncoded
    Call<ResponseBody> userLoginService(@Field("username") String mobile, @Field("password") String password, @Field("state") String statekey,@Field("source") String source);


    @POST(A3Services.REGISTRATION)
    @FormUrlEncoded
    Call<RegstrationResponsePojo> registrationService(@Field("email") String email, @Field("mobile_no") String mobilenumber,
                                                      @Field("first_name") String firstName, @Field("last_name") String lastName,
                                                      @Field("password") String password, @Field("source") String source,
                                                      @Field("user_type") String usertype,
                                                      @Field("dob") String dob, @Field("state") String stateKey);



    @POST(A3Services.REGISTRATION)
    @FormUrlEncoded
    Call<RegstrationResponsePojo> registrationServiceWithoutEmail(@Field("mobile_no") String mobilenumber,
                                                                  @Field("first_name") String firstName,@Field("last_name") String lastName,
                                                                  @Field("password") String password,@Field("source") String source,
                                                                  @Field("user_type") String usertype,
                                                                  @Field("dob") String dob,@Field("state") String stateKey);




    @POST(A3Services.FORGOTPASSWORD_GENERATE_OTP)
    @FormUrlEncoded
    Call<ForgotPassswordOtpPojo>generateOtpForForgotPassword(@Field("mobile_no") String mobile_no, @Field("state") String statekey);

    @POST(A3Services.FORGOTPASSWORD_RESETWITH_OTP)
    @FormUrlEncoded
    Call<ResetPasswordPojo>forgotPasswordResetWithOTP(@Field("mobile_no") String mobile_no, @Field("otp") String otp, @Field("password") String password, @Field("state") String statekey);


    @POST(A3Services.OTP_SIGNUP)
    @FormUrlEncoded
    Call<ResponseBody> otpSignUp(@Field("mobile_no") String mobile,@Field("otp") String otp,@Field("state") String statekey);


    @GET("/api/v1/boundary/states")
    Call<UserRolesPojos> getStateDeailFromNetwork();

    @GET
    Call<DistrictPojos> getAllDistrictData(@Url String url);


    @GET
    Call<BlockDetailPojo> getAllBlocksData(@Url String url, @Header("Authorization") String authHeader);

    @GET
    Call<ClusterDetailPojo> getAllClusterData(@Url String url, @Header("Authorization") String authHeader);

    @GET
    Call<SchoolDataPojo> getAllSchoolsData(@Url String url, @Header("Authorization") String authHeader);


    @FormUrlEncoded
    @PUT(A3Services.UPDATE_PROFILE)
    Call<ResponseBody> setUpdateProfile(@Field("first_name") String firstName, @Field("last_name") String lastName,
                                        @Field("user_type") String userType,@Field("dob") String dob,
                                        @Field("email") String email,
                                        @Header("Authorization") String authHeader,@Field("state") String stateKey);


    @GET
    Call<SchoolStudentPojo> getStudentAtClusterLevel(@Url String url,@Header("Authorization") String authHeader);

    @POST
    Call<QuestionSetPojos> getQuestionset(@Url String url, @Body QuestionSetPojo questionset);

    @POST("/api/v1/studentgroups/{id}/students/")
    Call<RegisterStdPojoResp> registerStudent(@Path("id") long id,
                                              @Header("Authorization") String authHeader,
                                              @Body ArrayList<RegisterStudentPojo> pojo);


    @POST
    Call<TelmetryResPojo> telemetryData(@Url String url,
                                        @Body TelemetryPojo pojo);


    @POST
    Call<ProgramsResPojo> getProgramsFromService(@Body AuthKeyPojo Authpojo,@Url String url);

    @POST
    Call<AssessmentPojoRes> getAssessmentFromService(@Body AuthKeyPojo Authpojo, @Url String url);



}