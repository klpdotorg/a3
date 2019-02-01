package com.akshara.assessment.a3.Pojo;

public class QuestionSetPojo {

    public QuestionSetPojo(String language, String subject, String grade, String assessmenttype, String program,String authkey,String statecode) {
        this.language = language;
        this.subject = subject;
        this.grade = grade;
        this.assessmenttype = assessmenttype;
        this.authkey = authkey;
        this.program=program;
        this.statecode=statecode;
    }

    String language,subject,grade,assessmenttype,authkey,program,statecode;

    public String getLanguage() {
        return language;
    }

    public String getStatecode() {
        return statecode;
    }

    @Override
    public String toString() {
        return "QuestionSetPojo{" +
                "language='" + language + '\'' +
                ", subject='" + subject + '\'' +
                ", grade='" + grade + '\'' +
                ", assessmenttype='" + assessmenttype + '\'' +

                ", authkey='" + authkey + '\'' +
                ", program='" + program + '\'' +
                '}';
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
