package cric_grab.Navication_selection_class.cric_grap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashok.android.cric_grap.R;
import cric_grab.sqlite.cric_grap.Add_player_SqliteManagement;
import cric_grab.utility.cric_grap.Custom_List_Adapter;
import cric_grab.utility.cric_grap.Player_Info;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class Score_Entry extends AppCompatActivity {

    private ListView listPlayer;
    private TextView txtErrorMessage;
    public static String title=null;
    Custom_List_Adapter listAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score__entry);
        Intent intent=getIntent();
        title=intent.getStringExtra("TitleBar");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(!TextUtils.isEmpty(title)){
            Log.v("Tool Bar intent "," Is not empty"+ title);
            toolbar.setTitle(title);
        }else{
            Log.v("Tool Bar intent else "," Is empty");
            toolbar.setTitle("Score Entry");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listPlayer = (ListView) findViewById(R.id.listPlayer);
        txtErrorMessage= (TextView) findViewById(R.id.txtErrorMessage);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(Score_Entry.this, Add_player.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AttachingList().execute();
    }

    private class AttachingList extends AsyncTask<Void,Void,ArrayList<Player_Info>>{
        Add_player_SqliteManagement management=null;
        private ArrayList<Player_Info> player_infos=null;
        private ProgressDialog myProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            player_infos=new ArrayList<>();
            myProgressDialog = new ProgressDialog(
                    Score_Entry.this);
            myProgressDialog.setMessage("Loading.....");
            myProgressDialog.setCancelable(false);
            myProgressDialog.show();

        }

        @Override
        protected ArrayList<Player_Info> doInBackground(Void... params) {
            management=new Add_player_SqliteManagement(Score_Entry.this);
            try{
                management.open();
                Thread.sleep(1000);
                JSONArray jsonArray = management.getdetails();
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Player_Info player_info=new Player_Info();
                        player_info.setPlayer_name(jsonObject.getString("player_name"));
                        player_info.setPlayer_mobile_number(jsonObject.getString("player_number"));
                        Log.i("Json feed", jsonObject.getString("player_name") + "_____" + jsonObject.getString("player_number"));
                        player_infos.add(player_info);
                    }
                }

                return player_infos;

            }catch (Exception e)
            {
                e.printStackTrace();
            }finally {
                if(management!=null){
                    management.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Player_Info> player_infos) {
            super.onPostExecute(player_infos);
            myProgressDialog.dismiss();
            Log.d("Player Length","______Length of ArrayList_______"+player_infos.size());
            if(player_infos==null){
                LinearLayout LinearHead= (LinearLayout) findViewById(R.id.LinearHead);
                txtErrorMessage.setText("Oops! There is no player in the List. Please ADD Players before you start the score management");
                LinearHead.setGravity(Gravity.CENTER);
                listPlayer.setVisibility(View.GONE);
                txtErrorMessage.setVisibility(View.VISIBLE);
            }else if(player_infos!=null){
                txtErrorMessage.setVisibility(View.GONE);
                if(listPlayer.getVisibility()==View.GONE){
                    listPlayer.setVisibility(View.VISIBLE);
                }
                Collections.sort(player_infos,Player_Info.comparator);
                listAdapter=new Custom_List_Adapter(Score_Entry.this,R.layout.custom_list_items,player_infos);
                listPlayer.setAdapter(listAdapter);
            }else{
                Toast.makeText(Score_Entry.this, "Something Went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
