package com.akshara.assessment.a3.TelPojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Assessmentdetail {

@SerializedName("id_question")
@Expose
private String idQuestion;
@SerializedName("pass")
@Expose
private String pass;

public String getIdQuestion() {
return idQuestion;
}

public void setIdQuestion(String idQuestion) {
this.idQuestion = idQuestion;
}

public String getPass() {
return pass;
}

public void setPass(String pass) {
this.pass = pass;
}

}