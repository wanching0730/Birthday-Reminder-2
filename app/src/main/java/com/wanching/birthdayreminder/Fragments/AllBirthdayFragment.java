package com.wanching.birthdayreminder.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
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

import com.wanching.birthdayreminder.Activities.ViewBirthdayActivity;
import com.wanching.birthdayreminder.Adapters.BirthdayCursorAdapter;
import com.wanching.birthdayreminder.R;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayContract;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbHelper;
import com.wanching.birthdayreminder.SQLiteDatabase.BirthdayDbQueries;
import com.wanching.birthdayreminder.SQLiteDatabase.DbColumns;

/**
 * Created by WanChing on 6/8/2017.
 */

/**
 * Fragment for displaying all birthday records
 */
public class AllBirthdayFragment extends Fragment
        implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private final static int LOADER_ID = 0;

    private ListView listView;
    private SearchView searchView = null;
    private TextView tvEmpty;
    private String subString;
    private BirthdayCursorAdapter adapter;
    private ProgressBar progressBar;
    private OnRereshFragmentListener listener;

    public AllBirthdayFragment(){}

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_birthday, container, false);

        progressBar = rootView.findViewById(R.id.progress_bar);
        listView = rootView.findViewById(R.id.birthday_list_view);
        tvEmpty = rootView.findViewById(R.id.empty_view);

        //To indicate an empty list
        listView.setEmptyView(tvEmpty);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(getContext(), ViewBirthdayActivity.class);
                intent.putExtra("ID", cursor.getLong(cursor.getColumnIndex(BirthdayContract.BirthdayEntry._ID)));
                startActivity(intent);
            }
        });

        //To display a new menu in fragment
        setHasOptionsMenu(true);

        return rootView;
    }

    public void onResume() {
        super.onResume();

        adapter = new BirthdayCursorAdapter(getContext(), null, 0);
        listView.setAdapter(adapter);
        getLoaderManager().restartLoader(LOADER_ID, null, this).forceLoad();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_all_birthday, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchView == null)
            return;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
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

                            //To refresh cursor
                            adapter.swapCursor(cursor);
                            listener.refreshFragment();

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

    @Override
    public boolean onClose() {
        subString = null;
        getLoaderManager().restartLoader(LOADER_ID, null, this).forceLoad();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {
            subString = newText;
            getLoaderManager().restartLoader(LOADER_ID, null, this).forceLoad();
        } else {
            subString = null;
            getLoaderManager().restartLoader(LOADER_ID, null, this).forceLoad();
        }

        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new DbLoader(getActivity(), subString);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        progressBar.setVisibility(View.GONE);
        tvEmpty.setText(getString(R.string.no_record));
        adapter.swapCursor(data);
        tvEmpty.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);

    }

    public interface OnRereshFragmentListener{
        void refreshFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            listener = (OnRereshFragmentListener) context;
        }catch (ClassCastException ex){
            throw new ClassCastException(context.toString() + " Must implement interface");
        }
    }

    /**
     * Asynchronous task for searching birthday records
     */
    public static final class DbLoader extends AsyncTaskLoader<Cursor> {

        private String substring;
        private Context context;

        public DbLoader(Context context, String substring) {
            super(context);

            this.context = context;
            this.substring = substring;
        }

        @Override
        public Cursor loadInBackground() {

            BirthdayDbQueries dbq = new BirthdayDbQueries(new BirthdayDbHelper(context));
            Cursor cursor;

            if (substring != null) {
                String[] selectionArgs = {"%" + substring + "%"};
                cursor = dbq.read(DbColumns.columns, BirthdayContract.BirthdayEntry.COLUMN_NAME_NAME + " LIKE ?", selectionArgs, null, null, null);
            } else {

                //Display all records if searching is not implemented
                cursor = dbq.read(DbColumns.columns, null, null, null, null, BirthdayContract.BirthdayEntry.COLUMN_NAME_NAME + " ASC");
            }
            return cursor;
        }
    }
}

