package cric_grab.sqlite.cric_grap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by userws49 on 11/11/2015.
 */
public class Add_player_SqliteManagement {
    private Context context;
    DBHelper dbHelper = null;
    SQLiteDatabase sqLiteDatabase = null;

    public Add_player_SqliteManagement(Context context) {
        this.context = context;

    }

    public void open() {
        dbHelper = new DBHelper(context);
        try {
            sqLiteDatabase = dbHelper.getWritableDatabase();
            Log.d("DataBase opened", "Open");
        } catch (Exception e) {
            Log.d("DataBase opened", "Failed exception");
            e.printStackTrace();
        }

    }

    public void close() {

        try {
            if (dbHelper != null) {
                Log.d("DataBase closed", "close");
                dbHelper.close();
            }
        } catch (Exception e) {
            Log.d("closed Exception", "exception");
            e.printStackTrace();
        }

    }

    public ArrayList<String> spinnerName(){
        ArrayList<String> arrayList=null;
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT "+DBHelper.NAME+" FROM "+DBHelper.TABLE_NAME,null);
        if(cursor.moveToFirst()){
            arrayList=new ArrayList<>();
            do{
                Log.e("Name",cursor.getString(cursor.getColumnIndex(DBHelper.NAME)));
                arrayList.add(cursor.getString(cursor.getColumnIndex(DBHelper.NAME)));
            }while(cursor.moveToNext());
        }

        return arrayList;
    }

    public Long registration(String userName, String mobileNumber) {
        long result = -1;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.NAME, userName);
            contentValues.put(DBHelper.MOBILE_NUMBER, mobileNumber);
            result = sqLiteDatabase.insert(DBHelper.TABLE_NAME, null, contentValues);
            if (result > 0) {
                Log.i("Registration", "Result" + result);
                return result;
            } else if (result <= 0) {
                Log.i("Registration else", "Result" + result);
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.w("Exception", "________Result_____" + result);
            return result;

        }
        return null;

    }

    public JSONArray getdetails() {
        String[] columns = {DBHelper.NAME,DBHelper.MOBILE_NUMBER};
        Cursor cursor = sqLiteDatabase.query(DBHelper.TABLE_NAME, columns, null, null, null, null, null, null);
        JSONArray jsonArray = new JSONArray();
        while (cursor.moveToNext()) {
            Log.e("Cursor Result", cursor.getString(cursor.getColumnIndex(DBHelper.NAME)));
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("player_name", cursor.getString(cursor.getColumnIndex(DBHelper.NAME)).toString());
                jsonObject.put("player_number", cursor.getString(cursor.getColumnIndex(DBHelper.MOBILE_NUMBER)).toString());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    public void delete() {
        try {

            sqLiteDatabase.delete(DBHelper.TABLE_NAME, null, null);
            System.out.println("DeleteTable Gets Called");
        } catch (Exception exception) {
            System.out.println("DeleteTable one Gets Exception");
        }

    }

    private static class DBHelper extends SQLiteOpenHelper {
        
        private static final String DATABASE_NAME = "PLAYER_DATABASE";
        private static final int DATABASE_VERSION = 2;
        private static final String TABLE_NAME = "PLAYER_LIST";
        private static final String NAME = "name";
        private static final String MOBILE_NUMBER = "mobileNumber";
        private Context context;
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + NAME + " VARCHAR2(255)," + MOBILE_NUMBER + " VARCHAR2(255),PRIMARY KEY(" + MOBILE_NUMBER + "))";
        private static final String Drop_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE);
                Log.i("OnCreate", "Table Created");

            } catch (SQLException e
                    ) {
                e.printStackTrace();
                Log.i("OnCreate", "Table Created Exception");

            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(Drop_TABLE);
                onCreate(db);
                Log.i("OnUpgrade", "Table upgraded");

            } catch (SQLException e) {
                e.printStackTrace();
                Log.i("OnUpgrade", "Table Upgraded Exception");

            }

        }
    }

}

