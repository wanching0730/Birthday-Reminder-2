package com.wanching.birthdayreminder.Activities;

import android.app.NotificationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wanching.birthdayreminder.Adapters.BirthdayCursorAdapter;
import com.wanching.birthdayreminder.R;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbHelper;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbQueries;

/**
 * Created by WanChing on 6/8/2017.
 */

/**
 * Activity for displaying today birthday records
 */

public class TodayBirthdayActivity extends AppCompatActivity {

    private ListView listView;
    private BirthdayCursorAdapter adapter;
    private TextView tvEmpty;
    private RelativeLayout rl;
    private BirthdayDbQueries dbq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_birthday);

        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        dbq = new BirthdayDbQueries(new BirthdayDbHelper(getApplicationContext()));

        rl = (RelativeLayout) findViewById(R.id.view_relative_layout);
        listView = (ListView) findViewById(R.id.today_birthday_list);
        tvEmpty = rl.findViewById(R.id.today_empty_view);

        adapter = new BirthdayCursorAdapter(this, dbq.retrieveTodayBirthday(), 0);
        listView.setAdapter(adapter);
        listView.setEmptyView(tvEmpty);
        tvEmpty.setText("No birthday record found today");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // To refresh cursor
        adapter.swapCursor(dbq.retrieveTodayBirthday());

        // To indicate an empty list
        tvEmpty.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }
}
