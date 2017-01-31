package nl.jhnbos.meetmeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

        email = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();

        final String url = LOGIN_URL + "?email=" + email + "&password=" + password;

        //Listeners
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(url);
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
                loading = ProgressDialog.show(Login.this, "Logging in...",null,true,true);
            }

            @Override
            protected String doInBackground(Void ... v) {

                HashMap<String,String> params = new HashMap<>();
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
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.putExtra("Email", email);

                    Toast.makeText(Login.this, "Login Succeeded!", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }

            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }
    //END OF METHODS
}
