package com.akshara.assessment.a3.Pojo;

public class QuestionSetPojo {

    public QuestionSetPojo(String language, String subject, String grade, String assessmenttype, String program,String authkey) {
        this.language = language;
        this.subject = subject;
        this.grade = grade;
        this.assessmenttype = assessmenttype;
        this.authkey = authkey;
        this.program=program;
    }

    String language,subject,grade,assessmenttype,authkey,program;

    public String getLanguage() {
        return language;
    }

    public String getSubject() {
        return subject;
    }

    public String getGrade() {
        return grade;
    }

    public String getAssessmenttype() {
        return assessmenttype;
    }

    public String getAuthkey() {
        return authkey;
    }

    public String getProgram() {
        return program;
    }
}
