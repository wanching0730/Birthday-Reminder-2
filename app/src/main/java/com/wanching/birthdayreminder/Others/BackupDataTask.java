package com.wanching.birthdayreminder.Others;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;

import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayContract;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbHelper;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbQueries;
import com.wanching.birthdayreminder.SQLiteDatabase.DbColumns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by WanChing on 6/8/2017.
 */

public class BackupDataTask extends android.content.AsyncTaskLoader<JSONObject> {

    private static final String JSON_URL = "http://labs.jamesooi.com/uecs3253-asg.php";
    private Activity activity;
    private ArrayList<Person> persons;

    public BackupDataTask(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public JSONObject loadInBackground() {
        readDbData();

        JSONObject response = null;

        try {
            response = postJson();
        } catch (IOException ex) {
            Log.e("IO_EXCEPTION", ex.toString());
        }

        return response;
    }

    public void readDbData() {

        persons = new ArrayList<Person>();
        BirthdayDbQueries dbq = new BirthdayDbQueries(new BirthdayDbHelper(getContext()));
        Cursor cursor = dbq.read(DbColumns.columns, null, null, null, null, BirthdayContract.BirthdayEntry.COLUMN_NAME_NAME + " ASC");

        //TODO: open static method
        while (cursor.moveToNext()) {
            Person person = BirthdayDbQueries.retrievePerson(cursor);

            persons.add(person);
        }
    }

    public JSONArray convertToJsonArray() {

        JSONArray jsonArray = new JSONArray();


        try {

            for (int i = 0; i < persons.size(); i++) {
                JSONObject jsonObject = new JSONObject();

                jsonArray.put(new JSONObject().put("id", persons.get(i).getId())
                        .put("name", persons.get(i).getName())
                        .put("email", persons.get(i).getEmail())
                        .put("phone", persons.get(i).getPhone())
                        .put("birthday", persons.get(i).getBirthday().getTime()));
            }

        } catch (JSONException ex) {
            Log.e("JSONEXCEPTION", ex.toString());
            return null;


        }
        return jsonArray;
    }

    private JSONObject postJson() throws IOException {

        InputStream is = null;
        OutputStream os = null;

        try {
            URL url = new URL(JSON_URL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(convertToJsonArray()));
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                is = conn.getInputStream();

                return readInputStream(is);
            } else {
                Log.e("HTTP_ERROR", Integer.toString(responseCode));
                return null;
            }


        } catch (Exception ex) {
            Log.e("EXCEPTION", ex.toString());
            return null;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public JSONObject readInputStream(InputStream is) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder builder = new StringBuilder();

        String input;
        while ((input = reader.readLine()) != null)
            builder.append(input);

        return new JSONObject(builder.toString());
    }

    private String getPostDataString(JSONArray jsonArray) throws Exception {

        StringBuilder result = new StringBuilder();

        result.append(URLEncoder.encode("data", "UTF-8"));
        result.append("=");
        result.append(URLEncoder.encode(jsonArray.toString(), "UTF-8"));

        return result.toString();
    }


}
