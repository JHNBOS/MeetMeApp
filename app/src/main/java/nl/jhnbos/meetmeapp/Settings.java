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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import yuku.ambilwarna.AmbilWarnaDialog;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    //STRINGS
    private static final String USER_UPDATE_URL = "http://jhnbos.nl/android/updateUser.php";
    private static final String GET_USER_URL = "http://jhnbos.nl/android/getUser.php";
    private String email;

    //LAYOUT ITEMS
    private EditText colorBox;
    private EditText oldPasswordBox;
    private EditText newPasswordBox;
    private Button colorButton;
    private Button updateButton;

    //INTEGERS
    private int currentColor;

    //OBJECTS
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //ALLOW HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //INITIALIZING VARIABLES
        email = getIntent().getStringExtra("Email");
        user = new User();

        colorBox = (EditText) findViewById(R.id.colorEditText2);
        oldPasswordBox = (EditText) findViewById(R.id.oldPasswordEditText);
        newPasswordBox = (EditText) findViewById(R.id.newPasswordEditText);

        colorButton = (Button) findViewById(R.id.colorButton2);
        updateButton = (Button) findViewById(R.id.updateButton);

        //LISTENERS
        colorButton.setOnClickListener(this);
        updateButton.setOnClickListener(this);

        //GetUser
        getUserJSON getUserJSON = null;

        try {
            getUserJSON = new getUserJSON();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        getUserJSON.execute();

    }

    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

    @Override
    public void onClick(View v) {
        if(v == colorButton){
            openDialog(false);
        }

        if(v == updateButton){
            if(colorBox.getText().toString().isEmpty() && oldPasswordBox.getText().toString().isEmpty()){
                Toast.makeText(Settings.this, "Please fill in a field to update!", Toast.LENGTH_LONG).show();
            } else{
                runUpdate();
            }
        }
    }

    //END OF LISTENERS
    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    private void updateUser(final String url, final HashMap<String, String> parameters){
        class GetJSON extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Settings.this, "Updating user...", null, true, true);
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

                if (!s.equals(user.getEmail())) {
                    Toast.makeText(Settings.this, s, Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(Settings.this, MainActivity.class);

                    Toast.makeText(Settings.this, "Updated user!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
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

                TextView colorView = (TextView) findViewById(R.id.colorEditText2);
                colorView.setText(hex.toUpperCase());
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    //Run updateUser
    private void runUpdate(){
        String fname = user.getFirstName();
        String lname = user.getLastName();
        String email = user.getEmail();
        int id = user.getID();

        String color = null;
        String password = null;

        if(colorBox.getText().toString() == "" || colorBox.getText().toString().isEmpty()){
            color = user.getColor();
        } else{
            color = colorBox.getText().toString();
        }

        if((oldPasswordBox.getText().toString().isEmpty() || oldPasswordBox.getText().toString() == "")
                && (newPasswordBox.getText().toString().isEmpty() || newPasswordBox.getText().toString() == "")){
            password = user.getPassword();

        } else{
            password = newPasswordBox.getText().toString();
        }

        String suffix = null;

        try {
            suffix = "?first_name='" + URLEncoder.encode(fname, "UTF-8") + "'"
                    + "&last_name='" + URLEncoder.encode(lname, "UTF-8") + "'"
                    + "&color='" + URLEncoder.encode(color, "UTF-8") + "'"
                    + "&password='" + URLEncoder.encode(password, "UTF-8") + "'"
                    + "&email='" + URLEncoder.encode(email, "UTF-8") + "'"
                    + "&id='" + URLEncoder.encode(String.valueOf(id), "UTF-8") + "'";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String cURL = USER_UPDATE_URL + suffix;

        Log.d("", cURL);

        final HashMap<String, String> parameter = new HashMap<>();
        parameter.put("first_name", fname);
        parameter.put("last_name", lname);
        parameter.put("color", color);
        parameter.put("password", password);
        parameter.put("email", email);
        parameter.put("id", String.valueOf(id));

        updateUser(cURL, parameter);

    }

    //GET USER
    private class getUserJSON extends AsyncTask<Void, Void, String> {
        String url = GET_USER_URL + "?email='" + URLEncoder.encode(email, "UTF-8") + "'";
        ProgressDialog loading;

        private getUserJSON() throws UnsupportedEncodingException {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(Settings.this, "Retrieving user...", null, true, true);
        }

        @Override
        protected String doInBackground(Void... v) {
            RequestHandler rh = new RequestHandler();
            String res = rh.sendGetRequest(url);
            return res;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();

            initUser(s);
        }
    }

    //INITIALIZE USER
    private void initUser(String response) {
        try {
            JSONArray jArray = new JSONArray(response);
            JSONArray ja = jArray.getJSONArray(0);

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);

                user.setID(jo.getInt("id"));
                user.setFirstName(jo.getString("first_name"));
                user.setLastName(jo.getString("last_name"));
                user.setPassword(jo.getString("password"));
                user.setEmail(jo.getString("email"));
                user.setColor(jo.getString("color"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //END OF METHODS
}
