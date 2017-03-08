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
    private TextView inputFirstName;
    private TextView inputLastName;
    private TextView inputEmail;
    private View viewColor;
    private Button btnReturn;

    //Objects
    private StringRequest stringRequest1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contact);

        //ALLOW HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Intent get extra
        contact = this.getIntent().getStringExtra("Contact");

        //Instantiating variables
        inputFirstName = (TextView) findViewById(R.id.input_cinfoFirstName);
        inputLastName = (TextView) findViewById(R.id.input_cinfoLastName);
        inputEmail = (TextView) findViewById(R.id.input_cinfoEmail);
        viewColor = (View) findViewById(R.id.cview_color);
        btnReturn = (Button) findViewById(R.id.btn_creturn);

        String url1 = null;

        try {
            url1 = GET_USER_URL + "?email='" + URLEncoder.encode(contact, "UTF-8") + "'";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        getData(url1);

        //Listeners
        btnReturn.setOnClickListener(this);

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

                        inputFirstName.setText(user.getFirstName());
                        inputLastName.setText(user.getLastName());
                        inputEmail.setText(user.getEmail());

                        String color = "#" + user.getColor();
                        int colorInt = Color.parseColor(color);

                        viewColor.setBackgroundColor(colorInt);
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
