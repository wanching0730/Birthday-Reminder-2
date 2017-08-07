package com.wanching.birthdayreminder;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class UpcomingBirthdayFragment extends Fragment {

    private ListView listView;
    public static final String EXTRA_ID_1 = "com.wanching.birthdayreminder.Birthdat.ID1";

    public UpcomingBirthdayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_upcoming_birthday, container, false);

        listView = rootView.findViewById(R.id.list_view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor)adapterView.getItemAtPosition(position);

                Intent intent = new Intent(getContext(), ViewBirthdayActivity.class);
                intent.putExtra(EXTRA_ID_1, cursor.getLong(cursor.getColumnIndex(BirthdayContract.BirthdayEntry._ID)));
                startActivity(intent);}
        });

        return  rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        BirthdayDbQueries dbq = new BirthdayDbQueries(new BirthdayDbHelper(getContext()));

        String [] columns = {
                BirthdayContract.BirthdayEntry._ID,
                BirthdayContract.BirthdayEntry.COLUMN_NAME_NAME,
                BirthdayContract.BirthdayEntry.COLUMN_NAME_EMAIL,
                BirthdayContract.BirthdayEntry.COLUMN_NAME_PHONE,
                BirthdayContract.BirthdayEntry.COLUMN_NAME_IMAGE,
                BirthdayContract.BirthdayEntry.COLUMN_NAME_DATE,
                BirthdayContract.BirthdayEntry.COLUMN_NAME_NOTIFY};

        Cursor cursor = dbq.read(columns, null, null, null, null, BirthdayContract.BirthdayEntry.COLUMN_NAME_DATE + " ASC");

        TextView tv = getActivity().findViewById(R.id.example);
        listView.setEmptyView(tv);

        BirthdayCursorAdapter adapter = new BirthdayCursorAdapter(getContext(), cursor, 0);
        listView.setAdapter(adapter);

    }
}
