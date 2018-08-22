package com.akshara.assessment.a3.TelPojos;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Assessment {

@SerializedName("id_assessment")
@Expose
private String idAssessment;
@SerializedName("id_questionset")
@Expose
private String idQuestionset;
@SerializedName("id_child")
@Expose
private String idChild;
@SerializedName("datetime_start")
@Expose
private String datetimeStart;
@SerializedName("datetime_submission")
@Expose
private String datetimeSubmission;
@SerializedName("score")
@Expose
private String score;
@SerializedName("language")
@Expose
private String language;
@SerializedName("subject")
@Expose
private String subject;
@SerializedName("grade")
@Expose
private String grade;
@SerializedName("program")
@Expose
private String program;
@SerializedName("assessmenttype")
@Expose
private String assessmenttype;
@SerializedName("assessmentdetails")
@Expose
private List<Assessmentdetail> assessmentdetails = null;

public String getIdAssessment() {
return idAssessment;
}

public void setIdAssessment(String idAssessment) {
this.idAssessment = idAssessment;
}

public String getIdQuestionset() {
return idQuestionset;
}

public void setIdQuestionset(String idQuestionset) {
this.idQuestionset = idQuestionset;
}

public String getIdChild() {
return idChild;
}

public void setIdChild(String idChild) {
this.idChild = idChild;
}

public String getDatetimeStart() {
return datetimeStart;
}

public void setDatetimeStart(String datetimeStart) {
this.datetimeStart = datetimeStart;
}

public String getDatetimeSubmission() {
return datetimeSubmission;
}

public void setDatetimeSubmission(String datetimeSubmission) {
this.datetimeSubmission = datetimeSubmission;
}

public String getScore() {
return score;
}

public void setScore(String score) {
this.score = score;
}

public String getLanguage() {
return language;
}

public void setLanguage(String language) {
this.language = language;
}

public String getSubject() {
return subject;
}

public void setSubject(String subject) {
this.subject = subject;
}

public String getGrade() {
return grade;
}

public void setGrade(String grade) {
this.grade = grade;
}

public String getProgram() {
return program;
}

public void setProgram(String program) {
this.program = program;
}

public String getAssessmenttype() {
return assessmenttype;
}

public void setAssessmenttype(String assessmenttype) {
this.assessmenttype = assessmenttype;
}

public List<Assessmentdetail> getAssessmentdetails() {
return assessmentdetails;
}

public void setAssessmentdetails(List<Assessmentdetail> assessmentdetails) {
this.assessmentdetails = assessmentdetails;
}

}