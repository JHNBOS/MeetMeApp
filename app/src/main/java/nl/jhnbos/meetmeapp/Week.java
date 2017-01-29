package nl.jhnbos.meetmeapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

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
    public ArrayList<WeekViewEvent> events;
    public User user = new User();

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
        contact = getIntent().getExtras().getString("Email");

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

        String url1 = GET_EVENTS_URL + "?group='" + group + "'";
        getData(url1);

        String url2 = GET_USER_URL + "?email='" + contact + "'";
        getUser(url2);

        mWeekView.notifyDatasetChanged();
    }

    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS
    /*
    @Override
    public void onResume(){
        super.onResume();

        String url1 = GET_EVENTS_URL + "?group='" + group + "'";
        getData(url1);
        mWeekView.notifyDatasetChanged();

    }
*/

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }

    @Override
    public void onEmptyViewClicked(Calendar time) {
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
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        int idset = 0;

        for (int i = 0; i < eventList.size(); i++) {
            String Title = eventList.get(i).getEvent_title(user.getFirstName() + " " + user.getLastName()).toString();
            String Start = eventList.get(i).getStart();
            String End = eventList.get(i).getEnd();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            Calendar start = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
            Calendar end = Calendar.getInstance(TimeZone.getTimeZone("CEST"));

            try {

                start.setTime(sdf.parse(Start));
                end.setTime(sdf.parse(End));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            WeekViewEvent event = new WeekViewEvent(idset++, Title, start, end);

            String color = "#" + user.getColor();

            event.setColor(Color.parseColor(color));

            long eventID = event.getId();
            String numberAsString = String.valueOf(eventID).toString();
            int id = Integer.parseInt(numberAsString);

            events.add(id, event);
            //mWeekView.notifyDatasetChanged();
        }

        //mWeekView.notifyDatasetChanged();
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

    private void getData(String url){
        stringRequest1 = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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

                        Log.d("Title Retrieved Event: ", jo.getString("title"));
                        eventList.add(e);
                    }

                    Log.d("Size eventList: ", String.valueOf(eventList.size()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Week.this, "Error while reading from url", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(Week.this).addToRequestQueue(stringRequest1);

    }

    public void getUser(String url1) {
        stringRequest2 = new StringRequest(url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jArray = new JSONArray(response);
                    JSONArray ja = jArray.getJSONArray(0);

                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);

                        user.setID(jo.getInt("id"));
                        user.setUsername(jo.getString("username"));
                        user.setFirstName(jo.getString("first_name"));
                        user.setLastName(jo.getString("last_name"));
                        user.setEmail(jo.getString("email"));
                        user.setPassword(jo.getString("password"));
                        user.setColor(jo.getString("color"));

                        Log.d("Username: ", jo.getString("username"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Week.this, "Error while reading from url", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(Week.this).addToRequestQueue(stringRequest2);
    }


    //END OF METHODS
}
