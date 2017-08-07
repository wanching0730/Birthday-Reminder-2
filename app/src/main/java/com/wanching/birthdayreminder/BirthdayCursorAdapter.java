package com.wanching.birthdayreminder;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
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

    private List<String> items;

    private LayoutInflater inflater;
    private Person person;


    public BirthdayCursorAdapter(Context context, Cursor cursor, int flags, List<String>items){
        super(context, cursor, flags);

        this.items = items;

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

        int newAge = Integer.parseInt(Long.toString(countdown.getDays()))/365;

        tvName.setText(name);
        tvMonth.setText(DateFormat.format("MMM", formattedDate));
        tvDay.setText(DateFormat.format("dd", formattedDate));
        ivImage.setImageBitmap(imageBitmap);

        if(Calendar.getInstance().get(Calendar.MONTH) < person.getDateAsCalendar().get(Calendar.MONTH)){
            tvCountdown.setText("Coming\nIn\n" +(Integer.parseInt(Long.toString(countdown.getDays()))-(newAge * 365)) + "\nDays");
            tvAge.setText("Turning " + (newAge+1));
        }
        else{
            tvCountdown.setText((Integer.parseInt(Long.toString(countdown.getDays()))-(newAge * 365)) + "\nDays\nAgo");
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
