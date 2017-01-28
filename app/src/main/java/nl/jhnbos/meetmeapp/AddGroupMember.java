package nl.jhnbos.meetmeapp;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import java.util.Map;

public class AddGroupMember extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    //STRINGS
    private String group;
    private String email;
    public static final String ADD_GROUPMEMBER_URL = "http://jhnbos.nl/android/addGroupMember.php";
    public static final String GET_ALL_CONTACTS_URL = "http://jhnbos.nl/android/getAllContacts.php";

    //LISTS
    public ArrayList<String> contactsList;
    public ArrayList<String> selectedList;

    //LAYOUT
    public ListView lv;
    public Button addGroupMemberButton;

    //OBJECTS
    public ArrayAdapter<String> adapter;
    public StringRequest stringRequest1;
    private HTTP http;

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

        http = new HTTP();

        String url1 = GET_ALL_CONTACTS_URL + "?email='" + email + "'";
        getData(url1);

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
        if(v == addGroupMemberButton){
            try{

                SparseBooleanArray checked = lv.getCheckedItemPositions();

                for (int i = 0; i < checked.size(); i++) {
                    int position = checked.keyAt(i);

                    if (checked.valueAt(i)){
                        selectedList.add(((TextView)lv.getChildAt(i)).getText().toString());
                    }
                }

                addGroupMembers(group, selectedList);
            } catch(Exception e){
                e.printStackTrace();
            }
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

                        contactsList.add(jo.getString("contact_email"));
                    }

                    lv.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddGroupMember.this, "Error while reading from url", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest1);
    }

    //ADD
    private void addGroupMembers(String group, ArrayList<String> members) {
        try {
            for (int i = 0; i < members.size(); i++) {
                String response = http.sendPost(ADD_GROUPMEMBER_URL + "?name='" + group + "'&email='" + members.get(i)  + "'");

                if(response.equals(members.get(i))){
                    i++;
                }
            }

            Intent main = new Intent(AddGroupMember.this, MainActivity.class);
            startActivity(main);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        parent.getChildAt(position).setSelected(true);
    }


    //END OF METHODS
}
