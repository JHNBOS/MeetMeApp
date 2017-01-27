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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddContact extends AppCompatActivity implements View.OnClickListener {

    private EditText contactEmailField;
    private Button addContactButton;
    private static final String ADDCONTACT_URL = "http://jhnbos.nl/android/addContact.php";
    private String currentUser;
    private String contactEmail;
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
    private void addContact(String contact_email, String email) {
        try {
            String response = http.sendGet(ADDCONTACT_URL + "?name=" + contact_email + "&email=" + email);

            if(!response.equals(contact_email) || contact_email.isEmpty() || email.isEmpty()){
                Toast.makeText(this, "Please enter an existing email address!", Toast.LENGTH_LONG).show();
            } else{
                AddContact.this.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //END OF METHODS
}