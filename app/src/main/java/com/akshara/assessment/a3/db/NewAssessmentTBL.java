package com.akshara.assessment.a3.db;

import android.support.annotation.NonNull;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by Shridhar on  5/18/2018.
 */
@TableModelSpec(className = "Assessment_Table", tableName = "Assessment_Table")
public class NewAssessmentTBL {
    @PrimaryKey
    @ColumnSpec(name="id")
    int Id;

    @ColumnSpec(name="id_assessment")
    @NonNull
    public String id_assessment;


    @ColumnSpec(name="id_questionset")
    @NonNull
    public int id_questionset;

    @ColumnSpec(name="id_child")
    @NonNull
    public String id_child;

    @ColumnSpec(name="datetime_start")
    @NonNull
    public String datetime_start;

    @ColumnSpec(name="datetime_submission")
    @NonNull
    public String datetime_submission;

    @ColumnSpec(name="assessmenttype")
    @NonNull
    public String assessmenttype;

    @ColumnSpec(name="score")
    int score;








}