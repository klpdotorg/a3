package com.akshara.assessment.a3.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by shridhars on 8/2/2017.
 */
@TableModelSpec(className = "StudentTable", tableName = "student")
public class StudentSpec {
    @PrimaryKey
    @ColumnSpec(name="id")
    long Id;

    @ColumnSpec(name="first_name")
    public String first_name;

    @ColumnSpec(name="middle_name")
    public String middle_name;

    @ColumnSpec(name="last_name")
    public String last_name;

    @ColumnSpec(name="uid")
    public String uid;

    @ColumnSpec(name="dob")
    public String dob;

    @ColumnSpec(name="gender")
    public String gender;

    @ColumnSpec(name="mt")
    public String mt;

    @ColumnSpec(name="status")
    public String status;


    @ColumnSpec(name="institution")
    public long institution;

    @ColumnSpec(name="STUDENT_GRADE")
    public int studentGrade;


}