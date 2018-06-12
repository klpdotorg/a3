package com.akshara.assessment.a3.QuestionSetPojos;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Questionset {

    @SerializedName("id_questionset")
    @Expose
    private String idQuestionset;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("language")
    @Expose
    private String language;

    @SerializedName("subject")
    @Expose
    private String subject;


    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    @SerializedName("program")
    @Expose

    private String program;

    public String getAssessmenttype()
    {
        return assessmenttype;
    }

    public void setAssessmenttype(String assessmenttype)
    {
        this.assessmenttype = assessmenttype;
    }

    @SerializedName("assessmenttype")
    @Expose
    private String assessmenttype;


    @SerializedName("grade")
    @Expose
    private String grade;
    @SerializedName("questions")
    @Expose
    private List<Question> questions = null;

    public String getIdQuestionset() {
        return idQuestionset;
    }

    public void setIdQuestionset(String idQuestionset) {
        this.idQuestionset = idQuestionset;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

}