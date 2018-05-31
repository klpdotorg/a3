package com.akshara.assessment.a3.db;

import android.support.annotation.NonNull;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by Shridhar on  5/30/2018.
 */
@TableModelSpec(className = "InstititeGradeIdTable", tableName = "Institute_Grade_Id_TBL")
public class InstituteIDGradeTBL {

    @PrimaryKey
    @ColumnSpec(name="id")
    int Id;

    @ColumnSpec(name="school_id")
    @NonNull
    public int schoolId;


    @ColumnSpec(name="grade_name")
    @NonNull
    public String grade_name;

}