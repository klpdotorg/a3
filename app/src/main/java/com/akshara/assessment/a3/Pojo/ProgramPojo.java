package com.akshara.assessment.a3.Pojo;

public class ProgramPojo {

    long id;
    String programName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    @Override
    public String toString() {
        return programName;
    }
}
