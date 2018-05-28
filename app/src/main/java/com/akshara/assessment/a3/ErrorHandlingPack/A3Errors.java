package com.akshara.assessment.a3.ErrorHandlingPack;

import com.akshara.assessment.a3.ErrorPojoPackage.LoginErrorPojo;
import com.google.gson.Gson;

import okhttp3.ResponseBody;

public class A3Errors {






    public static LoginErrorPojo loginErrorHandle(ResponseBody response) {
        Gson gson = new Gson();
        LoginErrorPojo messageObject = gson.fromJson(response.charStream(), LoginErrorPojo.class);

        return messageObject;
    }
}
