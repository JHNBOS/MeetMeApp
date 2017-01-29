package nl.jhnbos.meetmeapp;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    private String startDate;
    private String endDate;

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

    /*-------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    //ADD GROUP
    private void addEvent() {

        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

        try
        {
            String ev_title = titleField.getText().toString();
            String ev_loc = locField.getText().toString();
            Date ev_start = sdf.parse(startDate);
            Date ev_end = sdf.parse(endDate);
            String ev_creator = getIntent().getExtras().getString("EmailC");
            String ev_group = getIntent().getExtras().getString("GroupC");

            Log.d("Title", ev_title);
            Log.d("Location: ", ev_loc);
            Log.d("Start: ", ev_start.toString());
            Log.d("End: ", ev_end.toString());
            Log.d("Creator: ", ev_creator);
            Log.d("Group: ", ev_group);

            String response = http.sendPost(
                    ADDEVENT_URL + "?title=" + URLEncoder.encode(ev_title, "UTF-8")
                    + "&loc=" + URLEncoder.encode(ev_loc, "UTF-8")
                    + "&start=" + URLEncoder.encode(ev_start.toString(), "UTF-8")
                    + "&end=" + URLEncoder.encode(ev_end.toString(), "UTF-8")
                    + "&creator=" + URLEncoder.encode(ev_creator, "UTF-8")
                    + "&group=" + URLEncoder.encode(ev_group, "UTF-8")
            );

            if (response.equals(ev_title)) {
                Toast.makeText(Event.this, "Event created!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //END OF METHODS
    /*-------------------------------------------------------------------------*/
    //BEGIN OF GETTERS AND SETTERS

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

    //END OF GETTERS AND SETTERS
    /*-------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

    @Override
    public void onClick(View v) {
        //IF PRESSED ON CREATE EVENT BUTTON
        if(v == createEventButton){
            addEvent();

            super.onBackPressed();
        }

        //IF PRESSED ON PICK START DATE
        if(v == startDateButton){
            Calendar now = Calendar.getInstance();
            startdatepickerdialog =  DatePickerDialog.newInstance(
                    Event.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            startdatepickerdialog.setThemeDark(false); //set dark them for dialog?
            startdatepickerdialog.vibrate(false); //vibrate on choosing date?
            startdatepickerdialog.dismissOnPause(true); //dismiss dialog when onPause() called?
            startdatepickerdialog.showYearPickerFirst(false); //choose year first?
            startdatepickerdialog.setAccentColor(Color.BLUE); // custom accent color
            startdatepickerdialog.setTitle("Start Date"); //dialog title

            startdatepickerdialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                    String date = "You picked the following start date: " + dayOfMonth + "/" + (++monthOfYear) + "/" + year;
                    startDate = year + "-" + monthOfYear + "-" + dayOfMonth;
                }
            });

            startdatepickerdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(Event.this, "Cancel choosing date", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            startdatepickerdialog.show(getFragmentManager(), "Datepickerdialog"); //show dialog
        }

        //IF PRESSED ON PICK START TIME
        if(v == startTimeButton){
            Calendar now = Calendar.getInstance();
            starttimepickerdialog = TimePickerDialog.newInstance(
                    Event.this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE), true);
            starttimepickerdialog.setThemeDark(false); //Dark Theme?
            starttimepickerdialog.vibrate(false); //vibrate on choosing time?
            starttimepickerdialog.dismissOnPause(false); //dismiss the dialog onPause() called?
            starttimepickerdialog.enableSeconds(false); //show seconds?
            starttimepickerdialog.setTitle("Start Time");

            starttimepickerdialog.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                    String date = "You picked the following start time: " + hourOfDay + ":" + (++minute);
                    startDate += " " + hourOfDay + ":" + minute;
                }
            });

            //Handling cancel event
            starttimepickerdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Toast.makeText(Event.this, "Cancel choosing time", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            });

            starttimepickerdialog.show(getFragmentManager(), "Timepickerdialog"); //show time picker dialog
        }

        //IF PRESSED ON PICK END DATE
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
            enddatepickerdialog.setAccentColor(Color.BLUE); // custom accent color
            enddatepickerdialog.setTitle("End Date"); //dialog title

            enddatepickerdialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                    String date = "You picked the following end date: " + dayOfMonth + "/" + (++monthOfYear) + "/" + year;
                    endDate = year + "-" + monthOfYear + "-" + dayOfMonth;
                }
            });

            enddatepickerdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(Event.this, "Cancel choosing date", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            enddatepickerdialog.show(getFragmentManager(), "Datepickerdialog"); //show dialog
        }

        //IF PRESSED ON PICK END TIME
        if(v == endTimeButton){
            Calendar now = Calendar.getInstance();
            endtimepickerdialog = TimePickerDialog.newInstance(
                    Event.this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE), true);
            endtimepickerdialog.setThemeDark(false); //Dark Theme?
            endtimepickerdialog.vibrate(false); //vibrate on choosing time?
            endtimepickerdialog.dismissOnPause(false); //dismiss the dialog onPause() called?
            endtimepickerdialog.enableSeconds(false); //show seconds?
            endtimepickerdialog.setTitle("End Time");

            endtimepickerdialog.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                    String date = "You picked the following end time: " + hourOfDay + ":" + (++minute);
                    endDate += " " + hourOfDay + ":" + minute;
                }
            });

            //Handling cancel event
            endtimepickerdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Toast.makeText(Event.this, "Cancel choosing time", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            });

            endtimepickerdialog.show(getFragmentManager(), "Timepickerdialog"); //show time picker dialog
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

    }

    //END OF LISTENERS

}
