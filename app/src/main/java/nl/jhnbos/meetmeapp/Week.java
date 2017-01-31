package nl.jhnbos.meetmeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.CalendarContract;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class Week extends AppCompatActivity implements WeekView.EventClickListener, WeekView.EventLongPressListener, WeekView.EmptyViewClickListener, WeekView.ScrollListener, MonthLoader.MonthChangeListener {

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;

    //STRINGS
    private static final String GET_EVENTS_URL = "http://jhnbos.nl/android/getAllEvents.php";
    private static final String GET_USER_URL = "http://jhnbos.nl/android/getUser.php";
    private String contact;
    private String group;

    //OBJECTS
    public StringRequest stringRequest1;
    public StringRequest stringRequest2;
    public ArrayList<Event> eventList;
    public List<WeekViewEvent> events;
    public User user;

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

        group = getIntent().getExtras().getString("Group");
        user = (User) getIntent().getSerializableExtra("User");
        contact = user.getEmail();


        eventList = new ArrayList<>();


        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        //Set empty view listener
        mWeekView.setEmptyViewClickListener(this);

        mWeekView.setScrollListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(true);

    }

    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS

    @Override
    public void onResume(){
        super.onResume();

        String url2 = GET_EVENTS_URL + "?group='" + group + "'";
        getEvents(url2);

        mWeekView.notifyDatasetChanged();
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();

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

    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        //Create Event
        events = new ArrayList<WeekViewEvent>();

        int Colour = Color.parseColor("#" + user.getColor());

        Log.d("Size eventList", String.valueOf(eventList.size()));

        for (int i = 0; i < eventList.size(); i++) {
            String Title = eventList.get(i).getEvent_title(user.getFirstName() + " " + user.getLastName());
            String Start = eventList.get(i).getStart();
            String End = eventList.get(i).getEnd();

             /*--------------------------------------*/
            //Start
            String[] startdateSplit = Start.split(" ");

            String sDate = startdateSplit[0];
            String[] sDateSplit = sDate.split("-");

            String sYear = sDateSplit[0];
            String sMonth = sDateSplit[1];
            String sDay = sDateSplit[2];

            String sTime = startdateSplit[1];
            String[] sTimeSplit = sTime.split(":");

            String sHour = sTimeSplit[0];
            String sMinute = sTimeSplit[1];
            /*--------------------------------------*/
            //End
            String[] enddateSplit = End.split(" ");

            String eDate = enddateSplit[0];
            String[] eDateSplit = eDate.split("-");

            String eYear = eDateSplit[0];
            String eMonth = eDateSplit[1];
            String eDay = eDateSplit[2];

            String eTime = enddateSplit[1];
            String[] eTimeSplit = eTime.split(":");

            String eHour = eTimeSplit[0];
            String eMinute = eTimeSplit[1];
            /*--------------------------------------*/

            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sHour));
            startTime.set(Calendar.MINUTE, Integer.parseInt(sMinute));
            startTime.set(Calendar.DATE, Integer.parseInt(sDay));
            startTime.set(Calendar.MONTH, Integer.parseInt(sMonth)-1);
            startTime.set(Calendar.YEAR, Integer.parseInt(sYear));
            Calendar endTime = (Calendar) startTime.clone();
            endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(eHour));
            endTime.set(Calendar.MINUTE, Integer.parseInt(eMinute));
            endTime.set(Calendar.DATE, Integer.parseInt(eDay));
            endTime.set(Calendar.MONTH, Integer.parseInt(eMonth)-1);
            endTime.set(Calendar.YEAR, Integer.parseInt(eYear));
            WeekViewEvent event = new WeekViewEvent(i, Title, startTime, endTime);
            event.setColor(Colour);

            if (startTime.get(Calendar.MONTH) == newMonth && startTime.get(Calendar.YEAR) == newYear){
                events.add(event);
            }

        }

        return events;
    }


    public void onFirstVisibleDayChanged(Calendar calendar, Calendar calendar1) {
        mWeekView.notifyDatasetChanged();
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
                createEvent.putExtra("Name", user.getFirstName() + " " + user.getLastName());
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


    private void addEvents(String response){
        try {
            JSONArray jArray = new JSONArray(response);
            JSONArray ja = jArray.getJSONArray(0);

            Log.d("JSONARRAY: ", ja.toString());

            Event e = new Event();

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);

                e.setEvent_title(jo.getString("title"));
                e.setLocation(jo.getString("location"));
                e.setStart(jo.getString("start"));
                e.setEnd(jo.getString("end"));

                eventList.add(e);
            }

            Log.d("Size eventList: ", String.valueOf(eventList.size()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //GET GROUPS
    private void getEvents(final String url) {

        Log.d("URL", url);

        class GetJSON extends AsyncTask<Void, Void, String> {
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
        GetJSON gj = new GetJSON();
        gj.execute();
    }


    //END OF METHODS
}
