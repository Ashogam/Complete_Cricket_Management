package cric_grab.ashok.android.cric_grap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.LinearLayout;

import cric_grab.Navication_selection_class.cric_grap.Add_player;
import cric_grab.Navication_selection_class.cric_grap.Score_Entry;

import com.ashok.android.cric_grap.R;

import cric_grab.sqlite.cric_grap.Player_Score_Information;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DashBoard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static int over_count = 0;
    public static String user_Over;
    public static String MATCH_NUMBER;
    private Player_Score_Information information;
    private String created_Date;
    private TextView txtTotalScore, txtscore, txtExtras;
    private TextView txtDateSet, txtTotalOver, txtOngoingOver,txtTeamA,txtTeamB;
    private Spinner spinDashSelect;
    LinearLayout layout;
    private static final int DEFAULT_VALUE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Do you want to Download from Cloud", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(DashBoard.this, "Downloading", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        init();
        information = new Player_Score_Information(DashBoard.this);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        created_Date = dateFormat.format(date);
        txtDateSet.setText(created_Date);


        spinDashSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (spinDashSelect.getSelectedItem().toString().equals("Select Innings")) {
                    layout.setVisibility(View.GONE);
                } else {
                    sumOfScoreDashBoard(spinDashSelect.getSelectedItem().toString(),created_Date);
                    try {
                        information.open();
                        setTeaminfo(information.teamInfo(spinDashSelect.getSelectedItem().toString(), created_Date));
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally{
                        information.close();
                    }
                    layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void init() {
        txtTotalScore = (TextView) findViewById(R.id.txtTotalScore);
        txtDateSet = (TextView) findViewById(R.id.txtDateSet);
        spinDashSelect = (Spinner) findViewById(R.id.spinDashSelect);
        layout = (LinearLayout) findViewById(R.id.entireDashBoard);
        txtscore = (TextView) findViewById(R.id.txtscore);
        txtExtras = (TextView) findViewById(R.id.txtExtras);
        txtTotalOver = (TextView) findViewById(R.id.txtTotalOver);
        txtOngoingOver = (TextView) findViewById(R.id.txtOngoingOver);
        txtTeamA= (TextView) findViewById(R.id.txtTeamA);
        txtTeamB= (TextView) findViewById(R.id.txtTeamB);


    }

    @Override
    protected void onResume() {
        super.onResume();
        spinLoading();

        if (getSharedPreference("over_count") != 0) {
            over_count = getSharedPreference("over_count");
            Log.d("SP", over_count + "");
            if (getSharedPreference("innings") > 0) {
                MATCH_NUMBER = String.valueOf(getSharedPreference("innings"));
                Log.d("SP", MATCH_NUMBER);
            }
            spinDashSelect.setSelection(getIndex(spinDashSelect, MATCH_NUMBER));
        }

    }



    private void setTeaminfo(String[] result){
        if(result!=null){
            txtTeamA.setText(result[0]);
            txtTeamB.setText(result[1]);
        }else{
            txtTeamA.setText("TEAM A");
            txtTeamB.setText("TEAM B");
        }
    }

    private void spinLoading() {
        try {
            information.open();
            List<String> spinAdapterSet = information.spinDash(created_Date);
            if (spinAdapterSet.size() > 0) {
                ArrayAdapter dataAdapter = new ArrayAdapter<String>(DashBoard.this,
                        R.layout.spinner_item, spinAdapterSet);
                dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinDashSelect.setAdapter(dataAdapter);


            } else {
                spinDashSelect.setVisibility(View.GONE);
                spinDashSelect.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            information.close();
        }
    }

    public static int getIndex(Spinner spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }


    private void sumOfScoreDashBoard(String innings,String created_Date) {

        try {
            information.open();
            String ongoingOver = information.ongoingOver(created_Date, innings);
            String overDash = information.totalOverDash(created_Date, innings);
            int ex_sc1 = information.sumOfScore(created_Date, innings);
            int ex_sc2 = information.sumOfScoreExtras(created_Date, innings);
            Log.d("Good", "-----" + ex_sc1 + " " + ex_sc2);
            if (ex_sc1 > 0 || ex_sc2 > 0 || !overDash.equals("error")) {
                txtTotalOver.setText(overDash);
                txtTotalScore.setText(ex_sc1 + ex_sc2 + "");
                txtExtras.setText(ex_sc2 + "");
                txtscore.setText(ex_sc1 + "");
                txtOngoingOver.setText(ongoingOver);
            } else {
                txtOngoingOver.setText("NIL");
                txtTotalOver.setText("NIL");
                txtExtras.setText("NIL");
                txtscore.setText("NIL");
                txtTotalScore.setText("NIL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            information.close();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();


        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==event.KEYCODE_BACK) {
            new AlertDialog.Builder(DashBoard.this).setMessage("Do you want to exit?").setTitle("Close Application").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setCancelable(false).show();
            return true;
        }else{
        return super.onKeyDown(keyCode, event);}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sharedPreference(String key, int value) {

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();

    }

    public int getSharedPreference(String key) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        return preferences.getInt(key, DEFAULT_VALUE);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_player) {
            // Handle the camera action
            Intent intent = new Intent(DashBoard.this, Add_player.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(DashBoard.this, History.class);
            startActivity(intent);
        } else if (id == R.id.nav_score_entry) {

            if (over_count == 0) {
                createEvent();
            } else {
                new AlertDialog.Builder(DashBoard.this).setMessage("Previous Match!, Do you want to continue?").setPositiveButton("Previous Match", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(DashBoard.this, Select_Over.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("New Match", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createEvent();
                    }

                }).show();

            }

        } else if (id == R.id.nav_call) {
            Intent intent = new Intent(DashBoard.this, Score_Entry.class);
            intent.putExtra("TitleBar", "Caller Screen");
            startActivity(intent);

        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(DashBoard.this, Score_Entry.class);
            intent.putExtra("TitleBar", "Send Message");
            startActivity(intent);
        }else if(id==R.id.nav_remove){
            Intent intent = new Intent(DashBoard.this, Score_Entry.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void createEvent() {
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(DashBoard.this);
            View promptView = layoutInflater.inflate(R.layout.get_match_info, null);
            final Dialog alertDialogBuilder = new Dialog(
                    DashBoard.this/*,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen*/);
            alertDialogBuilder.setContentView(promptView);
            final EditText getScore = (EditText) promptView.findViewById(R.id.get_score);
            final EditText get_match_number = (EditText) promptView.findViewById(R.id.get_match_number);
            final EditText team1 = (EditText) promptView.findViewById(R.id.Team1);
            final EditText team2 = (EditText) promptView.findViewById(R.id.Team2);
            Button enter = (Button) promptView.findViewById(R.id.enter);
            Button cancel = (Button) promptView.findViewById(R.id.cancel);
            alertDialogBuilder.setTitle("Enter Over");
            alertDialogBuilder.setCancelable(true);

            alertDialogBuilder.show();
            enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user_Over = getScore.getText().toString();
                    MATCH_NUMBER = get_match_number.getText().toString();
                    getScore.setError(null);
                    View focusView = null;
                    if (!TextUtils.isEmpty(user_Over) && !TextUtils.isEmpty(MATCH_NUMBER) && !TextUtils.isEmpty(team1.getText().toString()) && !TextUtils.isEmpty(team2.getText().toString())) {
                        try {
                            information.open();
                            long id = information.insertDashBoard(team1.getText().toString(),team2.getText().toString(),user_Over, MATCH_NUMBER, created_Date);
                            over_count = Integer.parseInt(getScore.getText().toString());
                            sharedPreference("over_count", over_count);
                            sharedPreference("innings", Integer.parseInt(MATCH_NUMBER));

                            Intent intent = new Intent(DashBoard.this, Select_Over.class);

                            if (id == -1) {
                                alertDialogBuilder.dismiss();
                                //Toast.makeText(DashBoard.this, "Innings Exists Already! Start a new Innings", Toast.LENGTH_SHORT).show();
                                new AlertDialog.Builder(
                                        DashBoard.this).setTitle("Innings Exists Already").setMessage("Do you want to start new Match?").setCancelable(false).
                                        setPositiveButton("Start", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                get_match_number.setError("Try new Innings");
                                                alertDialogBuilder.show();
                                            }
                                        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        get_match_number.setError(null);
                                        dialog.dismiss();

                                    }
                                }).show();


                            } else {
                                startActivity(intent);
                                alertDialogBuilder.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(DashBoard.this, "Match Creation Failed", Toast.LENGTH_SHORT).show();
                        } finally {
                            information.close();
                        }


                    } else {
                        if (TextUtils.isEmpty(user_Over)) {
                            getScore.setError("Please specify the over");
                        }
                        if (TextUtils.isEmpty(MATCH_NUMBER)) {
                            get_match_number.setError("Please specify the over");
                        }
                        if (TextUtils.isEmpty(team1.getText().toString())){
                            team1.setError("Specify Team A Name");
                        }
                        if (TextUtils.isEmpty(team2.getText().toString())){
                            team2.setError("Specify Team B Name");
                        }
                    }
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogBuilder.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
