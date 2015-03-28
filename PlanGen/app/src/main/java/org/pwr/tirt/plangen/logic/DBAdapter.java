package org.pwr.tirt.plangen.logic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.pwr.tirt.plangen.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DBAdapter {
    private static final String LOG_TAG = "DBAdapter";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "database.db";
    private static final String DB_TABLE_NAME = "COURSES";

    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMN = 0;

    public static final String KEY_TITLE = "title";
    public static final String TITLE_OPTIONS = "TEXT";
    public static final int TITLE_COLUMN = 1;

    public static final String KEY_TYPE = "type";
    public static final String TYPE_OPTIONS = "TEXT";
    public static final int TYPE_COLUMN = 2;

    public static final String KEY_DATE = "date";
    public static final String DATE_OPTIONS = "TEXT";
    public static final int DATE_COLUMN = 3;

    public static final String KEY_TIME_START = "time_start";
    public static final String TIME_START_OPTIONS = "TEXT";
    public static final int TIME_START_COLUMN = 4;

    public static final String KEY_TIME_END = "time_end";
    public static final String TIME_END_OPTIONS = "TEXT";
    public static final int TIME_END_COLUMN = 5;

    public static final String KEY_LOCATION = "location";
    public static final String LOCATION_OPTIONS = "TEXT";
    public static final int LOCATION_COLUMN = 6;

    public static final String KEY_TUTOR = "tutor";
    public static final String TUTOR_OPTIONS = "TEXT";
    public static final int TUTOR_COLUMN = 7;

    private static final String DB_CREATE_TABLE =
            "CREATE TABLE " + DB_TABLE_NAME + "( " +
                    KEY_ID + " " + ID_OPTIONS + ", " +
                    KEY_TITLE + " " + TITLE_OPTIONS + ", " +
                    KEY_TYPE + " " + TYPE_OPTIONS + ", " +
                    KEY_DATE + " " + DATE_OPTIONS + ", " +
                    KEY_TIME_START + " " + TIME_START_OPTIONS + ", " +
                    KEY_TIME_END + " " + TIME_END_OPTIONS + ", " +
                    KEY_LOCATION + " " + LOCATION_OPTIONS + ", " +
                    KEY_TUTOR + " " + TUTOR_OPTIONS +
                    ");";
    private static final String DROP_TABLE =
            "DROP TABLE IF EXISTS " + DB_TABLE_NAME;

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_TABLE);
            Log.d(LOG_TAG, "Table " + DB_TABLE_NAME + " ver." + DB_VERSION + " created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TABLE); //TODO: copy data instead of drop and recreate
            onCreate(db);
            Log.d(LOG_TAG, "Table " + DB_TABLE_NAME + " updated from ver." + oldVersion + " to ver." + newVersion);
        }
    }

    public DBAdapter(Context context) {
        this.context = context;
    }

    public DBAdapter openConnection(){
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    public void closeConnection() {
        dbHelper.close();
    }

    public boolean insertData(Event event) {
        boolean result = false;

        ContentValues newValues = new ContentValues();
        newValues.put(KEY_TITLE, event.title);
        newValues.put(KEY_TYPE, event.type);
        String formattedValue = Constants.dateFormat.format(event.date.getTime());
        newValues.put(KEY_DATE, formattedValue);
        formattedValue = Constants.timeFormat.format(event.timeStart.getTime());
        newValues.put(KEY_TIME_START, formattedValue);
        formattedValue = Constants.timeFormat.format(event.timeEnd.getTime());
        newValues.put(KEY_TIME_END, formattedValue);
        newValues.put(KEY_LOCATION, event.location);
        newValues.put(KEY_TUTOR, event.tutor);

        if(db.insert(DB_TABLE_NAME, null, newValues) > 0)
            result = true;
        return result;
    }

    public void deleteAllData(){
        db.execSQL(DROP_TABLE);
        db.execSQL(DB_CREATE_TABLE);
    }

    public ArrayList<Event> getDailyEvents (Calendar date) {
        ArrayList<Event> events = new ArrayList<>();
        String[] columns = {KEY_ID, KEY_TITLE, KEY_TYPE, KEY_DATE, KEY_TIME_START, KEY_TIME_END, KEY_LOCATION, KEY_TUTOR};

        Cursor cursor;
        if(date != null) {
            String formattedDate = Constants.dateFormat.format(date.getTime());
            String where = "strftime('%Y-%m-%d', " + KEY_DATE + ") = '" + formattedDate + "'";
            cursor = db.query(DB_TABLE_NAME, columns, where, null, null, null, null);
        } else {
            cursor = db.query(DB_TABLE_NAME, columns, null, null, null, null, null);
        }

        if(cursor != null && cursor.moveToFirst()) {
            do {
                Event event = createEvent(cursor);
                events.add(event);
            } while(cursor.moveToNext());
            cursor.close();
        }
        return events;
    }

    private Event createEvent(Cursor cursor) {
        Event event = new Event();
        event.title = cursor.getString(TITLE_COLUMN);
        event.type = cursor.getString(TYPE_COLUMN);
        Calendar cal;
        try {
            cal = Calendar.getInstance();
            cal.setTime(Constants.dateFormat.parse(cursor.getString(DATE_COLUMN)));
            event.date = cal;
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Date parsing failed: " + e.getMessage());
        }
        try {
            cal = Calendar.getInstance();
            cal.setTime(Constants.timeFormat.parse(cursor.getString(TIME_START_COLUMN)));
            event.timeStart = cal;
            cal = Calendar.getInstance();
            cal.setTime(Constants.timeFormat.parse(cursor.getString(TIME_END_COLUMN)));
            event.timeEnd = cal;
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Time parsing failed: " + e.getMessage());
        }
        event.location = cursor.getString(LOCATION_COLUMN);
        event.tutor = cursor.getString(TUTOR_COLUMN);
        return event;
    }

    /*public Event getData(long id) {
        String[] columns = {KEY_ID, KEY_TITLE, KEY_TYPE, KEY_DATE, KEY_TIME_START, KEY_TIME_END, KEY_LOCATION, KEY_TUTOR};
        String where = KEY_ID + "=" + id;
        Cursor cursor = db.query(DB_TABLE_NAME, columns, where, null, null, null, null);
        Event event = null;
        if(cursor != null && cursor.moveToFirst()) {
            event = createEvent(cursor);
            cursor.close();
        }
        return event;
    }

    public boolean deleteData(long id){
        String where = KEY_ID + "=" + id;
        return db.delete(DB_TABLE_NAME, where, null) > 0;
    }*/
}
