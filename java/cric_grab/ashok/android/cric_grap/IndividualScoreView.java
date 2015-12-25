package cric_grab.ashok.android.cric_grap;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout;

import com.ashok.android.cric_grap.R;

import cric_grab.sqlite.cric_grap.Player_Score_Information;
import cric_grab.utility.cric_grap.Custom_History_ListView;
import cric_grab.utility.cric_grap.IndividualAdapt;
import cric_grab.utility.cric_grap.IndividualGetSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class IndividualScoreView extends AppCompatActivity {
    ListView individualList;
    TextView failedText,setOver,setScore;
    LinearLayout title;
    private Player_Score_Information information;
    IndividualAdapt individualAdapt;
    private ArrayList<IndividualGetSet> arrayList;
    int mpos;
    String dates;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_score_view);
        init();

        Intent intent=getIntent();
        mpos=intent.getIntExtra("position",0);

        dates=Custom_History_ListView.items.get(mpos).getDATE();
        Log.e("Position", mpos + "+++++" + Custom_History_ListView.items.get(mpos).getINNINGS());
        Log.e("over", mpos + "+++++" + Custom_History_ListView.items.get(mpos).getOVER());
        Log.e("Date",""+Custom_History_ListView.items.get(mpos).getDATE());
        new AsyncTask<Void,Void,ArrayList<IndividualGetSet>>(){


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                arrayList=new ArrayList<IndividualGetSet>();
            }

            @Override
            protected ArrayList<IndividualGetSet> doInBackground(Void... params) {
                try{
                    information.open();
                    //information.combineTableSet(Custom_History_ListView.items.get(mpos).getINNINGS(),dates);
                    JSONArray array=information.combineTableSet(Custom_History_ListView.items.get(mpos).getINNINGS(), dates);
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
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    information.close();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<IndividualGetSet> individualGetSets) {
                super.onPostExecute(individualGetSets);

                if(individualGetSets.size()>0){
                    Log.w("PostExecute",""+individualGetSets.size()+"++++++ "+individualGetSets+"");
                    failedText.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                    individualAdapt=new IndividualAdapt(IndividualScoreView.this,R.layout.individual_listview,individualGetSets);
                    individualList.setAdapter(individualAdapt);
                }else{
                    failedText.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                }
            }
        }.execute();


    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            information.open();
            String QuickScore=information.combineScore(Custom_History_ListView.items.get(mpos).getINNINGS(), dates);
            if(QuickScore!=null){
            setScore.setText("Total Score : "+QuickScore);}else{setScore.setText("Total Score : NA");}
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void init() {
        individualList= (ListView) findViewById(R.id.individualList);
        failedText
                = (TextView) findViewById(R.id.failedText);
        title= (LinearLayout) findViewById(R.id.title);
        information=new Player_Score_Information(IndividualScoreView.this);
        setOver=(TextView) findViewById(R.id.setOver);
        setScore=(TextView) findViewById(R.id.setScore);
        if(!TextUtils.isEmpty(Custom_History_ListView.items.get(mpos).getOVER()))
            setOver.setText("Total Over : "+Custom_History_ListView.items.get(mpos).getOVER());
        else
            setOver.setText("Total Over : NA");


    }
}
