package nl.jhnbos.meetmeapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class Login extends AppCompatActivity {

    //STRINGS
    private static final String LOGIN_URL = "http://jhnbos.nl/android/login.php";
    private String email;
    private String password;

    //LAYOUT ITEMS
    private Button loginButton;
    private Button registerButton;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //ALLOW HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Instantiating variables
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);

        SharedPreferences sharedPref = Login.this.getPreferences(Context.MODE_PRIVATE);
        String emailValue = getResources().getString(R.string.username);
        String passwordValue = getResources().getString(R.string.password);

        if(emailValue != "" || !emailValue.isEmpty()){
            email = emailValue;
        } else{
            email = emailEditText.getText().toString().trim();
        }

        if(passwordValue != "" || !passwordValue.isEmpty()){
            password = passwordValue;
        } else{
            password = passwordEditText.getText().toString().trim();
        }


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
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(URL);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

    }

    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    private void attemptLogin(final String url) {
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        class GetJSON extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Login.this, "Logging in...", null, true, true);
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
                } else {

                    SharedPreferences sharedPref = Login.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.username), email);
                    editor.putString(getString(R.string.password), password);
                    editor.commit();

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.putExtra("Email", email);

                    Toast.makeText(Login.this, "Login Succeeded!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }

            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }
    //END OF METHODS
}
