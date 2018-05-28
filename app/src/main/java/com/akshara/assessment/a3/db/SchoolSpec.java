package com.akshara.assessment.a3.db;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

/**
 * Created by shridhars on 8/2/2017.
 */
@TableModelSpec(className = "School", tableName = "school")
public class SchoolSpec {
    @PrimaryKey
    @ColumnSpec(name="_id")
    long Id;

    @ColumnSpec(name="boundary_id")
    public long boundary_id;

    public String name;

    @ColumnSpec(name="loc_name")
    public String loc_name;

    @ColumnSpec(name="dise")
    public String dise;


    @ColumnSpec(name="student_count")
    public int student_count;
}