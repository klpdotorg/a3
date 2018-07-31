package com.akshara.assessment.a3.regstdrespPojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("middle_name")
    @Expose
    private String middleName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("dob")
    @Expose
    private String dob;

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", uid='" + uid + '\'' +
                ", dob='" + dob + '\'' +
                ", gender='" + gender + '\'' +
                ", mt='" + mt + '\'' +
                ", status='" + status + '\'' +
                ", institution=" + institution +
                ", classes=" + classes +
                ", fatherName=" + fatherName +
                ", motherName=" + motherName +
                '}';
    }

    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("mt")
    @Expose
    private String mt;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("institution")
    @Expose
    private Long institution;
    @SerializedName("classes")
    @Expose
    private List<Class> classes = null;
    @SerializedName("father_name")
    @Expose
    private Object fatherName;
    @SerializedName("mother_name")
    @Expose
    private Object motherName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMt() {
        return mt;
    }

    public void setMt(String mt) {
        this.mt = mt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getInstitution() {
        return institution;
    }

    public void setInstitution(Long institution) {
        this.institution = institution;
    }

    public List<Class> getClasses() {
        return classes;
    }

    public void setClasses(List<Class> classes) {
        this.classes = classes;
    }

    public Object getFatherName() {
        return fatherName;
    }

    public void setFatherName(Object fatherName) {
        this.fatherName = fatherName;
    }

    public Object getMotherName() {
        return motherName;
    }

    public void setMotherName(Object motherName) {
        this.motherName = motherName;
    }

}