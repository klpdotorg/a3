package com.akshara.assessment.a3.TelemetryReport;

public class pojoAssessment {


    int id,id_questionset,score,synced;
    String id_assessment,id_child,datetime_start,datetime_submission;

    @Override
    public String toString() {
        return "pojoAssessment{" +
                "id=" + id +
                ", id_questionset=" + id_questionset +
                ", score=" + score +
                ", synced=" + synced +
                ", id_assessment='" + id_assessment + '\'' +
                ", id_child='" + id_child + '\'' +
                ", datetime_start='" + datetime_start + '\'' +
                ", datetime_submission='" + datetime_submission + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_questionset() {
        return id_questionset;
    }

    public void setId_questionset(int id_questionset) {
        this.id_questionset = id_questionset;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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

    public String getId_child() {
        return id_child;
    }

    public void setId_child(String id_child) {
        this.id_child = id_child;
    }

    public String getDatetime_start() {
        return datetime_start;
    }

    public void setDatetime_start(String datetime_start) {
        this.datetime_start = datetime_start;
    }

    public String getDatetime_submission() {
        return datetime_submission;
    }

    public void setDatetime_submission(String datetime_submission) {
        this.datetime_submission = datetime_submission;
    }
}
