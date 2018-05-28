package com.akshara.assessment.a3.ErrorPojoPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shridhars on 3/30/2018.
 */

public class LoginErrorPojo {

    @SerializedName("detail")
    @Expose
    private String detail;
    @SerializedName("non_field_errors")
    @Expose
    private List<String> nonFieldErrors = null;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<String> getNonFieldErrors() {
        return nonFieldErrors;
    }

    public void setNonFieldErrors(List<String> nonFieldErrors) {
        this.nonFieldErrors = nonFieldErrors;
    }


}
