package com.akshara.assessment.a3.Pojo;

public class RegisterStudentPojo {

    //institution id will be based on school for each grade one unique id

    String first_name,last_name,gender,institution,status,academic_year,uid;

    @Override
    public String toString() {
        return "RegisterStudentPojo{" +
                "first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", gender='" + gender + '\'' +
                ", institution='" + institution + '\'' +
                ", status='" + status + '\'' +
                ", academic_year='" + academic_year + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }

    public RegisterStudentPojo(String first_name, String last_name,
                               String gender, String institution,
                               String status, String academic_year, String uid) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.institution = institution;
        this.status = status;
        this.academic_year = academic_year;
        this.uid = uid;
    }
}
