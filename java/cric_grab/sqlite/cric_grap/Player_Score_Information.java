package cric_grab.sqlite.cric_grap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import cric_grab.utility.cric_grap.HistoryGetSet;
import cric_grab.utility.cric_grap.IndividualGetSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by ANDROID on 22-11-2015.
 */
public class Player_Score_Information {

    private Context context;
    DBHelper dbHelper = null;
    public static SQLiteDatabase sqLiteDatabase = null;
    ArrayList<HistoryGetSet> sets = new ArrayList<>();
    public static int changeButtonState;

    public Player_Score_Information(Context context) {
        this.context = context;
        Date date = new Date();
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


    public void delete() {
        try {

            sqLiteDatabase.delete(DBHelper.TABLE_NAME, null, null);
            System.out.println("DeleteTable Gets Called");
        } catch (Exception exception) {
            System.out.println("DeleteTable one Gets Exception");
        }

    }

    public long updateScore(String playerName, String ball_Number, String Score, String innings, String created_Date) {

        ContentValues contentValues = new ContentValues();
        if (Score.equalsIgnoreCase("WK")) {
            contentValues.put(DBHelper.SCORE, Score);
        } else {
            int score_num = Integer.parseInt(Score);
            contentValues.put(DBHelper.SCORE, score_num);
        }

        contentValues.put(DBHelper.PLAYER_NAME, playerName);
        long id = sqLiteDatabase.update(DBHelper.TABLE_NAME, contentValues, DBHelper.BALL_NUMBER + " =? AND " + DBHelper.INNINGS + " =? AND " + DBHelper.DATE + " =?", new String[]{ball_Number, innings, created_Date});
        Log.e("return ID", id + " long id");
        if (id < 0) {
            Toast.makeText(context, ball_Number + " is failed to Update", Toast.LENGTH_SHORT).show();
        }
        return id;

    }

    public JSONArray showOnList(String innings, String dates) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        ArrayList<IndividualGetSet> getSets = null;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_NAME + " WHERE " + DBHelper.INNINGS + "='" + innings + "' AND " + DBHelper.DATE + "='" + dates + "'", null);
        Log.w("cuesor", cursor.getCount() + "");

