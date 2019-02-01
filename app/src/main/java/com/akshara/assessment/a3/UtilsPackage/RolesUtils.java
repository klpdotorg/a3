package com.akshara.assessment.a3.UtilsPackage;

import android.content.Context;

import com.akshara.assessment.a3.AssessmentPojoPack.AssessmentTempPojo;
import com.akshara.assessment.a3.Pojo.ProgramPojo;
import com.akshara.assessment.a3.Pojo.SubjectTempPojo;
import com.akshara.assessment.a3.db.AssessmentTypeTable;
import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.ProgramTable;
import com.akshara.assessment.a3.db.Respondent;
import com.akshara.assessment.a3.db.SubjectTable;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;
import java.util.LinkedHashMap;


/**
 * Created by shridhars on 1/26/2018.
 */

public class RolesUtils {


    public static LinkedHashMap<String, String> getUserRoles(Context context, KontactDatabase db, String key) {
        LinkedHashMap<String, String> userType = null;
        Query listQGQquery = Query.select().from(Respondent.TABLE)
                .orderBy(Respondent.NAME.asc()).where(Respondent.STATE_KEY.eqCaseInsensitive(key));
        SquidCursor<Respondent> respondentCursor = db.query(Respondent.class, listQGQquery);

        if (respondentCursor != null) {
            //  userType = new LinkedHashMap<String, String>();
            //  userType.put(context.getResources().getString(R.string.pleaseSelectrespondanttype), "No");
            userType = new LinkedHashMap<>();
            while (respondentCursor.moveToNext()) {
                userType.put(respondentCursor.get(Respondent.NAME).toUpperCase(), respondentCursor.get(Respondent.ROLE_K_E_Y).toUpperCase());
                // Log.d("userroles","key:"+respondentCursor.get(Respondent.NAME).toUpperCase()+":: Value:"+respondentCursor.get(Respondent.KEY).toUpperCase());
            }
        }

        return userType;
    }


    public static String getUserRoleValueForFcmGroup(Context context, KontactDatabase db, String key) {
        String ressponse = "PARENTS";
        String userKey = key.trim().toUpperCase();

        Query listQGQquery = Query.select().from(Respondent.TABLE)
                .orderBy(Respondent.NAME.asc());
        SquidCursor<Respondent> respondentCursor = db.query(Respondent.class, listQGQquery);

        if (respondentCursor != null) {

            while (respondentCursor.moveToNext()) {
                if (respondentCursor.get(Respondent.ROLE_K_E_Y).toUpperCase().equalsIgnoreCase(userKey)) {
                    ressponse = respondentCursor.get(Respondent.NAME).toUpperCase().replaceAll("\\s+", "");
                    return ressponse;
                }

            }
        }
        return ressponse;
    }

    public static ArrayList<ProgramPojo> getProgramData(KontactDatabase db, String stateKey) {
        ArrayList<ProgramPojo> programPojoArrayList = new ArrayList<>();
        Query listProgramQuery = Query.select().from(ProgramTable.TABLE).where(ProgramTable.STATECODE.eq(stateKey))
                .orderBy(ProgramTable.ID.asc());
        SquidCursor<ProgramTable> programCursor = db.query(ProgramTable.class, listProgramQuery);
        if (programCursor != null && programCursor.getCount() > 0) {
            while (programCursor.moveToNext()) {
                ProgramTable programTable = new ProgramTable(programCursor);
                ProgramPojo pojo = new ProgramPojo();
                pojo.setId(programTable.getId());
                pojo.setProgramName(programTable.getProgramName());
                programPojoArrayList.add(pojo);

            }
        }
        return programPojoArrayList;

    }

    public static ArrayList<SubjectTempPojo> getSubjectForProgram(KontactDatabase db, long programId) {
        ArrayList<SubjectTempPojo> subjectArrayList = new ArrayList<>();
        Query listSubjectQuery = Query.select().from(SubjectTable.TABLE).where(SubjectTable.ID_PROGRAM.eq(programId));


        SquidCursor<SubjectTable> subjectCursor = db.query(SubjectTable.class, listSubjectQuery);
        if (subjectCursor != null && subjectCursor.getCount() > 0) {
            while (subjectCursor.moveToNext()) {
                SubjectTable subjectTable = new SubjectTable(subjectCursor);
                SubjectTempPojo pojo = new SubjectTempPojo();
                pojo.setIdSubject(subjectTable.getIdSubject());
                pojo.setSubjectName(subjectTable.getSubjectName());
                subjectArrayList.add(pojo);

            }
        }
        return subjectArrayList;

    }


    public static ArrayList<AssessmentTempPojo> getAssessmentType(String program_name, long program_id, KontactDatabase db) {
        ArrayList<AssessmentTempPojo> listData = new ArrayList<>();
        Query listAssessmentQuery = Query.select().from(AssessmentTypeTable.TABLE)
                .orderBy(AssessmentTypeTable.ASSESSTYPE_NAME.asc())
                .where(AssessmentTypeTable.ID_PROGRAM.eq(program_id)
                        .and(AssessmentTypeTable.PROGRAM_NAME.eqCaseInsensitive(program_name)));
        SquidCursor<AssessmentTypeTable> assessmentTypeTableSquidCursor = db.query(AssessmentTypeTable.class, listAssessmentQuery);

        if (assessmentTypeTableSquidCursor != null && assessmentTypeTableSquidCursor.getCount() > 0) {


            while (assessmentTypeTableSquidCursor.moveToNext()) {
                AssessmentTypeTable table = new AssessmentTypeTable(assessmentTypeTableSquidCursor);
                AssessmentTempPojo pojo = new AssessmentTempPojo();
                pojo.setId_assesstype(table.getId());
                pojo.setId_program(table.getIdProgram());
                pojo.setAssesstype_name(table.getAssesstypeName());
                pojo.setStart_month(table.getStartMonth());
                pojo.setEnd_month(table.getEndMonth());
                pojo.setProgram_name(table.getProgramName());
                pojo.setAssesstype_descr(table.getAssesstypeDescr());
                listData.add(pojo);
            }
        }
        return listData;
    }

}
