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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class AddContact extends AppCompatActivity implements View.OnClickListener {

    //STRINGS
    private static final String ADDCONTACT_URL = "http://jhnbos.nl/android/addContact.php";
    private String currentUser;

    //LAYOUT ITEMS
    private EditText inputContact;
    private Button btnAddContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        //ALLOW HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //INITIALIZING VARIABLES
        inputContact = (EditText) findViewById(R.id.input_contact);
        btnAddContact = (Button) findViewById(R.id.btn_addContact);
        currentUser = getIntent().getExtras().getString("Email");

        //Listeners
        btnAddContact.setOnClickListener(this);
    }


    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

    @Override
    public void onClick(View v) {
        if (v == btnAddContact) {
            String contactEmail = inputContact.getText().toString();
            String url = null;

            try {
                url = ADDCONTACT_URL + "?name=" + URLEncoder.encode(contactEmail, "UTF-8")
                        + "&email=" + URLEncoder.encode(currentUser, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                if (contactEmail == "" || contactEmail.isEmpty()) {
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
                loading = new ProgressDialog(AddContact.this, R.style.AppTheme_Dark_Dialog);
                loading.setIndeterminate(true);
                loading.setMessage("Adding Contact...");
                loading.show();
            }

            @Override
            protected String doInBackground(Void... v) {

                HashMap<String, String> params = new HashMap<>();
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
