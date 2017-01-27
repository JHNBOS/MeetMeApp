package nl.jhnbos.meetmeapp;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class CreateGroup extends AppCompatActivity implements View.OnClickListener {

    private EditText groupNameField;
    private Button createButton;
    private static final String ADDGROUP_URL = "http://jhnbos.nl/android/addGroup.php";
    private static final String ADDGROUPMEMBER_URL = "http://jhnbos.nl/android/addGroupMember.php";
    private String currentUser;
    private String groupName;
    private HTTP http;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        //BACK BUTTON
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ALLOW HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        groupNameField = (EditText) findViewById(R.id.gnameEditText);
        createButton = (Button) findViewById(R.id.createGButton);

        createButton.setOnClickListener(this);

        http = new HTTP();
    }

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

    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

    @Override
    public void onClick(View v) {
        if(v == createButton){
            groupName = groupNameField.getText().toString();
            currentUser =  getIntent().getStringExtra("Email");

            try{
                if(groupName == "" || groupName.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please fill in a name for the group!", Toast.LENGTH_LONG).show();
                } else {
                    addGroup(groupName, currentUser);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    //END OF LISTENERS
    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    //ADD GROUP
    private void addGroup(String name, String email) {
        try {
            String response = http.sendGet(ADDGROUP_URL + "?name=" + name + "&email=" + email);

            if(!response.equals(name) || name.isEmpty() || name.contains("'")){
                Toast.makeText(this, "Please enter a valid group name!", Toast.LENGTH_LONG).show();
            } else{
                addGroupMember(name, email);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //ADD GROUPMEMBER
    private void addGroupMember(String name, String email) {
        try {
            String response = http.sendGet(ADDGROUPMEMBER_URL + "?name=" + name + "&email=" + email);

            if(!response.equals(name) || name.isEmpty() || !name.contains("@") || !name.contains(".")){
                Toast.makeText(this, "Problem with creating group!", Toast.LENGTH_LONG).show();
            } else{
                CreateGroup.this.onBackPressed();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //END OF METHODS
}
