package com.akshara.assessment.a3;

public class StudentPojo {

    String name;
    String gender,lastName;
    long stsid;
    String uid;
    String father;
    public StudentPojo(String name, String gender,String lastName,long stsid,String uid,String father) {
        this.name=name;
        this.gender=gender;
        this.lastName=lastName;
        this.stsid=stsid;
        this.uid=uid;
        this.father=father;
    }
}
