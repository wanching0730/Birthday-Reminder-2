package com.wanching.birthdayreminder.Activities;

import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wanching.birthdayreminder.Adapters.SimpleFragmentPagerAdapter;
import com.wanching.birthdayreminder.Fragments.MultiTaskDialogFragment;
import com.wanching.birthdayreminder.Fragments.AllBirthdayFragment;
import com.wanching.birthdayreminder.Fragments.UpcomingBirthdayFragment;
import com.wanching.birthdayreminder.Notification.MyReceiver;
import com.wanching.birthdayreminder.Others.BackupDataTask;
import com.wanching.birthdayreminder.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by WanChing on 6/8/2017.
 */

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<JSONObject>,
        UpcomingBirthdayFragment.OnSetCountListener,
        AllBirthdayFragment.OnRereshFragmentListener {

    public static ArrayList<String> arrayList;
    public static TextView etUsername;
    public static TextView etEmail;

    private final static int LOADER_ID = 0;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private SimpleFragmentPagerAdapter adapter;

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
        viewPager.setCurrentItem(1);

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

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);

        View headerLayout = navigationView.getHeaderView(0);
        etUsername = headerLayout.findViewById(R.id.username);
        etEmail = headerLayout.findViewById(R.id.email);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Bundle bundle = new Bundle();
                MultiTaskDialogFragment dialog = new MultiTaskDialogFragment();


                if (item.getItemId() == R.id.change_username) {
                    bundle.putInt("id", item.getItemId());
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(), getString(R.string.dialog_param));
                }

                //To add new birthday wishes
                if (item.getItemId() == R.id.change_email) {
                    bundle.putInt("id", item.getItemId());
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(), getString(R.string.dialog_param));
                }

                //To backup data to cloud
                if(item.getItemId() == R.id.setting){
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                }

                if (item.getItemId() == R.id.add_wishes) {
                    bundle.putInt("id", item.getItemId());
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(), getString(R.string.dialog_param));
                }

                if (item.getItemId() == R.id.backup_data) {
                    backUpData();
                }

                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
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

    /**
     * Return message after data are synced to cloud
     * @param response Response from cloud
     * @return String Message to be output for user
     */
    public String outputMessage(JSONObject response) {
        try {
            return Integer.toString(response.getInt("recordsSynced")) + " " + getResources().getString(R.string.back_data_message);
        } catch (JSONException ex) {
            Log.e("JSONEXCEPTION", ex.toString());
            return null;
        }
    }

    /**
     * Connect to cloud for backup data purpose
     */
    private void backUpData (){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            getLoaderManager().restartLoader(LOADER_ID, null, this).forceLoad();
        } else {
            Toast.makeText(MainActivity.this, "Network is unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Display number of records for upcoming birthday on TabLayout
     * @param count Number of records for upcoming birthday
     */
    @Override
    public void setCount(int count) {
        tabLayout.getTabAt(0).setText(getResources().getString(R.string.title_upcoming_tab) + " (" + count + ")");
    }

    @Override
    public void refreshFragment() {
        adapter.getTargetFragment(0).onResume();
    }

    /**
     * Set up notification at 8am everyday to inform today birthday list
     */
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


        // Add one day to the alarm after it ringed
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
