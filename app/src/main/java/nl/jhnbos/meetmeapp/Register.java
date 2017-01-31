package nl.jhnbos.meetmeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import yuku.ambilwarna.AmbilWarnaDialog;

public class Register extends AppCompatActivity {
    //Strings
    private static final String REGISTER_URL = "http://jhnbos.nl/android/register.php";
    private String Email;

    //Integers
    private int currentColor;

    //Layout items
    private EditText editTextFirst;
    private EditText editTextLast;
    private EditText editTextColor;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextEmail;
    private Button buttonRegister;
    private Button btnPick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Instantiating variables
        editTextFirst = (EditText) findViewById(R.id.fnameEditText);
        editTextLast = (EditText) findViewById(R.id.lnameEditText);
        editTextColor = (EditText) findViewById(R.id.colorEditText);
        editTextUsername = (EditText) findViewById(R.id.unameEditText);
        editTextPassword = (EditText) findViewById(R.id.passEditText);
        editTextEmail = (EditText) findViewById(R.id.emailEditText);
        buttonRegister = (Button) findViewById(R.id.registerButton);
        btnPick = (Button) findViewById(R.id.colorButton);

        //Listeners
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fname = editTextFirst.getText().toString();
                String lname = editTextLast.getText().toString();
                String color = editTextColor.getText().toString().toUpperCase();
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                String email = editTextEmail.getText().toString().toLowerCase();
                Email = email;

                String suffix = "?first_name=" + fname + "&last_name=" + lname + "&color=" + color + "&username="
                        + username + "&password=" + password + "&email=" + email;

                String cURL = REGISTER_URL + suffix;

                final HashMap<String,String> parameter = new HashMap<>();
                parameter.put("username", username);
                parameter.put("first_name", fname);
                parameter.put("last_name", lname);
                parameter.put("color", color);
                parameter.put("password", password);
                parameter.put("email", email);

                attemptRegister(cURL, parameter);
            }
        });
        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(false);
            }
        });

    }

     /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

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

    //ATTEMPT REGISTER
    private void attemptRegister(final String url, final HashMap<String, String> parameters) {
        class GetJSON extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Register.this, "Registering...",null,true,true);
            }

            @Override
            protected String doInBackground(Void ... v) {

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(url, parameters);
                return res;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if (!s.equals(Email)) {
                    Toast.makeText(Register.this, s, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Register.this, "User Registered!", Toast.LENGTH_SHORT).show();
                    Register.this.onBackPressed();
                }

            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();

    }

    private void openDialog(boolean supportsAlpha) {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, currentColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                currentColor = color;
                String numbers = String.format("%x", color);

                String hex = numbers.substring(Math.max(0, numbers.length() - 6));

                Log.i("ColorTest", "Color: " + hex);

                TextView colorView = (TextView) findViewById(R.id.colorEditText);
                colorView.setText(hex.toUpperCase());
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    //END OF METHODS
}
