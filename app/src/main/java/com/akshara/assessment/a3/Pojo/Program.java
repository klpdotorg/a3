package com.akshara.assessment.a3.Pojo;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Program {

    @SerializedName("id_program")
    @Expose
    private String idProgram;


    @SerializedName("statecode")
    @Expose
    private String statecode;

    public String getStatecode() {
        return statecode;
    }

    public void setStatecode(String statecode) {
        this.statecode = statecode;
    }

    @SerializedName("program_name")
    @Expose

    private String programName;
    @SerializedName("program_descr")
    @Expose
    private String programDescr;
    @SerializedName("subjects")
    @Expose
    private List<Subject> subjects = null;

    public String getIdProgram() {
        return idProgram;
    }

    public void setIdProgram(String idProgram) {
        this.idProgram = idProgram;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramDescr() {
        return programDescr;
    }

    public void setProgramDescr(String programDescr) {
        this.programDescr = programDescr;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

}