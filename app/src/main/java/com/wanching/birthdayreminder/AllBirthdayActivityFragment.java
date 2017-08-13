package com.wanching.birthdayreminder;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * A placeholder fragment containing a simple view.
 */
public class AllBirthdayActivityFragment extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, LoaderManager.LoaderCallbacks<Cursor> {

    private ListView listView;
    private SearchView searchView = null;
    private TextView tvEmpty;
    private String subString;
    private BirthdayCursorAdapter adapter;
    private final static int LOADER_ID = 0;
    public static final String EXTRA_ID_2 = "com.wanching.birthdayreminder.Birthdat.ID2";

    public AllBirthdayActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_birthday, container, false);

        listView = rootView.findViewById(R.id.birthday_list_view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor)adapterView.getItemAtPosition(position);

                Intent intent = new Intent(getContext(), ViewBirthdayActivity.class);
                intent.putExtra(EXTRA_ID_2, cursor.getLong(cursor.getColumnIndex(BirthdayContract.BirthdayEntry._ID)));
                startActivity(intent);}
        });

        tvEmpty = (TextView) getActivity().findViewById(R.id.empty_view);
        listView.setEmptyView(tvEmpty);
        tvEmpty.setText("No Birthday Record Found!");


        //to display the menu in fragment
        setHasOptionsMenu(true);

        return  rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
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
        menu.clear();
        inflater.inflate(R.menu.menu_all_birthday, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if(searchView == null)
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
            builder .setCancelable(false)
                    .setMessage("Are you sure you want to delete all records?")
                    .setPositiveButton("YES",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {
                            BirthdayDbQueries dbq = new BirthdayDbQueries(new BirthdayDbHelper(getContext()));
                            Cursor cursor = dbq.read(DbColumns.columns, null, null, null, null, BirthdayContract.BirthdayEntry.COLUMN_NAME_NAME + " ASC");
                            dbq.deleteAll();
                            adapter.swapCursor(cursor);
                            Toast.makeText(getContext(), "Deleted All ", Toast.LENGTH_SHORT).show();
                            onResume();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle("WARNING");
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
        if(!TextUtils.isEmpty(newText)){
            subString = newText;
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
        adapter.swapCursor(data);
        tvEmpty.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
        //onResume();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);

    }

    public static final class DbLoader extends AsyncTaskLoader<Cursor>{

        private String substring;
        private Context context;

        public DbLoader (Context context, String substring){
            super(context);

            this.context = context;
            this.substring = substring;
        }

        @Override
        public Cursor loadInBackground(){

            BirthdayDbQueries dbq = new BirthdayDbQueries(new BirthdayDbHelper(context));
            Cursor cursor;

            if(substring != null){
                String[] selectionArgs = {"%" + substring + "%"};
                cursor = dbq.read(DbColumns.columns, BirthdayContract.BirthdayEntry.COLUMN_NAME_NAME + " LIKE ?", selectionArgs, null, null, null);
            }else{
                cursor = dbq.read(DbColumns.columns, null, null, null, null, BirthdayContract.BirthdayEntry.COLUMN_NAME_NAME + " ASC");
            }
            return cursor;
        }
    }
}

