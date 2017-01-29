package nl.jhnbos.meetmeapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Date;

public class Event extends AppCompatActivity {

    public String title;
    public Date start;
    public Date end;
    public String creator;
    public String group;
    public TimePicker starttime;
    public TimePicker endtime;

    public Event(){

    }

    public Event(String title, Date start, Date end, String creator, String group) {
        this.title = title;
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

        starttime = (TimePicker) findViewById(R.id.timePickerStart);
        endtime = (TimePicker) findViewById(R.id.timePickerEnd);

        starttime.setIs24HourView(true);
        endtime.setIs24HourView(true);
    }

    public void saveDate(){
        try {

        } catch (Exception e){
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
}