        if (cursor.moveToFirst()) {
            getSets = new ArrayList<>();
            Log.w("cuesor", "________" + cursor.getString(cursor.getColumnIndex(DBHelper.PLAYER_NAME)) + "");

            do {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("PlayerName", cursor.getString(cursor.getColumnIndex(DBHelper.PLAYER_NAME)));
                jsonObject.put("Ball_number", cursor.getString(cursor.getColumnIndex(DBHelper.BALL_NUMBER)));
                jsonObject.put("Score", cursor.getString(cursor.getColumnIndex(DBHelper.SCORE)));
                jsonArray.put(jsonObject);
            } while (cursor.moveToNext());
            Log.w("cuesor", jsonArray.toString());
            return jsonArray;
        }
        return null;
    }

    public JSONArray showOnExtraList(String innings, String dates) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        ArrayList<IndividualGetSet> getSets = null;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_EXTRA_NAME + " WHERE " + DBHelper.INNINGS + "='" + innings + "' AND " + DBHelper.DATE + "='" + dates + "'", null);
        Log.w("cuesor", cursor.getCount() + "");

        if (cursor.moveToFirst()) {
            getSets = new ArrayList<>();
            Log.w("cuesor", "________" + cursor.getString(cursor.getColumnIndex(DBHelper.PLAYER_NAME)) + "");

            do {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("PlayerName", cursor.getString(cursor.getColumnIndex(DBHelper.PLAYER_NAME)));
                jsonObject.put("Ball_number", cursor.getString(cursor.getColumnIndex(DBHelper.BALL_NUMBER)));
                jsonObject.put("Score", cursor.getString(cursor.getColumnIndex(DBHelper.EXTRA_RUN)));
                jsonArray.put(jsonObject);
            } while (cursor.moveToNext());
            Log.w("cuesor", jsonArray.toString());
            return jsonArray;
        }
        return null;
    }

    public JSONArray searchList(String date) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        Cursor cursor;
        HistoryGetSet historyGetSet = new HistoryGetSet();
        if (date.equalsIgnoreCase("getAll")) {
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_DASH_NAME, null);
        } else {
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_DASH_NAME + " WHERE " + DBHelper.DATE + "='" + date + "'", null);
        }
        if (cursor.moveToFirst()) {

            do {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("TeamA", cursor.getString(cursor.getColumnIndex(DBHelper.TEAM_A)));
                jsonObject.put("TeamB", cursor.getString(cursor.getColumnIndex(DBHelper.TEAM_B)));
                jsonObject.put("Innings", cursor.getString(cursor.getColumnIndex(DBHelper.INNINGS)));
                jsonObject.put("Total_Over", cursor.getString(cursor.getColumnIndex(DBHelper.TOTAL_OVER)));
                jsonObject.put("Date", cursor.getString(cursor.getColumnIndex(DBHelper.DATE)));

                /*historyGetSet.setTEAMA(cursor.getString(cursor.getColumnIndex(DBHelper.TEAM_A)));
                historyGetSet.setTEAMB(cursor.getString(cursor.getColumnIndex(DBHelper.TEAM_B)));
                historyGetSet.setINNINGS(cursor.getString(cursor.getColumnIndex(DBHelper.INNINGS)));
                historyGetSet.setOVER(cursor.getString(cursor.getColumnIndex(DBHelper.TOTAL_OVER)));*/
                jsonArray.put(jsonObject);
            } while (cursor.moveToNext());
            Log.w("JsonArray", jsonArray.toString());
            return jsonArray;


        }
        return null;
    }

    public int sumOfScore(String created_date, String innings) {
        int result;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT sum(" + DBHelper.SCORE + ") from " + DBHelper.TABLE_NAME + " where " + DBHelper.INNINGS + "='" + innings + "' and " + DBHelper.DATE + "='" + created_date + "'", null);
        if (cursor.moveToFirst()) {
            Log.e("sumOfScore", cursor.getCount() + "");
            Log.e("sumOfScore", cursor.getInt(0) + "");
            result = cursor.getInt(0);
            cursor.close();
            return result;
        } else {
            return -1;
        }
    }

    public String[] checkSet(String innings, String date, String ball_num) {
        String[] ret = new String[2];
        Log.w("get", innings + " " + date + " " + ball_num);
        Cursor cursor = sqLiteDatabase.rawQuery("select " + DBHelper.PLAYER_NAME + "," + DBHelper.SCORE + " from " + DBHelper.TABLE_NAME + " where " + DBHelper.INNINGS + "='" + innings + "' and " + DBHelper.DATE + "='" + date + "' and " + DBHelper.BALL_NUMBER + "='" + ball_num + "'", null);
        Log.e("checkSet", cursor.getCount() + "");
        changeButtonState = cursor.getCount();
        if (cursor.moveToFirst()) {
            Log.e("checkSet", cursor.getString(cursor.getColumnIndex(DBHelper.PLAYER_NAME)));
            Log.e("checkSet", cursor.getString(cursor.getColumnIndex(DBHelper.SCORE)));
            ret[0] = cursor.getString(cursor.getColumnIndex(DBHelper.PLAYER_NAME));
            ret[1] = cursor.getString(cursor.getColumnIndex(DBHelper.SCORE));
        }
        return ret;
    }

    public String combineScore(String innings, String given_date) throws JSONException {

        String queryScore = "SELECT sum(SCORE) as TOTAL_SCORE FROM (SELECT PLAYER_NAME,BALL_NUMBER,SCORE ,'Regular' AS EXTRA_TYPE,INNINGS,CREATED_DATE FROM PLAYER_SCORE_INF UNION ALL SELECT PLAYER_NAME,BALL_NUMBER,EXTRA_SCORE ,EXTRA_TYPE,INNINGS,CREATED_DATE FROM PLAYER_SCORE_EXTRAS) T1  WHERE INNINGS='" + innings + "' AND CREATED_DATE='" + given_date + "' ORDER BY CREATED_DATE,INNINGS,BALL_NUMBER";
        Cursor cursor = sqLiteDatabase.rawQuery(queryScore, null);
        Log.e("CursorCount", cursor.getCount() + "");
        if (cursor.moveToFirst()) {
            Log.e("Score", cursor.getString(cursor.getColumnIndex("TOTAL_SCORE")));
            return cursor.getString(cursor.getColumnIndex("TOTAL_SCORE"));
        } else {
            return null;
        }
    }

    public JSONObject combineScoreAlone(String innings, String given_date,String mName) throws JSONException {

        String queryScore = "SELECT sum(SCORE) as TOTAL_SCORE,count(BALL_NUMBER) as TOTAL_BALL FROM (SELECT PLAYER_NAME,BALL_NUMBER,SCORE ,'Regular' AS EXTRA_TYPE,INNINGS,CREATED_DATE FROM PLAYER_SCORE_INF UNION ALL SELECT PLAYER_NAME,BALL_NUMBER,EXTRA_SCORE ,EXTRA_TYPE,INNINGS,CREATED_DATE FROM PLAYER_SCORE_EXTRAS) T1  WHERE INNINGS='" + innings + "' AND CREATED_DATE='" + given_date + "' AND PLAYER_NAME='" + mName + "' ORDER BY CREATED_DATE,INNINGS,BALL_NUMBER";
        Cursor cursor = sqLiteDatabase.rawQuery(queryScore, null);
        Log.e("CursorCount", cursor.getCount() + "");
        if (cursor.moveToFirst()) {
            JSONObject jsonObject=new JSONObject();
            Log.e("Score", cursor.getString(cursor.getColumnIndex("TOTAL_SCORE")));
            Log.e("Score", cursor.getString(cursor.getColumnIndex("TOTAL_BALL")));
            jsonObject.put("Score", cursor.getString(cursor.getColumnIndex("TOTAL_SCORE")));
            jsonObject.put("Ball_Count",cursor.getString(cursor.getColumnIndex("TOTAL_BALL")));
            return jsonObject;
        } else {
            return null;
        }
    }

    public JSONArray combineTableSet(String innings, String given_date) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        // String query1="SELECT * FROM (SELECT PLAYER_NAME,'Actual' as SCORE_TYPE,BALL_NUMBER,SCORE,INNINGS,CREATED_DATE,NULL AS EXTRA_TYPE FROM PLAYER_SCORE_INF UNION SELECTPLAYER_NAME,'Extra',BALL_NUMBER,EXTRA_SCORE,INNINGS,CREATED_DATE,EXTRA_TYPE FROM PLAYER_SCORE_EXTRAS) T1 WHERE INNINGS='1' AND CREATED_DATE='2015/12/25' ORDER BY CREATED_DATE,INNINGS,BALL_NUMBER";
        String query2 = "SELECT * FROM (SELECT PLAYER_NAME,BALL_NUMBER,SCORE ,'Regular' AS EXTRA_TYPE,INNINGS,CREATED_DATE FROM PLAYER_SCORE_INF UNION ALL SELECT PLAYER_NAME,BALL_NUMBER,EXTRA_SCORE ,EXTRA_TYPE,INNINGS,CREATED_DATE FROM PLAYER_SCORE_EXTRAS) T1  WHERE INNINGS='" + innings + "' AND CREATED_DATE='" + given_date + "' ORDER BY CREATED_DATE,INNINGS,BALL_NUMBER";

        //String query="SELECT * FROM (SELECT "+DBHelper.PLAYER_NAME+",'Actual' as SCORE_TYPE,"+DBHelper.BALL_NUMBER+",SCORE,INNINGS,CREATED_DATE FROM "+DBHelper.TABLE_NAME+" UNION ALL SELECT PLAYER_NAME,'Extra',BALL_NUMBER,EXTRA_SCORE,INNINGS,CREATED_DATE FROM "+DBHelper.TABLE_EXTRA_NAME+") T1 WHERE INNINGS='"+innings+"' AND CREATED_DATE='"+given_date+"' ORDER BY CREATED_DATE,INNINGS,BALL_NUMBER";
        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);
        Log.d("Mine", cursor.getCount() + "");
        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Player_Name", cursor.getString(cursor.getColumnIndex(DBHelper.PLAYER_NAME)));
                jsonObject.put("BALL_NUM", cursor.getString(cursor.getColumnIndex(DBHelper.BALL_NUMBER)));
                jsonObject.put("SCORE", cursor.getString(cursor.getColumnIndex(DBHelper.SCORE)));
                jsonObject.put("INNINGS", cursor.getString(cursor.getColumnIndex(DBHelper.INNINGS)));
                jsonObject.put("Type", cursor.getString(cursor.getColumnIndex(DBHelper.EXTRA_TYPE)));
                jsonArray.put(jsonObject);
            } while (cursor.moveToNext());
            cursor.close();
            Log.e("Mine Json", jsonArray.toString());
            return jsonArray;
        } else {
            return null;
        }
    }

    public JSONArray combineTableAloneSet(String innings, String given_date,String mName) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        // String query1="SELECT * FROM (SELECT PLAYER_NAME,'Actual' as SCORE_TYPE,BALL_NUMBER,SCORE,INNINGS,CREATED_DATE,NULL AS EXTRA_TYPE FROM PLAYER_SCORE_INF UNION SELECTPLAYER_NAME,'Extra',BALL_NUMBER,EXTRA_SCORE,INNINGS,CREATED_DATE,EXTRA_TYPE FROM PLAYER_SCORE_EXTRAS) T1 WHERE INNINGS='1' AND CREATED_DATE='2015/12/25' ORDER BY CREATED_DATE,INNINGS,BALL_NUMBER";
        String query2 = "SELECT * FROM (SELECT PLAYER_NAME,BALL_NUMBER,SCORE ,'Regular' AS EXTRA_TYPE,INNINGS,CREATED_DATE FROM PLAYER_SCORE_INF UNION ALL SELECT PLAYER_NAME,BALL_NUMBER,EXTRA_SCORE ,EXTRA_TYPE,INNINGS,CREATED_DATE FROM PLAYER_SCORE_EXTRAS) T1  WHERE INNINGS='" + innings + "' AND CREATED_DATE='" + given_date + "' AND PLAYER_NAME='" + mName + "' ORDER BY CREATED_DATE,INNINGS,BALL_NUMBER";

        //String query="SELECT * FROM (SELECT "+DBHelper.PLAYER_NAME+",'Actual' as SCORE_TYPE,"+DBHelper.BALL_NUMBER+",SCORE,INNINGS,CREATED_DATE FROM "+DBHelper.TABLE_NAME+" UNION ALL SELECT PLAYER_NAME,'Extra',BALL_NUMBER,EXTRA_SCORE,INNINGS,CREATED_DATE FROM "+DBHelper.TABLE_EXTRA_NAME+") T1 WHERE INNINGS='"+innings+"' AND CREATED_DATE='"+given_date+"' ORDER BY CREATED_DATE,INNINGS,BALL_NUMBER";
        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);
        Log.d("Mine", cursor.getCount() + "");
        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Player_Name", cursor.getString(cursor.getColumnIndex(DBHelper.PLAYER_NAME)));
                jsonObject.put("BALL_NUM", cursor.getString(cursor.getColumnIndex(DBHelper.BALL_NUMBER)));
                jsonObject.put("SCORE", cursor.getString(cursor.getColumnIndex(DBHelper.SCORE)));
                jsonObject.put("INNINGS", cursor.getString(cursor.getColumnIndex(DBHelper.INNINGS)));
                jsonObject.put("Type", cursor.getString(cursor.getColumnIndex(DBHelper.EXTRA_TYPE)));
                jsonArray.put(jsonObject);
            } while (cursor.moveToNext());
            cursor.close();
            Log.e("Mine Json", jsonArray.toString());
            return jsonArray;
        } else {
            return null;
        }
    }

    public String ongoingOver(String created_date, String innings) {
        String result = null;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + DBHelper.BALL_NUMBER + " from " + DBHelper.TABLE_NAME + " where " + DBHelper.INNINGS + "='" + innings + "' and " + DBHelper.DATE + "='" + created_date + "'", null);
        if (cursor.moveToFirst()) {
            do {
                Log.e("sumOfScore", cursor.getCount() + "");
                Log.e("sumOfScore", cursor.getString(0) + "");
                result = cursor.getString(0);
            } while (cursor.moveToNext());
            cursor.close();
            return result;
        } else {
            return result;
        }
    }

    public int sumOfScoreExtras(String created_date, String innings) {
        int result;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT sum(" + DBHelper.EXTRA_RUN + ") from " + DBHelper.TABLE_EXTRA_NAME
                + " where " + DBHelper.INNINGS + "='" + innings + "' and " + DBHelper.DATE + "='" + created_date + "'", null);
        if (cursor.moveToFirst()) {
            Log.e("sumOfScore", cursor.getCount() + "");
            Log.e("sumOfScore", cursor.getInt(0) + "");
            result = cursor.getInt(0);
            cursor.close();
            return result;
        } else {
            return -1;
        }
    }

    public String totalOverDash(String created_date, String innings) {
        String result;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + DBHelper.TOTAL_OVER + " from " + DBHelper.TABLE_DASH_NAME
                + " where " + DBHelper.INNINGS + "='" + innings + "' and " + DBHelper.DATE + "='" + created_date + "'", null);
        if (cursor.moveToFirst()) {
            Log.e("sumOfScore", cursor.getCount() + "");
            Log.e("sumOfScore", cursor.getString(0) + "");
            result = cursor.getString(0);
            cursor.close();
            return result;
        } else {
            return "error";
        }
    }


    public long insertScore(String playerName, String ball_Number, String Score, String innings, String created_Date) {
        Log.e("Scpre", Score + "");


        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.PLAYER_NAME, playerName);
        contentValues.put(DBHelper.BALL_NUMBER, ball_Number);
        if (Score.equalsIgnoreCase("WK")) {
            contentValues.put(DBHelper.SCORE, Score);
        } else {
            int score_num = Integer.parseInt(Score);
            contentValues.put(DBHelper.SCORE, score_num);
        }
        contentValues.put(DBHelper.INNINGS, innings);
        contentValues.put(DBHelper.DATE, created_Date);

        long id = sqLiteDatabase.insert(DBHelper.TABLE_NAME, DBHelper.BALL_NUMBER, contentValues);

        Log.e("ID", id + "");

        return id;

    }

    public long insertDashBoard(String teamA, String teamB, String OverDash, String innings, String created_Date) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.TOTAL_OVER, OverDash);
        contentValues.put(DBHelper.INNINGS, innings);
        contentValues.put(DBHelper.DATE, created_Date);
        contentValues.put(DBHelper.TEAM_A, teamA);
        contentValues.put(DBHelper.TEAM_B, teamB);
        long id = sqLiteDatabase.insert(DBHelper.TABLE_DASH_NAME, DBHelper.INNINGS, contentValues);
        return id;
    }

    public long inserExtraScore(String playerName, String ball_Number, String extra_Score, String innings, String created_Date, String extra_type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.PLAYER_NAME, playerName);
        contentValues.put(DBHelper.BALL_NUMBER, ball_Number);
        contentValues.put(DBHelper.EXTRA_RUN, extra_Score);
        contentValues.put(DBHelper.EXTRA_TYPE, extra_type);
        contentValues.put(DBHelper.INNINGS, innings);
        contentValues.put(DBHelper.DATE, created_Date);
        long id = sqLiteDatabase.insert(DBHelper.TABLE_EXTRA_NAME, DBHelper.BALL_NUMBER, contentValues);
        return id;

    }

    public void UpdateExtraScore(String playerName, String ball_Number, String extra_Score, String innings, String created_Date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.PLAYER_NAME, playerName);
        contentValues.put(DBHelper.BALL_NUMBER, ball_Number);
        contentValues.put(DBHelper.EXTRA_RUN, extra_Score);
        long id = sqLiteDatabase.update(DBHelper.TABLE_EXTRA_NAME, contentValues, DBHelper.BALL_NUMBER + " =? AND " + DBHelper.INNINGS + " =? AND " + DBHelper.DATE + " =?", new String[]{ball_Number, innings, created_Date});
        if (id > 0) {
            Toast.makeText(context, "Score updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, ball_Number + " Score failed to update", Toast.LENGTH_SHORT).show();
        }

    }

    public String[] teamInfo(String innings, String date) {
        String[] result = new String[2];
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + DBHelper.TEAM_A + "," + DBHelper.TEAM_B + " FROM " + DBHelper.TABLE_DASH_NAME + " WHERE " + DBHelper.INNINGS + " = '" + innings + "' AND " + DBHelper.DATE + "='" + date + "'", null);
        Log.e("teamInfo", cursor.getCount() + "");
        if (cursor.moveToFirst()) {
            Log.e("teamInfo", cursor.getCount() + "");
            Log.e("teamInfo", cursor.getString(cursor.getColumnIndex(DBHelper.TEAM_A)));
            Log.e("teamInfo", cursor.getString(cursor.getColumnIndex(DBHelper.TEAM_B)));
            result[0] = cursor.getString(cursor.getColumnIndex(DBHelper.TEAM_A));
            result[1] = cursor.getString(cursor.getColumnIndex(DBHelper.TEAM_B));
        }
        return result;
    }

    public List<String> spinDash(String currentDate) {
        List<String> list = new ArrayList<>();
        list.add("Select Innings");
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + DBHelper.INNINGS + " from " + DBHelper.TABLE_DASH_NAME + " where " + DBHelper.DATE + "='" + currentDate + "'", null);
        Log.d("spinDash", "" + cursor.getCount());
        Log.d("spinDash", "" + currentDate);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex(DBHelper.INNINGS)));
                Log.d("spinDash", "" + cursor.getString(cursor.getColumnIndex(DBHelper.INNINGS)));

            } while (cursor.moveToNext());
            cursor.close();
        }

        return list;
    }


    private static class DBHelper extends SQLiteOpenHelper {
        private final String TAG = "Player_DB";
        private static File dir = new File(Environment.getExternalStorageDirectory() + "");
        private static String DATABASE_NAME = dir + "/Cric_Grab/Player.db";// dir+"/
        private static final int DATABASE_VERSION = 13;
        private static final String TABLE_NAME = "PLAYER_SCORE_INF";
        private static final String TABLE_EXTRA_NAME = "PLAYER_SCORE_EXTRAS";
        private static final String TABLE_DASH_NAME = "DASHBOARD";
        private static final String SCORE = "SCORE";
        /* private static final String FOUR = "FOUR";
         private static final String THREE = "THREE";
         private static final String TWO = "TWO";
         private static final String ONE = "ONE";*/
        private static final String INNINGS = "INNINGS";
        private static final String BALL_NUMBER = "BALL_NUMBER";
        private static final String TOTAL_OVER = "OVER_DASH";
        private static final String TEAM_A = "TEAM_A";
        private static final String TEAM_B = "TEAM_B";
        private static final String EXTRA_RUN = "EXTRA_SCORE";
        private static final String EXTRA_TYPE = "EXTRA_TYPE";
        private static final String PLAYER_NAME = "PLAYER_NAME";
        private static final String DATE = "CREATED_DATE";
        private Context context;
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + PLAYER_NAME + " VARCHAR2(255)," + BALL_NUMBER + " VARCHAR2(255)," + SCORE + " INTEGER," + INNINGS + " VARCHAR2(255)," + DATE + " DATETIME,PRIMARY KEY(" + BALL_NUMBER + "," + DATE + "," + INNINGS + "))";

        private static final String CREATE_EXTRA_TABLE = "CREATE TABLE " + TABLE_EXTRA_NAME + " (" + PLAYER_NAME + " VARCHAR2(255)," + BALL_NUMBER + " VARCHAR2(255)," + EXTRA_RUN + " VARCHAR2(255)," + EXTRA_TYPE + " VARCHAR2(255)," + INNINGS + " VARCHAR2(255)," + DATE + " DATETIME)";

        private static final String CREATE_DASH_TABLE = "CREATE TABLE " + TABLE_DASH_NAME + " (" + TEAM_A + " VARCHAR2(255)," + TEAM_B + " VARCHAR2(255)," + TOTAL_OVER + " VARCHAR2(255)," + INNINGS + " VARCHAR2(255)," + DATE + " DATETIME,PRIMARY KEY(" + DATE + "," + INNINGS + "))";
        private static final String Drop_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        private static final String Drop_Dash_TABLE = "DROP TABLE IF EXISTS " + TABLE_DASH_NAME;
        private static final String Drop_EXTRA_TABLE = "DROP TABLE IF EXISTS " + TABLE_EXTRA_NAME;

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE);
                db.execSQL(CREATE_EXTRA_TABLE);
                db.execSQL(CREATE_DASH_TABLE);
                Toast.makeText(context, "Table Created", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Table Created");

            } catch (SQLException e
                    ) {
                e.printStackTrace();
                Log.i(TAG, "Table Created Exception");

            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(Drop_TABLE);
                db.execSQL(Drop_EXTRA_TABLE);
                db.execSQL(Drop_Dash_TABLE);

                Toast.makeText(context, "Table Upgraded", Toast.LENGTH_SHORT).show();
                onCreate(db);
                Log.i(TAG, "Table upgraded");

            } catch (SQLException e) {
                e.printStackTrace();
                Log.i(TAG, "Table Upgraded Exception");

            }

        }
    }
}
