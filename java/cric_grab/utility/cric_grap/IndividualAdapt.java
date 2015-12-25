package cric_grab.utility.cric_grap;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ashok.android.cric_grap.R;

import java.util.ArrayList;

/**
 * Created by ANDROID on 10-12-2015.
 */
public class IndividualAdapt extends ArrayAdapter<IndividualGetSet>{

    /**
     * Created by ANDROID on 09-12-2015.
     */


        public ArrayList<IndividualGetSet> items;
        Holder holder = null;
        private Context context;
        private int resource;

        public IndividualAdapt(Context context, int resource, ArrayList<IndividualGetSet> objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
            this.items = objects;

            for(int i=0;i<items.size();i++){
                Log.d("for",items.get(i).getPLAYERNAME());
                Log.d("for", items.get(i).getBALL_NUMBER());
                Log.d("for",items.get(i).getSCORE());
            }

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            try {

                Log.d("posotion",position+"");
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
                    holder.indBall.setText("No Data");
                } else {

                    holder.indBall.setText(items.get(position).getBALL_NUMBER());
                    holder.indName.setText(items.get(position).getPLAYERNAME());
                    holder.indScore.setText(items.get(position).getSCORE());
                    if(items.get(position).getType().equalsIgnoreCase("regular"))
                        holder.indType.setTextColor(context.getResources().getColor(R.color.Chartreuse));
                    if(items.get(position).getType().equalsIgnoreCase("wide"))
                        holder.indType.setTextColor(context.getResources().getColor(R.color.Azure));
                    if(items.get(position).getType().equalsIgnoreCase("noball"))
                        holder.indType.setTextColor(context.getResources().getColor(R.color.Red));
                    if(items.get(position).getType().equalsIgnoreCase("overthrow"))
                        holder.indType.setTextColor(context.getResources().getColor(R.color.OrangeRed));
                    holder.indType.setText(items.get(position).getType());
                    holder.indinnings.setText(items.get(position).getInnings());
                    vi.setOnClickListener(new OnItemClick(position));
                }
                return vi;
            } catch (Exception vi) {
                vi.printStackTrace();
            }

            return null;
        }

        public class Holder {
            TextView indScore, indName, indBall,indType,indinnings;

            public Holder(View vi) {
                indScore = (TextView) vi.findViewById(R.id.indScore);
                indName = (TextView) vi.findViewById(R.id.indName);
                indBall = (TextView) vi.findViewById(R.id.indBall);
                indType= (TextView) vi.findViewById(R.id.indType);
                indinnings=(TextView) vi.findViewById(R.id.indinnings);

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


            }


        }


}
