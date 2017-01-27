package nl.jhnbos.meetmeapp;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateGroup extends AppCompatActivity implements View.OnClickListener {

    private EditText groupNameField;
    private Button createButton;
    private static final String ADDGROUP_URL = "http://jhnbos.nl/android/addGroup.php";
    private static final String ADDGROUPMEMBER_URL = "http://jhnbos.nl/android/addGroupMember.php";
    public static final String GET_ALL_GROUPS_URL = "http://jhnbos.nl/android/getAllGroups.php";
    private String currentUser;
    private String groupName;
    private HTTP http;
    public StringRequest stringRequest1;
    public ArrayList<String> controlList;


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

        http = new HTTP();
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

    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

    @Override
    public void onClick(View v) {
        if(v == createButton){
            groupName = groupNameField.getText().toString();
            currentUser =  getIntent().getStringExtra("Email");

            try{
                if(groupName == "" || groupName.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please fill in a name for the group!", Toast.LENGTH_LONG).show();
                } else {
                    addGroup(groupName, currentUser);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    //END OF LISTENERS
    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    //ADD GROUP
    private void addGroup(final String name, final String email) {
        try {
            String url1 = GET_ALL_GROUPS_URL+"?email='"+email+"'";;
            getData(url1);

            if(controlList.contains(name)){
                String response = http.sendGet(ADDGROUP_URL + "?name=" + name + "&email=" + email);

                if(response.equals(name)){
                    addGroupMember(name, email);
                }
            } else {
                Toast.makeText(this, "Please enter a valid group name!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //ADD GROUPMEMBER
    private void addGroupMember(String name, String email) {
        try {
            String response = http.sendGet(ADDGROUPMEMBER_URL + "?name=" + name + "&email=" + email);

            if(!response.equals(name) || name.isEmpty() || !name.contains("@") || !name.contains(".")){
                Toast.makeText(this, "Problem with creating group!", Toast.LENGTH_LONG).show();
            } else{
                CreateGroup.this.onBackPressed();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getData(String url1){
        stringRequest1 = new StringRequest(url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jArray = new JSONArray(response);
                    JSONArray ja = jArray.getJSONArray(0);

                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        Log.d("Control Group", jo.getString("name"));
                        controlList.add(jo.getString("name"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CreateGroup.this, "Error while reading from url", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(CreateGroup.this).addToRequestQueue(stringRequest1);
    }

    //END OF METHODS
}
