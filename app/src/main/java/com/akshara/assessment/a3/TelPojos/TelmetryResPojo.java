package com.akshara.assessment.a3.TelPojos;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TelmetryResPojo {

@SerializedName("status")
@Expose
private String status;

@SerializedName("description")
@Expose
private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @SerializedName("message")
@Expose
private String message;
@SerializedName("assessments")
@Expose
private List<Assessment> assessments = null;

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

public List<Assessment> getAssessments() {
return assessments;
}

public void setAssessments(List<Assessment> assessments) {
this.assessments = assessments;
}

}