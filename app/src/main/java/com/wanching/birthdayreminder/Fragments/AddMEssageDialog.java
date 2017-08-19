package com.wanching.birthdayreminder.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

import com.wanching.birthdayreminder.Activities.MainActivity;
import com.wanching.birthdayreminder.R;

/**
 * Created by WanChing on 17/8/2017.
 */

/**
 * DialogFragment for handing AlertDialog to add new birthday wishes
 */

public class AddMEssageDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final EditText etNewMessage = new EditText(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(etNewMessage);
        builder.setTitle(getString(R.string.new_wishes_dialog_title));
        builder.setPositiveButton(getString(R.string.response_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newMeessage = etNewMessage.getText().toString();
                MainActivity.arrayList.add(newMeessage);
                Toast.makeText(getContext(), "New wish added successfully", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(getString(R.string.response_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        return builder.create();
    }
}
