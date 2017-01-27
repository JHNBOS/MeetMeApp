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

    private HTTP http;

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

        try {
            String response = http.sendPost(LOGIN_URL + "?email=" + email + "&password=" + password);

            if(!response.equals(email)){
                Toast.makeText(this, "Invalid username and/or password!", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(Login.this, MainActivity.class);
                intent.putExtra("Email", email);

                Toast.makeText(this, "Login Succeeded!", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Login Failed!", Toast.LENGTH_LONG).show();
        }
    }

    //END OF METHODS
}
