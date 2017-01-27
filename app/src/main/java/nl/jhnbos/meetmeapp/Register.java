package nl.jhnbos.meetmeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import yuku.ambilwarna.AmbilWarnaDialog;

public class Register extends AppCompatActivity {
    private int currentColor;
    private EditText editTextFirst;
    private EditText editTextLast;
    private EditText editTextColor;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextEmail;

    private Button buttonRegister;
    private Button btnPick;

    private static final String REGISTER_URL = "http://jhnbos.nl/android/register.php";
    private HTTP http;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
                attemptRegister();
            }
        });
        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(false);
            }
        });

        http = new HTTP();

    }

    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    //ATTEMPT REGISTER
    private void attemptRegister() {
        String fname = editTextFirst.getText().toString();
        String lname = editTextLast.getText().toString();
        String color = editTextColor.getText().toString().toUpperCase();
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        String email = editTextEmail.getText().toString().toLowerCase();

        try {
            String suffix = "?first_name="+fname+"&last_name="+lname+"&color="+color+"&username="+username+"&password="+password+"&email="+email;
            String response = http.sendPost(REGISTER_URL + suffix);

            if(!response.equals(email) || fname.isEmpty() || lname.isEmpty() || color.isEmpty()
                    || username.isEmpty() || password.isEmpty() || email.isEmpty()){
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(Register.this, Login.class);

                Toast.makeText(this, "Succesfully registered!", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Register Failed!", Toast.LENGTH_LONG).show();
        }
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
