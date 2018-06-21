package com.akshara.assessment.a3.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by shridhars on 21/6/2017.
 */


@TableModelSpec(className = "AssessmentTypeTable", tableName = "Assessment_Type_Table")
public class AssessmentTypeSpe {
    @PrimaryKey
    @ColumnSpec(name = "id_assesstype")
    long Id;

    @ColumnSpec(name = "assesstype_name")
    public String assesstype_name;

    @ColumnSpec(name = "assesstype_descr")
    public String assesstype_descr;

    @ColumnSpec(name = "id_program")
    public long id_program;


    @ColumnSpec(name = "program_name")
    public String program_name;

    @ColumnSpec(name = "start_month")
    public String start_month;

    @ColumnSpec(name = "end_month")
    public String end_month;


}

