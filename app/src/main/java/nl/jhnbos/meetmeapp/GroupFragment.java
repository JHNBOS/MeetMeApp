package nl.jhnbos.meetmeapp;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    public static final String GET_ALL_GROUPS_URL = "http://jhnbos.nl/android/getAllGroups.php";
    public static final String GET_ALL_GROUPSMEMBERS_URL = "http://jhnbos.nl/android/getAllGroupMembers.php";
    public static final String DELETE_GROUP_URL = "http://jhnbos.nl/android/deleteGroup.php";
    public static final String DELETE_GROUPMEMBERS_URL = "http://jhnbos.nl/android/deleteGroupMembers.php";
    public static final String DELETE_GROUPMEMBER_URL = "http://jhnbos.nl/android/deleteGroupMember.php";
    private static final String GET_USER_URL = "http://jhnbos.nl/android/getUser.php";


    //LISTS
    public ArrayList<String> groupsList;
    public ArrayList<String> memberList;
    public HashMap<String, String> controlList;

    //LAYOUT
    public ListView lv;
    public Button createGroup;

    //OBJECTS
    public ArrayAdapter<String> adapter;
    public StringRequest stringRequest1;
    public StringRequest stringRequest2;
    public User user;


    //STRINGS
    private String email;
    private HTTP http;


    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.fragment_group, container, false);

        //ALLOW HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Instantiating variables
        email = getActivity().getIntent().getStringExtra("Email");
        lv = (ListView) rl.findViewById(R.id.glist);
        createGroup = (Button) rl.findViewById(R.id.createGroupButton);

        groupsList = new ArrayList<>();
        controlList = new HashMap<>();
        user = new User();

        http = new HTTP();

        //Listeners
        createGroup.setOnClickListener(this);
        lv.setOnItemLongClickListener(this);
        lv.setOnItemClickListener(this);
        lv.setLongClickable(true);

        registerForContextMenu(lv);

        //ADAPTER
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, groupsList);

        // Inflate the layout for this fragment
        return rl;
    }

    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    //SHOW DIALOG WHEN DELETING GROUP
    private void ShowDialog(final String data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Remove Group?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                //dialog.dismiss();
                removeGroup(data);

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

    //SHOW GROUPS IN LISTVIEW
    private void showGroups(String response){
        try {
            JSONArray jArray = new JSONArray(response);
            JSONArray ja = jArray.getJSONArray(0);

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);

                groupsList.add(jo.getString("name"));
                controlList.put(jo.getString("name"), jo.getString("creator"));
            }

            lv.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //REMOVE GROUP
    private void removeGroup(String group) {
        try {
            for (Map.Entry<String, String> entry : controlList.entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();

                if (value != email) {
                    http.sendPost(DELETE_GROUP_URL + "?name='" + group + "'");
                    removeGroupMembers(group);
                } else {
                    removeGroupMember(group, email);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //REMOVE GROUPMEMBERS
    private void removeGroupMembers(String group) {
        try {
            http.sendPost(DELETE_GROUPMEMBERS_URL + "?name='" + group + "'");
            onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //REMOVE GROUPMEMBER
    private void removeGroupMember(String group, String email) {
        try {
            http.sendPost(DELETE_GROUPMEMBER_URL + "?name='" + group + "'&email='" + email + "'");
            onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //GET GROUPS
    private void getGroups(final String url) {
        class GetJSON extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Retrieving groups...",null,true,true);
            }

            @Override
            protected String doInBackground(Void ... v) {
                RequestHandler rh = new RequestHandler();
                String res = rh.sendGetRequest(url);
                return res;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                groupsList.clear();
                showGroups(s);
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

        String url1 = GET_ALL_GROUPS_URL + "?email='" + email + "'";
        getGroups(url1);

        adapter.clear();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        if (v == createGroup) {
            Intent createGroupIntent = new Intent(getActivity(), CreateGroup.class);

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
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.group_context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                String selected = groupsList.get((int) info.id);
                ShowDialog(selected);

                return true;
            case R.id.addMember:
                Intent addMemberIntent = new Intent(getActivity(), AddGroupMember.class);

                addMemberIntent.putExtra("Group", groupsList.get((int) info.id));
                addMemberIntent.putExtra("Email", email);

                startActivity(addMemberIntent);

                return true;
            case R.id.showMember:
                Intent showMemberIntent = new Intent(getActivity(), ShowMembers.class);

                showMemberIntent.putExtra("Group", groupsList.get((int) info.id));

                startActivity(showMemberIntent);

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent weekviewIntent = new Intent(getActivity(), Week.class);

        weekviewIntent.putExtra("Group", groupsList.get(position));
        weekviewIntent.putExtra("User", user);

        startActivity(weekviewIntent);
    }

    //END OF LISTENERS
}
