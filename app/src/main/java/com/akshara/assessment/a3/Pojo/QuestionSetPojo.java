package com.akshara.assessment.a3.Pojo;

public class QuestionSetPojo {

    public QuestionSetPojo(String language, String subject, String grade, String assessmenttype, String authkey) {
        this.language = language;
        this.subject = subject;
        this.grade = grade;
        this.assessmenttype = assessmenttype;
        this.authkey = authkey;
    }

    String language,subject,grade,assessmenttype,authkey;
}
