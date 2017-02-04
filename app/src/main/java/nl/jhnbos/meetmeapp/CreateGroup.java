package nl.jhnbos.meetmeapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateGroup extends AppCompatActivity implements View.OnClickListener {

    public static final String GET_ALL_GROUPS_URL = "http://jhnbos.nl/android/getAllGroups.php";
    private static final String ADDGROUP_URL = "http://jhnbos.nl/android/addGroup.php";
    private static final String ADDGROUPMEMBER_URL = "http://jhnbos.nl/android/addGroupMember.php";
    public StringRequest stringRequest1;
    public ArrayList<String> controlList;
    private EditText groupNameField;
    private Button createButton;
    private String currentUser;
    private String groupName;
    private HTTP http;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        //BACK BUTTON
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ALLOW HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        groupNameField = (EditText) findViewById(R.id.gnameEditText);
        createButton = (Button) findViewById(R.id.createGButton);
        controlList = new ArrayList<>();

        createButton.setOnClickListener(this);

        currentUser = getIntent().getStringExtra("Email");

        http = new HTTP();
    }
    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

    @Override
    public void onClick(View v) {
        if (v == createButton) {

            groupName = groupNameField.getText().toString();
            String url1 = null;
            String url2 = null;

            try {
                url1 = ADDGROUP_URL + "?name=" + URLEncoder.encode(groupName, "UTF-8")
                        + "&email=" + URLEncoder.encode(currentUser, "UTF-8");

                url2 = ADDGROUPMEMBER_URL + "?name=" + URLEncoder.encode(groupName, "UTF-8")
                        + "&email=" + URLEncoder.encode(currentUser, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            try {
                if (groupName == "" || groupName.isEmpty()) {
                    Toast.makeText(CreateGroup.this, "Please fill in a name for the group!", Toast.LENGTH_LONG).show();
                } else {
                    addGroup(url1, url2, groupName, currentUser);
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


    //END OF LISTENERS
    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    //ADD GROUPMEMBER
    private void addGroupMember(String name, String email) {
        try {
            String response = http.sendGet(ADDGROUPMEMBER_URL + "?name=" + URLEncoder.encode(name, "UTF-8")
                    + "&email=" + URLEncoder.encode(email, "UTF-8"));

            if (response.equals(name)) {
                //Go back to main
                CreateGroup.this.onBackPressed();
            } else {
                Toast.makeText(this, "Problem with creating group!", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //ADD GROUP
    private void addGroup(final String url, final String url2, final String group, final String email) {
        class GetJSON extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(CreateGroup.this, "Creating group...", null, true, true);
            }

            @Override
            protected String doInBackground(Void... v) {

                HashMap<String, String> params = new HashMap<>();
                params.put("name", group);
                params.put("email", email);

                HashMap<String, String> params2 = new HashMap<>();
                params2.put("name", group);
                params2.put("email", email);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(url, params);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                RequestHandler rh2 = new RequestHandler();
                String res2 = rh2.sendPostRequest(url2, params2);

                return res;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if (!s.equals(group)) {
                    Toast.makeText(CreateGroup.this, s, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CreateGroup.this, s, Toast.LENGTH_LONG).show();
                    CreateGroup.this.onBackPressed();
                    //addGroupMember(group, email);
                }

            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    //END OF METHODS
}
