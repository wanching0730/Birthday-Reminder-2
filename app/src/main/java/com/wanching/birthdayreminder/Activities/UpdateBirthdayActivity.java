package com.wanching.birthdayreminder.Activities;

import android.content.Intent;
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
 * Activity for updating the personal details of a selected person
 */


public class UpdateBirthdayActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE = 1;
    private static Bitmap bitmap;

    private Person person;
    private Date formattedDate;
    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etDate;
    private ImageView ivImage;
    private Switch swNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_birthday);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        person = (Person) intent.getSerializableExtra(ViewBirthdayActivity.EXTRA_BIRTHDAY);

        bitmap = person.getImage();

        etName = findViewById(R.id.add_name);
        etEmail = findViewById(R.id.add_email);
        etPhone = findViewById(R.id.add_phone);
        etDate = findViewById(R.id.date_selection);
        ivImage = findViewById(R.id.person_image);
        swNotification = findViewById(R.id.notification);

        etName.setText(person.getName());
        etEmail.setText(person.getEmail());
        etPhone.setText(person.getPhone());
        etDate.setText(new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).format(person.getBirthday()));
        ivImage.setImageBitmap(bitmap);
        swNotification.setChecked(person.isNotify());

        Log.v("birthdy", person.getBirthday().getTime() + "");

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select an Image"), SELECT_IMAGE);
            }
        });

        FloatingActionButton fabSave = findViewById(R.id.fab_save);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = etDate.getText().toString();

                try {
                    formattedDate = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).parse(date);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                person.setName(etName.getText().toString());
                person.setEmail(etEmail.getText().toString());
                person.setPhone(etPhone.getText().toString());
                person.setBirtday(formattedDate);
                person.setImage(bitmap);
                person.setNotify(swNotification.isChecked());

                BirthdayDbQueries dbq = new BirthdayDbQueries(new BirthdayDbHelper(getApplicationContext()));
                dbq.update(person);
                Toast.makeText(UpdateBirthdayActivity.this, "Record updated successfully", Toast.LENGTH_SHORT).show();

                finish();
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
     * To update a new birthday date
     * @param view The selected EditText view
     */
    public void SetDate(View view) {
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "datePicker");
    }
}
