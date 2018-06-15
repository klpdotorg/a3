package com.akshara.assessment.a3.TelemetryReport;

public class pojoAssessmentDetail {



    int id,synced;
    String id_assessment,id_question,answer_given,pass;


    @Override
    public String toString() {
        return "pojoAssessmentDetail{" +
                "id=" + id +
                ", synced=" + synced +
                ", id_assessment='" + id_assessment + '\'' +
                ", id_question='" + id_question + '\'' +
                ", answer_given='" + answer_given + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    public String getId_assessment() {
        return id_assessment;
    }

    public void setId_assessment(String id_assessment) {
        this.id_assessment = id_assessment;
    }

    public String getId_question() {
        return id_question;
    }

    public void setId_question(String id_question) {
        this.id_question = id_question;
    }

    public String getAnswer_given() {
        return answer_given;
    }

    public void setAnswer_given(String answer_given) {
        this.answer_given = answer_given;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
