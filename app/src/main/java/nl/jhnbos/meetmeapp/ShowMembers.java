package nl.jhnbos.meetmeapp;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShowMembers extends AppCompatActivity implements View.OnClickListener {

    //STRINGS
    private String group;
    public static final String GET_ALL_MEMBERS_URL = "http://jhnbos.nl/android/getAllGroupMembers.php";

    //LISTS
    public ArrayList<String> memberList;

    //LAYOUT
    public ListView lv;
    public Button returnButton;

    //OBJECTS
    public ArrayAdapter<String> adapter;
    public StringRequest stringRequest1;
    private HTTP http;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_members);

        //ALLOW HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Instantiating variables
        group = this.getIntent().getStringExtra("Group");
        lv = (ListView) findViewById(R.id.memberlistView);
        returnButton = (Button) findViewById(R.id.returnButton);
        memberList = new ArrayList<>();

        http = new HTTP();

        String url1 = GET_ALL_MEMBERS_URL + "?name='" + group + "'";
        getData(url1);

        //ADAPTER
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, memberList);

        //Listeners
        returnButton.setOnClickListener(this);

    }

      /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

    @Override
    public void onClick(View v) {
        if(v == returnButton){
            super.onBackPressed();
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

    public void getData(String url1){
        stringRequest1 = new StringRequest(url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jArray = new JSONArray(response);
                    JSONArray ja = jArray.getJSONArray(0);

                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);

                        memberList.add(jo.getString("email"));
                    }

                    lv.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShowMembers.this, "Error while reading from url", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest1);
    }


}
