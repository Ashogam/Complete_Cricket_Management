package cric_grab.Navication_selection_class.cric_grap;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.ashok.android.cric_grap.R;

import cric_grab.sqlite.cric_grap.Add_player_SqliteManagement;
import cric_grab.utility.cric_grap.Player_Info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ANDROID on 15-11-2015.
 */
public class Add_player extends AppCompatActivity {

    AutoCompleteTextView mName, mNumber;
    private Set<String> display_Name;
    Button btnSave;
    ArrayList<Player_Info> player_infos;
    private String mNum = null;
    View focusView = null;
    int sdk = android.os.Build.VERSION.SDK_INT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_player);
        initialize();


        SharedPreferences sharedPreference = getSharedPreferences("Contacts", MODE_PRIVATE);
        Set<String> number = sharedPreference.getStringSet("contact", null);
        SharedPreferences pref = getSharedPreferences("myContact", MODE_PRIVATE);
        Set<String> str = pref.getStringSet("key", null);
        try {
            Log.i("Checking", "displayContactName " + number.toString() + " " + number.size());
            Log.i("Checking", "displayContactName " + str.toString() + " " + str.size());

            if ((number != null && str != null)||(number.size()>1&&str.size()>1)) {
                List<String> contactNumber = new ArrayList<>(number);
                Log.e("SharedPreference", "Result" + contactNumber.toString() + "-" + contactNumber.size());
                contactNumber.removeAll(Collections.singleton(null));
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(Add_player.this,
                                android.R.layout.simple_spinner_dropdown_item, contactNumber);
                if (!(sdk == Build.VERSION_CODES.KITKAT))
                    mNumber.setAdapter(adapter);

                List<String> contactName = new ArrayList<>(str);
                Log.e("DisplayName Preference", "Result " + contactName.size());

                contactName.removeAll(Collections.singleton(null));

                Log.e("DisplayName Preference", "Result " + contactName.size());

                ArrayAdapter<String> adapt =
                        new ArrayAdapter<String>(Add_player.this,
                                android.R.layout.simple_spinner_dropdown_item, contactName);

                if (!(sdk == Build.VERSION_CODES.KITKAT))
                    mName.setAdapter(adapt);

            } else {
                new ContactFetchTask().execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new ContactFetchTask().execute();
        }


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mName.length() > 0 && edtTextCheck() == true) {
                    System.out.println(mName.length());
                    System.out.println(mNumber.length());
                    String name = mName.getText().toString();
                    String number = mNumber.getText().toString();

                    new Save().execute(name, number);
                } else {
                    if (mName.length() == 0) {
                        Toast.makeText(Add_player.this, "Please check the playerName field", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private boolean edtTextCheck() {
        if (mNumber.length() > 0) {
            Log.e("Pin", "First Con");
            if (mNumber.length() == 10 || mNumber.length() == 12 && mNumber.length() != 11) {
                Log.e("Pin", "True");
                return true;
            } else {
                Log.e("Pin", "Failse dfrom second condition");
                Toast.makeText(Add_player.this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Log.e("Pin", "Failed from first con");
            Toast.makeText(Add_player.this, "Type Mobile Number", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public Set<String> contactNumberAutoDoTask() {
        try {
            Cursor cursor = getContacts();
            Set<String> contact = new HashSet<String>();

            while (cursor != null && cursor.moveToNext()) {
                //String contactNumber=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.d("Result ID", cursor.getCount() + "" + cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                Log.d("Result DisplayName", cursor.getCount() + "" + cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                //Adding all display name in the sets
                display_Name.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                Log.e("Adding", "Display_Name" + display_Name.toString());
                Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                                ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                        new String[]{cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))},
                        null);
                cursorPhone.moveToFirst();
                while (!cursorPhone.isAfterLast()) {
                    contact.add(cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    cursorPhone.moveToNext();

                }

            /*if (cursorPhone.moveToFirst()) {
               String contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.d("Result_mobile", "Contact Phone Number: " + contactNumber);
            }
*/
                cursorPhone.close();


            }
            return contact;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private Cursor getContacts() {
        // Run query
        final ContentResolver cr = getContentResolver();
        String[] projection = {ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts._ID};
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ?";
        String[] selectionArgs = {"1"};
        final Cursor contacts = cr.query(ContactsContract.Contacts.CONTENT_URI,
                projection, selection, selectionArgs, "UPPER("

                        + ContactsContract.Contacts.DISPLAY_NAME + ") ASC");
        return contacts;


    }


    private void initialize() {
        mName = (AutoCompleteTextView) findViewById(R.id.playerName);
        mNumber = (AutoCompleteTextView) findViewById(R.id.playerNumber);
        btnSave = (Button) findViewById(R.id.btnSave);
        player_infos = new ArrayList<>();
        display_Name = new HashSet<String>();
    }

    private class Save extends AsyncTask<String, Void, Long> {
        Add_player_SqliteManagement add_player_sqliteManagement = null;
        private ProgressDialog myProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myProgressDialog = new ProgressDialog(
                    Add_player.this);
            myProgressDialog.setMessage("Saving Data");
            myProgressDialog.setCancelable(false);
            myProgressDialog.show();
        }

        @Override
        protected Long doInBackground(String... params) {
            long result = 0;
            add_player_sqliteManagement = new Add_player_SqliteManagement(Add_player.this);
            try {
                Thread.sleep(500);
                add_player_sqliteManagement.open();
                result = add_player_sqliteManagement.registration(params[0], params[1]);
                Log.d("Result Database", "result :" + result);
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("DOInbackGround", "Exception came");
            } finally {
                if (add_player_sqliteManagement != null) {
                    add_player_sqliteManagement.close();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Long aVoid) {
            super.onPostExecute(aVoid);
            myProgressDialog.dismiss();
            try {
                Log.d("OnPostExecuteMethod", "aVoid" + aVoid);
                if (aVoid > 0) {
                    Toast.makeText(Add_player.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                    Add_player_SqliteManagement management = new Add_player_SqliteManagement(Add_player.this);
                    management.open();
                    try {
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    management.close();
                } else if (aVoid == -1) {
                    Toast.makeText(Add_player.this, "Already Registered", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Add_player.this, "Save Denied! DataBase Error", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private class ContactFetchTask extends AsyncTask<Void, Void, Set<String>> {
        @Override
        protected Set<String> doInBackground(Void... params) {
            try {
                Set<String> list = contactNumberAutoDoTask();
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Set<String> strings) {
            super.onPostExecute(strings);
            if (strings != null) {
                addContactToAutoComplete(strings);
            }
        }
    }

    private void sharedPreference(Set<String> contactCollection, String fileName, String PreferenceName) {
        try {
            Log.v("Files", "Details " + contactCollection);
            SharedPreferences sharedPreferences = getSharedPreferences(PreferenceName, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(fileName, contactCollection);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addContactToAutoComplete(Set<String> contactCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        try {
            sharedPreference(contactCollection, "contact", "Contacts");
            Log.v("DisplayName sha", display_Name.size() + "++++++++++++++++++++++++++++ ");
            Log.v("DisplayName error", display_Name.toString() + "++++++++++++++++++++++++++++ ");
            SharedPreferences preferences = getSharedPreferences("myContact", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet("key", display_Name);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(Add_player.this);
                return true;
        }
        /*if (id == R.id.action_search) {
            Toast.makeText(getBaseContext(), "Search in progress", Toast.LENGTH_LONG).show();

        }*/

        return super.onOptionsItemSelected(item);
    }

}
