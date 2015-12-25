package cric_grab.utility.cric_grap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import cric_grab.ashok.android.cric_grap.IndividualScoreView;
import com.ashok.android.cric_grap.R;

import java.util.ArrayList;

/**
 * Created by ANDROID on 09-12-2015.
 */
public class Custom_History_ListView extends ArrayAdapter<HistoryGetSet> {


    /**
     * Created by ANDROID on 16-11-2015.
     */

    public static ArrayList<HistoryGetSet> items;
    Holder holder = null;
    private Context context;
    private int resource;

    public Custom_History_ListView(Context context, int resource, ArrayList<HistoryGetSet> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.items = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            View vi = convertView;

            if (convertView == null) {
                LayoutInflater inflater = ((Activity) context)
                        .getLayoutInflater();
                vi = inflater.inflate(resource, parent, false);
                holder = new Holder(vi);
                vi.setTag(holder);

            } else {
                holder = (Holder) vi.getTag();

            }
            if (items.size() <= 0) {
                holder.teamA.setText("No Data");
            } else {

                holder.teamA.setText(items.get(position).getTEAMA());
                holder.teamB.setText(items.get(position).getTEAMB());
                holder.innings.setText(items.get(position).getINNINGS());
                holder.over.setText(items.get(position).getOVER());
                holder.date.setText(items.get(position).getDATE());
                vi.setOnClickListener(new OnItemClick(position));
            }
            return vi;
        } catch (Exception vi) {
            vi.printStackTrace();
        }

        return null;
    }

    public class Holder {
        TextView teamA, teamB, innings, over,date;

        public Holder(View vi) {
            teamA = (TextView) vi.findViewById(R.id.TA);
            teamB = (TextView) vi.findViewById(R.id.TB);
            innings = (TextView) vi.findViewById(R.id.TI);
            over = (TextView) vi.findViewById(R.id.TO);
            date = (TextView) vi.findViewById(R.id.TD);
        }
    }


    private class OnItemClick implements View.OnClickListener {
        private int mPosition;

        public OnItemClick(int position) {
            // TODO Auto-generated constructor stub
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent=new Intent(context.getApplicationContext(), IndividualScoreView.class);
            intent.putExtra("position",mPosition);
            context.startActivity(intent);

        }


    }
}
