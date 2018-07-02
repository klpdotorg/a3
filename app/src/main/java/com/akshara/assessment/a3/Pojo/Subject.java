package com.akshara.assessment.a3.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Subject {

@SerializedName("id_subject")
@Expose
private String idSubject;
@SerializedName("subject_name")
@Expose
private String subjectName;

public String getIdSubject() {
return idSubject;
}

public void setIdSubject(String idSubject) {
this.idSubject = idSubject;
}

public String getSubjectName() {
return subjectName;
}

public void setSubjectName(String subjectName) {
this.subjectName = subjectName;


}

}