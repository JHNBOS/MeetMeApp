package nl.jhnbos.meetmeapp;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
    private String email;
    private HTTP http;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LinearLayout rl = (LinearLayout) inflater.inflate(R.layout.fragment_contact, container, false);

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
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, contactsList);

        // Inflate the layout for this fragment
        return rl;
    }

   /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    //SHOW DIALOG WHEN DELETING GROUP
    private void ShowDialog(final String data, final String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
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

    //SHOW CONTACTS IN LISTVIEW
    private void showContacts(String response) {
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

    //REMOVE CONTACT
    private void removeContact(String contact, String email) {
        try {
            http.sendPost(DELETE_CONTACT_URL + "?name='" + URLEncoder.encode(contact, "UTF-8")
                    + "'&email='" + URLEncoder.encode(email, "UTF-8") + "'");
            onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //GET CONTACTS
    private void getContacts(final String url) {
        class GetJSON extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
                loading.setIndeterminate(true);
                loading.setMessage("Retrieving Contacts...");
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

                contactsList.clear();
                showContacts(s);
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    //END OF METHODS
    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

    @Override
    public void onResume() {
        super.onResume();

        String url1 = null;

        try {
            url1 = GET_ALL_CONTACTS_URL + "?email='" + URLEncoder.encode(email, "UTF-8") + "'";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        getContacts(url1);

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
        String selected = (String) parent.getItemAtPosition(position);

        Intent showContact = new Intent(getActivity(), ShowContact.class);
        showContact.putExtra("Contact", selected);

        startActivity(showContact);
    }

    //END OF LISTENERS
}
