package org.pwr.tirt.plangen.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.pwr.tirt.plangen.R;

public class TestActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickLayout(View view) {
        Intent intent = new Intent(this, LayoutActivity.class);
        startActivity(intent);
    }

    public void onClickLogic(View view) {
        Intent intent = new Intent(this, LogicActivity.class);
        startActivity(intent);
    }

    public void onClickAddEvent(View view) {
        Intent intent = new Intent(this, AddEventActivity.class);
        startActivity(intent);
    }

    public void onClickWeekView(View view) {
        Intent intent = new Intent(this, WeekViewActivity.class);
        startActivity(intent);
    }
}
