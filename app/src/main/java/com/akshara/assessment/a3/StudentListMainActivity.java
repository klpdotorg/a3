package com.akshara.assessment.a3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.akshara.assessment.a3.R;

import java.util.ArrayList;
import java.util.List;

public class StudentListMainActivity extends BaseActivity {



    String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabed_activity);

        title=getIntent().getStringExtra("A3APP_GRADESTRING");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
        setupNavigationView();



    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
       navigateBackStack();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
               navigateBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void navigateBackStack()
    {





        Intent intent=new Intent(getApplicationContext(),AssessmentSelectorActivity.class);
        intent .putExtra("A3APP_INSTITUTIONID",getIntent().getLongExtra("A3APP_INSTITUTIONID",0L));
        intent  .putExtra("A3APP_GRADEID",getIntent().getIntExtra("A3APP_GRADEID",0));
        intent  .putExtra("A3APP_GRADESTRING",getIntent().getStringExtra("A3APP_GRADESTRING"));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        finish();
    }

    private void setupNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {

            // Select first menu item by default and show Fragment accordingly.
            Menu menu = bottomNavigationView.getMenu();
            selectFragment(menu.getItem(0));
            bottomNavigationView.setItemIconTintList(null);
            // Set action to perform when any menu-item is selected.
            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            selectFragment(item);
                            return false;
                        }
                    });
        }
    }

    protected void selectFragment(MenuItem item) {

        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.action_studentList:
                // Action to perform when Home Menu item is selected.
                pushFragment(new StudentListFragment());
                break;
            case R.id.action_register:
                // Action to perform when Bag Menu item is selected.
                pushFragment(new RegisterStudentActivity());
                break;

        }
    }
    protected void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.rootLayout, fragment);
                ft.commit();
            }
        }
    }

}