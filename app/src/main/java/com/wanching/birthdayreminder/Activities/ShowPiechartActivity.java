package com.wanching.birthdayreminder.Activities;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.wanching.birthdayreminder.R;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayContract;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbHelper;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbQueries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by WanChing on 20/8/2017.
 */

/**
 * Activity for displaying Pie Chart of the upcoming birthday records
 */

public class ShowPiechartActivity extends AppCompatActivity {

    private BirthdayDbQueries dbq;
    private Cursor cursor;
    private SimpleDateFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_piechart);

        formatter = new SimpleDateFormat("EEEE, d MMM, yyyy", Locale.ENGLISH);
        dbq = new BirthdayDbQueries(new BirthdayDbHelper(this));
        cursor = dbq.retrieveUpcomingBirthday();

        setupChart();
    }

    /**
     * Return amount of today birthday records
     * @return float Amount today birthday records
     */
    private float getToday(){
        int count = 0;

        Calendar calendar = Calendar.getInstance();
        String formattedCalendar = formatter.format(calendar.getTime());

        if(cursor.moveToFirst()){
            do{
                Calendar birthday = Calendar.getInstance();
                birthday.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(BirthdayContract.BirthdayEntry.COLUMN_NAME_DATE)));
                birthday.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                String formattedBirthday = formatter.format(birthday.getTime());
                if(formattedBirthday.matches(formattedCalendar) ){
                    Log.v("dat", count + "");
                    count++;
                }
            }while (cursor.moveToNext());
        }
        return (float) count;
    }

    /**
     * Return amount of tomorrow birthday records
     * @return float Amount tomorrow birthday records
     */
    private float getTomorrow(){
        int count = 0;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        String formattedCalendar = formatter.format(calendar.getTime());

        if(cursor.moveToFirst()){
            do{
                Calendar birthday = Calendar.getInstance();
                birthday.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(BirthdayContract.BirthdayEntry.COLUMN_NAME_DATE)));
                birthday.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                String formattedBirthday = formatter.format(birthday.getTime());
                if(formattedBirthday.matches(formattedCalendar) ){
                    Log.v("dat", count + "");
                    count++;
                }
            }while (cursor.moveToNext());
        }
        return (float) count;
    }

    /**
     * Return amount of day after tomorrow birthday records
     * @return float Amount day after tomorrow birthday records
     */
    private float getDayAfterTomorrow(){
        int count = 0;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 2);
        String formattedCalendar = formatter.format(calendar.getTime());

        if(cursor.moveToFirst()){
            do{
                Calendar birthday = Calendar.getInstance();
                birthday.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(BirthdayContract.BirthdayEntry.COLUMN_NAME_DATE)));
                birthday.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                String formattedBirthday = formatter.format(birthday.getTime());
                if(formattedBirthday.matches(formattedCalendar) ){
                    Log.v("dat", count + "");
                    count++;
                }
            }while (cursor.moveToNext());
        }
        return (float)count;
    }

    /**
     * Display Pie Chart
     * which display the values in percentage format
     */
    private void setupChart(){

        float today = getToday();
        float tomorrow = getTomorrow();
        float dayAfterTomorrow = getDayAfterTomorrow();
        float total = (float) dbq.retrieveUpcomingBirthday().getCount();

        float upcomingBirthday[] = {today/total*100f, tomorrow/total*100f, dayAfterTomorrow/total*100f};
        String days[] = {"Today", "Tomorrow", "Day after tomorrow"};

        List<PieEntry> pieEntryList = new ArrayList<>();

        for(int i = 0; i < upcomingBirthday.length; i++)
            if(upcomingBirthday[i] != 0f)
                pieEntryList.add(new PieEntry(upcomingBirthday[i], days[i]));

        PieDataSet dataSet = new PieDataSet(pieEntryList, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(20f);
        data.setValueFormatter(new PercentFormatter());

        PieChart chart = findViewById(R.id.chart);
        chart.setData(data);
        chart.setEntryLabelTextSize(30f);
        chart.setDrawSliceText(false);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(58f);
        chart.setHoleColor(Color.parseColor("#c6e2ff"));
        chart.setTransparentCircleRadius(61f);
        chart.setRotationAngle(90);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
        chart.invalidate();
    }
}
