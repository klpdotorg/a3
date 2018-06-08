package com.akshara.assessment.a3;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.akshara.assessment.a3.NetworkRetrofitPackage.A3NetWorkCalls;
import com.akshara.assessment.a3.NetworkRetrofitPackage.CurrentStateInterface;
import com.akshara.assessment.a3.Pojo.RegisterStudentPojo;
import com.akshara.assessment.a3.UtilsPackage.DailogUtill;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.db.InstititeGradeIdTable;
import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.StudentTable;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RegisterStudentActivity extends android.support.v4.app.Fragment {


    EditText edtStsId, edtFirstName, edtStuLastName;
    RadioGroup studentGender;
    Button btnRegisterstudent;
    StudentListMainActivity activity;
    KontactDatabase database;
    Long institution;
    int grade;
    ProgressDialog progressDialog;
    static String gradeString = "";
    SquidCursor<InstititeGradeIdTable> gradeIdCursor;
    SessionManager sessionManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.studentregistration, container, false);

        activity = (StudentListMainActivity) getActivity();
        studentGender = view.findViewById(R.id.studentGender);
        edtStsId = view.findViewById(R.id.edtStsId);
        edtFirstName = view.findViewById(R.id.edtFirstName);
        edtStuLastName = view.findViewById(R.id.edtStuLastName);
        sessionManager=new SessionManager(getActivity().getApplicationContext());
        btnRegisterstudent = view.findViewById(R.id.btnRegisterstudent);
        database = new KontactDatabase(activity);
        institution = activity.getIntent().getLongExtra("A3APP_INSTITUTIONID", 0);
        gradeString = activity.getIntent().getStringExtra("A3APP_GRADESTRING");
        grade = activity.getIntent().getIntExtra("A3APP_GRADEID", 0);

        final Query GradeIDTable = Query.select().from(InstititeGradeIdTable.TABLE)
                .where(InstititeGradeIdTable.SCHOOL_ID.eq(institution)
                        .and(InstititeGradeIdTable.GRADE_NAME.eq(grade))
                );
        gradeIdCursor = database.query(InstititeGradeIdTable.class, GradeIDTable);

        Log.d("shri", institution + ":" + grade);

        btnRegisterstudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validation()) {

                    gradeIdCursor = database.query(InstititeGradeIdTable.class, GradeIDTable);

                    String firstName = edtFirstName.getText().toString().trim();
                    String lastName = edtStuLastName.getText().toString().trim();
                    final RadioButton rb = getActivity().findViewById(studentGender.getCheckedRadioButtonId());
                    String gender = rb.getText().toString().trim().toLowerCase();
                    String stsId = edtStsId.getText().toString().trim();
                    RegisterStudentPojo studentPojo =
                            new RegisterStudentPojo(firstName, lastName, gender, institution + "", "AC", getAcademicYear(), stsId);
                    long groupId = 0l;
                    if (gradeIdCursor.moveToNext()) {
                        InstititeGradeIdTable table = new InstititeGradeIdTable(gradeIdCursor);
                        groupId = table.getId();
                    }
                    if (groupId == 0l) {
                        activity.finish();
                    }
                    Log.d("shri", "groupId:" + groupId);
                    Log.d("shri", "inst:" + institution);
                    ArrayList<RegisterStudentPojo> list = new ArrayList<>();
                    list.add(studentPojo);
                    initPorgresssDialogForSchool();
                 // new A3NetWorkCalls(activity).registerStudentservice(groupId, sessionManager.getToken(), list, new CurrentStateInterface() {
                    new A3NetWorkCalls(activity).registerStudentservice(groupId, "Token f68deebe2fa4f85ec53ea012197dd66cc2b785cb", list, new CurrentStateInterface() {
                        @Override
                        public void setSuccess(String message) {
                            finishProgress();
                            edtFirstName.setText("");
                            edtStuLastName.setText("");
                            edtStsId.setText("");
                            rb.setChecked(false);

                            DailogUtill.showDialog( message, getFragmentManager(), activity);

                        }

                        @Override
                        public void setFailed(String message) {
                            finishProgress();
                            DailogUtill.showDialog( message, getFragmentManager(), activity);
                        }
                    });
                }
                //validation();

            }
        });
        //  Toast.makeText(getActivity(),getAcademicYear()+"",Toast.LENGTH_SHORT).show();
        return view;
    }

    private void finishProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

    private void initPorgresssDialogForSchool() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Student registering..");


        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    private boolean validation() {
        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtStuLastName.getText().toString().trim();
        String studentId = edtStsId.getText().toString().trim();
        boolean gender = studentGender.getCheckedRadioButtonId() != -1;
        if (TextUtils.isEmpty(studentId)) {
            edtStsId.setError(getResources().getString(R.string.errorStudentId));
            edtStsId.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(firstName)) {
            edtFirstName.setError(getResources().getString(R.string.errorFirstName));
            edtFirstName.requestFocus();
            return false;

        } else if (TextUtils.isEmpty(lastName)) {
            edtStuLastName.setError(getResources().getString(R.string.errorLastName));
            edtStuLastName.requestFocus();
            return false;
        } else if (!gender) {

            Toast.makeText(getActivity(), "select gender", Toast.LENGTH_SHORT).show();
            return false;

        } else if (gradeIdvalidation() == false) {
            Toast.makeText(getActivity(), "Sorry you cant register student currently", Toast.LENGTH_SHORT).show();
            return false;
        } else {


            return true;
        }


    }


    public boolean gradeIdvalidation() {
        if (gradeIdCursor != null) {
            return gradeIdCursor.getCount() > 0 && grade > 0;
          //  Log.d("shri", "kkkk" + gradeIdCursor.getCount() + ":" + grade);


        }

      //  Log.d("shri", "23232");
        return false;
    }


    public String getAcademicYear() {

        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        df.format(Calendar.getInstance().getTime());
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        String academic = "";
        if (month < 6) {
            int year = Integer.parseInt(df.format(Calendar.getInstance().getTime()));
            academic = (year - 1) + "" + year;
            //Previous + current
        } else {
            //current+ next
            int year = Integer.parseInt(df.format(Calendar.getInstance().getTime()));
            academic = year + "" + (year + 1);
        }
        return academic;


    }
}
