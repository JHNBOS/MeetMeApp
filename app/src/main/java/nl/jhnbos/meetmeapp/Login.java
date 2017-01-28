package nl.jhnbos.meetmeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;

public class Login extends AppCompatActivity {
    //LAYOUT ITEMS
    private Button loginButton;
    private Button registerButton;
    private EditText emailEditText;
    private EditText passwordEditText;

    //STRINGS
    public static final String EMAIL = "USER_NAME";
    public static final String PASSWORD = "PASSWORD";
    private static final String LOGIN_URL = "http://jhnbos.nl/android/login.php";
    private static final String GET_ALL_USERS_URL = "http://jhnbos.nl/android/getAllUsers.php";

    private HTTP http;
    public StringRequest stringRequest1;
    public HashMap<String, String> controlList;

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

        controlList = new HashMap<>();

        //Listeners
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });


        http = new HTTP();

    }

    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    //ATTEMPT LOGIN
    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        try{
            String response = http.sendPost(LOGIN_URL + "?email=" + email + "&password=" + password);

            if(!response.equals(email + password)){
                Toast.makeText(this, "Invalid username and/or password!", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(Login.this, MainActivity.class);
                intent.putExtra("Email", email);

                Toast.makeText(this, "Login Succeeded!", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        try {
            String url1 = GET_ALL_USERS_URL;
            getData(url1);

            if(controlList.containsKey(email)) {
                String response = http.sendPost(LOGIN_URL + "?email=" + email + "&password=" + password);

                if (!response.equals(email)) {
                    Toast.makeText(this, "Invalid email and/or password!", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.putExtra("Email", email);

                    Toast.makeText(this, "Login Succeeded!", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Login Failed!", Toast.LENGTH_LONG).show();
        }
        */
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
                        Log.d("Control User", jo.getString("email"));
                        Log.d("Control User", jo.getString("password"));
                        controlList.put(jo.getString("email"), jo.getString("password"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "Error while reading from url", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(Login.this).addToRequestQueue(stringRequest1);
    }

    //END OF METHODS
}
