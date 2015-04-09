package org.pwr.tirt.plangen.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.pwr.tirt.plangen.R;
import org.pwr.tirt.plangen.logic.DBAdapter;
import org.pwr.tirt.plangen.logic.Event;
import org.pwr.tirt.plangen.logic.EventListAdapter;
import org.pwr.tirt.plangen.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;

public class LayoutActivity extends ActionBarActivity {
    private DBAdapter dbAdapter;
    private Event[] eventsArray;
    private Calendar date;
    private TextView dayOfWeek;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2015); //TODO: delete
        date.set(Calendar.MONTH, 2);
        date.set(Calendar.DAY_OF_MONTH, 27);

        initDatabase();
        getData();

        final Activity activity = this;

        final EventListAdapter adapter = new EventListAdapter(this, R.layout.listview_event_item, eventsArray);
        listView = (ListView) findViewById(R.id.listViewEvents);
        View header = getLayoutInflater().inflate(R.layout.listview_event_header, null);
        dayOfWeek = (TextView) header.findViewById(R.id.textViewHeader);
        dayOfWeek.setText(getDayOfWeek());
        listView.addHeaderView(header);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event selectedFromList = adapter.getItem(position - 1);
                if(!selectedFromList.title.equals(Constants.FREE_TIME_TAG)) {
                    Intent intent = new Intent(activity, DetailsActivity.class);
                    intent.putExtra("eventID", selectedFromList.id);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(dbAdapter != null)
            dbAdapter.closeConnection();
        super.onDestroy();
    }

    private void initDatabase() {
        dbAdapter = new DBAdapter(getApplicationContext());
        dbAdapter.openConnection();
    }

    private void getData() {
        ArrayList<Event> eventsList = dbAdapter.getDailyEvents(date);
        eventsArray = new Event[eventsList.size()];
        eventsArray = eventsList.toArray(eventsArray);
    }

    private String getDayOfWeek() {
        switch (date.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return getString(R.string.monday_long);
            case Calendar.TUESDAY:
                return getString(R.string.tuesday_long);
            case Calendar.WEDNESDAY:
                return getString(R.string.wednesday_long);
            case Calendar.THURSDAY:
                return getString(R.string.thursday_long);
            case Calendar.FRIDAY:
                return getString(R.string.friday_long);
            case Calendar.SATURDAY:
                return getString(R.string.saturday_long);
            case Calendar.SUNDAY:
                return getString(R.string.sunday_long);
            default:
                return Constants.NO_DATA;
        }
    }

    private void setDate(int dayNumber) {
        Calendar calendar = Calendar.getInstance();
        int diff = dayNumber - calendar.get(Calendar.DAY_OF_WEEK);
        if (!(diff > 0))
            diff += 7;
        calendar.add(Calendar.DAY_OF_MONTH, diff);
        date = calendar;
        if (dayNumber == Calendar.FRIDAY) { //TODO: delete
            date.set(Calendar.YEAR, 2015);
            date.set(Calendar.MONTH, 2);
            date.set(Calendar.DAY_OF_MONTH, 27);
        }
        dayOfWeek.setText(getDayOfWeek());
        getData();
        listView.setAdapter(new EventListAdapter(this, R.layout.listview_event_item, eventsArray));
    }

    public void onClickMonday(View view) {
        setDate(Calendar.MONDAY);
    }

    public void onClickTuesday(View view) {
        setDate(Calendar.TUESDAY);
    }

    public void onClickWednesday(View view) {
        setDate(Calendar.WEDNESDAY);
    }

    public void onClickThursday(View view) {
        setDate(Calendar.THURSDAY);
    }

    public void onClickFriday(View view) {
        setDate(Calendar.FRIDAY);
    }

    public void onClickSaturday(View view) {
        setDate(Calendar.SATURDAY);
    }

    public void onClickSunday(View view) {
        setDate(Calendar.SUNDAY);
    }
}
