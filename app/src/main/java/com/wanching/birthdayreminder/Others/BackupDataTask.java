package com.wanching.birthdayreminder.Others;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

/**
 * Created by WanChing on 14/8/2017.
 */

public class BackupDataTask extends LoaderManager.LoaderCallbacks<Cursor> {

    private static final String JSON_URL = "http://labs.jamesooi.com/uecs3253-asg.php";
    private Activity activity;

    public BackupDataTask(Activity activity){
        this.activity = activity;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
