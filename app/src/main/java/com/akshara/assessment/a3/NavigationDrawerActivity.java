package com.akshara.assessment.a3;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akshara.assessment.a3.NetworkRetrofitPackage.A3NetWorkCalls;
import com.akshara.assessment.a3.NetworkRetrofitPackage.A3Services;
import com.akshara.assessment.a3.NetworkRetrofitPackage.CurrentStateInterface;
import com.akshara.assessment.a3.Pojo.AuthKeyPojo;
import com.akshara.assessment.a3.UtilsPackage.AppSettings;
import com.akshara.assessment.a3.UtilsPackage.AppStatus;
import com.akshara.assessment.a3.UtilsPackage.ConstantsA3;
import com.akshara.assessment.a3.UtilsPackage.DailogUtill;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.UtilsPackage.StringWithTags;
import com.akshara.assessment.a3.db.Boundary;
import com.akshara.assessment.a3.db.DBHelper;
import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.School;
import com.crashlytics.android.Crashlytics;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    SessionManager sessionManager;
    TextView navHeader, navMobile, navProgram;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private ProgressDialog progressDialog = null;
    Long bid;

    String type = "", district = "", block = "", cluster = "";
    SquidCursor<Boundary> boundary_cursor = null;
    SquidCursor<School> school_cursor = null;

    DBHelper dbHelper;

    LinearLayout linBackSchool;
    KontactDatabase db;

    String blockid, distrciId;

    Spinner sp_district, sp_block, sp_cluster;

    TextView txBlock, txCluster;
    boolean b = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.prompt_school));
        sessionManager = new SessionManager(getApplicationContext());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        navHeader = headerView.findViewById(R.id.navHeader);
        navMobile = headerView.findViewById(R.id.navMobile);
        navProgram = headerView.findViewById(R.id.navProgram);


        db = ((A3Application) getApplicationContext()).getDb();


        //     dbHelper=new DBHelper(this);


        //
        sp_district = findViewById(R.id.select_district);
        sp_block = findViewById(R.id.select_block);
        sp_cluster = findViewById(R.id.select_cluster);

        txBlock = findViewById(R.id.txBlock);
        txCluster = findViewById(R.id.txCluster);

        linBackSchool = findViewById(R.id.linBackSchool);


        fill_dropdown(1, sp_district.getId(), 1);
