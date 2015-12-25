package cric_grab.ashok.android.cric_grap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.ashok.android.cric_grap.R;

import cric_grab.sqlite.cric_grap.Add_player_SqliteManagement;
import cric_grab.sqlite.cric_grap.Player_Score_Information;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Scorum extends AppCompatActivity implements OnItemSelectedListener {

    protected TextView ball1, ball2, ball3, ball4, ball5, ball6;
    /*TextView textView[]={ball1, ball2, ball3, ball4, ball5, ball6};*/
    protected Spinner edtscore1, edtscore2, edtscore3, edtscore4, edtscore5, edtscore6;
    protected Spinner spinner1, spinner2, spinner3, spinner4, spinner5, spinner6;
    int getResult;
    Player_Score_Information scoreInformation;
    ArrayList<String> individualBall = new ArrayList<>();
    int count = 0;
    String spn1, spn2, spn3, spn4, spn5, spn6;
    String ba1, ba2, ba3, ba4, ba5, ba6;
    String scr1, scr2, scr3, scr4, scr5, scr6;
    public static String created_Date;
    Button btnStoreDatabase;
    ArrayAdapter<String> dataAdapter;
    String extra_ball;
    String extra_score;
    String extra_spinner;
    String extra_type_value;
    ArrayAdapter<String> editSpinAdapter;
    private static String[] editScoreSpin = {"0", "1", "2", "3", "4", "6", "WK"};
    ArrayAdapter<String> perOverBall;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scorum);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        init();




       /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spinnerarray);
        spinner1.setAdapter(adapter);*/
        new AsyncTask<Void, Void, ArrayList<String>>() {

            @Override
            protected ArrayList<String> doInBackground(Void... params) {
                Add_player_SqliteManagement sqliteManagement = new Add_player_SqliteManagement(Scorum.this);
                try {
                    sqliteManagement.open();
                    ArrayList<String> arrayList = sqliteManagement.spinnerName();
                    return arrayList;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    sqliteManagement.close();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<String> aVoid) {
                super.onPostExecute(aVoid);
                if (aVoid != null) {
                    dataAdapter = new ArrayAdapter<String>(Scorum.this,
                            android.R.layout.simple_spinner_item, aVoid);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // spinner2.setAdapter(dataAdapter);
                    setAdapterList(dataAdapter);

                    checkAndSetData();
                }
            }
        }.execute();


        Intent result = getIntent();
        getResult = result.getIntExtra("Over_ID", 0);
        //Toast.makeText(Scorum.this, "result " + getResult, Toast.LENGTH_SHORT).show();
        //calc();
        btnStoreDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
                if (btnStoreDatabase.getText().equals("Confirm")) {
                    if (TextUtils.isEmpty(scr1) || TextUtils.isEmpty(scr2) || TextUtils.isEmpty(scr3) || TextUtils.isEmpty(scr4) || TextUtils.isEmpty(scr5) || TextUtils.isEmpty(scr6)) {
                        new AlertDialog.Builder(Scorum.this).setTitle("Attention").setMessage("Some Score might be seems as empty.\n Can you save default score as Zero?").
                                setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (TextUtils.isEmpty(scr1)) scr1 = String.valueOf(0);
                                        if (TextUtils.isEmpty(scr2)) scr2 = String.valueOf(0);
                                        if (TextUtils.isEmpty(scr3)) scr3 = String.valueOf(0);
                                        if (TextUtils.isEmpty(scr4)) scr4 = String.valueOf(0);
                                        if (TextUtils.isEmpty(scr5)) scr5 = String.valueOf(0);
                                        if (TextUtils.isEmpty(scr6)) scr6 = String.valueOf(0);
                                        new StoreData().execute();
                                    }
                                }).show();

                    } else {
                        new StoreData().execute();
                    }
                } else if (btnStoreDatabase.getText().equals("Update Score")) {
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                scoreInformation.open();
                                scoreInformation.updateScore(spn1, ba1, scr1, DashBoard.MATCH_NUMBER, created_Date);
                                scoreInformation.updateScore(spn2, ba2, scr2, DashBoard.MATCH_NUMBER, created_Date);
                                scoreInformation.updateScore(spn3, ba3, scr3, DashBoard.MATCH_NUMBER, created_Date);
                                scoreInformation.updateScore(spn4, ba4, scr4, DashBoard.MATCH_NUMBER, created_Date);
                                scoreInformation.updateScore(spn5, ba5, scr5, DashBoard.MATCH_NUMBER, created_Date);
                                scoreInformation.updateScore(spn6, ba6, scr6, DashBoard.MATCH_NUMBER, created_Date);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                scoreInformation.close();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Toast.makeText(Scorum.this, "Updated", Toast.LENGTH_SHORT).show();
                        }
                    }.execute();
                }
            }
        });


    }

    public void setAdapterList(ArrayAdapter<String> dataAdapter) {
        spinner1.setAdapter(dataAdapter);
        spinner2.setAdapter(dataAdapter);
        spinner3.setAdapter(dataAdapter);
        spinner4.setAdapter(dataAdapter);
        spinner5.setAdapter(dataAdapter);
        spinner6.setAdapter(dataAdapter);

    }


    @Override
    protected void onStart() {
        super.onStart();
        calcOver(Select_Over.SELECTED_OVER);
        Log.e("Invidiual", "Value individual " + individualBall.size());
        if (individualBall.size() > 0) {
            Log.e("Invidiual", "Value individual " + individualBall.size());
            ball1.setText(individualBall.get(0));
            ball2.setText(individualBall.get(1));
            ball3.setText(individualBall.get(2));
            ball4.setText(individualBall.get(3));
            ball5.setText(individualBall.get(4));
            ball6.setText(individualBall.get(5));

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        created_Date = dateFormat.format(date);
        Log.e("OnResume", "Onresume   ------");
//        checkAndSetData();

    }

    public void CAScascade(Spinner spinner, Spinner text, String[] ret) {
       // text.setText(ret[1]);
        text.setSelection(DashBoard.getIndex(text, ret[1]));
        spinner.setSelection(DashBoard.getIndex(spinner, ret[0]));
    }

    public void checkAndSetData() {
        String[] ret;
        try {

            scoreInformation.open();
            ret = scoreInformation.checkSet(DashBoard.MATCH_NUMBER, created_Date, individualBall.get(0));
            if (Player_Score_Information.changeButtonState != 0) {
                btnStoreDatabase.setText("Update Score");
                //btnStoreDatabase.setBackgroundDrawable(and);
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    btnStoreDatabase.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.saup_des));
                } else {
                    btnStoreDatabase.setBackgroundResource(R.drawable.saup_des);
                }
                //btnStoreDatabase.setBackground(getDrawable(R.drawable.saup_des));
                btnStoreDatabase.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_upload, 0, 0, 0);
                Log.e("CAS", ret.length + " " + ret[0] + " " + ret[1]);
                if (ret != null) {
                    CAScascade(spinner1, edtscore1, ret);
                }
                ret = scoreInformation.checkSet(DashBoard.MATCH_NUMBER, created_Date, individualBall.get(1));
                if (ret != null) {
                    CAScascade(spinner2, edtscore2, ret);
                }
                ret = scoreInformation.checkSet(DashBoard.MATCH_NUMBER, created_Date, individualBall.get(2));
                if (ret != null) {
                    CAScascade(spinner3, edtscore3, ret);
                }

                ret = scoreInformation.checkSet(DashBoard.MATCH_NUMBER, created_Date, individualBall.get(3));
                if (ret != null) {
                    CAScascade(spinner4, edtscore4, ret);
                }

                ret = scoreInformation.checkSet(DashBoard.MATCH_NUMBER, created_Date, individualBall.get(4));
                if (ret != null) {
                    CAScascade(spinner5, edtscore5, ret);
                }

                ret = scoreInformation.checkSet(DashBoard.MATCH_NUMBER, created_Date, individualBall.get(5));
                if (ret != null) {
                    CAScascade(spinner6, edtscore6, ret);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {
            scoreInformation.close();
        }
    }

    public void setData() {
        spn1 = spinner1.getSelectedItem().toString();
        spn2 = spinner2.getSelectedItem().toString();
        spn3 = spinner3.getSelectedItem().toString();
        spn4 = spinner4.getSelectedItem().toString();
        spn5 = spinner5.getSelectedItem().toString();
        spn6 = spinner6.getSelectedItem().toString();

        scr1 = edtscore1.getSelectedItem().toString();

        Log.e("score", edtscore1.getSelectedItem().toString());
        scr2 = edtscore2.getSelectedItem().toString();
        scr3 = edtscore3.getSelectedItem().toString();
        scr4 = edtscore4.getSelectedItem().toString();
        scr5 = edtscore5.getSelectedItem().toString();
        scr6 = edtscore6.getSelectedItem().toString();

        ba1 = ball1.getText().toString();
        ba2 = ball2.getText().toString();
        ba3 = ball3.getText().toString();
        ba4 = ball4.getText().toString();
        ba5 = ball5.getText().toString();
        ba6 = ball6.getText().toString();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        created_Date = dateFormat.format(date);


    }

    public void calcOver(String n) {

        Log.d("DashBoard Count", "Value " + DashBoard.over_count);
        for (double k = 0.1f; k <= DashBoard.over_count + 1; k += 0.1f) {

            String value = String.format("%.1f", k) + "";

            String value2 = value.split("\\.")[1];

            if ((!value2.equalsIgnoreCase("7")) && (!value2.equalsIgnoreCase("8")) && (!value2.equalsIgnoreCase("9")) && (!value2.equalsIgnoreCase("0"))) {

                if (value.split("\\.")[0].equalsIgnoreCase(n)) {
                    Log.d("Count", count + "");
                    individualBall.add(count, value);
                    Log.e("Over Number", value);
                    count++;
                }
            }

        }
    }


    public void init() {
        ball1 = (TextView) findViewById(R.id.ball1);
        ball2 = (TextView) findViewById(R.id.ball2);
        ball3 = (TextView) findViewById(R.id.ball3);
        ball4 = (TextView) findViewById(R.id.ball4);
        ball5 = (TextView) findViewById(R.id.ball5);
        ball6 = (TextView) findViewById(R.id.ball6);

        edtscore1 = (Spinner) findViewById(R.id.edtscore1);
        edtscore2 = (Spinner) findViewById(R.id.edtscore2);
        edtscore3 = (Spinner) findViewById(R.id.edtscore3);
        edtscore4 = (Spinner) findViewById(R.id.edtscore4);
        edtscore5 = (Spinner) findViewById(R.id.edtscore5);
        edtscore6 = (Spinner) findViewById(R.id.edtscore6);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        spinner4 = (Spinner) findViewById(R.id.spinner4);
        spinner5 = (Spinner) findViewById(R.id.spinner5);
        spinner6 = (Spinner) findViewById(R.id.spinner6);
        btnStoreDatabase = (Button) findViewById(R.id.btnStoreDatabase);
        scoreInformation = new Player_Score_Information(Scorum.this);

        editSpinAdapter = new ArrayAdapter<String>(Scorum.this,
                android.R.layout.simple_spinner_item, editScoreSpin);
        editSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // spinner2.setAdapter(dataAdapter);
        setEditSpin(editSpinAdapter);


    }

    public void setEditSpin(ArrayAdapter<String> dataAdapter) {

        edtscore1.setAdapter(dataAdapter);
        edtscore2.setAdapter(dataAdapter);
        edtscore3.setAdapter(dataAdapter);
        edtscore4.setAdapter(dataAdapter);
        edtscore5.setAdapter(dataAdapter);
        edtscore6.setAdapter(dataAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scorum_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.extra) {
            try {

                String[] typeExtra={"Wide","NoBall","OverThrow"};
                String[] perOver_ball={individualBall.get(0),individualBall.get(1),individualBall.get(2),individualBall.get(3),individualBall.get(4),individualBall.get(5)};

                Log.d("perball_over",individualBall.get(0)+individualBall.get(1)+individualBall.get(3));
                String[] doEnter = {"1", "2", "3", "4", "5","6"};
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();
                created_Date = dateFormat.format(date);
                LayoutInflater layoutInflater = LayoutInflater.from(Scorum.this);
                View promptView = layoutInflater.inflate(R.layout.extra_scorum, null);
                final Dialog alertDialogBuilder = new Dialog(
                        Scorum.this/*,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen*/);

                alertDialogBuilder.setContentView(promptView);
                alertDialogBuilder.setTitle("LOAD EXTRA SCORE");
                final Spinner ext_ball = (Spinner) promptView.findViewById(R.id.extra_ball);
                final Spinner ext_score = (Spinner) promptView.findViewById(R.id.extra_edtscore);
                final Spinner spin = (Spinner) promptView.findViewById(R.id.extra_spinner);
                final Spinner extra_type = (Spinner) promptView.findViewById(R.id.extra_type);
                ext_ball.setAdapter(extraAdapeter(perOver_ball));
                extra_type.setAdapter(extraAdapeter(typeExtra));
                ext_score.setAdapter(extraAdapeter(doEnter));
                spin.setAdapter(dataAdapter);

                Button enter = (Button) promptView.findViewById(R.id.btn_enter);
                Button cancel = (Button) promptView.findViewById(R.id.btn_cancel);
                alertDialogBuilder.setTitle("Enter Over");
                alertDialogBuilder.setCancelable(true);

                alertDialogBuilder.show();
                enter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        extra_ball = ext_ball.getSelectedItem().toString();
                        extra_score = ext_score.getSelectedItem().toString();
                        extra_spinner = spin.getSelectedItem().toString();
                        extra_type_value = extra_type.getSelectedItem().toString();
                        View focusView = null;
                        if (!TextUtils.isEmpty(extra_ball) && !TextUtils.isEmpty(extra_score)) {
                            Toast.makeText(Scorum.this, extra_ball + " " + extra_score + " " + extra_spinner, Toast.LENGTH_SHORT).show();
                            try {
                                scoreInformation.open();
                                long result = scoreInformation.inserExtraScore(extra_spinner, extra_ball, extra_score, DashBoard.MATCH_NUMBER, created_Date,extra_type_value);
                                if (result > 0) {
                                    Toast.makeText(Scorum.this, "Score Saved", Toast.LENGTH_SHORT).show();
                                } else if (result == -1) {
                                    new AlertDialog.Builder(Scorum.this).setTitle("Update Score").setMessage("Score Already Exist!\nPress yes to update")
                                            .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    scoreInformation.updateScore(extra_spinner, extra_ball, extra_score, DashBoard.MATCH_NUMBER, created_Date);

                                                }
                                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                } else {
                                    Toast.makeText(Scorum.this, "Failed to Save the Score", Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                scoreInformation.close();
                            }
                            alertDialogBuilder.dismiss();
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

            return true;
        }
        if(id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayAdapter<String> extraAdapeter(String[] value){
        ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(Scorum.this,
                android.R.layout.simple_spinner_item, value);
        aAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return aAdapter;
    }



    class StoreData extends AsyncTask<Void, Void, Void> {
        long success1, success2, success3, success4, success5, success6;
        ProgressDialog dialog = new ProgressDialog(Scorum.this);

        public StoreData() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Data Inserting");
            dialog.setMessage("Progressing........");
            dialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                scoreInformation.open();
                success1 = scoreInformation.insertScore(spn1, ba1, scr1, DashBoard.MATCH_NUMBER, created_Date);
                success2 = scoreInformation.insertScore(spn2, ba2, scr2, DashBoard.MATCH_NUMBER, created_Date);
                success3 = scoreInformation.insertScore(spn3, ba3, scr3, DashBoard.MATCH_NUMBER, created_Date);
                success4 = scoreInformation.insertScore(spn4, ba4, scr4, DashBoard.MATCH_NUMBER, created_Date);
                success5 = scoreInformation.insertScore(spn5, ba5, scr5, DashBoard.MATCH_NUMBER, created_Date);
                success6 = scoreInformation.insertScore(spn6, ba6, scr6, DashBoard.MATCH_NUMBER, created_Date);

            } catch (Exception e) {

                e.printStackTrace();
            } finally {

                scoreInformation.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            if (success1 > 0 && success2 > 0 && success3 > 0 && success4 > 0 && success5 > 0 && success6 > 0) {
                Toast.makeText(Scorum.this, "Data Sucessfully Inserted", Toast.LENGTH_SHORT).show();
            } else {

                /*if (!success1 == true && !success2 == true && !success3 == true && !success4 == true && !success5 == true && !success6 == true) {*/

                if (success1 == -1) {
                    Toast.makeText(Scorum.this, ba1 + " Already Exist", Toast.LENGTH_SHORT).show();
                }
                if (success2 == -1) {
                    Toast.makeText(Scorum.this, ba2 + " Already Exist", Toast.LENGTH_SHORT).show();
                }
                if (success3 == -1) {
                    Toast.makeText(Scorum.this, ba3 + " Already Exist", Toast.LENGTH_SHORT).show();
                }
                if (success4 == -1) {
                    Toast.makeText(Scorum.this, ba4 + " Already Exist", Toast.LENGTH_SHORT).show();
                }
                if (success5 == -1) {
                    Toast.makeText(Scorum.this, ba5 + " Already Exist", Toast.LENGTH_SHORT).show();
                }
                if (success6 == -1) {
                    Toast.makeText(Scorum.this, ba6 + " Already Exist", Toast.LENGTH_SHORT).show();
                }

            } /*else {



                }
*/
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
