package com.akshara.assessment.a3.Pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProgramsResPojo {

@SerializedName("status")
@Expose
private String status;
@SerializedName("message")
@Expose
private String message;
@SerializedName("programs")
@Expose
private List<Program> programs = null;

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

public List<Program> getPrograms() {
return programs;
}

public void setPrograms(List<Program> programs) {
this.programs = programs;
}

}