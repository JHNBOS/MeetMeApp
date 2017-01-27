package nl.jhnbos.meetmeapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Contacts extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    private String email;
    public static final String GET_ALL_CONTACTS_URL = "http://jhnbos.nl/android/getAllContacts.php";
    public static final String DELETE_CONTACT_URL = "http://jhnbos.nl/android/deleteContact.php";
    public ArrayList<String> contactsList;
    public ListView lv;
    public Button addContact;
    public ArrayAdapter<String> adapter;
    public StringRequest stringRequest1;
    private HTTP http;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        //BACK BUTTON
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ALLOW HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Instantiating variables
        email = this.getIntent().getStringExtra("Email");
        lv = (ListView)findViewById(R.id.clist);
        addContact = (Button) findViewById(R.id.addContactsButton);
        contactsList = new ArrayList<>();

        http = new HTTP();

        //Listeners
        addContact.setOnClickListener(this);
        lv.setOnItemLongClickListener(this);
        lv.setLongClickable(true);

        registerForContextMenu(lv);

        //ADAPTER
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactsList);
        String url1 = GET_ALL_CONTACTS_URL+"?email='"+email+"'";
        getData(url1);

    }

    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    //SHOW DIALOG WHEN DELETING GROUP
    private void ShowDialog(final String data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Contact?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                //dialog.dismiss();
                removeContact(data, email);

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
                        Log.d("Contact", jo.getString("contact_email"));
                        contactsList.add(jo.getString("contact_email"));
                    }

                    lv.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Contacts.this, "Error while reading from url", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest1);
    }

    //REMOVE CONTACT
    private void removeContact(String contact, String email) {
        try {
            http.sendPost(DELETE_CONTACT_URL + "?name='" + contact + "'" + "&email='" + email + "'");
            onRestart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //END OF METHODS
    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

    @Override
    protected void onRestart(){
        super.onRestart();

        String url1 = GET_ALL_CONTACTS_URL+"?email='"+email+"'";
        getData(url1);

        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if(v == addContact){
            Intent createGroupIntent = new Intent(Contacts.this, AddContact.class);

            createGroupIntent.putExtra("Email", email);
            createGroupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            createGroupIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            startActivity(createGroupIntent);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                String selected = contactsList.get((int) info.id);
                ShowDialog(selected);
                return true;
            case R.id.addMember:
                Toast.makeText(this, "Coming soon!", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    //END OF LISTENERS
}
