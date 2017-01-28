package nl.jhnbos.meetmeapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    public static final String GET_ALL_CONTACTS_URL = "http://jhnbos.nl/android/getAllContacts.php";
    public static final String DELETE_CONTACT_URL = "http://jhnbos.nl/android/deleteContact.php";
    public ArrayList<String> contactsList;
    public ListView lv;
    public Button addContact;
    public ArrayAdapter<String> adapter;
    public StringRequest stringRequest1;
    private String email;
    private HTTP http;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.fragment_contact, container, false);

        //ALLOW HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Instantiating variables
        email = getActivity().getIntent().getStringExtra("Email");
        lv = (ListView) rl.findViewById(R.id.clist);
        addContact = (Button) rl.findViewById(R.id.addContactButton);
        contactsList = new ArrayList<>();

        http = new HTTP();

        //Listeners
        addContact.setOnClickListener(this);
        lv.setOnItemLongClickListener(this);
        lv.setOnItemClickListener(this);
        lv.setClickable(true);
        lv.setLongClickable(true);

        registerForContextMenu(lv);

        //ADAPTER
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, contactsList);
        //String url1 = GET_ALL_GROUPS_URL+"?email='"+email+"'";
        //getData(url1);

        // Inflate the layout for this fragment
        return rl;
    }

   /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    //SHOW DIALOG WHEN DELETING GROUP
    private void ShowDialog(final String data, final String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    public void getData(String url1) {
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
                Toast.makeText(getActivity(), "Error while reading from url", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest1);
    }

    //REMOVE CONTACT
    private void removeContact(String contact, String email) {
        try {
            http.sendPost(DELETE_CONTACT_URL + "?name='" + contact + "'&email='" + email + "'");
            onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //END OF METHODS
    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

    @Override
    public void onResume() {
        super.onResume();

        String url1 = GET_ALL_CONTACTS_URL + "?email='" + email + "'";
        getData(url1);

        adapter.clear();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        if (v == addContact) {
            Intent addContactIntent = new Intent(getActivity(), AddContact.class);

            addContactIntent.putExtra("Email", email);
            addContactIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            addContactIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            startActivity(addContactIntent);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.contact_context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                String selected = contactsList.get((int) info.id);
                ShowDialog(selected, email);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selected = (String)parent.getItemAtPosition(position);

        Intent showContact = new Intent(getActivity(), ShowContact.class);
        showContact.putExtra("Contact", selected);

        startActivity(showContact);
    }

    //END OF LISTENERS
}
