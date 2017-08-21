package com.wanching.birthdayreminder.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Telephony;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.wanching.birthdayreminder.Others.Person;
import com.wanching.birthdayreminder.R;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayContract;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbHelper;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbQueries;
import com.wanching.birthdayreminder.SQLiteDatabase.DbColumns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by WanChing on 6/8/2017.
 */

/**
 * Activity for viewing personal details of a selected person
 */

public class ViewBirthdayActivity extends AppCompatActivity {

    // Message to be sent out through SMS
    public static final String EXTRA_BIRTHDAY = "com.wanching.birthdayreminder.BIRTHDAY";

    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_birthday);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Intent received from fragment
        Intent intent = getIntent();
        long id = intent.getLongExtra("ID", 0);

        BirthdayDbQueries dbq = new BirthdayDbQueries((new BirthdayDbHelper(getApplicationContext())));

        String selection = BirthdayContract.BirthdayEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};

        Cursor cursor = dbq.read(DbColumns.columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToNext()) {
            person = BirthdayDbQueries.retrievePerson(cursor);

            setTitle(getResources().getString(R.string.birthday_wish) + " to " + person.getName());

            TextView tvEmail = findViewById(R.id.show_email);
            TextView tvPhone = findViewById(R.id.show_phone);
            TextView tvDate = findViewById(R.id.date);
            TextView tvLeft = findViewById(R.id.left);
            Switch swNotification = findViewById(R.id.view_switch);

            tvEmail.setText("Email: " + person.getEmail());
            tvPhone.setText("Phone: " + person.getPhone());
            tvLeft.setText("Left");
            swNotification.setChecked(person.isNotify());
            swNotification.setClickable(false);

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
        cursor.close();
    }

    /**
     * Display countdown animation for a birthday
     * @param millisUntilFinished End time of the countdown
     */
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

    /**
     * Edit personal details of the selected person
     * @param view The selected button
     */
    public void editBirthday(View view) {
        Intent intent = new Intent(getApplicationContext(), UpdateBirthdayActivity.class);
        intent.putExtra(EXTRA_BIRTHDAY, person);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }

    /**
     * Choose a birthday wish to send to the selected person through SMS
     * @param view The selected button
     */
    public void messageWish(View view) {

        List<CharSequence> charSequences = new ArrayList<>();

        charSequences.add(getString(R.string.first_wish));
        charSequences.add(getString(R.string.second_wish));
        charSequences.add(getString(R.string.third_wish));

        int count = 0;

        // To add new birthday wish (which are inserted by user through DrawerLayout) into the dialog list
        if(MainActivity.arrayList != null) {
            while (count < MainActivity.arrayList.size()) {
                String newMessage = MainActivity.arrayList.get(count);
                charSequences.add(new String(newMessage));
                count++;
            }
        }

        final CharSequence[] charSequencesArray = charSequences.toArray(new CharSequence[charSequences.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewBirthdayActivity.this);

        builder.setTitle(String.format(getString(R.string.wishes_dialog_title), person.getName()));
        builder.setSingleChoiceItems(charSequencesArray, 0, null);
        builder.setPositiveButton(R.string.response_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int selectedPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                String messageToSend = charSequencesArray[selectedPosition].toString();
                String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(getApplicationContext());

                // Send a SMS to a particular phone number
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + Uri.encode(person.getPhone().toString())));
                intent.putExtra("sms_body", messageToSend);

                // Can be null in case that there is no default, then the user would be able to choose any app that supports this intent.
                if (defaultSmsPackageName != null)
                {
                    intent.setPackage(defaultSmsPackageName);
                }

                startActivity(intent);

            }
        });
        builder.setNegativeButton(R.string.response_cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), getString(R.string.task_cancelled), Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    /**
     * Delete current birthday record
     * @param view The selected button
     */
    public void deleteBirthday(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewBirthdayActivity.this);
        builder.setCancelable(false)
                .setMessage(getString(R.string.dialog_message))
                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        BirthdayDbQueries dbq = new BirthdayDbQueries(new BirthdayDbHelper(getApplicationContext()));
                        dbq.delete(person.getId());
                        Toast.makeText(getApplicationContext(), "Deleted " + person.getName(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), getString(R.string.task_cancelled), Toast.LENGTH_SHORT).show();
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle(getString(R.string.dialog_title));
        alert.show();
    }
}
