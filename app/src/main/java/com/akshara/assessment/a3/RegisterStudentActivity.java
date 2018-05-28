package com.akshara.assessment.a3;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

public class RegisterStudentActivity extends android.support.v4.app.Fragment {


    EditText edtStsId,edtFirstName,edtStuLastName;
    RadioGroup studentGender;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.studentregistration, container, false);

        studentGender=view.findViewById(R.id.studentGender);
        edtStsId=view.findViewById(R.id.edtStsId);
        edtFirstName=view.findViewById(R.id.edtFirstName);
        edtStuLastName=view.findViewById(R.id.edtStuLastName);

  //  Toast.makeText(getActivity(),getAcademicYear()+"",Toast.LENGTH_SHORT).show();
        return view;
    }


    private boolean validation()
    {
      String firstName=edtFirstName.getText().toString().trim();
      String lastName=edtStuLastName.getText().toString().trim();
      String studentId=edtStsId.getText().toString().trim();
      boolean gender=studentGender.isSelected();
      if(TextUtils.isEmpty(studentId))
      {
          edtFirstName.setError(getResources().getString(R.string.errorStudentId));
          edtFirstName.requestFocus();
          return false;
      }
      else if(TextUtils.isEmpty(firstName))
      {
          edtFirstName.setError(getResources().getString(R.string.errorFirstName));
          edtFirstName.requestFocus();
          return false;

      }else if(TextUtils.isEmpty(lastName))
      {
          edtStuLastName.setError(getResources().getString(R.string.errorLastName));
          edtStuLastName.requestFocus();
          return false;
      }
      else if(!gender)
      {
          Toast.makeText(getActivity(),"select gender",Toast.LENGTH_SHORT).show();
          return false;
      }
      else
      {
          return true;
      }





    }


    public String getAcademicYear()
    {

        DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
         df.format(Calendar.getInstance().getTime());
        Calendar cal = Calendar.getInstance();
        int month =cal.get(Calendar.MONTH)+1;
        String academic="";
        if(month<6)
        {
           int year=Integer.parseInt( df.format(Calendar.getInstance().getTime()));
            academic=(year-1)+""+year;
            //Previous + current
        }else {
            //current+ next
            int year=Integer.parseInt( df.format(Calendar.getInstance().getTime()));
             academic=year+""+(year+1);
        }
        return academic;






    }
}
