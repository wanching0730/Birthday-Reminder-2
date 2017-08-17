package com.wanching.birthdayreminder.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.wanching.birthdayreminder.R;

import java.util.ArrayList;

/**
 * Created by WanChing on 17/8/2017.
 */

public class AddMEssageDialog extends DialogFragment {

    public static ArrayList<String> arrayList;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Context context = getActivity();
        arrayList = new ArrayList<String>();
        //LayoutInflater inflater = getActivity().getLayoutInflater();

        final EditText etNewMessage = new EditText(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(etNewMessage);
        builder.setTitle(getResources().getString(R.string.new_wishes_dialog_title));

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newMeessage = etNewMessage.getText().toString();
                arrayList.add(newMeessage);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        return builder.create();
    }
}
