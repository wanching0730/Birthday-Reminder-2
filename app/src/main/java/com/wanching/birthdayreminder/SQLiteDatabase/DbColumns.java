package com.wanching.birthdayreminder.SQLiteDatabase;

/**
 * Created by WanChing on 4/8/2017.
 */

/**
 * Class for combining all database columns into an array of String
 */

public class DbColumns {
    public static String[] columns = {
            BirthdayContract.BirthdayEntry._ID,
            BirthdayContract.BirthdayEntry.COLUMN_NAME_NAME,
            BirthdayContract.BirthdayEntry.COLUMN_NAME_EMAIL,
            BirthdayContract.BirthdayEntry.COLUMN_NAME_PHONE,
            BirthdayContract.BirthdayEntry.COLUMN_NAME_IMAGE,
            BirthdayContract.BirthdayEntry.COLUMN_NAME_DATE,
            BirthdayContract.BirthdayEntry.COLUMN_NAME_NOTIFY};
}
