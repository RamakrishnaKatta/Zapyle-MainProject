package DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;


/**
 * Created by haseeb on 5/5/16.
 */


public class DatabaseDB {
    public DatabaseHelper dataHelper;
    public SQLiteDatabase mDb;
    Context ctx;
    String DATABASE_PATH = "/data/data/com.zapyle.zapyle/databases/";
    static String DATABASE_NAME = "zapyle";
    private static final int DATABASE_VERSION = 4;


    public DatabaseDB(Context ctx) {
        this.ctx = ctx;
        dataHelper = new DatabaseHelper(ctx);
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {
        Context myContext = null;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.myContext = context;
            // TODO Auto-generated constructor stub
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS CART");
            String query= "CREATE TABLE IF NOT EXISTS " + "CART" + " (" + "albumId" + " VARCHAR, " + "json" + " TEXT " + ");";
            db.execSQL(query);
            onCreate(db);
        }
    }


    public boolean checkDataBaseexist() {
        SQLiteDatabase checkDB = null;
        try {
            File file = new File(DATABASE_PATH);
            if (file.exists() && !file.isDirectory()) {
                //System.out.println("INSIDE checkdatabase");
                checkDB = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY);
                checkDB.close();
            }
        } catch (SQLiteException e) {
            //System.out.println("INSIDE except");
            // database doesn't exist yet.
        }
        //System.out.println("CHECKEDB:"+checkDB);
        return checkDB != null;
    }


    public boolean checkDataBase() {
        String myPath = DATABASE_PATH + DATABASE_NAME;
        File f = new File(myPath);
        return f.exists();
    }


    public DatabaseDB openDB() throws SQLException {
        mDb = dataHelper.getWritableDatabase();
        return this;
    }

    public void processData(String strCommand) {
        System.out.println("DATABASE:"+strCommand);

        mDb.execSQL(strCommand);
    }

    public void close() {
        try {
            mDb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    Database record count
//    -------------------------------------------------

    public int getRecordsCount() {
        String countQuery = "SELECT  * FROM " + "FEED";
        mDb = dataHelper.getReadableDatabase();
        Cursor cursor = mDb.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }


    //    Database record count
//    -------------------------------------------------

    public int getTableRecordsCount(String Table) {
        String countQuery = "SELECT  * FROM " + Table;
        mDb = dataHelper.getReadableDatabase();
        Cursor cursor = mDb.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }



    //    Database record count
//    -------------------------------------------------

    public int getDIscoverRecordsCount() {
        String countQuery = "SELECT  * FROM " + "DISCOVER";
        mDb = dataHelper.getReadableDatabase();
        Cursor cursor = mDb.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }


    public Cursor getData(String query){
        Cursor mCursor= mDb.rawQuery(query,null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
}