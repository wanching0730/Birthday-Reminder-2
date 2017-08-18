package com.wanching.birthdayreminder.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.widget.Toast;

import com.wanching.birthdayreminder.R;

/**
 * Created by WanChing on 17/8/2017.
 */

public class MyPreferenceActivity extends PreferenceActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String username = sharedPreferences.getString("username", "yo");
//        Log.v("username11", username);
        //MainActivity.changeUsername();

    }
    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            getPreferenceScreen().removeAll();
            addPreferencesFromResource(R.xml.preference);
        }
    }
}
