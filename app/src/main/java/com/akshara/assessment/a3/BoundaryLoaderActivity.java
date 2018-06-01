package com.akshara.assessment.a3;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akshara.assessment.a3.NetworkRetrofitPackage.A3NetWorkCalls;
import com.akshara.assessment.a3.NetworkRetrofitPackage.A3Services;
import com.akshara.assessment.a3.NetworkRetrofitPackage.CurrentStateInterface;
import com.akshara.assessment.a3.Pojo.StatePojo;
import com.akshara.assessment.a3.UtilsPackage.DailogUtill;
import com.akshara.assessment.a3.UtilsPackage.NoDefaultSpinner;
import com.akshara.assessment.a3.UtilsPackage.SchoolStateInterface;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.UtilsPackage.StringWithTags;
import com.akshara.assessment.a3.db.Boundary;
import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.Respondent;
import com.akshara.assessment.a3.db.School;
import com.akshara.assessment.a3.db.State;

import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shridhars on 1/24/2018.
 */

public class BoundaryLoaderActivity extends BaseActivity implements OnItemSelectedListener {
    ArrayAdapter statelistAdp;
    private KontactDatabase db;
    private ArrayList<StatePojo> stateList;
    NoDefaultSpinner select_state, select_district, select_block,select_cluster,select_school;
    SquidCursor<Boundary> boundary_cursor = null;

