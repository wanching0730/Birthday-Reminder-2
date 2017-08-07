package com.wanching.birthdayreminder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddBirthdayActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE = 1;
    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etDate;
    private Date newDate;
    private ImageView ivImage;
    private Bitmap bitmap = null;
    private boolean saved = false;
    private Conversion conversion;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_birthday);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("spSaveState", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        etName = (EditText) findViewById(R.id.add_name);
        etEmail = (EditText) findViewById(R.id.add_email);
        etPhone = (EditText) findViewById(R.id.add_phone);
        ivImage = (ImageView) findViewById(R.id.person_image);
        etDate = (EditText) findViewById(R.id.date_selection);

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

                if(bitmap == null) {
                    Toast.makeText(AddBirthdayActivity.this, "Please select an image to proceed", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select an option to insert image"), SELECT_IMAGE);
                }else {
                    try{
                        String name = etName.getText().toString();
                        String email = etEmail.getText().toString();
                        String phone = etPhone.getText().toString();
                        String date = etDate.getText().toString();

                        if(name.matches("") && email.matches("") && phone.matches("")){
                            Toast.makeText(AddBirthdayActivity.this, "Please enter all details to proceed", Toast.LENGTH_SHORT).show();
                        }else{
                            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH);
                            newDate = formatter.parse(date);

                            BirthdayDbQueries dbq = new BirthdayDbQueries(new BirthdayDbHelper(getApplicationContext()));
                            Person person = new Person(0, name, email, phone, bitmap, newDate, false);

                            if (dbq.insert(person) != 0) {
                                saved = true;
                                Toast.makeText(AddBirthdayActivity.this, "New record created successfully!", Toast.LENGTH_SHORT).show();

                                finish();
                            }
                        }
                    }catch (ParseException ex){
                        ex.printStackTrace();
                        Toast.makeText(AddBirthdayActivity.this, "Please select a date", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_IMAGE){
                bitmap = null;
                if(intent != null){
                    try{
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), intent.getData());
                    }catch (IOException ex){
                        Log.wtf("Ioexception", ex);
                    }
                }

                ivImage.setImageBitmap(bitmap);
            }
        }
    }

    public void SetDate(View view){
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "datePicker");
    }

    protected void onPause() {
        super.onPause();

        if(saved){
            editor.clear();
        }
        else{
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            String date = etDate.getText().toString();

            editor.putString("SAVE_STATE_NAME", name);
            editor.putString("SAVE_STATE_EMAIL", email);
            editor.putString("SAVE_STATE_PHONE", phone);
            editor.putString("SAVE_STATE_DATE", date);

            if(bitmap != null)
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

        etName.setText(name);
        etEmail.setText(email);
        etPhone.setText(phone);
        etDate.setText(date);

        if(bitmap == null)
            ivImage.setImageResource(R.drawable.login);
        else{
            String image = sharedPreferences.getString("SAVE_STATE_IMAGE", "");
            ivImage.setImageBitmap(conversion.decodeToBase64(image));
        }
    }

}
