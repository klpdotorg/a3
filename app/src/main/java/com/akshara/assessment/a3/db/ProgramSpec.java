package com.akshara.assessment.a3.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by shridhars on 21/6/2017.
 */


@TableModelSpec(className = "ProgramTable", tableName = "Program_Table")
public class ProgramSpec {
    @PrimaryKey
    @ColumnSpec(name = "id_program")
    long Id;


    @ColumnSpec(name = "statecode")
    String statecode;

    @ColumnSpec(name = "program_name")
    public String program_name;

    @ColumnSpec(name = "program_descr")
    public String program_descr;

    @ColumnSpec(name = "program_descr3")
    public String program_descr3;



}