//        Log.d("dim",dbHelper.getData()+"");


        SharedPreferences sharedPreferences = getSharedPreferences("Navigationboundary", MODE_PRIVATE);

        int d = sharedPreferences.getInt("dist", 0);
        int b = sharedPreferences.getInt("block", 0);
        int c = sharedPreferences.getInt("cluster", 0);

        sp_district.setSelection(d);
        sp_block.setSelection(b);
        sp_cluster.setSelection(c);


    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            try {


                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                super.onBackPressed();
                startActivity(a);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            } catch (Exception e) {

            }
        } else {
            Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
            startActivity(new Intent(getApplicationContext(), UpdateProfileActivity.class));
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            // finish();
        } else if (id == R.id.nav_download) {

            // Crashlytics.getInstance().crash();
            startActivity(new Intent(getApplicationContext(), BoundaryLoaderActivity.class));

        } else if (id == R.id.nav_downloadQuestionSet) {
            startActivity(new Intent(getApplicationContext(), DownloadQuestionSetActivity.class));

        } else if (id == R.id.nav_language) {
            startActivity(new Intent(getApplicationContext(), AppSettings.class));

        } else if (id == R.id.nav_updateProgram) {
           // Crashlytics.getInstance().crash();

            updateProgram();

        } else if (id == R.id.nav_logout) {
            android.support.v7.app.AlertDialog alertDailog = new android.support.v7.app.AlertDialog.Builder(NavigationDrawerActivity.this).create();

            alertDailog.setCancelable(false);
            alertDailog.setMessage(getResources().getString(R.string.doyouwantToLogout));
            alertDailog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.response_positive),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            sessionManager.logoutUserDB();
                            finish();

                        }
                    });
            alertDailog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.response_negative),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDailog.show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateProgram() {

        showProgress(true);

        final AuthKeyPojo pojo = new AuthKeyPojo(A3Services.AUTH_KEY);
        new A3NetWorkCalls(NavigationDrawerActivity.this).getProgramsList(pojo, new CurrentStateInterface() {
            @Override
            public void setSuccess(String message) {


                new A3NetWorkCalls(NavigationDrawerActivity.this).getAssessmentList(pojo, new CurrentStateInterface() {
                    @Override
                    public void setSuccess(String message) {

                        showProgress(false);
                        DailogUtill.showDialog(getResources().getString(R.string.updatedProAssessment), getSupportFragmentManager(), getApplicationContext());


                    }

                    @Override
                    public void setFailed(String message) {
                        showProgress(false);


                        if (AppStatus.isConnected(getApplicationContext())) {
                            DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

                        } else {
                            DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

                        }

                    }
                });


            }

            @Override
            public void setFailed(String message) {

                showProgress(false);
                if (AppStatus.isConnected(getApplicationContext())) {
                    DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

                } else {
                    DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

                }

            }
        });

    }


    //

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        SharedPreferences sharedPreferences = getSharedPreferences("Navigationboundary", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringWithTags boundaryForSelector = (StringWithTags) parent.getItemAtPosition(pos);
        int viewid = parent.getId();
        switch (viewid) {
            case R.id.select_district:
                fill_dropdown(1, R.id.select_block, Integer.parseInt(boundaryForSelector.id.toString()));
                fill_dropdown(1, R.id.select_cluster, Integer.parseInt(boundaryForSelector.id.toString()));
                fill_schools(R.id.school_list, Integer.parseInt(boundaryForSelector.id.toString()));
                editor.putInt("dist", pos);
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
        final ListView listView = findViewById(id); //nothing
        List<StringWithTags> schoolList = get_school_data(parent);
        final ArrayAdapter<StringWithTags> schoolArrayAdapter = new ArrayAdapter<StringWithTags>(this, R.layout.schoollisttextview, schoolList);
        listView.setAdapter(schoolArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences sharedPreferences = getSharedPreferences("Navigationboundary", MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("dist", sp_district.getSelectedItemPosition());
                editor.putInt("block", sp_block.getSelectedItemPosition());
                editor.putInt("cluster", sp_cluster.getSelectedItemPosition());
                editor.commit();
                long id = new Long(schoolArrayAdapter.getItem(i).id.toString());
                String name = ((TextView) view.findViewById(R.id.cust_view)).getText().toString();
                showAltertDailog(id,name);
                 /*Intent gradeIntent=new Intent(getApplicationContext(),GradeActivity.class);
                gradeIntent.putExtra("A3APP_INSTITUTIONID",id);
                gradeIntent.putExtra("institutionName",name);

                startActivity(gradeIntent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);*/
                //finish();

            }
        });

    }

    private void showAltertDailog(final long schoolid, final String name) {


        final ArrayAdapter<String> gradeAdapter = new ArrayAdapter<String>(
                NavigationDrawerActivity.this, R.layout.spinnertextview, getResources().getStringArray(R.array.grade));

        LayoutInflater li = LayoutInflater.from(NavigationDrawerActivity.this);

        View promptsView = li.inflate(R.layout.my_altert_dailog_spinner, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NavigationDrawerActivity.this);

        alertDialogBuilder.setView(promptsView);


// create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final Spinner mSpinner = promptsView

                .findViewById(R.id.spn_grade);

        mSpinner.setAdapter(gradeAdapter);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.response_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int grade = Integer.parseInt(mSpinner.getSelectedItem().toString());
                String gradeS = getResources().getStringArray(R.array.array_grade)[grade - 1];
                ConstantsA3.schoolName=name;
                // Toast.makeText(getApplicationContext(),"GradeId:"+grade+":Insti:"+schoolid+":grade"+gradeS,Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), AssessmentSelectorActivity.class)
                        .putExtra("A3APP_INSTITUTIONID", schoolid).
                                putExtra("A3APP_GRADEID", grade).
                                putExtra("A3APP_GRADESTRING", gradeS));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.response_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);

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
                if ((b.getHierarchy().equalsIgnoreCase("district") || b.getHierarchy().equalsIgnoreCase("block")) && b.isFlag() == true) {
                    StringWithTags boundary = new StringWithTags(b.getName(), b.getId(), b.getHierarchy().equals("district") ? "1" : b.getParentId(), getLocTextBoundary(b), sessionManager, b.isFlag(), b.isFlagCB());
                    boundaryList.add(boundary);
                //    Log.d("shri", "dist block" + b.getName());

                } else {
                    if (!b.getHierarchy().equalsIgnoreCase("district") && !b.getHierarchy().equalsIgnoreCase("block") && b.isFlag() == true) {
                        StringWithTags boundary = new StringWithTags(b.getName(), b.getId(), b.getHierarchy().equals("district") ? "1" : b.getParentId(), getLocTextBoundary(b), sessionManager, b.isFlag(), b.isFlagCB());
                        boundaryList.add(boundary);
                        //cluster
                        // Log.d("shri","cluster"+b.getName());
                    }
                }
            } while (boundary_cursor.moveToNext());
        }
        if (boundary_cursor != null)
            boundary_cursor.close();
        return boundaryList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        navHeader.setText(sessionManager.getFirstName() + " " + sessionManager.getLastName());
        navMobile.setText(sessionManager.getMobile());
        navProgram.setText(sessionManager.getProgramFromSession());
        try {
            //fill_dropdown(1, sp_district.getId(), 1);

        } catch (Exception e) {

        }


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
                .where(School.BOUNDARY_ID.eq(parent).and(School.STUDENT_COUNT.isNotNull()));


     /*   Query listschool = Query.select().from(School.TABLE)
                .where(School.BOUNDARY_ID.eq(parent));*/
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

    private void showProgress(final boolean show) {
        if (show) {
            progressDialog = new ProgressDialog(NavigationDrawerActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);

            progressDialog.setMessage(getResources().getString(R.string.updating_program_Assessment));
            progressDialog.show();
        } else {
            if (progressDialog != null) {
                if (!NavigationDrawerActivity.this.isFinishing()) {
                    progressDialog.cancel();
                }
            }
        }
    }
}
