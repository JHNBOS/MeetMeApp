package nl.jhnbos.meetmeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import java.util.HashMap;

public class AddContact extends AppCompatActivity implements View.OnClickListener {

    //STRINGS
    private static final String ADDCONTACT_URL = "http://jhnbos.nl/android/addContact.php";
    private String currentUser;

    //LAYOUT ITEMS
    private EditText contactEmailField;
    private Button addContactButton;

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

        currentUser = getIntent().getExtras().getString("Email");

        //Listeners
        addContactButton.setOnClickListener(this);
    }


    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

    @Override
    public void onClick(View v) {
        if (v == addContactButton) {
            String contactEmail = contactEmailField.getText().toString();
            String url = ADDCONTACT_URL + "?name=" + contactEmail + "&email=" + currentUser;

            try {
                if (contactEmail == "") {
                    Toast.makeText(getApplicationContext(), "Please enter a existing email!", Toast.LENGTH_LONG).show();
                } else {
                    addContact(url, contactEmail, currentUser);
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

    //ADD CONTACT
    private void addContact(final String url, final String contact_email, final String email) {
        class GetJSON extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AddContact.this, "Adding contact...",null,true,true);
            }

            @Override
            protected String doInBackground(Void ... v) {

                HashMap<String,String> params = new HashMap<>();
                params.put("contact_email", contact_email);
                params.put("email", email);


                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(url, params);
                return res;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if (!s.equals(contact_email)) {
                    Toast.makeText(AddContact.this, s, Toast.LENGTH_LONG).show();
                } else {
                    AddContact.this.onBackPressed();
                }

            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    //END OF METHODS
}
