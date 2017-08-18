package com.wanching.birthdayreminder.Activities;

import android.app.NotificationManager;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wanching.birthdayreminder.Adapters.BirthdayCursorAdapter;
import com.wanching.birthdayreminder.R;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayContract;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbHelper;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbQueries;
import com.wanching.birthdayreminder.SQLiteDatabase.DbColumns;

import java.util.Calendar;

public class TodayBirthdayActivity extends AppCompatActivity {

    private ListView listView;
    private BirthdayCursorAdapter adapter;
    private TextView tvEmpty;
    private RelativeLayout rl;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_birthday);

        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        BirthdayDbQueries dbq = new BirthdayDbQueries(new BirthdayDbHelper(this));
        String selection = "strftime('%m-%d'," + BirthdayContract.BirthdayEntry.COLUMN_NAME_DATE + "/1000, 'unixepoch')" + " == strftime('%m-%d',?/1000, 'unixepoch')";
        String[] selectionArgs = {Long.toString(Calendar.getInstance().getTimeInMillis())};
        cursor = dbq.read(DbColumns.columns, selection, selectionArgs, null, null, BirthdayContract.BirthdayEntry.COLUMN_NAME_NAME + " ASC");


        rl = (RelativeLayout) findViewById(R.id.view_relative_layout);
        listView = (ListView) findViewById(R.id.today_birthday_list);
        adapter = new BirthdayCursorAdapter(this, cursor, 0);
        listView.setAdapter(adapter);

        tvEmpty = rl.findViewById(R.id.today_empty_view);
        listView.setEmptyView(tvEmpty);
        tvEmpty.setText("No birthday record found today");
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.swapCursor(cursor);
        tvEmpty.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }
}
