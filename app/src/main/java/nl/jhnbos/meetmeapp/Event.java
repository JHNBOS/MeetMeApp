package nl.jhnbos.meetmeapp;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;

public class Event extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public String title;
    public String location;
    public Date start;
    public Date end;
    public String creator;
    public String group;

    private Button createEventButton;
    private EditText titleField;
    private EditText locField;
    private Button startDateButton;
    private Button startTimeButton;
    private Button endDateButton;
    private Button endTimeButton;
    private DatePickerDialog startdatepickerdialog;
    private DatePickerDialog enddatepickerdialog;
    private TimePickerDialog starttimepickerdialog;
    private TimePickerDialog endtimepickerdialog;

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

        creator = getIntent().getExtras().getString("Email");
        group = getIntent().getExtras().getString("Group");

        titleField = (EditText) findViewById(R.id.titleField);
        locField = (EditText) findViewById(R.id.locField);

        startDateButton = (Button) findViewById(R.id.startDateButton);
        startTimeButton = (Button) findViewById(R.id.startTimeButton);

        endDateButton = (Button) findViewById(R.id.endDateButton);
        endTimeButton = (Button) findViewById(R.id.endTimeButton);
        createEventButton = (Button) findViewById(R.id.addEventButton);

        //Listeners
        startDateButton.setOnClickListener(this);
        startTimeButton.setOnClickListener(this);
        endDateButton.setOnClickListener(this);
        endTimeButton.setOnClickListener(this);
        createEventButton.setOnClickListener(this);

        http = new HTTP();

    }

    //ADD GROUP
    private void addEvent() {

        try
        {
            String ev_title = titleField.getText().toString();
            String ev_loc = locField.getText().toString();
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

        if(v == startDateButton){
            Calendar now = Calendar.getInstance();
            startdatepickerdialog = DatePickerDialog.newInstance(
                    Event.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            startdatepickerdialog.setThemeDark(false); //set dark them for dialog?
            startdatepickerdialog.vibrate(false); //vibrate on choosing date?
            startdatepickerdialog.dismissOnPause(true); //dismiss dialog when onPause() called?
            startdatepickerdialog.showYearPickerFirst(false); //choose year first?
            startdatepickerdialog.setAccentColor(Color.GREEN); // custom accent color
            startdatepickerdialog.setTitle("Start Date"); //dialog title
            startdatepickerdialog.show(getFragmentManager(), "Datepickerdialog"); //show dialog
        }

        if(v == startTimeButton){
            Calendar now = Calendar.getInstance();
            starttimepickerdialog = TimePickerDialog.newInstance(Event.this,
                    now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
            starttimepickerdialog.setThemeDark(false); //Dark Theme?
            starttimepickerdialog.vibrate(false); //vibrate on choosing time?
            starttimepickerdialog.dismissOnPause(false); //dismiss the dialog onPause() called?
            starttimepickerdialog.enableSeconds(false); //show seconds?
            starttimepickerdialog.setTitle("Start Time");

            //Handling cancel event
            starttimepickerdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Toast.makeText(Event.this, "Cancel choosing time", Toast.LENGTH_SHORT).show();
                }
            });
            starttimepickerdialog.show(getFragmentManager(), "Timepickerdialog"); //show time picker dialog
        }

        if(v == endDateButton){
            Calendar now = Calendar.getInstance();
            enddatepickerdialog = DatePickerDialog.newInstance(
                    Event.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            enddatepickerdialog.setThemeDark(false); //set dark them for dialog?
            enddatepickerdialog.vibrate(false); //vibrate on choosing date?
            enddatepickerdialog.dismissOnPause(true); //dismiss dialog when onPause() called?
            enddatepickerdialog.showYearPickerFirst(false); //choose year first?
            enddatepickerdialog.setAccentColor(Color.GREEN); // custom accent color
            enddatepickerdialog.setTitle("End Date"); //dialog title
            enddatepickerdialog.show(getFragmentManager(), "Datepickerdialog"); //show dialog
        }

        if(v == endTimeButton){
            Calendar now = Calendar.getInstance();
            endtimepickerdialog = TimePickerDialog.newInstance(Event.this,
                    now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
            endtimepickerdialog.setThemeDark(false); //Dark Theme?
            endtimepickerdialog.vibrate(false); //vibrate on choosing time?
            endtimepickerdialog.dismissOnPause(false); //dismiss the dialog onPause() called?
            endtimepickerdialog.enableSeconds(false); //show seconds?
            endtimepickerdialog.setTitle("End Time");

            //Handling cancel event
            endtimepickerdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Toast.makeText(Event.this, "Cancel choosing time", Toast.LENGTH_SHORT).show();
                }
            });
            endtimepickerdialog.show(getFragmentManager(), "Timepickerdialog"); //show time picker dialog
        }
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if(view == startdatepickerdialog){
            String date = "You picked the following start date: " + dayOfMonth + "/" + (++monthOfYear) + "/" + year;
        }

        if(view == enddatepickerdialog){
            String date = "You picked the following end date: " + dayOfMonth + "/" + (++monthOfYear) + "/" + year;
        }

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

    }
}
