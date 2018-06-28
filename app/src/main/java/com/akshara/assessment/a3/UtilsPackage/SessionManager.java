package com.akshara.assessment.a3.UtilsPackage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.akshara.assessment.a3.A3Application;
import com.akshara.assessment.a3.LoginActivity;
import com.akshara.assessment.a3.R;
import com.akshara.assessment.a3.SpalashScreenActivity;
import com.akshara.assessment.a3.db.AssessmentTypeTable;
import com.akshara.assessment.a3.db.Boundary;
import com.akshara.assessment.a3.db.InstititeGradeIdTable;
import com.akshara.assessment.a3.db.KontactDatabase;
import com.akshara.assessment.a3.db.ProgramTable;
import com.akshara.assessment.a3.db.QuestionDataTable;
import com.akshara.assessment.a3.db.QuestionSetDetailTable;
import com.akshara.assessment.a3.db.QuestionSetTable;
import com.akshara.assessment.a3.db.QuestionTable;
import com.akshara.assessment.a3.db.Respondent;
import com.akshara.assessment.a3.db.School;
import com.akshara.assessment.a3.db.State;
import com.akshara.assessment.a3.db.StudentTable;


import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


public class SessionManager {
    private static final String STATEPOSITION = "STATEPOSITION";
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    private KontactDatabase db;
    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "in.org.klp.mobile.KLPPrefs";

    // All Shared Preferences Keys
    private static final String IS_LOGGED_IN = "isLoggedIn";

    // User name (make variable public to access from outside)
    //first Name
    public static final String KEY_NAME = "userName";

    // Email address (make variable public to access from outside)
    public static final String KEY_ID = "userId";
    // Token
    public static final String KEY_TOKEN = "token";
    public static final String USER_TYPE = "userType";
    public static final String STATE = "STATE";
    public static final String LANGUAGE = "LANGUAGE";
    public static final String LANGUAGE_KEY = "LANGUAGE_KEY";
    public static final String STATE_KEY = "STATE_KEY";


    public static final String DOB = "DOB";
    public static final String LAST_NAME = "LAST_NAME";
    public static final String EMAIL = "EMAIL";
    public static final String MOBILE = "MOBILE";

    public static final String SETUP = "SETUP";
    public static final String STATESELECTION = "STATESELECTION";

    public static final String LANGUAGEPOSITION = "LANGUAGEPOSITION";
    public static final String PROGRAM = "PROGRAM";
    public static final String PROGRAM_ID = "PROGRAM_ID";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        db = ((A3Application) context).getDb();

    }

    /**
     * Create login session
     */
    public void createLoginSession(String name, String id, String token, String lastName, String email, String mobile, String dob, String usertype) {

        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_TOKEN, token);
        editor.putString(USER_TYPE, usertype);
        editor.putString(LAST_NAME, lastName);
        editor.putString(EMAIL, email);
        editor.putString(MOBILE, mobile);
        editor.putString(DOB, dob);
        editor.putBoolean(SETUP, false);

        // commit changes
        editor.commit();
    }


    public void updateSetup(boolean flag) {
        editor.putBoolean(SETUP, flag);

        // commit changes
        editor.commit();
    }


    public void setStateSelection(String stateKey) {
        editor.putString(STATESELECTION, stateKey);

        // commit changes
        editor.commit();
    }

    public String getStateSelection() {

        return pref.getString(STATESELECTION, "ka");
    }


    public boolean isSetupDone() {

        return pref.getBoolean(SETUP, false);
    }


    public void updateSession(String firstName, String lastName, String dob, String email, String usertype) {
        editor.putString(KEY_NAME, firstName);

        editor.putString(USER_TYPE, usertype);
        editor.putString(LAST_NAME, lastName);
        editor.putString(EMAIL, email);
        editor.putString(DOB, dob);
        // commit changes
        editor.commit();

    }

    public String getUserType() {
        return pref.getString(USER_TYPE, "PR");
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        // user token
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     */

    public void setLanguage(String state, String language, String langkey, String stateKey) {
        editor.putString(STATE, state);
        editor.putString(STATE_KEY, stateKey);
        editor.putString(LANGUAGE, language);
        editor.putString(LANGUAGE_KEY, langkey);
        editor.commit();
    }


    public String getFirstName() {
        return pref.getString(KEY_NAME, null);

    }

    public int getLanguagePosition() {
        return pref.getInt(LANGUAGEPOSITION, 0);

    }

    public String getLastName() {
        return pref.getString(LAST_NAME, null);
    }

    public String getDOB() {
        return pref.getString(DOB, null);
    }

    public String getEmail() {
        return pref.getString(EMAIL, "");
    }

    public String getMobile() {
        return pref.getString(MOBILE, null);
    }


    public String getStateKey() {
        return pref.getString(STATE_KEY, "");
    }

    public String getState() {
        return pref.getString(STATE, "no");
    }


    public String getLanguage() {
        return pref.getString(LANGUAGE, "no");
    }


    public String getLanguageKey() {
        return pref.getString(LANGUAGE_KEY, "no");
    }

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, SpalashScreenActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);

    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }


    public String getToken() {
        return "Token " + pref.getString(KEY_TOKEN, "");

    }

    public void setStatePosition(int statePosition) {
        editor.putInt(STATEPOSITION, statePosition);
        editor.commit();
    }

    public void setLanguagePosition(int langpos) {
        editor.putInt(LANGUAGEPOSITION, langpos);
        editor.commit();
    }


    public int getStatePosition() {

        return pref.getInt(STATEPOSITION, 0);
    }

    public void logoutUserDB() {
        // KLPApplication.setLanguage(getApplicationContext(), "en");

        A3Application.setLanguage(_context, "en");

        db.deleteAll(Boundary.class);
        db.deleteAll(School.class);
        db.deleteAll(StudentTable.class);
        db.deleteAll(QuestionSetDetailTable.class);
        db.deleteAll(QuestionDataTable.class);
        db.deleteAll(QuestionSetTable.class);
        db.deleteAll(QuestionTable.class);
        db.deleteAll(Respondent.class);
        db.deleteAll(State.class);
        db.deleteAll(ProgramTable.class);
        db.deleteAll(InstititeGradeIdTable.class);
        db.deleteAll(AssessmentTypeTable.class);
        db.clear();
        _context.getSharedPreferences("Navigationboundary", MODE_PRIVATE).edit().clear().commit();
        _context.getSharedPreferences("loader", MODE_PRIVATE).edit().clear().commit();


        logoutUser();
    }

    public void setProgram(String program) {
        editor.putString(PROGRAM, program);
        editor.commit();
    }

    public String getProgramFromSession() {

        return pref.getString(PROGRAM,"");
    }

    public void setProgramId(long programId) {
        editor.putLong(PROGRAM_ID, programId);
        editor.commit();
    }

    public long getProgramIdFromSession() {

        return pref.getLong(PROGRAM_ID,0);
    }


}

