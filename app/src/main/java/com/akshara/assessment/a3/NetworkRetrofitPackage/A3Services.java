package com.akshara.assessment.a3.NetworkRetrofitPackage;

import com.akshara.assessment.a3.BuildConfig;

public class A3Services {
    public static final String LOGIN_API = "/api/v1/users/login/";
    public static final String OTP_SIGNUP = "/api/v1/users/otp-update/";
    public static final String FORGOTPASSWORD_GENERATE_OTP = "/api/v1/users/otp-generate/";
    public static final String REGISTRATION = "/api/v1/users/register/";
    public static final String FORGOTPASSWORD_RESETWITH_OTP = "/api/v1/users/otp-password-reset/";
    public static final String SCHOOLS = "/api/v1/institutions/?&s_type=primaryschools";
    public static final String UPDATE_PROFILE = "/api/v1/users/profile";


    public static final String AUTH_KEY = "A3APIAKSHARAAUTHKEY#2018";



    //   URLS

    //dev
 /* public static final String PROGRAMSURL = BuildConfig.HOST_ASSESSMENT + "/a3/getPrograms";
    public static final String ASSESSMENT_URL = BuildConfig.HOST_ASSESSMENT + "/a3/getAssessmentTypes";
    public static final String QUESTIONSET_URL = BuildConfig.HOST_ASSESSMENT + "/a3/getQuestionSets";
*/

 //Production
    public static final String PROGRAMSURL = BuildConfig.HOST_ASSESSMENT + "/getPrograms";
    public static final String ASSESSMENT_URL = BuildConfig.HOST_ASSESSMENT + "/getAssessmentTypes";
    public static final String QUESTIONSET_URL = BuildConfig.HOST_ASSESSMENT + "/getQuestionSets";

}
