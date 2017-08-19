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

        String selection = "strftime('%m-%d'," + BirthdayContract.BirthdayEntry.COLUMN_NAME_DATE + "/1000, 'unixepoch')" + " BETWEEN strftime('%m-%d',?/1000, 'unixepoch') AND strftime('%m-%d',?/1000, 'unixepoch')";
        String[] selectionArgs = {Long.toString(Calendar.getInstance().getTimeInMillis() - 86400000), Long.toString(Calendar.getInstance().getTimeInMillis() + 86400000)};

        Cursor cursor = dbq.read(DbColumns.columns, selection, selectionArgs, null, null, BirthdayContract.BirthdayEntry.COLUMN_NAME_DATE + " ASC");
        Log.v("upcoming cursor", "got cursor2");

        adapter = new BirthdayCursorAdapter(getContext(), cursor, 0);
        adapter.swapCursor(cursor);
        listView.setAdapter(adapter);

        listener.setCount(cursor.getCount());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_upcoming_birthday, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_delete_all) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(false)
                    .setMessage(getString(R.string.dialog_message))
                    .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            BirthdayDbQueries dbq = new BirthdayDbQueries(new BirthdayDbHelper(getContext()));
                            Cursor cursor = dbq.read(DbColumns.columns, null, null, null, null, BirthdayContract.BirthdayEntry.COLUMN_NAME_NAME + " ASC");
                            dbq.deleteAll();
                            adapter.swapCursor(cursor);
                            cursor.close();
                            Toast.makeText(getContext(), getString(R.string.task_deleted_all), Toast.LENGTH_SHORT).show();
                            onResume();
                        }
                    })
                    .setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getContext(), getString(R.string.task_cancelled), Toast.LENGTH_SHORT).show();
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getString(R.string.dialog_title));
            alert.show();
        }

        return super.onOptionsItemSelected(item);
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
