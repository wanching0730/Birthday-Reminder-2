package com.wanching.birthdayreminder.Others;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by WanChing on 6/8/2017.
 */

public class Person implements Serializable {
    private static transient Bitmap image;
    private long id;
    private String name;
    private String email;
    private String phone;
    private Date birthday;
    private boolean notify;

    public Person(){
    }

    public Person(long id, String name, String email, String phone, Bitmap image, Date birthday, boolean notify) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.birthday = birthday;
        this.notify = notify;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        this.birthday = new Date(calendar.getTimeInMillis());
    }

    public Calendar getThisYearBirthday() {
        Calendar thisBirthday = getBirthdayAsCalendar();
        thisBirthday.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));

        return thisBirthday;
    }

    public Calendar getNextYearBirthday() {
        Calendar nextBirthday = getBirthdayAsCalendar();
        nextBirthday.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1);

        return nextBirthday;
    }

    public Calendar getBirthdayAsCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(birthday.getTime());
        return calendar;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public void setBirtday(Date birthday) {
        this.birthday = birthday;
    }

    public long getCountdown(){
        Calendar today = Calendar.getInstance();
        Calendar birthday = getThisYearBirthday();

        long duration = birthday.getTimeInMillis() - today.getTimeInMillis();
        long days = TimeUnit.MILLISECONDS.toDays(duration);

        return Math.abs(days);
    }

//    public Countdown getCountdown() {
//        Calendar today = Calendar.getInstance();
//        Calendar birthday = getThisYearBirthday();
//
//        long duration = birthday.getTimeInMillis() - today.getTimeInMillis();
//
//        Countdown countdown = new Countdown();
//
//        countdown.days = TimeUnit.MILLISECONDS.toDays(duration);
//
//        return countdown;
//    }
//
//    public class Countdown {
//
//        private long days;
//
//        public long getDays() {
//            return Math.abs(days);
//        }
//    }
}
