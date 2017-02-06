package nl.jhnbos.meetmeapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ShowContact extends AppCompatActivity implements View.OnClickListener {

    //Strings
    private static final String GET_USER_URL = "http://jhnbos.nl/android/getUser.php";
    private String contact;

    //Textviews
    private TextView usernameField;
    private TextView firstnameField;
    private TextView lastnameField;
    private TextView emailField;
    private View colorBox;
    private TextView colorField;

    //Layout
    private Button returnButton;

    //Objects
    private StringRequest stringRequest1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contact);

        //BACK BUTTON
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ALLOW HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Intent get extra
        contact = this.getIntent().getStringExtra("Contact");

        //Instantiating variables
        usernameField = (TextView) findViewById(R.id.usernameField);
        firstnameField = (TextView) findViewById(R.id.firstnameField);
        lastnameField = (TextView) findViewById(R.id.lastnameField);
        emailField = (TextView) findViewById(R.id.emailField);
        colorField = (TextView) findViewById(R.id.colorField);
        colorBox = (View) findViewById(R.id.colorBox);
        returnButton = (Button) findViewById(R.id.returnContactButton);

        String url1 = null;

        try {
            url1 = GET_USER_URL + "?email='" + URLEncoder.encode(contact, "UTF-8") + "'";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        getData(url1);

        //Listeners
        returnButton.setOnClickListener(this);

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

    public void getData(String url1) {
        stringRequest1 = new StringRequest(url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jArray = new JSONArray(response);
                    JSONArray ja = jArray.getJSONArray(0);

                    User user = new User();

                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);

                        user.setID(jo.getInt("id"));
                        user.setFirstName(jo.getString("first_name"));
                        user.setLastName(jo.getString("last_name"));
                        user.setEmail(jo.getString("email"));
                        user.setPassword(jo.getString("password"));
                        user.setColor(jo.getString("color"));

                        firstnameField.setText(user.getFirstName());
                        lastnameField.setText(user.getLastName());
                        emailField.setText(user.getEmail());
                        colorField.setText("#" + user.getColor());

                        String color = "#" + user.getColor();
                        int colorInt = Color.parseColor(color);

                        colorBox.setBackgroundColor(colorInt);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShowContact.this, "Error while reading from url", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(ShowContact.this).addToRequestQueue(stringRequest1);
    }

    @Override
    public void onClick(View v) {
        super.onBackPressed();
    }

    //END OF METHODS


}
