package com.akshara.assessment.a3;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.UtilsPackage.StringWithTags;
import com.akshara.assessment.a3.db.Boundary;
import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.School;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class BoundarySelectionActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    Long bid;

    String  type = "", district = "", block = "", cluster = "";
    SquidCursor<Boundary> boundary_cursor = null;
    SquidCursor<School> school_cursor = null;



    LinearLayout  linBackSchool;
    KontactDatabase db;
SessionManager sessionManager;
    String blockid, distrciId;

    Spinner sp_district, sp_block, sp_cluster;

    TextView txBlock, txCluster;
    boolean b = false;





    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boundary_selection);
        db = ((A3Application) getApplicationContext()).getDb();




        this.setTitle(getResources().getString(R.string.app_name));


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        sessionManager=new SessionManager(getApplicationContext());


        sp_district = findViewById(R.id.select_district);
        sp_block = findViewById(R.id.select_block);
        sp_cluster = findViewById(R.id.select_cluster);
        txBlock = findViewById(R.id.txBlock);
        txCluster = findViewById(R.id.txCluster);
        linBackSchool = findViewById(R.id.linBackSchool);
        fill_dropdown(1, sp_district.getId(), 1);







    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            fill_dropdown(1, sp_district.getId(), 1);

        } catch (Exception e) {

        }


    }




















    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        SharedPreferences sharedPreferences = getSharedPreferences("boundary", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringWithTags boundaryForSelector = (StringWithTags) parent.getItemAtPosition(pos);
        int viewid = parent.getId();
        switch (viewid) {
            case R.id.select_district:
                fill_dropdown(1, R.id.select_block, Integer.parseInt(boundaryForSelector.id.toString()));
                fill_dropdown(1, R.id.select_cluster, Integer.parseInt(boundaryForSelector.id.toString()));
                fill_schools(R.id.school_list, Integer.parseInt(boundaryForSelector.id.toString()));
                editor.putInt("district", pos);
                //  Toast.makeText(getApplicationContext(),"district",Toast.LENGTH_SHORT).show();
                district = ((StringWithTags) parent.getItemAtPosition(pos)).string;
                distrciId = ((StringWithTags) parent.getItemAtPosition(pos)).id.toString();
                // Toast.makeText(getApplicationContext(), distrciId + ":district", Toast.LENGTH_SHORT).show();

                break;
            case R.id.select_block:
                fill_dropdown(1, R.id.select_cluster, Integer.parseInt(boundaryForSelector.id.toString()));
                editor.putInt("block", pos);
                //     Toast.makeText(getApplicationContext(),"block",Toast.LENGTH_SHORT).show();
                block = ((StringWithTags) parent.getItemAtPosition(pos)).string;
                blockid = (((StringWithTags) parent.getItemAtPosition(pos)).id.toString());
                //   Toast.makeText(getApplicationContext(), blockid + ":block", Toast.LENGTH_SHORT).show();
                break;
            case R.id.select_cluster:
                fill_schools(R.id.school_list, Integer.parseInt(boundaryForSelector.id.toString()));
                //   Toast.makeText(getApplicationContext(),"school",Toast.LENGTH_SHORT).show();
                 cluster = ((StringWithTags) parent.getItemAtPosition(pos)).string;
                editor.putInt("cluster", pos);
                bid = new Long(((StringWithTags) parent.getItemAtPosition(pos)).id.toString());
                //  Toast.makeText(getApplicationContext(), bid + ":cluster", Toast.LENGTH_SHORT).show();
                break;
        }
        editor.commit();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void fill_dropdown(int type, int id, int parent) {
        List<StringWithTags> stringWithTags = get_boundary_data(parent);
          Spinner spinner = findViewById(id);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<StringWithTags> boundaryArrayAdapter = new ArrayAdapter<StringWithTags>(this, R.layout.spinnertextview, stringWithTags);
        spinner.setAdapter(boundaryArrayAdapter);
        boundaryArrayAdapter.setDropDownViewResource(R.layout.spinnertextview);
    }



    private void fill_schools(int id, int parent) {
        ListView listView = findViewById(id); //nothing
        List<StringWithTags> schoolList = get_school_data(parent);
        final ArrayAdapter<StringWithTags> schoolArrayAdapter = new ArrayAdapter<StringWithTags>(this, R.layout.schoollisttextview, schoolList);
        listView.setAdapter(schoolArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
             /*   Intent intent = new Intent(BoundarySelectionActivity.this, QuestionActivity.class);

                intent.putExtra("schoolId", new Long(schoolArrayAdapter.getItem(i).id.toString()));
              startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);*/
                Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_SHORT).show();
                //finish();

            }
        });

    }

    private List<StringWithTags> get_boundary_data(int parent) {
        Query listboundary = Query.select().from(Boundary.TABLE)
                .where(Boundary.PARENT_ID.eq(parent).and(Boundary.TYPE.eq("primaryschool").and(Boundary.STATE_KEY.eqCaseInsensitive(sessionManager.getStateSelection()))))
                .orderBy(Boundary.NAME.asc());

        List<StringWithTags> boundaryList = new ArrayList<StringWithTags>();
        boundary_cursor = db.query(Boundary.class, listboundary);
        if (boundary_cursor.moveToFirst()) {
            do {
                Boundary b = new Boundary(boundary_cursor);
                if ((b.getHierarchy().equalsIgnoreCase("district") || b.getHierarchy().equalsIgnoreCase("block"))&&b.isFlag()==true) {
                    StringWithTags boundary = new StringWithTags(b.getName(), b.getId(), b.getHierarchy().equals("district") ? "1" : b.getParentId(), getLocTextBoundary(b), sessionManager,b.isFlag(),b.isFlagCB());
                    boundaryList.add(boundary);

                } else {
                    if (!b.getHierarchy().equalsIgnoreCase("district") && !b.getHierarchy().equalsIgnoreCase("block")) {
                        StringWithTags boundary = new StringWithTags(b.getName(), b.getId(), b.getHierarchy().equals("district") ? "1" : b.getParentId(), getLocTextBoundary(b), sessionManager,b.isFlag(),b.isFlagCB());
                        boundaryList.add(boundary);
                    }
                }
            } while (boundary_cursor.moveToNext());
        }
        if (boundary_cursor != null)
            boundary_cursor.close();
        return boundaryList;
    }


    public String getLocTextBoundary(Boundary b) {
        if (sessionManager.getLanguagePosition() <= 1) {
            //english
            return b.getName() != null ? b.getName() : b.getLocName();

        } else {
            //native
            return b.getLocName() != null ? b.getLocName() : b.getName();
        }
    }

    private List<StringWithTags> get_school_data(int parent) {
        Query listschool = Query.select().from(School.TABLE)
                .where(School.BOUNDARY_ID.eq(parent));
        List<StringWithTags> schoolList = new ArrayList<StringWithTags>();
        school_cursor = db.query(School.class, listschool);
        if (school_cursor.moveToFirst()) {
            do {
                School sch = new School(school_cursor);
                StringWithTags school = new StringWithTags(sch.getName(), sch.getId(), 1, true, sessionManager);
                schoolList.add(school);
            } while (school_cursor.moveToNext());
        }
        if (school_cursor != null)
            school_cursor.close();
        return schoolList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }





}
