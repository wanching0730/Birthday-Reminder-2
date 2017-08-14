package com.wanching.birthdayreminder.SQLiteDatabase;

import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayContract;

/**
 * Created by WanChing on 13/8/2017.
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
