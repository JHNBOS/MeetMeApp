package nl.jhnbos.meetmeapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    public static final String DELETE_GROUPMEMBER_URL = "http://jhnbos.nl/android/deleteGroupMember.php";

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
        registerForContextMenu(lv);

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
    public void onResume(){
        super.onResume();

        String url1 = GET_ALL_MEMBERS_URL + "?name='" + group + "'";
        getData(url1);

        adapter.clear();
        adapter.notifyDataSetChanged();
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.members_context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                String selected = memberList.get((int) info.id);
                ShowDialog(selected);

                return true;
            default:
                return super.onContextItemSelected(item);
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

    //SHOW DIALOG WHEN DELETING GROUP
    private void ShowDialog(final String data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowMembers.this);
        builder.setTitle("Remove Group?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                //dialog.dismiss();
                removeGroupMember(data);

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //REMOVE GROUPMEMBER
    private void removeGroupMember(String email) {
        try {
            http.sendPost(DELETE_GROUPMEMBER_URL + "?name='" + group + "'&email='" + email + "'");
            onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
