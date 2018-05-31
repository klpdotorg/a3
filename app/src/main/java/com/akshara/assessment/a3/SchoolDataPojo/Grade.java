package com.akshara.assessment.a3.SchoolDataPojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Grade {

@SerializedName("grpname")
@Expose
private String grpname;
@SerializedName("grpid")
@Expose
private Integer grpid;

public String getGrpname() {
return grpname;
}

public void setGrpname(String grpname) {
this.grpname = grpname;
}

public Integer getGrpid() {
return grpid;
}

public void setGrpid(Integer grpid) {
this.grpid = grpid;
}

}