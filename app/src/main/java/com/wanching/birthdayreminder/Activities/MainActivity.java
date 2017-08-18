package com.wanching.birthdayreminder.Activities;

import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.wanching.birthdayreminder.Adapters.SimpleFragmentPagerAdapter;
import com.wanching.birthdayreminder.Fragments.AddMEssageDialog;
import com.wanching.birthdayreminder.Fragments.AllBirthdayActivityFragment;
import com.wanching.birthdayreminder.Fragments.UpcomingBirthdayFragment;
import com.wanching.birthdayreminder.Notification.MyReceiver;
import com.wanching.birthdayreminder.Others.BackupDataTask;
import com.wanching.birthdayreminder.R;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbHelper;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbQueries;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by WanChing on 6/8/2017.
 */

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject>, UpcomingBirthdayFragment.OnSetCountListener, AllBirthdayActivityFragment.OnRereshFragmentListener {

    private final static int LOADER_ID = 0;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private TabLayout tabLayout;
    private SimpleFragmentPagerAdapter adapter;

    public static ArrayList<String> arrayList;

    //public static ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpNptification();

        arrayList = new ArrayList<>();

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddBirthdayActivity.class);
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.add_wishes) {
//                    Intent intent = new Intent(MainActivity.this, AddBirthdayActivity.class);
//                    startActivity(intent);
                }

                if (item.getItemId() == R.id.add_wishes) {
                    AddMEssageDialog dialog = new AddMEssageDialog();
                    dialog.show(getSupportFragmentManager(), "AddNewMessageDialog");
                }

                if(item.getItemId() == R.id.drawer_backup_data){
                    backUpData();
                }

                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        BirthdayDbQueries dbq = new BirthdayDbQueries(new BirthdayDbHelper(this));
        Cursor cursor = dbq.retrieveTodayBirthday();
        Log.v("database noti", cursor.getCount() + "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_backup) {
            backUpData();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        return new BackupDataTask(MainActivity.this);
    }

    @Override
    public void onLoadFinished(Loader<JSONObject> loader, JSONObject response) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, outputMessage(response), Snackbar.LENGTH_LONG).show();
        Log.v("data synced", response.toString());
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    public String outputMessage(JSONObject response) {
        try {
            return Integer.toString(response.getInt("recordsSynced")) + " " + getResources().getString(R.string.back_data_message);
        } catch (JSONException ex) {
            Log.e("JSONEXCEPTION", ex.toString());
            return null;
        }
    }

    private void backUpData (){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            getLoaderManager().restartLoader(LOADER_ID, null, this).forceLoad();
        } else {
            Toast.makeText(MainActivity.this, "Network is unavaiable", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setCount(int count) {
        tabLayout.getTabAt(0).setText(getResources().getString(R.string.title_upcoming_tab) + " (" + count + ")");
    }

    @Override
    public void refreshFragment() {
        adapter.getTargetFragment(0).onResume();
    }

    private void setUpNptification(){

        AlarmManager alarmManager;

        Calendar calendar = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        Date date = new Date();
        now.setTime(date);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

        if(calendar.before(now)){
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(this, MyReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Log.v("alarm started", "alarm");

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    }
}
