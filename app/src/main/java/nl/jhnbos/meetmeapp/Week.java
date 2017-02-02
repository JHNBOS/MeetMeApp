package nl.jhnbos.meetmeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Week extends AppCompatActivity implements WeekView.EventClickListener ,WeekView.EventLongPressListener, WeekView.EmptyViewClickListener, WeekView.ScrollListener, MonthLoader.MonthChangeListener {

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;

    //STRINGS
    private static final String GET_EVENTS_URL = "http://jhnbos.nl/android/getAllEvents.php";
    private String contact;
    private String group;

    //OBJECTS
    private ArrayList<Event> eventList;
    private List<WeekViewEvent> events;
    private List<WeekViewEvent> matchedEvents;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);

        //BACK BUTTON
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ALLOW HTTP
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Get objects and strings from intent
        group = getIntent().getExtras().getString("Group");
        user = (User) getIntent().getSerializableExtra("User");
        contact = user.getEmail();

        //Lists
        eventList = new ArrayList<>();
        events = new ArrayList<WeekViewEvent>();
        matchedEvents = new ArrayList<>();

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        mWeekView.setShowNowLine(true);

        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        //Set empty view listener
        mWeekView.setEmptyViewClickListener(this);

        //Set scroll listener
        mWeekView.setScrollListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(true);

        /*
        GetJSON get = new GetJSON();
        eventList.clear();
        get.execute();

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mWeekView.notifyDatasetChanged();
        */

    }

    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

    @Override
    public void onResume(){
        super.onResume();

        GetJSON get = new GetJSON();
        eventList.clear();
        get.execute();

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mWeekView.notifyDatasetChanged();

    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
    }

    @Override
    public void onEmptyViewClicked(Calendar time) {
        Toast.makeText(Week.this, String.valueOf(time.get(Calendar.MONTH)+1), Toast.LENGTH_LONG).show();

        mWeekView.notifyDatasetChanged();
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     *
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE");
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat("d/MM");

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                return weekday.toUpperCase() + " " + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                if (hour == 24) hour = 0;
                if (hour == 0) hour = 0;
                return hour + ":00";
            }

        });
    }

    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == (month-1)) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }

    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        mWeekView.notifyDatasetChanged();

        int idset = 0;

        Calendar startCal = null;
        Calendar endCal = null;

        for (int i = 0; i < eventList.size(); i++) {
            startCal = Calendar.getInstance();
            endCal = (Calendar) startCal.clone();

            String Title =
                    user.getFirstName() + " " + user.getLastName()
                    + "\n"
                    + eventList.get(i).getEvent_title()
                    + eventList.get(i).getLocation();

            Timestamp Start = eventList.get(i).getStart();
            Timestamp End = eventList.get(i).getEnd();

            startCal.setTime(Start);
            endCal.setTime(End);

            //+2 is april
            //+1 is march
            //+0 is february => correct
            startCal.set(Calendar.MONTH, (startCal.get(Calendar.MONTH)+0));
            endCal.set(Calendar.MONTH, (endCal.get(Calendar.MONTH)+0));

            int Colour = Color.parseColor(eventList.get(i).getColor());

            WeekViewEvent event = new WeekViewEvent(idset++, Title, startCal, endCal);
            event.setColor(Colour);

            boolean month = false;

            Log.d("startTime Month", String.valueOf(event.getStartTime().get(Calendar.MONTH)));
            Log.d("newMonth", String.valueOf(newMonth));

            if(event.getStartTime().get(Calendar.MONTH) == newMonth && event.getStartTime().get(Calendar.YEAR) == newYear){
                month = true;
            }

            Log.d("Boolean", String.valueOf(month));

            if(!events.contains(event) && month == true){
                Log.d("Event: ", event.getName());
                events.add(event);
            }

            matchedEvents = new ArrayList<>();

            int c = 0;

            for (WeekViewEvent we: events) {
                if(eventMatches(we, newYear, newMonth)){
                    matchedEvents.add(c++, we);
                }
            }

            startCal = null;
            endCal = null;
            event = null;

        }

        mWeekView.notifyDatasetChanged();

        return matchedEvents;
    }


    public void onFirstVisibleDayChanged(Calendar calendar, Calendar calendar1) {
        //mWeekView.notifyDatasetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.week, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id) {
            case R.id.eventcreate:
                Intent createEvent = new Intent(this, Event.class);
                createEvent.putExtra("User", user);
                createEvent.putExtra("GroupC", group);
                createEvent.putExtra("EmailC", contact);

                startActivity(createEvent);
                return true;

            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                super.onBackPressed();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    public WeekView getWeekView() {
        return mWeekView;
    }


    //END OF LISTENERS
    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS
    private void addEvents(String response) {
        try {
            JSONArray jArray = new JSONArray(response);
            JSONArray ja = jArray.getJSONArray(0);

            Event e = null;
            Date start = null;
            Date end = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                e = new Event();


                try {
                    start = sdf.parse(jo.get("start").toString());
                    end = sdf.parse(jo.get("end").toString());
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

                e.setEvent_title(jo.getString("title"));
                e.setLocation(jo.getString("location"));
                e.setStart(new Timestamp(start.getTime()));
                e.setEnd(new Timestamp(end.getTime()));
                e.setColor("#" + jo.getString("color"));


                Log.d("Title", e.getEvent_title());
                Log.d("Location", e.getLocation());
                Log.d("Start", e.getStart().toString());
                Log.d("End", e.getEnd().toString());
                Log.d("Color", e.getColor().toString());

                eventList.add(e);

                e = null;
                start = null;
                end = null;
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }


    //GET GROUPS
    private class GetJSON extends AsyncTask<Void, Void, String> {
        String url = GET_EVENTS_URL + "?group='" + group + "'";
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(Week.this, "Retrieving events...",null,true,true);
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

            addEvents(s);
        }
    }

    //END OF METHODS
}
