package com.akshara.assessment.a3.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

import java.util.Collections;

/**
 * Created by Shridhar on  4/20/2018.
 */

public class QuestionSetPojo {

    int Id;




    @Override
    public String toString() {
        return assesstype_name ;

    }

    public int Id_questionset;


    public String qset_title;





    public String qset_name;





    public String language_name;




    public String subject_name;
    public String grade_name;


    public String program_name;


    public String assesstype_name;

    public String getProgram_name() {
        return program_name;
    }

    public void setProgram_name(String program_name) {
        this.program_name = program_name;
    }

    public String getAssesstype_name() {
        return assesstype_name;
    }

    public void setAssesstype_name(String assesstype_name) {
        this.assesstype_name = assesstype_name;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getId_questionset() {
        return Id_questionset;
    }

    public void setId_questionset(int id_questionset) {
        Id_questionset = id_questionset;
    }

    public String getQset_title() {
        return qset_title;
    }

    public void setQset_title(String qset_title) {
        this.qset_title = qset_title;
    }

    public String getQset_name() {
        return qset_name;
    }

    public void setQset_name(String qset_name) {
        this.qset_name = qset_name;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public void setLanguage_name(String language_name) {
        this.language_name = language_name;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getGrade_name() {
        return grade_name;
    }

    public void setGrade_name(String grade_name) {
        this.grade_name = grade_name;
    }
}