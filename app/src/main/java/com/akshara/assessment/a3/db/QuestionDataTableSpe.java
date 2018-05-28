package com.akshara.assessment.a3.db;

import android.support.annotation.NonNull;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by Shridhar on  4/20/2018.
 */
@TableModelSpec(className = "QuestionDataTable", tableName = "a3app_questiondata_tbl")
public class QuestionDataTableSpe {

    @PrimaryKey
    @ColumnSpec(name = "id")
    int Id;

    @ColumnSpec(name = "id_question")
    @NonNull
       public String id_question;



    @ColumnSpec(name="id_questionset")
    public int Id_questionset;

    @ColumnSpec(name = "name")
    @NonNull
    public String name;


    @ColumnSpec(name = "label")
    @NonNull
    public String label;

    @ColumnSpec(name = "datatype")
    public String datatype;


    @ColumnSpec(name = "role")
    public String role;

    @ColumnSpec(name = "position")
    public String position;


    @ColumnSpec(name = "val")
    public String val;


    @ColumnSpec(name = "filecontent_base64")
    public String filecontent_base64;


}