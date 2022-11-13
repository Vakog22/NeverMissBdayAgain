package com.example.nevermissbdayagain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_EVENTS_TABLE = "create table " +
            DBStructure.EVENT_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBStructure.PERSON_NAME + " TEXT, " +
            DBStructure.PERSON_AGE + " TEXT, " +
            DBStructure.DATE + " TEXT, " +
            DBStructure.MONTH + " TEXT, " +
            DBStructure.YEAR + " TEXT)";

    private static final String DROP_EVENTS_TABLE = "DROP TABLE IF EXISTS " + DBStructure.EVENT_TABLE_NAME;


    public DBOpenHelper(@Nullable Context context) {
        super(context, DBStructure.DB_NAME, null, DBStructure.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_EVENTS_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void clearDatabase(String TABLE_NAME, SQLiteDatabase sqLiteDatabase) {
        String clearDBQuery = "DELETE FROM "+TABLE_NAME;
        sqLiteDatabase.execSQL(clearDBQuery);
    }

    public void SaveEvent(String person_name, String person_age, String date, String month, String year, SQLiteDatabase sqLiteDatabase){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBStructure.PERSON_NAME,person_name);
        contentValues.put(DBStructure.PERSON_AGE,person_age);
        contentValues.put(DBStructure.DATE,date);
        contentValues.put(DBStructure.MONTH,month);
        contentValues.put(DBStructure.YEAR,year);
        sqLiteDatabase.insert(DBStructure.EVENT_TABLE_NAME, null, contentValues);
    }

    public Cursor ReadEvents(String date, SQLiteDatabase sqLiteDatabase){
        String[] Projections = {DBStructure.PERSON_NAME, DBStructure.PERSON_AGE, DBStructure.DATE, DBStructure.MONTH, DBStructure.YEAR};
        String Selection = DBStructure.DATE + "=?";
        String[] SelectionArgs = {date};

        return  sqLiteDatabase.query(DBStructure.EVENT_TABLE_NAME,Projections,Selection,SelectionArgs,null,null,null);
    }

    public Cursor ReadEventsMonth(String month,String year, SQLiteDatabase sqLiteDatabase){
        String[] Projections = {DBStructure.PERSON_NAME, DBStructure.PERSON_AGE, DBStructure.DATE, DBStructure.MONTH, DBStructure.YEAR};
        String Selection = DBStructure.MONTH + "=? and " + DBStructure.YEAR + "=?";
        String[] SelectionArgs = {month,year};

        return  sqLiteDatabase.query(DBStructure.EVENT_TABLE_NAME,Projections,Selection,SelectionArgs,null,null,null);
    }

    public void DeleteEvent(String person_name, String person_age, String date, SQLiteDatabase sqLiteDatabase){
        String Selection = DBStructure.PERSON_NAME + "=? and " + DBStructure.PERSON_AGE + "=? and " + DBStructure.DATE + "=?";
        String[] SelectionArgs = {person_name,person_age,date};
        sqLiteDatabase.delete(DBStructure.EVENT_TABLE_NAME, Selection, SelectionArgs);
    }
}
