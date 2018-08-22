package com.akshara.assessment.a3.db;

import android.support.annotation.NonNull;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by Shridhar on  5/18/2018.
 */
@TableModelSpec(className = "Assessment_Detail_Table", tableName = "Assessment_Detail_Table")
public class NewAssessmentDetailTBL {
    @PrimaryKey
    @ColumnSpec(name="id")
    int Id;

    @ColumnSpec(name="id_assessment")
    @NonNull
    public String id_assessment;


    @ColumnSpec(name="id_question")
    @NonNull
    public String id_question;


    @ColumnSpec(name="pass")
    public String pass;









}