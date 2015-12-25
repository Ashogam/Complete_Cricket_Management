package cric_grab.ashok.android.cric_grap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ashok.android.cric_grap.R;

import cric_grab.sqlite.cric_grap.Player_Score_Information;
import cric_grab.utility.cric_grap.Custom_List_Adapter;

import java.util.regex.Pattern;

public class ScoreBoard extends AppCompatActivity {
    private static int PLAYER_NAME;

    Button autoComplete;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        Intent intent = getIntent();
        PLAYER_NAME = intent.getIntExtra("mPosition", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(Custom_List_Adapter.items.get(PLAYER_NAME).getPlayer_name().toUpperCase());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        autoComplete = (Button) findViewById(R.id.autoComplete);

        autoComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Player_Score_Information information = new Player_Score_Information(ScoreBoard.this);
                information.open();
                Log.d("ScoreBoard", "Opeing++++++++");
                try {
                    Thread.sleep(1000);
                    Log.d("ScoreBoard","Sleeping++++++++");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                information.close();
                Log.d("ScoreBoard", "closing++++++++");
            }
        });
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        /*if (id == R.id.action_search) {
            Toast.makeText(getBaseContext(), "Search in progress", Toast.LENGTH_LONG).show();

        }*/

        return super.onOptionsItemSelected(item);
    }


}
