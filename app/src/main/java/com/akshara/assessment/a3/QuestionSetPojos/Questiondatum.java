package com.akshara.assessment.a3.QuestionSetPojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Questiondatum {

@SerializedName("id_question")
@Expose
private String idQuestion;
@SerializedName("name")
@Expose
private String name;
@SerializedName("label")
@Expose
private String label;
@SerializedName("datatype")
@Expose
private String datatype;
@SerializedName("role")
@Expose
private String role;
@SerializedName("position")
@Expose
private String position;
@SerializedName("val")
@Expose
private String val;
@SerializedName("filecontent_base64")
@Expose
private String filecontentBase64;

public String getIdQuestion() {
return idQuestion;
}

public void setIdQuestion(String idQuestion) {
this.idQuestion = idQuestion;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getLabel() {
return label;
}

public void setLabel(String label) {
this.label = label;
}

public String getDatatype() {
return datatype;
}

public void setDatatype(String datatype) {
this.datatype = datatype;
}

public String getRole() {
return role;
}

public void setRole(String role) {
this.role = role;
}

public String getPosition() {
return position;
}

public void setPosition(String position) {
this.position = position;
}

public String getVal() {
return val;
}

public void setVal(String val) {
this.val = val;
}

public String getFilecontentBase64() {
return filecontentBase64;
}

public void setFilecontentBase64(String filecontentBase64) {
this.filecontentBase64 = filecontentBase64;
}

}