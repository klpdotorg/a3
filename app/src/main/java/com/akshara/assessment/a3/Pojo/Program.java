package com.akshara.assessment.a3.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Program {

@SerializedName("id_program")
@Expose
private String idProgram;
@SerializedName("program_name")
@Expose
private String programName;
@SerializedName("program_descr")
@Expose
private String programDescr;

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

public String getProgramDescr() {
return programDescr;
}

public void setProgramDescr(String programDescr) {
this.programDescr = programDescr;
}

}