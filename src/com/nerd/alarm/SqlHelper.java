package com.nerd.alarm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
public class SqlHelper extends SQLiteOpenHelper {
    public static final String DATABASE_PATH = "/data/data/com.nerd.alarm/databases/";
    public static final String DATABASE_NAME = "NuclearAlarms";
    public static final String TABLE_NAME = "Alarms";
    public static final String COLUMN_ID = "_id";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TIME = "time";
    public static final String KEY_TITLE = "title";
    public static final String KEY_REPEAT = "repeat";    
    public static final String KEY_ENABLED = "enabled";    
    public static final String KEY_COUNTER = "counter";    
    public SQLiteDatabase dbSqlite;
    private final Context myContext;
    public SqlHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.myContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // check if exists and copy database from resource
        createDB();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SqlHelper", "Upgrading database from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data");
        onCreate(db);
    }
    public void createDatabase() {
        createDB();
    }
    private void createDB() {
        boolean dbExist = DBExists();
        if (!dbExist) {
            copyDBFromResource();
 
        }
    }
    private boolean DBExists() {
        SQLiteDatabase db = null;
 
        try {
            String databasePath = DATABASE_PATH + DATABASE_NAME;
            db = SQLiteDatabase.openDatabase(databasePath, null,
                    SQLiteDatabase.OPEN_READWRITE);
            db.setLocale(Locale.getDefault());
            db.setLockingEnabled(true);
            db.setVersion(1);

        } catch (SQLiteException e) {
            Log.e("SqlHelper", "database not found"); 
        }
        if (db != null) {
            db.close();
        }
        return db != null ? true : false;
    }
    
    private void copyDBFromResource() {
        InputStream inputStream = null;
        OutputStream outStream = null;
        String dbFilePath = DATABASE_PATH + DATABASE_NAME;
        try {
            inputStream = myContext.getAssets().open(DATABASE_NAME);
            outStream = new FileOutputStream(dbFilePath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }
            outStream.flush();
            outStream.close();
            inputStream.close();
        } catch (IOException e) {
            throw new Error("Problem copying database from resource file.");
        }
    }
    public void openDataBase() throws SQLException {
        String myPath = DATABASE_PATH + DATABASE_NAME;
        dbSqlite = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);
    }
    @Override
    public synchronized void close() {
        if (dbSqlite != null)
            dbSqlite.close();
        super.close();
    }
    public Cursor getCursor() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);
        String[] asColumnsToReturn = new String[] { COLUMN_ID, KEY_TITLE,
        		KEY_TIME, KEY_REPEAT };
        Cursor mCursor = queryBuilder.query(dbSqlite, asColumnsToReturn, null,
                null, null, null, "time desc");
        return mCursor;
    }
    public void clearSelections() {
        ContentValues values = new ContentValues();
        values.put(" selected", 0);
        this.dbSqlite.update(SqlHelper.TABLE_NAME, values, null, null);
    }
}