    ProgressDialog progressDialog;
    SessionManager mSession;
    TextView tvNoteText;
    boolean flagForState, flagForDistrict, flagForblock,flagForCluster,flagForSchool;
    CardView linLayState;
    Button btnNext;
    boolean LOGIN=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boundaryloader);
        db = ((A3Application) getApplicationContext()).getDb();
        mSession = new SessionManager(getApplicationContext());
        tvNoteText = findViewById(R.id.tvNoteText);
        select_state = findViewById(R.id.select_state);
        select_block = findViewById(R.id.select_block);
        select_cluster = findViewById(R.id.select_cluster);
        select_school = findViewById(R.id.select_school);
        linLayState = findViewById(R.id.linLayState);
        btnNext = findViewById(R.id.btnNext);
        getSupportActionBar().setTitle("Download Students");
        select_district = findViewById(R.id.select_district);
        Query listStateQuery = Query.select().from(State.TABLE).orderBy(State.STATE.asc());
        select_state.setOnItemSelectedListener(this);
        select_block.setOnItemSelectedListener(this);
        select_district.setOnItemSelectedListener(this);
         select_cluster.setOnItemSelectedListener(this);
        select_school.setOnItemSelectedListener(this);
        LOGIN=getIntent().getBooleanExtra("LOGIN",false);
        final String statePersonalKey = mSession.getStateSelection();
        SquidCursor<State> stateCursor = db.query(State.class, listStateQuery);
        stateList = new ArrayList<>();
        btnNext.setVisibility(View.GONE);
        if (stateCursor.getCount() > 0) {
            // we have surveys in DB, get them
            try {
                while (stateCursor.moveToNext()) {
                    State state = new State(stateCursor);
                    StatePojo pojo = new StatePojo(state.getState(), state.getStateLocText(), state.getStatekey(), state.getLangKey(), state.getLangName());
                    stateList.add(pojo);
                }

            } finally {
                if (stateCursor != null) {
                    stateCursor.close();
                }
            }
            /*StatePojo pojo = new StatePojo(0, getString(R.string.selectYourState), getString(R.string.selectYourState));
            stateList.add(0, pojo);*/

            if (stateList != null && stateList.size() > 1) {
                statelistAdp = new ArrayAdapter(this, R.layout.spinnertextview, stateList);
                select_state.setAdapter(statelistAdp);
                if (mSession.isSetupDone()) {
                    tvNoteText.setText(getResources().getString(R.string.downloadDistrcitData));
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    select_state.setEnabled(false);
                    btnNext.setVisibility(View.VISIBLE);
                    linLayState.setVisibility(View.GONE);

                } else {
                    tvNoteText.setText(getResources().getString(R.string.downloadDistrcitDataNext));
                    select_state.setSelection(mSession.getStatePosition() - 1);
                    select_state.setEnabled(false);
                    btnNext.setVisibility(View.GONE);
                    linLayState.setVisibility(View.VISIBLE);
                    linLayState.setVisibility(View.GONE);

                }


            }
            select_state.setSelection(mSession.getStatePosition() - 1);
        }


        fill_dropdown(1, select_district.getId(), 1, ((StatePojo) select_state.getSelectedItem()).getStateKey());
        int dist = getSharedPreferences("loader", MODE_PRIVATE).getInt("dist",0);
        select_district.setSelection(dist);
        if(dist!=0)
        {
            fill_dropdown(1, select_block.getId(), Integer.parseInt(((StringWithTags) select_district.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey());
            fill_dropdown(1, select_cluster.getId(), Integer.parseInt(((StringWithTags) select_block.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey());

        }else {
            fill_dropdown(1, select_block.getId(), 0, ((StatePojo) select_state.getSelectedItem()).getStateKey());
            fill_dropdown(1, select_cluster.getId(), 0, ((StatePojo) select_state.getSelectedItem()).getStateKey());
        }

        flagForDistrict=false;
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getApplicationContext(),NavigationDrawerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        //setPosition();
        select_state.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fill_dropdown(1, select_district.getId(), 1, ((StatePojo) select_state.getSelectedItem()).getStateKey());

                if (flagForState)
                    loadDistrictData(((StatePojo) select_state.getSelectedItem()).getStateKey(), statePersonalKey);
                else
                    flagForState = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });


        select_district.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("shri","dist");
                 if (flagForDistrict) {

                    //   boundaryForSelector.id.toString()
                    if (select_district.getSelectedItem() != null) {
                        if (select_district.getSelectedItemPosition() == 0) {
                            fill_dropdown(1, select_block.getId(), 0, ((StatePojo) select_state.getSelectedItem()).getStateKey());
                            checkCluster();
                            checkSchool();


                        } else {
                            // mSession.setBoundaryPosition(select_district.getSelectedItemPosition());
                            fill_dropdown(1, R.id.select_block, Integer.parseInt(((StringWithTags) select_district.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey());
                            checkCluster();
                            checkSchool();
                            if (((StringWithTags) select_district.getSelectedItem()).flagCb == true) {
                                android.support.v7.app.AlertDialog noAnswerDialog = new android.support.v7.app.AlertDialog.Builder(BoundaryLoaderActivity.this).create();

                                noAnswerDialog.setCancelable(false);
                                noAnswerDialog.setMessage(select_district.getSelectedItem().toString() + " " + getResources().getString(R.string.districtDataAlreadyFound));
                                noAnswerDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.response_neutral),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                loadData(Long.parseLong(((StringWithTags) select_district.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey(), true, mSession.getToken());

                                            }
                                        });
                                noAnswerDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.response_negative),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                noAnswerDialog.show();


                            } else {
                                loadData(Long.parseLong(((StringWithTags) select_district.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey(), false, mSession.getToken());
                            }
                        }
                    } else {
                        //Toast.makeText(getApplicationContext(), "load district", Toast.LENGTH_SHORT).show();
                        DailogUtill.showDialog(getResources().getString(R.string.pleaseLoadDataforSelectedState), getSupportFragmentManager(), getApplicationContext());
                    }
                } else {

                    flagForDistrict = true;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });




        select_cluster.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                flagForSchool=false;
                if(flagForCluster) {
                    if (select_cluster.getSelectedItemPosition() != 0) {

                       fill_SchoolsForSpinner(Integer.parseInt(((StringWithTags) select_cluster.getSelectedItem()).id.toString()));

                     if (((StringWithTags) select_cluster.getSelectedItem()).flag == true) {
                            android.support.v7.app.AlertDialog noAnswerDialog = new android.support.v7.app.AlertDialog.Builder(BoundaryLoaderActivity.this).create();

                            noAnswerDialog.setCancelable(false);
                            noAnswerDialog.setMessage(select_cluster.getSelectedItem().toString() + " " + getResources().getString(R.string.clusterDataAlreadyFound));
                            noAnswerDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.response_neutral),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            loadSchooldataForBlock(Long.parseLong(((StringWithTags) select_cluster.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey(), Long.parseLong(((StringWithTags) select_district.getSelectedItem()).id.toString()), mSession.getToken(),Long.parseLong(((StringWithTags) select_block.getSelectedItem()).id.toString()));
                                        }
                                    });
                            noAnswerDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.response_negative),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            noAnswerDialog.show();


                        } else {
                            loadSchooldataForBlock(Long.parseLong(((StringWithTags) select_cluster.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey(), Long.parseLong(((StringWithTags) select_district.getSelectedItem()).id.toString()), mSession.getToken(),Long.parseLong(((StringWithTags) select_block.getSelectedItem()).id.toString()));
                           }


                    }
                    else {
                        checkSchool();
                    }
                }else {
                    flagForCluster=true;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


select_school.setOnItemSelectedListener(new OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (flagForSchool) {
          //  Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_SHORT).show();

            if (select_school.getSelectedItemPosition() > 0) {
      initPorgresssDialogForSchool();

                        updateProgressMessage(select_school.getSelectedItem().toString() + " " + getResources().getString(R.string.loadingStudent), 0);
Log.d("shri",((StringWithTags) select_school.getSelectedItem()).id.toString());
int schoolId=Integer.parseInt(((StringWithTags) select_school.getSelectedItem()).id.toString());
                String URL =  BuildConfig.HOST +"/api/v1/institutestudents/?institution_id="+schoolId;
                new A3NetWorkCalls(BoundaryLoaderActivity.this).downloadStudent(URL,schoolId, new SchoolStateInterface() {
                    public void success(String message) {
                        finishProgress();
                        if (mSession.isSetupDone() == false) {

                            mSession.updateSetup(true);
                            Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        } else {


                            int position = select_school.getSelectedItemPosition();
                            DailogUtill.showDialog(select_school.getSelectedItem().toString() + " " + message, getSupportFragmentManager(), getApplicationContext());
                            flagForSchool=false;
                            checkSchool();
                            flagForSchool=false;
                            select_school.setSelection(position);

                            SharedPreferences sharedPreferences = getSharedPreferences("Navigationboundary", MODE_PRIVATE);
                            sharedPreferences.edit().clear();


                        }
                    }

                    @Override
                    public void failed(String message) {
                        finishProgress();
                        DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
                    }

                    @Override
                    public void update(int message) {

                    }
                });
            }
            } else {
                flagForSchool = true;
            }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
});
     /*   select_school.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (flagForSchool) {
                    Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_SHORT).show();

                    if (select_school.getSelectedItemPosition() > 0) {

                        Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_SHORT).show();
                        initPorgresssDialogForSchool();
                        updateProgressMessage(select_school.getSelectedItem().toString() + " " + getResources().getString(R.string.loadingStudent), 0);

                        String URL = "http://next.ilp.org.in/api/v1/institutestudents/?institution_id=" + Integer.parseInt(((StringWithTags)select_school.getSelectedItem()).id.toString());
                        new A3NetWorkCalls(BoundaryLoaderActivity.this).downloadStudent(URL, new SchoolStateInterface() {
                            @Override
                            public void success(String message) {
                                finishProgress();
                                if (mSession.isSetupDone() == false) {

                                    mSession.updateSetup(true);
                                    Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                } else {


                                    int position = select_school.getSelectedItemPosition();
                                    DailogUtill.showDialog(select_school.getSelectedItem().toString() + " " + getResources().getString(R.string.schoolStudentDownloaded), getSupportFragmentManager(), getApplicationContext());
                                    checkSchool();

                                    SharedPreferences sharedPreferences = getSharedPreferences("Navigationboundary", MODE_PRIVATE);
                                    sharedPreferences.edit().clear();


                                }
                            }

                            @Override
                            public void failed(String message) {
                                finishProgress();
                                DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
                            }

                            @Override
                            public void update(int message) {

                            }
                        });

                    } else {
                        flagForSchool = true;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        select_block.setOnItemSelectedListener(new OnItemSelectedListener() {



                        @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (flagForblock) {
                    //   boundaryForSelector.id.toString()
                    if (select_block.getSelectedItem() != null) {
                        if (select_block.getSelectedItemPosition() == 0) {

                          checkCluster();
                          checkSchool();

                        } else
                        {
                            fill_dropdown(1, select_cluster.getId(), Integer.parseInt(((StringWithTags) select_block.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey());
                            checkSchool();
                        }
                    } else {
                        //Toast.makeText(getApplicationContext(), "load district", Toast.LENGTH_SHORT).show();
                        DailogUtill.showDialog(getResources().getString(R.string.pleaseLoadDataforSelectedState), getSupportFragmentManager(), getApplicationContext());
                    }
                } else {
                    flagForblock = true;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (!mSession.isSetupDone())
            getMenuInflater().inflate(R.menu.logout_menu, menu);

        return true;
    }


    public void logout(MenuItem item) {
        if (item.getItemId() == R.id.action_logoutdataload) {
            android.support.v7.app.AlertDialog alertDailog = new android.support.v7.app.AlertDialog.Builder(BoundaryLoaderActivity.this).create();

            alertDailog.setCancelable(false);
            alertDailog.setMessage(getResources().getString(R.string.doyouwantToLogout));
            alertDailog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.response_positive),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mSession.logoutUserDB();

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


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if(!LOGIN) {
                    Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDistrictData(final String stateKey, String personalStateKey) {

        initPorgresssDialogForSchool();
        updateProgressMessage(select_state.getSelectedItem().toString() + " " + getResources().getString(R.string.loadingStateDistrict), 0);
        String URL = BuildConfig.HOST + "/api/v1/boundary/admin1s/?per_page=0&state=" + stateKey;

        new A3NetWorkCalls(BoundaryLoaderActivity.this).downloadDistrictForState(URL, stateKey, new CurrentStateInterface() {
            @Override
            public void setSuccess(String message) {
                finishProgress();
                flagForDistrict = false;
                fill_dropdown(1, select_district.getId(), 1, ((StatePojo) select_state.getSelectedItem()).getStateKey());
                DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

            }

            @Override
            public void setFailed(String message) {
                finishProgress();
                DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

            }
        });

    }


    public int getBlockID(Object parent) {
        StringWithTags boundaryForSelector = (StringWithTags) parent;
        if (boundaryForSelector != null) {
            return Integer.parseInt(boundaryForSelector.id.toString());
        }
        return 0;


    }

    @Override
    public void onBackPressed() {
        if(!LOGIN){
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),NavigationDrawerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);}
    }

    private void loadSchooldataForBlock(final long id, String stateKey, long distId, String token,long blockId) {
        initPorgresssDialogForSchool();

        updateProgressMessage(select_cluster.getSelectedItem().toString() + " "
                + getResources().getString(R.string.clusterSchoolLoading), 0);
        //String URL=BuildConfig.HOST+ ILPService.SCHOOLS+"&admin2="+id;

        String URL = BuildConfig.HOST + A3Services.SCHOOLS + "&geometry=yes&admin3=" +
                id + "&state=" + stateKey.toLowerCase();

        new A3NetWorkCalls(BoundaryLoaderActivity.this).DownloadSchoolData(URL, id, distId, token,blockId, new SchoolStateInterface() {
            @Override
            public void success(String message) {


                SharedPreferences sharedPreferences1 = getSharedPreferences("loader", MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences1.edit();
                editor.putInt("dist",select_district.getSelectedItemPosition());
                editor.commit();
                int position = select_cluster.getSelectedItemPosition();
                fill_dropdown(1, select_cluster.getId(), Integer.parseInt(((StringWithTags) select_block.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey());


                flagForCluster=false;

                select_cluster.setSelection(position);
                fill_SchoolsForSpinner(Integer.parseInt(((StringWithTags) select_cluster.getSelectedItem()).id.toString()));
                flagForSchool=false;
                SharedPreferences sharedPreferences = getSharedPreferences("Navigationboundary", MODE_PRIVATE);
                sharedPreferences.edit().clear();
                finishProgress();
                DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());







            }

            @Override
            public void failed(String message) {
                finishProgress();
                DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());

            }


            @Override
            public void update(int message) {
                updateProgressMessage(select_cluster.getSelectedItem().toString() + " "
                        + getResources().getString(R.string.clusterSchoolLoading), message);

            }
        });
    }


    private void fill_dropdown(int type, int id, int parent, String statekey) {
        List<StringWithTags> stringWithTags = get_boundary_data(parent, statekey, id);
        NoDefaultSpinner spinner = findViewById(id);
    //     spinner.setOnItemSelectedListener(this);
        ArrayAdapter<StringWithTags> boundaryArrayAdapter = new ArrayAdapter<StringWithTags>(this, R.layout.spinnertextview, stringWithTags);
        if(id==R.id.select_district) {


            select_district.setAdapter(boundaryArrayAdapter);

        }else if(id==R.id.select_block)
        {


            select_block.setAdapter(boundaryArrayAdapter);

        }else if(id==R.id.select_cluster)
        {


            select_cluster.setAdapter(boundaryArrayAdapter);

        }
        boundaryArrayAdapter.setDropDownViewResource(R.layout.spinnertextview);
      //  boundaryArrayAdapter.notifyDataSetChanged();
    }


    private List<StringWithTags> get_boundary_data(int parent, String stateKey, int idview) {
        Query listboundary = Query.select().from(Boundary.TABLE)
                .where(Boundary.PARENT_ID.eq(parent).and(Boundary.TYPE.eq("primaryschool").and(Boundary.STATE_KEY.eq(stateKey))))
                .orderBy(Boundary.NAME.asc());

        List<StringWithTags> boundaryList = new ArrayList<StringWithTags>();
        boundary_cursor = db.query(Boundary.class, listboundary);
        if (boundary_cursor.moveToFirst()) {
            do {
                Boundary b = new Boundary(boundary_cursor);

                StringWithTags boundary = new StringWithTags(b.getName(), b.getId(), b.getHierarchy().equals("district") ? "1" : b.getParentId(), getLocTextBoundary(b), mSession, b.isFlag(), b.isFlagCB());
                boundaryList.add(boundary);

            } while (boundary_cursor.moveToNext());
        }
        if (boundary_cursor != null)
            boundary_cursor.close();
        if (idview == R.id.select_district) {
            boundaryList.add(0, new StringWithTags(getResources().getString(R.string.selectDistrict), "0", "0", getResources().getString(R.string.selectDistrict), mSession, false, false));
        } else if(idview == R.id.select_block) {
            boundaryList.add(0, new StringWithTags(getResources().getString(R.string.selectblock), "0", "0", getResources().getString(R.string.selectblock), mSession, false, false));

        }else {
            boundaryList.add(0, new StringWithTags(getResources().getString(R.string.selectCluster), "0", "0", getResources().getString(R.string.selectCluster), mSession, false, false));

        }
        return boundaryList;
    }

    public String getLocTextBoundary(Boundary b) {
        if (mSession.getLanguagePosition() <= 1) {
            //english
            return b.getName() != null ? b.getName() : b.getLocName();

        } else {
            //native
            return b.getLocName() != null ? b.getLocName() : b.getName();
        }
    }




    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        StringWithTags boundaryForSelector = null;
        try {
            boundaryForSelector = (StringWithTags) parent.getItemAtPosition(pos);
        } catch (Exception e) {

        }

        int viewid = 0;
        if (parent != null) {
            viewid = parent.getId();
        }


        switch (viewid) {


            case R.id.select_state:
                flagForDistrict = false;
                fill_dropdown(1, R.id.select_district, 1, ((StatePojo) select_state.getSelectedItem()).getStateKey());

                if (boundaryForSelector != null) {
                    fill_dropdown(1, R.id.select_block, Integer.parseInt(boundaryForSelector.id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey());

                } else {

                    fill_dropdown(1, R.id.select_block, 0, ((StatePojo) select_state.getSelectedItem()).getStateKey());

                }

              checkCluster();

            checkSchool();



                break;


            case R.id.select_district:
                flagForDistrict = false;
                Log.d("shri","view");
                 if (boundaryForSelector != null) {
                    fill_dropdown(1, R.id.select_block, Integer.parseInt(boundaryForSelector.id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey());

                } else {
                    fill_dropdown(1, R.id.select_block, 0, ((StatePojo) select_state.getSelectedItem()).getStateKey());

                }

               checkCluster();
                checkSchool();



                break;

            case R.id.select_block:
                checkCluster();
               checkSchool();
                break;
                case R.id.select_cluster:
                   checkSchool();
                    break;


            default:

                break;


        }

    }

    public void checkSchool()
    {
        if(select_cluster.getSelectedItemPosition()>0) {
            fill_SchoolsForSpinner(Integer.parseInt(((StringWithTags) select_cluster.getSelectedItem()).id.toString()));

        }else {
            fill_SchoolsForSpinner(0);

        }
    }


    public void checkCluster()
    {
        if (select_block.getSelectedItemPosition()!=0&& select_block.getSelectedItem() != null) {
            fill_dropdown(1, R.id.select_cluster, Integer.parseInt(((StringWithTags)select_block.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey());

        } else {
            fill_dropdown(1, R.id.select_cluster, 0, ((StatePojo) select_state.getSelectedItem()).getStateKey());

        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void loadData(final long id, final String stateKey, final boolean isDataAlreadyDownloaded, final String token) {

        //loading block,cluster and school
        initPorgresssDialogForSchool();

        updateProgressMessage(select_district.getSelectedItem().toString() + " " + getResources().getString(R.string.districtblockloading), 0);

        String url = BuildConfig.HOST + "/api/v1/boundary/admin1/" + id + "/admin2/?per_page=0";

        new A3NetWorkCalls((BoundaryLoaderActivity.this)).DownloadBlocksData(url, stateKey, isDataAlreadyDownloaded, token, new SchoolStateInterface() {
            @Override
            public void success(String message) {

                updateProgressMessage(select_district.getSelectedItem().toString() + " " + getResources().getString(R.string.districtClusterLoading), 0);
                String url = BuildConfig.HOST + "/api/v1/boundary/admin1/" + id + "/admin3?per_page=0";
                new A3NetWorkCalls(BoundaryLoaderActivity.this).DownloadClusterData(url, id, stateKey, isDataAlreadyDownloaded, token, new SchoolStateInterface() {
                    @Override
                    public void success(String message) {
                        finishProgress();
                        DailogUtill.showDialog(select_district.getSelectedItem().toString() + " " + getResources().getString(R.string.blockandCLusterdownloaded), getSupportFragmentManager(), getApplicationContext());
                        int position = select_district.getSelectedItemPosition();
                        fill_dropdown(1, R.id.select_district, 1, ((StatePojo) select_state.getSelectedItem()).getStateKey());
                        select_district.setSelection(position);
                        flagForDistrict = false;
                        flagForblock = false;
                        flagForSchool=false;
                        //setPosition();
                        fill_dropdown(1, R.id.select_block, Integer.parseInt(((StringWithTags) select_district.getSelectedItem()).id.toString()), ((StatePojo) select_state.getSelectedItem()).getStateKey());
                        checkCluster();
                        checkSchool();
                    }

                    @Override
                    public void failed(String message) {
                        finishProgress();
                        DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
                    }

                    @Override
                    public void update(int message) {
                        updateProgressMessage(select_district.getSelectedItem().toString() + " " + getResources().getString(R.string.districtClusterLoading), message);
                    }


                });

            }

            @Override
            public void failed(String message) {
                finishProgress();

                DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
            }

            @Override
            public void update(int message) {
                //show progress bar for block
                updateProgressMessage(select_district.getSelectedItem().toString() + " " + getResources().getString(R.string.districtblockloading), message);
            }
        });

    }


    /*public void setPosition() {
        select_district.setSelection(mSession.getBounaryPosition());
        flagForDistrict = false;
    }
*/

    private void fill_SchoolsForSpinner(int parent) {
        List<StringWithTags> schoolList;
        schoolList = get_school_data(parent);

        ArrayAdapter<StringWithTags> adapter=new ArrayAdapter<StringWithTags>(this, R.layout.spinnertextview, schoolList);

        select_school.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinnertextview);
       /* if(schoolList.size()<=0)
        {
            showDialogConstant("No Schools found");
        }*/


    }

    private List<StringWithTags> get_school_data(int parent) {
        Query listschool = Query.select().from(School.TABLE)
                .where(School.BOUNDARY_ID.eq(parent));
        List<StringWithTags> schoolList = new ArrayList<StringWithTags>();
      SquidCursor<School>  school_cursor = db.query(School.class, listschool);
        if (school_cursor.moveToFirst()) {
            do {
                School sch = new School(school_cursor);
                StringWithTags school = new StringWithTags(sch.getName(), sch.getId(), 1,sch.getName() , mSession,false, false);

               // StringWithTags school = new StringWithTags(sch.getName(), sch.getId(), 1, true, mSession);
                schoolList.add(school);

            } while (school_cursor.moveToNext());
        }
        if (school_cursor != null)
            school_cursor.close();
        StringWithTags school = new StringWithTags(getResources().getString(R.string.selectSchool), 0, 0,getResources().getString(R.string.selectSchool) , mSession,false, false);

         schoolList.add(0,school);
        return schoolList;
    }
    private void initPorgresssDialogForSchool() {
        progressDialog = new ProgressDialog(BoundaryLoaderActivity.this);
        progressDialog.setMessage("");
        progressDialog.setProgress(0);//initially progress is 0
        progressDialog.setMax(100);//sets the maximum value 100
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    private void updateProgressMessage(String message, int count) {

        progressDialog.setMessage(message);
        progressDialog.setProgress(count);

    }

    private void finishProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }


}
