package cric_grab.ashok.android.cric_grap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.app.ActionBar.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ashok.android.cric_grap.R;

public class Over_selection extends AppCompatActivity implements View.OnClickListener {
    int i;
    LinearLayout linear;
    LinearLayout row;
    static int over;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        Intent intent = getIntent();
        if (intent.hasExtra("User_Over")) {
            over = intent.getIntExtra("User_Over", 0);
        }
        linear = (LinearLayout) findViewById(R.id.layout);
        row = new LinearLayout(this);
        if (over != 0) {
            if (over % 2 == 0) {
                doOperation();
                linear.addView(row);


            } else {
                doOperation();
                Button button = new Button(this);
                button.setId(over);
                button.setText("Button " + over);
                row.addView(button);
                button.setBackgroundResource(R.drawable.listselect);
                button.setOnClickListener(this);
                linear.addView(row);
                //Toast.makeText(Over_selection.this, "Failed", Toast.LENGTH_SHORT).show();
            }


        } else {
            Toast.makeText(Over_selection.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


    public void doOperation() {
        for (int i = 0; i < over / 2; i++) {


            LinearLayout.LayoutParams param_row = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(param_row);
            row.setOrientation(LinearLayout.VERTICAL);
            LinearLayout linearLayout = new LinearLayout(this);
            for (int j = 0; j < 2; j++) {

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
                linearLayout.setLayoutParams(param);
                linear.setOrientation(LinearLayout.HORIZONTAL);
                Button button = new Button(this);
                button.setId(j + 1 + (i * 2));
                button.setText("Button " + (j + 1 + (i * 2)));

                button.setOnClickListener(this);
                linearLayout.addView(button);

            }
            row.addView(linearLayout);


        }

    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Over_selection.this, Scorum.class);
        intent.putExtra("Over_ID", v.getId());
        startActivity(intent);
        //Toast.makeText(Over_selection.this, "Clicked " + v.getId(), Toast.LENGTH_SHORT).show();

    }
}
