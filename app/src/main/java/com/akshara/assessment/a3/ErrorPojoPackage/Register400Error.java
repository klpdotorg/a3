package com.akshara.assessment.a3.ErrorPojoPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Register400Error {

@SerializedName("email")
@Expose
private List<String> email = null;
@SerializedName("mobile_no")
@Expose
private List<String> mobileNo = null;

public List<String> getEmail() {
return email;
}

public void setEmail(List<String> email) {
this.email = email;
}

public List<String> getMobileNo() {
return mobileNo;
}

public void setMobileNo(List<String> mobileNo) {
this.mobileNo = mobileNo;
}

}