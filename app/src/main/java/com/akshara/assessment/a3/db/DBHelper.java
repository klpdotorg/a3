package com.akshara.assessment.a3.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

   public static final String DATABASE_NAME = DB_CONSTANTS.DB_NAME;


   public DBHelper(Context context) {
      super(context, DATABASE_NAME , null, DB_CONSTANTS.DB_VERSION);
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
      // TODO Auto-generated method stub

   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // TODO Auto-generated method stub
    /*  db.execSQL("DROP TABLE IF EXISTS contacts");
      onCreate(db);*/
      Log.d("shri","Upgrade");
   }


   
   public int getData() {
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor res =  db.rawQuery( "select * from state", null );
      return   res.getCount();

   }
   




}