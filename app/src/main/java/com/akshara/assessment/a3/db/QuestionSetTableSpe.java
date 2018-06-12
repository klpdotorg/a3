package com.akshara.assessment.a3.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by Shridhar on  4/20/2018.
 */
@TableModelSpec(className = "QuestionSetTable", tableName = "a3app_questionset_tbl")
public class QuestionSetTableSpe {
    @PrimaryKey
    @ColumnSpec(name="id")
    int Id;

    @ColumnSpec(name="id_questionset")
    public int Id_questionset;

    @ColumnSpec(name="qset_title")
    public String qset_title;




    @ColumnSpec(name="qset_name")
    public String qset_name;




    @ColumnSpec(name="language_name")
    public String language_name;



    @ColumnSpec(name="subject_name")
    public String subject_name;



    @ColumnSpec(name="grade_name")
    public String grade_name;

    @ColumnSpec(name="program_name")
    public String program_name;

    @ColumnSpec(name="assesstype_name")
    public String assesstype_name;


}