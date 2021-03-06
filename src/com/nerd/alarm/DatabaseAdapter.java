package com.nerd.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/* Credits:
 * http://www.devx.com/wireless/Article/40842
 * 
 * Nerd Alarm - Handles all database transactions to store/update/delete alarms
 * TODO Add stat to insert and update so that we can keep track of how many times alarm was used etc.
 * 
 */

public class DatabaseAdapter{
	
	
		public static final String KEY_ROWID = "_id";
	    public static final String KEY_TIME = "time";
	    public static final String KEY_TITLE = "title";
	    public static final String KEY_REPEAT = "repeat";    
	    public static final String KEY_ENABLED = "enabled";    
	    public static final String KEY_COUNTER = "counter"; // Used to determine behavior    
	    public static final String KEY_MODE = "mode";    
	    public static final String KEY_TEST = "test";    
	    private static final String TAG = "Nerd Alarm DBAdapter";
	    
	    private static final String DATABASE_NAME = "NuclearAlarms";
	    private static final String DATABASE_TABLE = "Alarms";
	    private static final int DATABASE_VERSION = 1;
	    
	    private static final String ALL_ALARM_DELETE = "DELETE FROM " + DATABASE_TABLE + ";";
	    private static final String DATABASE_CREATE =
	        "CREATE TABLE " + DATABASE_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
	        + "time TEXT NOT NULL, title TEXT NOT NULL, " 
	        + "repeat INTEGER NOT NULL, enabled INTEGER NOT NULL, counter INTEGER NOT NULL," +
	        		"mode INTEGER NOT NULL, test INTEGER NOT NULL);";
	        
	    private final Context context; 
	    
	    private DatabaseHelper DBHelper;
	    private SQLiteDatabase db;

	    public DatabaseAdapter(Context ctx) 
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
	    
	    public Cursor getEnabledAlarms() throws SQLException 
	    {
	        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
	        								KEY_ROWID,
						            		KEY_ENABLED}, 
						            		KEY_ENABLED + "=1", 
						            		null,
						            		null, 
						            		null, 
						            		null, 
						            		null);
    
				    if (mCursor != null) {
				        mCursor.moveToFirst();}
				 
				    return mCursor;
	       
	    }
	    
	    public Cursor getLatestAlarm(long rowId) throws SQLException 
	    {
	        Cursor mCursor =
	                db.query(true, DATABASE_TABLE, new String[] {
	                		KEY_ROWID,
	                		KEY_TIME, 
	                		KEY_TITLE,
	                		KEY_REPEAT,
	                		KEY_ENABLED,
	                		KEY_MODE,
	                		KEY_TEST,
	                		KEY_COUNTER}, 
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
	    
	    //---opens the database---
	    public DatabaseAdapter open() throws SQLException 
	    {
	        db = DBHelper.getWritableDatabase();
	        return this;
	    }

	    //---closes the database---    
	    public void close() 
	    {
	        DBHelper.close();
	    }
	    
	  //---Drops the alarm table---    
	    public void drop() 
	    {
	    	db.execSQL("DROP TABLE IF EXISTS Alarms");
	    }
	    
	    //---insert an alarm into the database---
	    public long insertAlarm(String time, String title, int repeat,int enabled, int counter, int mode, int test)//, int stat) 
	    {
	        ContentValues initialValues = new ContentValues();
	        initialValues.put(KEY_TIME, time);
	        initialValues.put(KEY_TITLE, title);
	        initialValues.put(KEY_REPEAT, repeat);
	        initialValues.put(KEY_ENABLED, enabled);
	        initialValues.put(KEY_COUNTER, counter);
	        initialValues.put(KEY_MODE, mode);
	        initialValues.put(KEY_TEST, test);
	        
	        return db.insert(DATABASE_TABLE, null, initialValues);
	    }

	    //---deletes a particular alarm---
	    public boolean deleteAllAlarms() 
	    {
	    		db.execSQL(ALL_ALARM_DELETE);
	    		return true;
	    }
	    
	    //---deletes all alarms---
	    public boolean deleteAlarm(long rowID) 
	    {
	        return db.delete(DATABASE_TABLE, KEY_ROWID + 
	        		"=" + rowID, null) > 0;
	    }
	    
	    
	    public Cursor getAlarms() throws SQLException 
	    {
	        return db.query(DATABASE_TABLE, new String[] {
	        		KEY_ROWID, 
	        		KEY_TIME,
	        		KEY_TITLE,
	                KEY_REPEAT,
	                KEY_ENABLED,
	                KEY_COUNTER,
	                KEY_MODE,
	                KEY_TEST}, 
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
	                		KEY_REPEAT,
	                		KEY_ENABLED,
	                		KEY_MODE,KEY_COUNTER,KEY_TEST},
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
	    
	    public boolean updateStatistic(long rowId,int counter, int enabled, String AlarmTime) 
	    	    {
	    			//TODO Change this to include Stat
	    	        ContentValues args = new ContentValues();
	    	        //args.put(KEY_TITLE, AlarmTime); //test
	    	        args.put(KEY_COUNTER, counter);
	    	        args.put(KEY_ENABLED, enabled);
	    	        return db.update(DATABASE_TABLE, args, 
	    	                         KEY_ROWID + "=" + rowId, null) > 0;
	    	    }
	    	
	    
	    //---updates an alarm---
	    public boolean updateAlarm(long rowId, String time, 
	    String title, Integer repeat, int enabled, int counter, int mode, int test) 
	    {
	        ContentValues args = new ContentValues();
	        args.put(KEY_TIME, time);
	        args.put(KEY_TITLE, title);
	        args.put(KEY_REPEAT, repeat);
	        args.put(KEY_ENABLED, enabled);
	        args.put(KEY_COUNTER, counter);
	        args.put(KEY_MODE, mode);
	        args.put(KEY_TEST, test);
	    	        
	        return db.update(DATABASE_TABLE, args, 
	                         KEY_ROWID + "=" + rowId, null) > 0;
	    }
	    
	    public boolean enableAlarm(long rowId,int enabled) 
	    	    {
	    	        ContentValues args = new ContentValues();
	    	        args.put(KEY_ENABLED, enabled);
	    	        
	    	        return db.update(DATABASE_TABLE, args, 
	    	                         KEY_ROWID + "=" + rowId, null) > 0;
	    	    }
	}
