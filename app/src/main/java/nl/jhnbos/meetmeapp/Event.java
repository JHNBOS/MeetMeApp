package nl.jhnbos.meetmeapp;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.net.URLEncoder;
import java.sql.Timestamp;

public class Event extends AppCompatActivity implements View.OnClickListener {

    //STRINGS
    private static final String ADDEVENT_URL = "http://jhnbos.nl/android/addEvent.php";
    public String event_title;
    public String location;
    public String creator;
    public String group;
    public String color;
    public String name;
    public Timestamp start;
    public Timestamp end;
    private String startDate;
    private String endDate;
    //LAYOUT ITEMS
    private Button createEventButton;
    private EditText titleField;
    private EditText locField;
    private DatePicker startdatepickerdialog;
    private DatePicker enddatepickerdialog;
    private TimePicker starttimepickerdialog;
    private TimePicker endtimepickerdialog;
    //OBJECTS
    private HTTP http;
    private User user;

    public Event() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //BACK BUTTON
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ALLOW HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        creator = getIntent().getExtras().getString("EmailC");
        name = getIntent().getExtras().getString("Name");
        user = (User) getIntent().getSerializableExtra("User");

        Log.d("User ID in Event", String.valueOf(user.getID()));
        Log.d("User name in Event", String.valueOf(user.getUsername()));

        titleField = (EditText) findViewById(R.id.titleField);
        locField = (EditText) findViewById(R.id.locField);
        startdatepickerdialog = (DatePicker) findViewById(R.id.startDatePicker);
        enddatepickerdialog = (DatePicker) findViewById(R.id.endDatePicker);
        starttimepickerdialog = (TimePicker) findViewById(R.id.startTimePicker);
        endtimepickerdialog = (TimePicker) findViewById(R.id.endTimePicker);
        createEventButton = (Button) findViewById(R.id.addEventButton);

        //Set 24 hour
        starttimepickerdialog.setIs24HourView(true);
        endtimepickerdialog.setIs24HourView(true);

        //Listeners
        createEventButton.setOnClickListener(this);

        http = new HTTP();
    }

    /*-------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    //ADD GROUP
    private void addEvent() {
        try {
            String ev_loc = locField.getText().toString();
            String ev_start = startDate.toString();
            String ev_end = endDate.toString();
            String ev_creator = getIntent().getExtras().getString("EmailC");
            String ev_group = getIntent().getExtras().getString("GroupC");
            String ev_title = titleField.getText().toString();

            String response = http.sendPost(
                    ADDEVENT_URL + "?title=" + URLEncoder.encode(ev_title, "UTF-8")
                            + "&loc=" + URLEncoder.encode(ev_loc, "UTF-8")
                            + "&start=" + URLEncoder.encode(ev_start.toString(), "UTF-8")
                            + "&end=" + URLEncoder.encode(ev_end.toString(), "UTF-8")
                            + "&creator=" + URLEncoder.encode(ev_creator, "UTF-8")
                            + "&group=" + URLEncoder.encode(ev_group, "UTF-8")
                            + "&color=" + URLEncoder.encode(user.getColor(), "UTF-8")
            );

            if (response.equals(ev_title)) {
                Toast.makeText(Event.this, response, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //END OF METHODS
    /*-------------------------------------------------------------------------*/
    //BEGIN OF GETTERS AND SETTERS

    public String getEvent_title() {
        return event_title;
    }

    public void setEvent_title(String event_title) {
        this.event_title = event_title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    //END OF GETTERS AND SETTERS
    /*-------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        //IF PRESSED ON CREATE EVENT BUTTON
        if (v == createEventButton) {
            //Start date and time
            String startDay = String.valueOf(startdatepickerdialog.getDayOfMonth());
            String startMonth = String.valueOf(startdatepickerdialog.getMonth()+1);
            String startYear = String.valueOf(startdatepickerdialog.getYear());

            String startHour = String.valueOf(starttimepickerdialog.getHour());
            String startMinute = String.valueOf(starttimepickerdialog.getMinute());

            //End date and time
            String endDay = String.valueOf(enddatepickerdialog.getDayOfMonth());
            String endMonth = String.valueOf(enddatepickerdialog.getMonth()+1);
            String endYear = String.valueOf(enddatepickerdialog.getYear());

            String endHour = String.valueOf(endtimepickerdialog.getHour());
            String endMinute = String.valueOf(endtimepickerdialog.getMinute());

            startDate = startYear + "-" + startMonth + "-" + startDay + " " + startHour + ":" + startMinute;
            endDate = endYear + "-" + endMonth + "-" + endDay + " " + endHour + ":" + endMinute;

            addEvent();

            super.onBackPressed();
        }
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

    //END OF LISTENERS

}
