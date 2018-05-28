package com.akshara.assessment.a3.Pojo;

public class StudentDetailGrade {



    int boys;

    public int getBoys() {
        return boys;
    }

    public int getGirls() {
        return girls;
    }

    public int getTotal() {
        return total;
    }

    public int getGrade() {
        return grade;
    }

    public String getGradeString() {
        return gradeString;
    }

    public StudentDetailGrade(int boys, int girls, int total, int grade, String gradeString) {
        this.boys = boys;
        this.girls = girls;
        this.total = total;
        this.grade = grade;

        this.gradeString=gradeString;
    }

    int girls;
    int total;
    int grade;
    String gradeString;


}
