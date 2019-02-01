package com.akshara.assessment.a3.Pojo;

import java.util.ArrayList;

public class TelemetryPojo {

    String language,subject,grade,program,assessmenttype,fromdate,todate,authkey,statecode ;
    ArrayList<String> childids;

    public TelemetryPojo(String language, String subject, String grade, String program, String assessmenttype, String fromdate, String todate, String authkey, ArrayList<String> childids,String statecode) {
        this.language = language;
        this.subject = subject;
        this.grade = grade;
        this.program = program;
        this.assessmenttype = assessmenttype;
        this.fromdate = fromdate;
        this.todate = todate;
        this.authkey = authkey;
        this.childids = childids;
        this.statecode=statecode;
    }
}
