package nl.jhnbos.meetmeapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Date;

public class Event extends AppCompatActivity implements View.OnClickListener {

    public String title;
    public String location;
    public Date start;
    public Date end;
    public String creator;
    public String group;
    public TimePicker starttime;
    public TimePicker endtime;

    private Button createEventButton;

    private HTTP http;
    private static final String ADDEVENT_URL = "http://jhnbos.nl/android/addEvent.php";

    public Event(){

    }

    public Event(String title, String location, Date start, Date end, String creator, String group) {
        this.title = title;
        this.location = location;
        this.start = start;
        this.end = end;
        this.creator = creator;
        this.group = group;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        createEventButton = (Button) findViewById(R.id.addEventButton);
        createEventButton.setOnClickListener(this);

        creator = getIntent().getExtras().getString("Email");

        starttime = (TimePicker) findViewById(R.id.timePickerStart);
        endtime = (TimePicker) findViewById(R.id.timePickerEnd);

        starttime.setIs24HourView(true);
        endtime.setIs24HourView(true);
    }

    //ADD GROUP
    private void addEvent() {
        try
        {
            String ev_title = this.title;
            String ev_loc = this.getLocation();
            String ev_start = this.getStart().toString();
            String ev_end = this.getEnd().toString();
            String ev_creator = this.getCreator();
            String ev_group = this.getGroup();

            String response = http.sendGet(ADDEVENT_URL + "?title='" + ev_title
                    + "'&loc='" + ev_loc + "'&start='" + ev_start + "'&end='" + ev_end + "'"
                    + "'&creator='" + ev_creator + "'&group='" + ev_group + "'" );

            if (response.equals(ev_title)) {
                Toast.makeText(Event.this, "Event created!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getTitles() {
        ///GET TITLE TEXT
        EditText name = (EditText) findViewById(R.id.titleField);
        String title = creator + ": " + "\n" + name.getText().toString();

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {return location;}

    public void setLocation(String location) {this.location = location;}

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getCreator() {return creator;}

    public void setCreator(String creator) {this.creator = creator;}

    public String getGroup() {return group;}

    public void setGroup(String group) {this.group = group;}

    @Override
    public void onClick(View v) {
        if(v == createEventButton){
            addEvent();
        }
    }
}
