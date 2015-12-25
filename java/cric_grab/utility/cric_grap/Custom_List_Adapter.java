package cric_grab.utility.cric_grap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cric_grab.Navication_selection_class.cric_grap.Score_Entry;
import com.ashok.android.cric_grap.R;
import cric_grab.ashok.android.cric_grap.ScoreBoard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ANDROID on 16-11-2015.
 */

public class Custom_List_Adapter extends ArrayAdapter<Player_Info> {
    public static List<Player_Info> items;
    Holder holder = null;
    private Context context;
    private int resource;

    public Custom_List_Adapter(Context context, int resource, ArrayList<Player_Info> objects) {
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
                holder = new Holder();
                holder.username = (TextView) vi.findViewById(R.id.userName);
                vi.setTag(holder);

            } else {
                holder = (Holder) vi.getTag();

            }
            if (items.size() <= 0) {
                holder.username.setText("No Data");
            } else {

                holder.username.setText(items.get(position).getPlayer_name());
                vi.setOnClickListener(new OnItemClick(position));
            }
            return vi;
        } catch (Exception vi) {
            vi.printStackTrace();
        }

        return null;
    }

    public class Holder {

        TextView username;
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
            if (Score_Entry.title != null && !TextUtils.isEmpty(Score_Entry.title)) {

                switch (Score_Entry.title) {
                    case "Caller Screen":
                        Intent intent = new Intent(Intent.ACTION_CALL);

                        intent.setData(Uri.parse("tel:" + items.get(mPosition).getPlayer_mobile_number()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Log.d("Call Option for M", "Passing intent");
                            context.startActivity(intent);

                        } else {
                            Log.d("Call Option ", "Passing intent");
                            context.startActivity(intent);
                        }
                        break;
                    case "Send Message":
                        try {
                            LayoutInflater layoutInflater = LayoutInflater.from(context);
                            View promptView = layoutInflater.inflate(R.layout.sms_dialog, null);
                            final Dialog alertDialogBuilder = new Dialog(
                                    context);
                            alertDialogBuilder.setContentView(promptView);
                            final EditText edtMessageContent = (EditText) promptView.findViewById(R.id.edtMessageContent);
                            Button sendMessage = (Button) promptView.findViewById(R.id.sendMessage);
                            Button messageCancel = (Button) promptView.findViewById(R.id.messageCancel);
                            alertDialogBuilder.setTitle("Send Message");
                            alertDialogBuilder.setCancelable(true);
                            alertDialogBuilder.show();
                            sendMessage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String message = edtMessageContent.getText().toString();
                                    edtMessageContent.setError(null);
                                    View focusView = null;
                                    Intent intent = new Intent(context.getApplicationContext(), Custom_List_Adapter.class);
                                    final PendingIntent pi = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, 0);

                                    final SmsManager sms = SmsManager.getDefault();
                                    if (!TextUtils.isEmpty(message)) {
                                        sms.sendTextMessage(items.get(mPosition).getPlayer_mobile_number(), null, message, pi, null);
                                        Toast.makeText(context, "Message send Successfully", Toast.LENGTH_SHORT).show();
                                        alertDialogBuilder.dismiss();
                                    } else {
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("Empty text, \nDo you want to send?").setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                sms.sendTextMessage(items.get(mPosition).getPlayer_mobile_number(), null, message.length() == 0 ? "Empty Text" : message, pi, null);
                                                Toast.makeText(context, "Message send Successfully", Toast.LENGTH_SHORT).show();
                                                alertDialogBuilder.dismiss();

                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();

                                    }
                                }
                            });

                            messageCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialogBuilder.dismiss();
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                }

            } else {
                Log.i("Switching Activity ", "Passing inttent");
                Intent intent = new Intent(context.getApplicationContext(), ScoreBoard.class);
                intent.putExtra("mPosition", mPosition);
                context.startActivity(intent);

            }

        }
    }
}
