package com.akshara.assessment.a3.QuestionSetPojos;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Question {

@SerializedName("id_question")
@Expose
private String idQuestion;

@SerializedName("correct_answer")
@Expose
private String correct_answer;

    public String getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }

    @SerializedName("title")
@Expose
private String title;
@SerializedName("text")
@Expose
private String text;
@SerializedName("language")
@Expose
private String language;
@SerializedName("subject")
@Expose
private String subject;
@SerializedName("grade")
@Expose
private String grade;
@SerializedName("level")
@Expose
private String level;
@SerializedName("answerunitlabel")
@Expose
private String answerunitlabel;

    public String getAnswerunitlabel() {
        return answerunitlabel;
    }

    public void setAnswerunitlabel(String answerunitlabel) {
        this.answerunitlabel = answerunitlabel;
    }

    @SerializedName("concept")
@Expose
private String concept;
@SerializedName("microconcept")
@Expose
private String microconcept;
@SerializedName("questiontype")
@Expose
private String questiontype;
@SerializedName("questiontempltype")
@Expose
private String questiontempltype;
@SerializedName("questiondata")
@Expose
private List<Questiondatum> questiondata = null;

public String getIdQuestion() {
return idQuestion;
}

public void setIdQuestion(String idQuestion) {
this.idQuestion = idQuestion;
}

public String getTitle() {
return title;
}

public void setTitle(String title) {
this.title = title;
}

public String getText() {
return text;
}

public void setText(String text) {
this.text = text;
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

public String getLevel() {
return level;
}

public void setLevel(String level) {
this.level = level;
}


public String getConcept() {
return concept;
}

public void setConcept(String concept) {
this.concept = concept;
}

public String getMicroconcept() {
return microconcept;
}

public void setMicroconcept(String microconcept) {
this.microconcept = microconcept;
}

public String getQuestiontype() {
return questiontype;
}

public void setQuestiontype(String questiontype) {
this.questiontype = questiontype;
}

public String getQuestiontempltype() {
return questiontempltype;
}

public void setQuestiontempltype(String questiontempltype) {
this.questiontempltype = questiontempltype;
}

public List<Questiondatum> getQuestiondata() {
return questiondata;
}

public void setQuestiondata(List<Questiondatum> questiondata) {
this.questiondata = questiondata;
}

}