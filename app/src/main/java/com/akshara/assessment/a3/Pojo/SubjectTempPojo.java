package com.akshara.assessment.a3.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubjectTempPojo {



    private long idSubject;

    private String subjectName;

    public long getIdSubject() {

        return idSubject;
    }

    public void setIdSubject(long idSubject) {
        this.idSubject = idSubject;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;


    }

    @Override
    public String toString() {
        return
                subjectName ;

    }
}
