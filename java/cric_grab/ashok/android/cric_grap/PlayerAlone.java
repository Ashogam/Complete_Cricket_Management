package cric_grab.ashok.android.cric_grap;

import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.ashok.android.cric_grap.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cric_grab.sqlite.cric_grap.Player_Score_Information;

import cric_grab.utility.cric_grap.IndividualAdapt;
import cric_grab.utility.cric_grap.IndividualAloneAdapt;
import cric_grab.utility.cric_grap.IndividualGetSet;

/**
 * Created by ANDROID on 26-12-2015.
 */
public class PlayerAlone extends AppCompatActivity {
    private int mPosition;
    private String mName;
    private String mInnings;
    private String mDate;
    ListView PAList;
    TextView PABall, PAScore,PAFailed;
    LinearLayout PATitle,PAFooter;
    private Player_Score_Information score_information;
    private ArrayList<IndividualGetSet> arrayList;
    private IndividualAloneAdapt individualAdapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playeralone);
        setViewById();

        mPosition = getIntent().getIntExtra("mPosition", 0);

        getSupportActionBar().setTitle(IndividualAdapt.items.get(mPosition).getPLAYERNAME());
        Log.d("PlayerAlone", IndividualAdapt.items.get(mPosition).getPLAYERNAME());
        Log.d("PlayerAlone", IndividualAdapt.items.get(mPosition).getInnings());
        Log.d("PlayerAlone", IndividualScoreView.DATES);
        mName = IndividualAdapt.items.get(mPosition).getPLAYERNAME();
        mInnings = IndividualAdapt.items.get(mPosition).getInnings();
        mDate = IndividualScoreView.DATES;
    }

    private void setViewById() {
        PAList = (ListView) findViewById(R.id.PAList);
        PAScore = (TextView) findViewById(R.id.PAScore);
        PABall = (TextView) findViewById(R.id.PABallCount);
        PATitle= (LinearLayout) findViewById(R.id.PATitle);
        PAFooter= (LinearLayout) findViewById(R.id.PAFooter);
        PAFailed= (TextView) findViewById(R.id.PAFailed);
        score_information = new Player_Score_Information(PlayerAlone.this);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        new PlayerAloneFetch().execute();
    }

    class PlayerAloneFetch extends AsyncTask<Void, Void, ArrayList<IndividualGetSet>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arrayList=new ArrayList<IndividualGetSet>();
        }

        @Override
        protected ArrayList<IndividualGetSet> doInBackground(Void... params) {
            try {
                score_information.open();
                JSONArray array= score_information.combineTableAloneSet(mInnings, mDate, mName);
                if(array!=null){
                    Log.w("arrayyyy", array.toString());
                    for(int i=0;i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        IndividualGetSet indi = new IndividualGetSet();
                        indi.setPLAYERNAME(object.getString("Player_Name"));
                        indi.setBALL_NUMBER(object.getString("BALL_NUM"));
                        indi.setSCORE(object.getString("SCORE"));
                        indi.setType(object.getString("Type"));
                        indi.setInnings(object.getString("INNINGS"));
                        arrayList.add(indi);
                    }
                }
                return arrayList;

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                score_information.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<IndividualGetSet> individualGetSets) {
            super.onPostExecute(individualGetSets);
            try {
                if (individualGetSets.size() > 0) {
                    Log.w("PostExecute", "" + individualGetSets.size() + "++++++ " + individualGetSets + "");
                    PAFailed.setVisibility(View.GONE);
                    PATitle.setVisibility(View.VISIBLE);
                    individualAdapt = new IndividualAloneAdapt(PlayerAlone.this, R.layout.individual_listview, individualGetSets);
                    PAList.setAdapter(individualAdapt);
                    doSetValues();
                } else {
                    PAFailed.setVisibility(View.VISIBLE);
                    PATitle.setVisibility(View.GONE);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }



        }
    private void doSetValues(){
        try {
            score_information.open();
            JSONObject QuickScore = score_information.combineScoreAlone(mInnings, mDate, mName);
            if (QuickScore != null) {
                PAScore.setText("Total Score : " + QuickScore.getString("Score"));
                PABall.setText("Total Ball : " + QuickScore.getString("Ball_Count"));
            } else {
                PAScore.setText("Total Score : NA");
                PABall.setText("Total Ball : NA");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            score_information.close();
        }
    }
}
