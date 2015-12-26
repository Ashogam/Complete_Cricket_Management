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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.ashok.android.cric_grap.R;

import cric_grab.sqlite.cric_grap.Add_player_SqliteManagement;
import cric_grab.utility.cric_grap.Custom_List_Adapter;
import cric_grab.utility.cric_grap.Player_Info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class Score_Entry extends AppCompatActivity {

    private ListView listPlayer;
    private TextView txtErrorMessage;
    public static String title = null;
    Custom_List_Adapter listAdapter;
    private Button removeButton;
    Add_player_SqliteManagement management = null;
    public static boolean CHECKSTATUS = false;
    public static boolean CALLSTATUS = false;
    public static boolean MESSAGESTATUS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score__entry);
        listPlayer = (ListView) findViewById(R.id.listPlayer);
        txtErrorMessage = (TextView) findViewById(R.id.txtErrorMessage);
        removeButton = (Button) findViewById(R.id.removeButton);
        management=new Add_player_SqliteManagement(Score_Entry.this);
        Intent intent = getIntent();
        title = intent.getStringExtra("TitleBar");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (!TextUtils.isEmpty(title)) {
            Log.v("Tool Bar intent ", " Is not empty" + title);
            toolbar.setTitle(title);
            if(title.equalsIgnoreCase("Caller Screen")){
                CALLSTATUS=true; MESSAGESTATUS=false;CHECKSTATUS=false;}
            if(title.equalsIgnoreCase("Send Message")){
                MESSAGESTATUS=true;CALLSTATUS=false;CHECKSTATUS=false;}
            removeButton.setVisibility(View.GONE);
        } else {
            Log.v("Tool Bar intent else ", " Is empty");
            toolbar.setTitle("Customize Player");
            removeButton.setVisibility(View.VISIBLE);
            CHECKSTATUS = true;
            MESSAGESTATUS=false;
            CALLSTATUS=false;
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*//*

            }
        });
*/
        removeButton.setOnClickListener(new View.OnClickListener() {
            Player_Info info;

            @Override
            public void onClick(View v) {
                JSONArray arrayData = new JSONArray();
                Log.e("RemoveButtonClick", listAdapter.getCount() + "");
                try {
                    for (int i = 0; i < listAdapter.getCount(); i++) {
                        info = listAdapter.getItem(i);
                        JSONObject jsonObject = new JSONObject();

                        if (info.getCheckBoxStatus() == true) {
                            Log.e("removeButton", info.getPlayer_name());
                            Log.e("removeButton", info.getPlayer_mobile_number());
                            jsonObject.put("NAME", info.getPlayer_name());
                            jsonObject.put("NUMBER", info.getPlayer_mobile_number());
                            arrayData.put(jsonObject);
                        }

                    }
                    Log.e("ArrayData",arrayData.length()+"");
                    if(arrayData.length()>0){
                        new AsyncTask<JSONArray,Void,Void>(){
                            ProgressDialog dialog=null;
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                dialog=new ProgressDialog(Score_Entry.this);
                                dialog.setMessage("Deleting..... Please Wait");
                                dialog.setCancelable(false);
                                dialog.show();
                            }

                            @Override
                            protected Void doInBackground(JSONArray... params) {
                                try {
                                    management.open();
                                    management.removePlayer(params[0]);
                                    Thread.sleep(500);
                                } catch (InterruptedException e){
                                    e.printStackTrace();
                                } catch (Exception e){
                                    e.printStackTrace();
                                } finally {
                                    management.close();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                dialog.dismiss();
                                onResume();
                            }
                        }.execute(arrayData);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AttachingList().execute();
    }

    private class AttachingList extends AsyncTask<Void, Void, ArrayList<Player_Info>> {

        private ArrayList<Player_Info> player_infos = null;
        private ProgressDialog myProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            player_infos = new ArrayList<>();
            myProgressDialog = new ProgressDialog(
                    Score_Entry.this);
            myProgressDialog.setMessage("Loading.....");
            myProgressDialog.setCancelable(false);
            myProgressDialog.show();

        }

        @Override
        protected ArrayList<Player_Info> doInBackground(Void... params) {

            try {
                management.open();
                Thread.sleep(250);
                JSONArray jsonArray = management.getdetails();
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Player_Info player_info = new Player_Info();
                        player_info.setPlayer_name(jsonObject.getString("player_name"));
                        player_info.setPlayer_mobile_number(jsonObject.getString("player_number"));
                        Log.i("Json feed", jsonObject.getString("player_name") + "_____" + jsonObject.getString("player_number"));
                        player_infos.add(player_info);
                    }
                }

                return player_infos;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (management != null) {
                    management.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Player_Info> player_infos) {
            super.onPostExecute(player_infos);
            myProgressDialog.dismiss();
            Log.d("Player Length", "______Length of ArrayList_______" + player_infos.size());
            if (player_infos.size() <= 0) {
                LinearLayout LinearHead = (LinearLayout) findViewById(R.id.LinearHead);
                txtErrorMessage.setText("Oops! There is no player in the List. Please ADD Players before you start the score management");
                LinearHead.setGravity(Gravity.CENTER);
                listPlayer.setVisibility(View.GONE);
                removeButton.setVisibility(View.GONE);
                txtErrorMessage.setVisibility(View.VISIBLE);
            } else if (player_infos.size() > 0) {
                txtErrorMessage.setVisibility(View.GONE);
                if (listPlayer.getVisibility() == View.GONE||removeButton.getVisibility()==View.GONE) {
                    listPlayer.setVisibility(View.VISIBLE);
                    removeButton.setVisibility(View.VISIBLE);
                }
                Collections.sort(player_infos, Player_Info.comparator);
                listAdapter = new Custom_List_Adapter(Score_Entry.this, R.layout.custom_list_items, player_infos);
                listPlayer.setAdapter(listAdapter);
            } else {
                Toast.makeText(Score_Entry.this, "Something Went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addplayer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.action_add:
                Intent intent = new Intent(Score_Entry.this, Add_player.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
