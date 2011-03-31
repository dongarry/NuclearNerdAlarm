package com.nerd.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class database_adapter{
	
		// Used tutorial : http://www.devx.com/wireless/Article/40842
	
		public static final String KEY_ROWID = "_id";
	    public static final String KEY_TIME = "time";
	    public static final String KEY_TITLE = "title";
	    public static final String KEY_REPEAT = "repeat";    
	    private static final String TAG = "DBAdapter";
	    
	    private static final String DATABASE_NAME = "NuclearAlarms";
	    private static final String DATABASE_TABLE = "Alarms";
	    private static final int DATABASE_VERSION = 1;

	    private static final String DATABASE_CREATE =
	        "create table NuclearAlarms (_id integer primary key autoincrement, "
	        + "time text not null, title text not null, " 
	        + "repeat integer not null);";
	        
	    private final Context context; 
	    
	    private DatabaseHelper DBHelper;
	    private SQLiteDatabase db;

	    public database_adapter(Context ctx) 
	    {
	        this.context = ctx;
	        DBHelper = new DatabaseHelper(context);
	    }
	        
	    private static class DatabaseHelper extends SQLiteOpenHelper 
	    {
	        DatabaseHelper(Context context) 
	        {
	            super(context, DATABASE_NAME, null, DATABASE_VERSION);
	        }

	        @Override
	        public void onCreate(SQLiteDatabase db) 
	        {
	            db.execSQL(DATABASE_CREATE);
	        }

	        @Override
	        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
	        int newVersion) 
	        {
	            Log.w(TAG, "Upgrading database from version " + oldVersion 
	                    + " to "
	                    + newVersion + ", which will destroy all old data");
	            db.execSQL("DROP TABLE IF EXISTS Alarms");
	            onCreate(db);
	        }
	    }    
	    
	    //---opens the database---
	    public database_adapter open() throws SQLException 
	    {
	        db = DBHelper.getWritableDatabase();
	        return this;
	    }

	    //---closes the database---    
	    public void close() 
	    {
	        DBHelper.close();
	    }
	    
	    //---insert a title into the database---
	    public long insertAlarm(String time, String title, int repeat) 
	    {
	        ContentValues initialValues = new ContentValues();
	        initialValues.put(KEY_TIME, time);
	        initialValues.put(KEY_TITLE, title);
	        initialValues.put(KEY_REPEAT, repeat);
	        return db.insert(DATABASE_TABLE, null, initialValues);
	    }

	    //---deletes a particular title---
	    public boolean deleteAlarm(long rowId) 
	    {
	        return db.delete(DATABASE_TABLE, KEY_ROWID + 
	        		"=" + rowId, null) > 0;
	    }

	    //---retrieves all the alarms---
	    public Cursor getAllAlarms() 
	    {
	        return db.query(DATABASE_TABLE, new String[] {
	        		KEY_ROWID, 
	        		KEY_TIME,
	        		KEY_TITLE,
	                KEY_REPEAT}, 
	                null, 
	                null, 
	                null, 
	                null, 
	                null);
	    }

	    //---retrieves a particular Alarm---
	    public Cursor getAlarm(long rowId) throws SQLException 
	    {
	        Cursor mCursor =
	                db.query(true, DATABASE_TABLE, new String[] {
	                		KEY_ROWID,
	                		KEY_TIME, 
	                		KEY_TITLE,
	                		KEY_REPEAT
	                		}, 
	                		KEY_ROWID + "=" + rowId, 
	                		null,
	                		null, 
	                		null, 
	                		null, 
	                		null);
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
	        return mCursor;
	    }

	    //---updates an alarm---
	    public boolean updateAlarm(long rowId, String time, 
	    String title, Integer repeat) 
	    {
	        ContentValues args = new ContentValues();
	        args.put(KEY_TIME, time);
	        args.put(KEY_TITLE, title);
	        args.put(KEY_REPEAT, repeat);
	        return db.update(DATABASE_TABLE, args, 
	                         KEY_ROWID + "=" + rowId, null) > 0;
	    }
	}
