package com.wanching.birthdayreminder.SQLiteDatabase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.wanching.birthdayreminder.Others.Person;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

public class BirthdayDbQueries {

    private BirthdayDbHelper helper;

    public BirthdayDbQueries(BirthdayDbHelper helper) {
        this.helper = helper;
    }

    public Cursor read(String[] columns, String selection, String[] selectionArgs, String groupby, String having, String orderBy) {
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(
                BirthdayContract.BirthdayEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                groupby,
                having,
                orderBy
        );
    }

    public long insert(Person person) {
        SQLiteDatabase db = helper.getWritableDatabase();

        long id = db.insert(BirthdayContract.BirthdayEntry.TABLE_NAME, null, insertValue(person));
        person.setId(id);

        return id;
    }

    public int update(Person person) {
        SQLiteDatabase db = helper.getWritableDatabase();


        String selection = BirthdayContract.BirthdayEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(person.getId())};

        return db.update(
                BirthdayContract.BirthdayEntry.TABLE_NAME,
                insertValue(person),
                selection,
                selectionArgs
        );
    }

    public void delete(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();

        String selection = BirthdayContract.BirthdayEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};

        db.delete(BirthdayContract.BirthdayEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void deleteAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(BirthdayContract.BirthdayEntry.TABLE_NAME, null, null);
    }

    public byte[] convertToByteArray(Person person) {
        Bitmap imageBitmap = person.getImage();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    private ContentValues insertValue(Person person){
        ContentValues values = new ContentValues();
        values.put(BirthdayContract.BirthdayEntry.COLUMN_NAME_NAME, person.getName());
        values.put(BirthdayContract.BirthdayEntry.COLUMN_NAME_EMAIL, person.getEmail());
        values.put(BirthdayContract.BirthdayEntry.COLUMN_NAME_PHONE, person.getPhone());
        values.put(BirthdayContract.BirthdayEntry.COLUMN_NAME_IMAGE, convertToByteArray(person));
        values.put(BirthdayContract.BirthdayEntry.COLUMN_NAME_DATE, person.getBirthdayAsCalendar().getTimeInMillis());
        values.put(BirthdayContract.BirthdayEntry.COLUMN_NAME_NOTIFY, changeInt(person.isNotify()));

        return values;
    }

    public static Person retrievePerson(Cursor cursor){
        byte[] imageByte = cursor.getBlob(cursor.getColumnIndex(BirthdayContract.BirthdayEntry.COLUMN_NAME_IMAGE));

        Person person = new Person(
                cursor.getLong(cursor.getColumnIndex(BirthdayContract.BirthdayEntry._ID)),
                cursor.getString(cursor.getColumnIndex(BirthdayContract.BirthdayEntry.COLUMN_NAME_NAME)),
                cursor.getString(cursor.getColumnIndex(BirthdayContract.BirthdayEntry.COLUMN_NAME_EMAIL)),
                cursor.getString(cursor.getColumnIndex(BirthdayContract.BirthdayEntry.COLUMN_NAME_PHONE)),
                BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length),
                new Date(cursor.getLong(cursor.getColumnIndex(BirthdayContract.BirthdayEntry.COLUMN_NAME_DATE))),
                changeBoolean(cursor.getInt(cursor.getColumnIndex(BirthdayContract.BirthdayEntry.COLUMN_NAME_NOTIFY)))
        );
        cursor.close();
        return person;
    }

    public Cursor retrieveTodayBirthday(){
        Calendar cal = Calendar.getInstance();
        String selection = "strftime('%m-%d'," + BirthdayContract.BirthdayEntry.COLUMN_NAME_DATE + "/1000, 'unixepoch')" + " BETWEEN strftime('%m-%d',?/1000, 'unixepoch') AND strftime('%m-%d',?/1000, 'unixepoch') AND " + BirthdayContract.BirthdayEntry.COLUMN_NAME_NOTIFY + " = ?";
        String[] selectionArgs = {Long.toString(cal.getTimeInMillis()-86400000), Long.toString(cal.getTimeInMillis()-86400000), "1"};
        Cursor cursor = read(DbColumns.columns, selection, selectionArgs, null, null, BirthdayContract.BirthdayEntry.COLUMN_NAME_NAME + " ASC");

        return cursor;
    }

//    public Cursor retrieveTodayBirthday1(){
//        Calendar cal = Calendar.getInstance();
//        String selection = BirthdayContract.BirthdayEntry.COLUMN_NAME_NOTIFY + " = '1'";
//        Cursor cursor = read(DbColumns.columns, selection, null, null, null, BirthdayContract.BirthdayEntry.COLUMN_NAME_NAME + " ASC");
//
//        return cursor;
//    }

    private static boolean changeBoolean(int notify) {
        return notify > 0;
    }

    private static int changeInt (boolean notify){
        if(notify)
            return 1;
        else
            return 0;
    }


}