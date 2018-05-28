package com.akshara.assessment.a3.db;

import android.support.annotation.NonNull;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by Shridhar on  5/18/2018.
 */
@TableModelSpec(className = "QuestionSetDetailTable", tableName = "a3app_questionsetdetail_tbl")
public class QuestionSetDetailTableSpe {
    @PrimaryKey
    @ColumnSpec(name="id")
    int Id;

    @ColumnSpec(name="id_questionset")
    @NonNull
    public int Id_questionset;

    @ColumnSpec(name="id_question")
    @NonNull
    public String Id_question;





}