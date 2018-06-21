package com.akshara.assessment.a3.AssessmentPojoPack;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Program {

@SerializedName("id_assesstype")
@Expose
private String idAssesstype;
@SerializedName("assesstype_name")
@Expose
private String assesstypeName;
@SerializedName("assesstype_descr")
@Expose
private String assesstypeDescr;
@SerializedName("id_program")
@Expose
private String idProgram;
@SerializedName("program_name")
@Expose
private String programName;
@SerializedName("start_month")
@Expose
private String startMonth;
@SerializedName("end_month")
@Expose
private String endMonth;

public String getIdAssesstype() {
return idAssesstype;
}

public void setIdAssesstype(String idAssesstype) {
this.idAssesstype = idAssesstype;
}

public String getAssesstypeName() {
return assesstypeName;
}

public void setAssesstypeName(String assesstypeName) {
this.assesstypeName = assesstypeName;
}

public String getAssesstypeDescr() {
return assesstypeDescr;
}

public void setAssesstypeDescr(String assesstypeDescr) {
this.assesstypeDescr = assesstypeDescr;
}

public String getIdProgram() {
return idProgram;
}

public void setIdProgram(String idProgram) {
this.idProgram = idProgram;
}

public String getProgramName() {
return programName;
}

public void setProgramName(String programName) {
this.programName = programName;
}

public String getStartMonth() {
return startMonth;
}

public void setStartMonth(String startMonth) {
this.startMonth = startMonth;
}

public String getEndMonth() {
return endMonth;
}

public void setEndMonth(String endMonth) {
this.endMonth = endMonth;
}

}