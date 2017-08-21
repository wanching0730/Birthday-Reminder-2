package com.wanching.birthdayreminder.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.wanching.birthdayreminder.Fragments.DatePickerFragment;
import com.wanching.birthdayreminder.Others.Conversion;
import com.wanching.birthdayreminder.Others.Person;
import com.wanching.birthdayreminder.R;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbHelper;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbQueries;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by WanChing on 6/8/2017.
 */

/**
 * Activity for adding new birthday record
 */

public class AddBirthdayActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE = 1;

    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etDate;
    private Date newDate;
    private ImageView ivImage;
    private Switch swNotification;
    private Bitmap bitmap = null;
    private Conversion conversion;
    private boolean saved = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_birthday);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences("spSaveState", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        etName = (EditText) findViewById(R.id.add_name);
        etEmail = (EditText) findViewById(R.id.add_email);
        etPhone = (EditText) findViewById(R.id.add_phone);
        ivImage = (ImageView) findViewById(R.id.person_image);
        etDate = (EditText) findViewById(R.id.date_selection);
        swNotification = (Switch) findViewById(R.id.notification);

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select an Image"), SELECT_IMAGE);
            }
        });

        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bitmap == null) {
                    Toast.makeText(AddBirthdayActivity.this, "Please select an image to proceed", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select an option to insert image"), SELECT_IMAGE);
                } else {
                    try {
                        String name = etName.getText().toString();
                        String email = etEmail.getText().toString();
                        String phone = etPhone.getText().toString();
                        String date = etDate.getText().toString();
                        Boolean notification = swNotification.isChecked();

                        // Check whether all information are filled up completely
                        if (name.matches("") || email.matches("") || phone.matches("")) {
                            Toast.makeText(AddBirthdayActivity.this, "Please enter all details to proceed", Toast.LENGTH_SHORT).show();
                        } else {
                            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH);
                            newDate = formatter.parse(date);

                            BirthdayDbQueries dbq = new BirthdayDbQueries(new BirthdayDbHelper(getApplicationContext()));
                            Person person = new Person(0, name, email, phone, bitmap, newDate, notification);

                            if (dbq.insert(person) != 0) {
                                saved = true;
                                Toast.makeText(AddBirthdayActivity.this, "New record added successfully!", Toast.LENGTH_SHORT).show();

                                finish();
                            }
                        }
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                        Toast.makeText(AddBirthdayActivity.this, "Please select a date to proceed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                bitmap = null;
                if (intent != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), intent.getData());
                    } catch (IOException ex) {
                        Log.wtf("IOEXCEPTION", ex);
                    }
                }

                ivImage.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * To add a new birthday date
     * @param view The selected EditText view
     */
    public void SetDate(View view) {
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Save all the incomplete data to shared preference
     */
    protected void onPause() {
        super.onPause();

        if (saved) {
            editor.clear();
        } else {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            String date = etDate.getText().toString();
            Boolean notification = swNotification.isChecked();

            editor.putString("SAVE_STATE_NAME", name);
            editor.putString("SAVE_STATE_EMAIL", email);
            editor.putString("SAVE_STATE_PHONE", phone);
            editor.putString("SAVE_STATE_DATE", date);
            editor.putBoolean("SAVE_STATE_NOTIFICATION", notification);

            if (bitmap != null)
                editor.putString("SAVE_STATE_IMAGE", conversion.encodeToBase64(bitmap));
        }

        editor.commit();
    }

    protected void onResume() {
        super.onResume();

        String name = sharedPreferences.getString("SAVE_STATE_NAME", "");
        String email = sharedPreferences.getString("SAVE_STATE_EMAIL", "");
        String phone = sharedPreferences.getString("SAVE_STATE_PHONE", "");
        String date = sharedPreferences.getString("SAVE_STATE_DATE", "");
        Boolean notification = sharedPreferences.getBoolean("SAVE_STATE_NOTIFICATION", false);

        etName.setText(name);
        etEmail.setText(email);
        etPhone.setText(phone);
        etDate.setText(date);
        swNotification.setChecked(notification);

        if (bitmap == null)
            ivImage.setImageResource(R.drawable.login);
        else {
            String image = sharedPreferences.getString("SAVE_STATE_IMAGE", "");
            ivImage.setImageBitmap(conversion.decodeToBase64(image));
        }
    }
}
