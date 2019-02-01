package com.akshara.assessment.a3.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by shridhars on 21/6/2017.
 */


@TableModelSpec(className = "SubjectTable", tableName = "Subject_Table")
public class SubjectSpec {
    @PrimaryKey
    @ColumnSpec(name = "id_")
    long id_;


    @ColumnSpec(name = "id_subject")
    long id_subject;


    @ColumnSpec(name = "subject_name")
    public String subject_name;


    @ColumnSpec(name = "program_name")
    public String program_name;

    @ColumnSpec(name = "id_program")
    long id_program;


}

