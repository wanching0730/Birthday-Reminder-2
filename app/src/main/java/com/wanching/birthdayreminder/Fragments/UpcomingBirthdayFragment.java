package com.wanching.birthdayreminder.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayContract;
import com.wanching.birthdayreminder.Activities.ViewBirthdayActivity;
import com.wanching.birthdayreminder.Adapters.BirthdayCursorAdapter;
import com.wanching.birthdayreminder.R;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbHelper;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbQueries;
import com.wanching.birthdayreminder.SQLiteDatabase.DbColumns;

import java.util.Calendar;

/**
 * Created by WanChing on 6/8/2017.
 */

/**
 * Fragment for displaying upcoming birthday records
 */

public class UpcomingBirthdayFragment extends Fragment {

    private ListView listView;
    private BirthdayCursorAdapter adapter;
    private TextView tvEmpty;
    private ProgressBar progressBar;
    private OnSetCountListener listener;

    public UpcomingBirthdayFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_upcoming_birthday, container, false);

        progressBar = rootView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        listView = rootView.findViewById(R.id.birthday_list_view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(getContext(), ViewBirthdayActivity.class);
                intent.putExtra("ID", cursor.getLong(cursor.getColumnIndex(BirthdayContract.BirthdayEntry._ID)));
                startActivity(intent);
            }
        });

        tvEmpty = rootView.findViewById(R.id.empty_view);
        listView.setEmptyView(tvEmpty);
        tvEmpty.setText(getString(R.string.no_record));

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        BirthdayDbQueries dbq = new BirthdayDbQueries(new BirthdayDbHelper(getContext()));

        Cursor cursor = dbq.retrieveUpcomingBirthday();
        Log.v("upcoming cursor", "got cursor2");

        adapter = new BirthdayCursorAdapter(getContext(), cursor, 0);
        adapter.swapCursor(cursor);
        listView.setAdapter(adapter);

        listener.setCount(cursor.getCount());
    }

    public interface OnSetCountListener {
        void setCount(int count);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            listener = (OnSetCountListener) context;
        }catch (ClassCastException ex){
            throw new ClassCastException(context.toString() + " must implement OnSetCountListener");
        }
    }
}
