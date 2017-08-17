package com.wanching.birthdayreminder.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Telephony;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wanching.birthdayreminder.Others.Person;
import com.wanching.birthdayreminder.R;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayContract;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbHelper;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbQueries;
import com.wanching.birthdayreminder.SQLiteDatabase.DbColumns;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by WanChing on 6/8/2017.
 */

public class ViewBirthdayActivity extends AppCompatActivity {

    public static final String EXTRA_BIRTHDAY = "com.wanching.birthdayreminder.BIRTHDAY";
    private Person person;

    public static boolean changeBoolean(int notify) {
        return notify > 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_birthday);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        long id = intent.getLongExtra("ID", 0);

        BirthdayDbQueries dbq = new BirthdayDbQueries((new BirthdayDbHelper(getApplicationContext())));

        String selection = BirthdayContract.BirthdayEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};

        Cursor cursor = dbq.read(DbColumns.columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToNext()) {
            person = BirthdayDbQueries.retrievePerson(cursor);

            setTitle(getResources().getString(R.string.birthday_wish) + " to " + person.getName());

            TextView tvEmail = (TextView) findViewById(R.id.show_email);
            TextView tvPhone = (TextView) findViewById(R.id.show_phone);
            TextView tvDate = (TextView) findViewById(R.id.date);
            TextView tvLeft = (TextView) findViewById(R.id.left);

            tvEmail.setText("Email: " + person.getEmail());
            tvPhone.setText("Phone: " + person.getPhone());
            tvLeft.setText("Left");

            Calendar today = Calendar.getInstance();
            Calendar annualBirthday = person.getThisYearBirthday();
            Calendar nextBirthday = person.getNextYearBirthday();
            if (today.get(Calendar.MONTH) < annualBirthday.get(Calendar.MONTH) || (today.get(Calendar.MONTH) == annualBirthday.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < annualBirthday.get(Calendar.DAY_OF_MONTH))) {
                tvDate.setText("Birthday: " + new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).format(person.getThisYearBirthday().getTime()));
                CountDownTimer timer = new CountDownTimer(annualBirthday.getTimeInMillis(), 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        onTickCalculation(millisUntilFinished);
                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();
            } else {
                tvDate.setText("Next Birthday: " + new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).format(person.getNextYearBirthday().getTime()));
                CountDownTimer timer = new CountDownTimer(nextBirthday.getTimeInMillis(), 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        onTickCalculation(millisUntilFinished);
                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();
            }

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        } else {
            Log.e("id not found", Long.toString(cursor.getLong(cursor.getColumnIndex(BirthdayContract.BirthdayEntry._ID))));
            finish();
        }
    }

    public void onTickCalculation(long millisUntilFinished) {

        TextView tvDay = (TextView) findViewById(R.id.day);
        TextView tvHour = (TextView) findViewById(R.id.hour);
        TextView tvMinute = (TextView) findViewById(R.id.minute);
        TextView tvSecond = (TextView) findViewById(R.id.second);

        long startTime = System.currentTimeMillis();
        startTime = startTime - 1;
        long serverUptimeSeconds = (millisUntilFinished - startTime) / 1000;

        tvDay.setText(Long.toString(serverUptimeSeconds / 86400));
        tvHour.setText(Long.toString((serverUptimeSeconds % 86400) / 3600));
        tvMinute.setText(Long.toString(((serverUptimeSeconds % 86400) % 3600) / 60));
        tvSecond.setText(Long.toString(((serverUptimeSeconds % 86400) % 3600) % 60));
    }

    public void editBirthday(View view) {
        Intent intent = new Intent(getApplicationContext(), UpdateBirthdayActivity.class);
        intent.putExtra(EXTRA_BIRTHDAY, person);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }

    public void messageWish(View view) {

//        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
//        smsIntent.setType("vnd.android-dir/mms-sms");
//        smsIntent.putExtra("address", person.getPhone());
//        smsIntent.putExtra("sms_body", "happy birthday");
//        if(smsIntent.resolveActivity(getPackageManager()) != null)
//            startActivity(smsIntent);

        String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(getApplicationContext());

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + Uri.encode(person.getPhone().toString())));
        intent.putExtra("sms_body", "happy birthday");

        // Can be null in case that there is no default, then the user would be able to choose any app that supports this intent.
        if (defaultSmsPackageName != null)
        {
            intent.setPackage(defaultSmsPackageName);
        }

        startActivity(intent);
    }

    public void deleteBirthday(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewBirthdayActivity.this);
        builder.setCancelable(false)
                .setMessage("Are you sure you want to delete this record?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        BirthdayDbQueries dbq = new BirthdayDbQueries(new BirthdayDbHelper(getApplicationContext()));
                        dbq.delete(person.getId());
                        Toast.makeText(getApplicationContext(), "Deleted " + person.getName(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("WARNING");
        alert.show();
    }
}
