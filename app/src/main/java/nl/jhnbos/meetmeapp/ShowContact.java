package nl.jhnbos.meetmeapp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    //Edit Texts
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputEmail;
    private View viewColor;
    private Button btnReturn;

    //Objects
    private User user;

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
        inputFirstName = (EditText) findViewById(R.id.input_cinfoFirstName);
        inputLastName = (EditText) findViewById(R.id.input_cinfoLastName);
        inputEmail = (EditText) findViewById(R.id.input_cinfoEmail);
        viewColor = (View) findViewById(R.id.cview_color);
        btnReturn = (Button) findViewById(R.id.btn_creturn);
        user = new User();

        //Listeners
        btnReturn.setOnClickListener(this);

        //GetUser
        ShowContact.getUserJSON getUserJSON = null;

        try {
            getUserJSON = new ShowContact.getUserJSON();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        getUserJSON.execute();
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

    @Override
    public void onClick(View v) {
        super.onBackPressed();
    }


    //END OF LISTENERS
    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

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

            inputFirstName.setText(user.getFirstName());
            inputLastName.setText(user.getLastName());
            inputEmail.setText(user.getEmail());

            String color = "#" + user.getColor();
            int colorInt = Color.parseColor(color);

            viewColor.setBackgroundColor(colorInt);

            //Set non editable
            inputFirstName.setEnabled(false);
            inputLastName.setEnabled(false);
            inputEmail.setEnabled(false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //GET USER
    private class getUserJSON extends AsyncTask<Void, Void, String> {
        String url = GET_USER_URL + "?email='" + URLEncoder.encode(contact, "UTF-8") + "'";
        ProgressDialog loading;

        private getUserJSON() throws UnsupportedEncodingException {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(ShowContact.this, R.style.AppTheme_Dark_Dialog);
            loading.setIndeterminate(true);
            loading.setMessage("Retrieving Contact...");
            loading.show();
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

    //END OF METHODS


}
