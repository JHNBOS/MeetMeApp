package nl.jhnbos.meetmeapp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import yuku.ambilwarna.AmbilWarnaDialog;

public class Register extends AppCompatActivity {
    //Strings
    private static final String REGISTER_URL = "http://jhnbos.nl/android/register.php";
    private String Email;
    private String chosenColor;

    //Integers
    private int currentColor;

    //Layout items
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputPassword;
    private EditText inputEmail;
    private Button btnSignUp;
    private Button btnColorPick;
    private View viewColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Instantiating variables
        inputFirstName = (EditText) findViewById(R.id.input_firstName);
        inputLastName = (EditText) findViewById(R.id.input_lastName);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputEmail = (EditText) findViewById(R.id.input_email);
        btnSignUp = (Button) findViewById(R.id.btn_signUp);
        viewColor = (View) findViewById(R.id.rview_color);
        btnColorPick = (Button) findViewById(R.id.btn_color);
        chosenColor = new String();

        //Listeners
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fname = inputFirstName.getText().toString();
                String lname = inputLastName.getText().toString();
                String color = chosenColor.toUpperCase();
                String password = inputPassword.getText().toString();
                String email = inputEmail.getText().toString().toLowerCase();
                Email = email;

                String suffix = null;

                try {
                    suffix = "?first_name=" + URLEncoder.encode(fname, "UTF-8")
                            + "&last_name=" + URLEncoder.encode(lname, "UTF-8")
                            + "&color=" + URLEncoder.encode(color, "UTF-8")
                            + "&password=" + URLEncoder.encode(password, "UTF-8")
                            + "&email=" + URLEncoder.encode(email, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String cURL = REGISTER_URL + suffix;

                final HashMap<String, String> parameter = new HashMap<>();
                parameter.put("first_name", fname);
                parameter.put("last_name", lname);
                parameter.put("color", color);
                parameter.put("password", password);
                parameter.put("email", email);

                if (email == "" || fname == "" || lname == "" || color == "" || password == "") {
                    Toast.makeText(getApplicationContext(), "Please enter all fields!", Toast.LENGTH_LONG).show();
                } else if (!email.contains("@") || !email.contains(".")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid email!", Toast.LENGTH_LONG).show();
                } else {
                    attemptRegister(cURL, parameter);
                }

            }
        });
        btnColorPick.setOnClickListener(new View.OnClickListener() {
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
        btnSignUp.setEnabled(false);
        class GetJSON extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = new ProgressDialog(Register.this, R.style.AppTheme_Dark_Dialog);
                loading.setIndeterminate(true);
                loading.setMessage("Creating Account...");
                loading.show();
            }

            @Override
            protected String doInBackground(Void... v) {

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
                    btnSignUp.setEnabled(true);
                } else {
                    Toast.makeText(Register.this, "User Registered!", Toast.LENGTH_SHORT).show();
                    //Register.this.onBackPressed();
                    finish();
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

                chosenColor = hex.toUpperCase();
                viewColor.setBackgroundColor(Color.parseColor("#" + chosenColor));

            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }
        });
        dialog.show();
    }

    //END OF METHODS
}
