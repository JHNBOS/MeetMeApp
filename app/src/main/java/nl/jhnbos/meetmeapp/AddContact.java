package nl.jhnbos.meetmeapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AddContact extends AppCompatActivity implements View.OnClickListener {

    private EditText contactEmailField;
    private Button addContactButton;
    private static final String ADDCONTACT_URL = "http://jhnbos.nl/android/addContact.php";
    public static final String GET_ALL_USERS_URL = "http://jhnbos.nl/android/getAllUsers.php";
    private String currentUser;
    private String contactEmail;
    public StringRequest stringRequest1;
    public ArrayList<String> controlList;
    private HTTP http;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        //BACK BUTTON
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ALLOW HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        contactEmailField = (EditText) findViewById(R.id.contactEditText);
        addContactButton = (Button) findViewById(R.id.addContactsButton);
        controlList = new ArrayList<>();

        addContactButton.setOnClickListener(this);

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

    @Override
    public void onRestart(){
        super.onRestart();
        
        String url1 = GET_ALL_USERS_URL;
        getData(url1); 
        
        adapter.clear();
        adapter.notifyDataSetChanged();
    {
    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

    @Override
    public void onClick(View v) {
        if(v == addContactButton){
            contactEmail = contactEmailField.getText().toString();
            currentUser =  getIntent().getStringExtra("Email");

            try{
                if(contactEmail == "" || contactEmail.isEmpty() || !contactEmail.contains("@") || !contactEmail.contains(".")){
                    Toast.makeText(getApplicationContext(), "Please enter a existing email!", Toast.LENGTH_LONG).show();
                } else {
                    addContact(contactEmail, currentUser);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    //END OF LISTENERS
    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    //ADD CONTACT
    private void addContact(final String contact_email, final String email) {
        try {
            

            if(controlList.contains(contact_email)){
                String response = http.sendGet(ADDCONTACT_URL + "?name=" + contact_email + "&email=" + email);

                if(response.equals(contact_email)){
                    AddContact.this.onBackPressed();
                }
            } else{
                Toast.makeText(this, "Please enter an existing email address!", Toast.LENGTH_LONG).show();
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
                        Log.d("Control Contact", jo.getString("email"));
                        controlList.add(jo.getString("email"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddContact.this, "Error while reading from url", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(AddContact.this).addToRequestQueue(stringRequest1);
    }

    //END OF METHODS
}
