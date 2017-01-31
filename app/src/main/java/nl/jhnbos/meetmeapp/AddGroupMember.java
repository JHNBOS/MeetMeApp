package nl.jhnbos.meetmeapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddGroupMember extends AppCompatActivity implements View.OnClickListener {

    public static final String ADD_GROUPMEMBER_URL = "http://jhnbos.nl/android/addGroupMember.php";
    public static final String GET_ALL_CONTACTS_URL = "http://jhnbos.nl/android/getAllContacts.php";

    //LISTS
    private ArrayList<String> contactsList;
    private ArrayList<String> selectedList;

    //LAYOUT
    private ListView lv;
    private Button addGroupMemberButton;

    //OBJECTS
    private ArrayAdapter<String> adapter;

    //STRINGS
    private String group;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_member);

        //ALLOW HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Instantiating variables
        group = this.getIntent().getStringExtra("Group");
        email = this.getIntent().getStringExtra("Email");

        lv = (ListView) findViewById(R.id.gmlist);
        addGroupMemberButton = (Button) findViewById(R.id.addGMButton);

        contactsList = new ArrayList<>();
        selectedList = new ArrayList<>();

        //Listeners
        addGroupMemberButton.setOnClickListener(this);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //ADAPTER
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, contactsList);
    }

    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

    @Override
    public void onClick(View v) {
        if (v == addGroupMemberButton) {
            try {

                SparseBooleanArray checked = lv.getCheckedItemPositions();

                for (int i = 0; i < checked.size(); i++) {
                    int position = checked.keyAt(i);

                    if (checked.valueAt(i)) {
                        String mail = ((TextView) lv.getChildAt(position)).getText().toString();
                        Log.d("Mail: ", mail);
                        selectedList.add(((TextView) lv.getChildAt(position)).getText().toString());
                    }
                }

                for (int i = 0; i < selectedList.size(); i++) {
                    String cEmail = selectedList.get(i);
                    String url = ADD_GROUPMEMBER_URL + "?name=" + group + "&email=" + cEmail;

                    addGroupMember(url, group, cEmail);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                super.onBackPressed();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        String url1 = GET_ALL_CONTACTS_URL + "?email='" + email + "'";
        getContacts(url1);

        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    //END OF LISTENERS
    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    private void showContacts(String response){
        try {
            JSONArray jArray = new JSONArray(response);
            JSONArray ja = jArray.getJSONArray(0);

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);

                contactsList.add(jo.getString("contact_email"));
            }

            lv.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //ADD GROUPMEMBER(S)
    private void addGroupMember(final String url, final String group, final String cEmail) {
        class GetJSON extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AddGroupMember.this, "Adding groupmember(s)...",null,true,true);
            }

            @Override
            protected String doInBackground(Void ... v) {

                HashMap<String,String> parameters = new HashMap<>();
                parameters.put("name", group);
                parameters.put("email", cEmail);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(url, parameters);

                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if (!s.contains(group)) {
                    Toast.makeText(AddGroupMember.this, s, Toast.LENGTH_LONG).show();
                } else {
                    AddGroupMember.this.onBackPressed();
                }
            }

        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    //GET CONTACTS
    private void getContacts(final String url) {
        class GetJSON extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AddGroupMember.this, "Retrieving contacts...",null,true,true);
            }

            @Override
            protected String doInBackground(Void ... v) {
                RequestHandler rh = new RequestHandler();
                String res = rh.sendGetRequest(url);
                return res;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                contactsList.clear();
                showContacts(s);
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }


    //END OF METHODS
}
