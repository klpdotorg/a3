package com.akshara.assessment.a3.TelemetryReport;

import android.support.annotation.NonNull;

import com.akshara.assessment.a3.db.StudentTable;

import java.util.ArrayList;
import java.util.Map;

public class pojoReportData implements Comparable {


    StudentTable table;

    public int getScore() {
        return score;
    }


    public void setScore(int score) {
        this.score = score;
    }

    int score;

    public StudentTable getTable() {
        return table;
    }

    public void setTable(StudentTable table) {
        this.table = table;
    }

    Map<Long,ArrayList<CombinePojo>> detailReportsMap;

    public Map<Long, ArrayList<CombinePojo>> getDetailReportsMap() {
        return detailReportsMap;
    }

    public void setDetailReportsMap(Map<Long, ArrayList<CombinePojo>> detailReportsMap) {
        this.detailReportsMap = detailReportsMap;
    }

    @Override
    public String toString() {
        return "pojoReportData{" +
                "table=" + table +
                ", detailReportsMap=" + detailReportsMap +
                '}';
    }

    @Override
    public int compareTo(@NonNull Object o) {
        int compareage=((pojoReportData)o).getScore();
        StudentTable std=((pojoReportData)o).getTable();
        /* For Ascending order*/
        return compareage-this.score ;

        /* For Descending order do like this */
        //return compareage-this.studentage;
    }
}
