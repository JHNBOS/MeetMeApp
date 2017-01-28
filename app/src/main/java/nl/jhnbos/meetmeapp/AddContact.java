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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddContact extends AppCompatActivity implements View.OnClickListener {

    private static final String GET_ALL_USERS_URL = "http://jhnbos.nl/android/getAllUsers.php";
    private static final String ADDCONTACT_URL = "http://jhnbos.nl/android/addContact.php";
    private StringRequest stringRequest1;
    private ArrayList<String> controlList;
    private EditText contactEmailField;
    private Button addContactButton;
    private String currentUser;
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


    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

    @Override
    public void onClick(View v) {
        if (v == addContactButton) {
            String contactEmail = contactEmailField.getText().toString();
            currentUser = getIntent().getStringExtra("Email");

            try {
                if (contactEmail == "" || contactEmail.isEmpty() || !contactEmail.contains("@") || !contactEmail.contains(".")) {
                    Toast.makeText(getApplicationContext(), "Please enter a existing email!", Toast.LENGTH_LONG).show();
                } else {
                    addContact(contactEmail, currentUser);
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

        String url1 = GET_ALL_USERS_URL;
        getData(url1);

    }

    //END OF LISTENERS
    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    //ADD CONTACT
    private void addContact(String contact_email, String email) {
        try {
            String response = http.sendGet(ADDCONTACT_URL + "?name=" + contact_email + "&email=" + email);

            if (response.equals(contact_email)) {
                AddContact.this.onBackPressed();
            } else {
                Toast.makeText(this, "Please enter an existing email address!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getData(String url1) {
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
