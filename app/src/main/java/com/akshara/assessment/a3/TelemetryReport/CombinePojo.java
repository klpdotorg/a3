package com.akshara.assessment.a3.TelemetryReport;

import com.akshara.assessment.a3.db.StudentTable;

import java.util.ArrayList;

public class CombinePojo {

    pojoAssessment pojoAssessment;
    ArrayList<pojoAssessmentDetail> pojoAssessmentDetail;




    public com.akshara.assessment.a3.TelemetryReport.pojoAssessment getPojoAssessment() {
        return pojoAssessment;
    }

    public void setPojoAssessment(com.akshara.assessment.a3.TelemetryReport.pojoAssessment pojoAssessment) {
        this.pojoAssessment = pojoAssessment;
    }

    public ArrayList<com.akshara.assessment.a3.TelemetryReport.pojoAssessmentDetail> getPojoAssessmentDetail() {
        return pojoAssessmentDetail;
    }

    public void setPojoAssessmentDetail(ArrayList<com.akshara.assessment.a3.TelemetryReport.pojoAssessmentDetail> pojoAssessmentDetail) {
        this.pojoAssessmentDetail = pojoAssessmentDetail;
    }


    @Override
    public String toString() {
        return "CombinePojo{" +
                "pojoAssessment=" + pojoAssessment +
                ", pojoAssessmentDetail=" + pojoAssessmentDetail +
                '}';
    }
}
