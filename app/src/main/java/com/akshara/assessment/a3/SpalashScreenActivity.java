package com.akshara.assessment.a3;

import android.content.Intent;
import android.os.Build;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akshara.assessment.a3.NetworkRetrofitPackage.A3NetWorkCalls;
import com.akshara.assessment.a3.NetworkRetrofitPackage.CurrentStateInterface;
import com.akshara.assessment.a3.UtilsPackage.AppStatus;
import com.akshara.assessment.a3.UtilsPackage.DailogUtill;
import com.akshara.assessment.a3.UtilsPackage.SessionManager;
import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.State;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;


public class SpalashScreenActivity extends BaseActivity {

    TextView tv2;
    ImageView tv1;

    KontactDatabase db;
    private SessionManager mSession;
    ProgressBar dailog;
    Button btnInternt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_spalash_screen);
        mSession = new SessionManager(getApplicationContext());
        //DatabaseCopyHelper dbCopyHelper = new DatabaseCopyHelper(this);
//        SQLiteDatabase dbCopy = dbCopyHelper.getReadableDatabase();
        db = ((A3Application) getApplicationContext()).getDb();
        btnInternt = findViewById(R.id.btnInternt);
        btnInternt.setVisibility(View.GONE);
//Log.d("shri", this.getLocalClassName());
        dailog = findViewById(R.id.progressBar);
        //dailog = ProgressUtil.showProgress(SpalashScreenActivity.this, getResources().getString(R.string.authenticating));
        //   dailog=new ProgressDialog(SpalashScreenActivity.this);
        //dailog.setProgressStyle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryToolbar));
        }


        // check state and language if user first time login

        if (mSession.isLoggedIn()) {
            Log.d("shri",mSession.isLoggedIn()+"------");
            //langauge screen
            if (getStateCount() == 0) {
                loadStateDeatil();
            } else {
                if (mSession.isLoggedIn() && mSession.isSetupDone()) {
                    Log.d("shri",mSession.isSetupDone()+"----set--");
                    Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    Toast.makeText(getApplicationContext(), "loggedin", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), BoundaryLoaderActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            }

            //direct it survey screen
        } else {
            loadStateDeatil();


        }


        btnInternt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadStateDeatil();
            }
        });


    }


    public void loadStateDeatil() {
        dailog.setVisibility(View.VISIBLE);
        new A3NetWorkCalls(SpalashScreenActivity.this).getStateAndUserDeail(new CurrentStateInterface() {
            @Override
            public void setSuccess(String message) {

                dailog.setVisibility(View.GONE);

                Intent intent = new Intent(getApplicationContext(), LanguageSelectionActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }

            @Override
            public void setFailed(String message) {

                dailog.setVisibility(View.GONE);

                if (AppStatus.isConnected(getApplicationContext())) {
                    DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
                    btnInternt.setVisibility(View.VISIBLE);
                } else {
                    DailogUtill.showDialog(message, getSupportFragmentManager(), getApplicationContext());
                    btnInternt.setVisibility(View.VISIBLE);
                }

            }
        });
    }


    public int getStateCount() {
        Query listStateQuery = Query.select().from(State.TABLE);
        SquidCursor<State> stateCursor = db.query(State.class, listStateQuery);

        if (stateCursor != null && stateCursor.getCount() > 0)

            return stateCursor.getCount();
        else
            return 0;


    }
}


