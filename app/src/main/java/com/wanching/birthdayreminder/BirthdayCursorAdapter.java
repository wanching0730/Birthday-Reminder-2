package com.wanching.birthdayreminder;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BirthdayCursorAdapter extends CursorAdapter{

    private LayoutInflater inflater;
    private Person person;


    public BirthdayCursorAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, flags);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String name = cursor.getString(cursor.getColumnIndex(BirthdayContract.BirthdayEntry.COLUMN_NAME_NAME));
        String email = cursor.getString(cursor.getColumnIndex(BirthdayContract.BirthdayEntry.COLUMN_NAME_EMAIL));
        String phone = cursor.getString(cursor.getColumnIndex(BirthdayContract.BirthdayEntry.COLUMN_NAME_PHONE));
        byte[] imageByte = cursor.getBlob(cursor.getColumnIndex(BirthdayContract.BirthdayEntry.COLUMN_NAME_IMAGE));
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        Long date = cursor.getLong(cursor.getColumnIndex(BirthdayContract.BirthdayEntry.COLUMN_NAME_DATE));
        Date formattedDate = new Date(date);

        person = new Person(
                cursor.getLong(cursor.getColumnIndex(BirthdayContract.BirthdayEntry._ID)),
                name,
                email,
                phone,
                imageBitmap,
                formattedDate,
                changeBoolean(cursor.getInt(cursor.getColumnIndex(BirthdayContract.BirthdayEntry.COLUMN_NAME_NOTIFY))));

        Person.Countdown countdown = person.getCountdown();

        TextView tvName = view.findViewById(R.id.name);
        TextView tvMonth = view.findViewById(R.id.month);
        TextView tvDay = view.findViewById(R.id.day);
        TextView tvCountdown = view.findViewById(R.id.countdown);
        TextView tvAge = view.findViewById(R.id.new_age);
        ImageView ivImage = view.findViewById(R.id.image);

        Calendar today = Calendar.getInstance();
        int newAge = today.get(Calendar.YEAR) - person.getBirthdayAsCalendar().get(Calendar.YEAR);

        tvName.setText(name);
        tvMonth.setText(DateFormat.format("MMM", formattedDate));
        tvDay.setText(DateFormat.format("dd", formattedDate));
        ivImage.setImageBitmap(imageBitmap);

        if(today.get(Calendar.MONTH) == person.getBirthdayAsCalendar().get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) == person.getBirthdayAsCalendar().get(Calendar.DAY_OF_MONTH)){
            tvCountdown.setText("Today");
            tvAge.setText("Turned " + newAge);
        }else if(today.get(Calendar.MONTH) == person.getBirthdayAsCalendar().get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < person.getBirthdayAsCalendar().get(Calendar.DAY_OF_MONTH)){
            tvCountdown.setText("Coming\nIn\n" + countdown.getDays() + "\nDays");
            tvAge.setText("Turning " + (newAge+1));
        }else if(today.get(Calendar.MONTH) < person.getBirthdayAsCalendar().get(Calendar.MONTH)){
            tvCountdown.setText("Coming\nIn\n" + countdown.getDays() + "\nDays");
            tvAge.setText("Turning " + (newAge+1));
        }else{
            tvCountdown.setText(countdown.getDays() + "\nDays\nAgo");
            tvAge.setText("Turned " + newAge);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.list_item, parent, false);
    }

    public boolean changeBoolean(int notify){
        return notify > 0;
    }
}
