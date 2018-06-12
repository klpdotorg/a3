package com.akshara.assessment.a3.db;

import android.support.annotation.NonNull;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by Shridhar on  1/30/2018.
 */
@TableModelSpec(className = "QuestionTable", tableName = "a3app_question_tbl")
public class QuestionTableSpe {

    @PrimaryKey
    @ColumnSpec(name="id")
    int Id;

    @ColumnSpec(name="id_question")
    @NonNull
    public String id_question;




    @ColumnSpec(name="question_title")
    @NonNull
    public String question_title;

    @ColumnSpec(name="question_text")
    @NonNull
    public String question_text;



    @ColumnSpec(name="correct_answer")

    public String correct_answer;



    @ColumnSpec(name="language_name")
    @NonNull
    public String language_name;



    @ColumnSpec(name="subject_name")
    @NonNull
    public String subject_name;



    @ColumnSpec(name="grade_name")
    @NonNull
    public String grade_name;



    @ColumnSpec(name="level_name")
    @NonNull
    public String level_name;



    @ColumnSpec(name="questiontype_name")
    @NonNull
    public String questiontype_name;




    @ColumnSpec(name="questiontempltype_name")
    @NonNull
    public String questiontempltype_name;



    @ColumnSpec(name="assesstype_name")
    @NonNull
    public String assesstype_name;


    @ColumnSpec(name="concept_name")
    @NonNull
    public String concept_name;


    @ColumnSpec(name="mconcept_name")
    @NonNull
    public String mconcept_name;
}