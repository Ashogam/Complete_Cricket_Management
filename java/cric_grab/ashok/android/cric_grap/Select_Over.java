package cric_grab.ashok.android.cric_grap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Button;

import com.ashok.android.cric_grap.R;

public class Select_Over extends AppCompatActivity implements View.OnClickListener {
    ScrollView root_layout;
    private int over=DashBoard.over_count;
    LinearLayout row;
    Button btnTag;
    LinearLayout layout;
    public static String SELECTED_OVER;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__over);

        root_layout = (ScrollView) findViewById(R.id.rootLinear);

        RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout = new LinearLayout(Select_Over.this);
        layout.setLayoutParams(layoutParam);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Below will add three linear layout with 4 buttons in each

        if (over % 2 == 0) {
            setLayout();
            root_layout.addView(layout);

        } else {
            setLayout();
            Button button=new Button(this);
            button.setText("Over "+over);
            button.setId(over);
            button.setOnClickListener(this);
            layout.addView(button);

            root_layout.addView(layout);

        }
    }

    public void setLayout() {

        for (int i = 0; i < over / 2; i++) {
            row = new LinearLayout(Select_Over.this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            // Here is important
            row.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < 2; j++) {
                btnTag = new Button(Select_Over.this);
                btnTag.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

                btnTag.setText("Over " + (j + 1 + (i * 2)));
                btnTag.setId(j + 1 + (i * 2));
                btnTag.setOnClickListener(this);
                row.addView(btnTag);
            }
            layout.addView(row);
        }

    }

    @Override
    public void onClick(View v) {
        Log.d("Click Id","ID++"+v.getId());
        //Toast.makeText(Select_Over.this, String.valueOf(v.getId()-1)+"", Toast.LENGTH_SHORT).show();
        SELECTED_OVER= String.valueOf(v.getId()-1);
        Intent intent=new Intent(Select_Over.this,Scorum.class);
        startActivity(intent);
    }
}
