package com.akshara.assessment.a3.AssessmentPojoPack;

public class AssessmentTempPojo {

    long id_assesstype;
    long id_program;
    String assesstype_name;
    String assesstype_descr;
    String program_name;
    String start_month;
    String end_month;

    public long getId_assesstype() {
        return id_assesstype;
    }

    public void setId_assesstype(long id_assesstype) {
        this.id_assesstype = id_assesstype;
    }

    public long getId_program() {
        return id_program;
    }

    public void setId_program(long id_program) {
        this.id_program = id_program;
    }

    public String getAssesstype_name() {
        return assesstype_name;
    }

    public void setAssesstype_name(String assesstype_name) {
        this.assesstype_name = assesstype_name;
    }

    public String getAssesstype_descr() {
        return assesstype_descr;
    }

    public void setAssesstype_descr(String assesstype_descr) {
        this.assesstype_descr = assesstype_descr;
    }

    public String getProgram_name() {
        return program_name;
    }

    public void setProgram_name(String program_name) {
        this.program_name = program_name;
    }

    public String getStart_month() {
        return start_month;
    }

    public void setStart_month(String start_month) {
        this.start_month = start_month;
    }

    public String getEnd_month() {
        return end_month;
    }

    public void setEnd_month(String end_month) {
        this.end_month = end_month;
    }

    @Override
    public String toString() {
        return assesstype_name ;
    }
}
