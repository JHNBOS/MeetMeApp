package nl.jhnbos.meetmeapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class Login extends AppCompatActivity {

    //STRINGS
    private static final String LOGIN_URL = "http://jhnbos.nl/android/login.php";
    private String email = "";
    private String password = "";
    private SharedPreferences sharedPref;

    //LAYOUT ITEMS
    private Button btnLogin;
    private TextView signUpLink;
    private EditText emailInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //ALLOW HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        //Instantiating variables
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        emailInput = (EditText) findViewById(R.id.input_email);
        passwordInput = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        signUpLink = (TextView) findViewById(R.id.link_signup);

        checkCredentials();

        //API URL
        final String URL;
        String url = null;

        try {
            url = LOGIN_URL + "?email=" + URLEncoder.encode(email, "UTF-8")
                    + "&password=" + URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        URL = url;

        //Listeners
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(URL);
            }
        });
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

    }

    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    @Override
    public void onResume() {
        super.onResume();
        checkCredentials();

    }

    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    //Save username and password
    private void saveCredentials(String email, String password) {
        Log.d("Login", "Start Saving preferences");
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userEmail", email);
        editor.putString("userPassword", password);

        Log.d("EMAIL", email);
        Log.d("PASSWORD", password);
        editor.apply();
    }

    //Check username and password
    private void checkCredentials() {
        Log.d("Login", "Start Searching preferences");

        SharedPreferences.Editor editor = sharedPref.edit();

        if (sharedPref != null) {
            String emailValue = sharedPref.getString("userEmail", "");
            String passwordValue = sharedPref.getString("userPassword", "");

            if (emailValue != "" || !emailValue.isEmpty()) {
                email = emailValue;
                emailInput.setText(emailValue);
            } else {
                email = emailInput.getText().toString().trim();
            }

            if (passwordValue != "" || !passwordValue.isEmpty()) {
                password = passwordValue;
                passwordInput.setText(passwordValue);
            } else {
                password = passwordInput.getText().toString().trim();
            }

            if (!emailValue.equals(emailInput.getText().toString().trim())
                    && !passwordValue.equals(passwordInput.getText().toString().trim())) {
                editor.remove("username");
                editor.remove("password");
                editor.commit();
            }
        }
    }

    private void attemptLogin(final String url) {
        btnLogin.setEnabled(false);
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        class GetJSON extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = new ProgressDialog(Login.this, R.style.AppTheme_Dark_Dialog);
                loading.setIndeterminate(true);
                loading.setMessage("Authenticating...");
                loading.show();
            }

            @Override
            protected String doInBackground(Void... v) {

                HashMap<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);


                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(url, params);
                return res;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if (!s.equals(email + password)) {
                    Toast.makeText(Login.this, s, Toast.LENGTH_LONG).show();
                    btnLogin.setEnabled(true);
                } else {
                    saveCredentials(email, password);

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.putExtra("Email", email);

                    Toast.makeText(Login.this, "Login Succeeded!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    //finish();
                }

            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }


    //END OF METHODS
}
