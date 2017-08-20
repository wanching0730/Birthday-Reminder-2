package com.wanching.birthdayreminder.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.wanching.birthdayreminder.Activities.MainActivity;
import com.wanching.birthdayreminder.R;

import org.w3c.dom.Text;

/**
 * Created by WanChing on 17/8/2017.
 */

/**
 * DialogFragment for handing AlertDialog to add new birthday wishes
 */

public class MultiTaskDialogFragment extends DialogFragment {

    private int resourceId;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final EditText etNewMessage = new EditText(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        resourceId = getArguments().getInt("id", 0);

        if(resourceId == R.id.change_username){
            builder.setTitle(getString(R.string.change_username_dialog_title));
            etNewMessage.setText(MainActivity.etUsername.getText());
        }else if(resourceId == R.id.change_email){
            builder.setTitle(getString(R.string.change_email_dialog_title));
            etNewMessage.setText(MainActivity.etEmail.getText());
        }else
            builder.setTitle(getString(R.string.new_wishes_dialog_title));

        builder.setView(etNewMessage);
        builder.setPositiveButton(getString(R.string.response_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String newMessage = etNewMessage.getText().toString();
                Log.v("newMessage", newMessage);

                //Ensure the EditText is not empty before proceed
                if(!newMessage.matches("")){
                    if(resourceId == R.id.change_username){
                        MainActivity.etUsername.setText(newMessage);
                        Toast.makeText(getContext(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                    }else if(resourceId == R.id.change_email){
                        MainActivity.etEmail.setText(newMessage);
                        Toast.makeText(getContext(), "Email updated successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        MainActivity.arrayList.add(newMessage);
                        Toast.makeText(getContext(), "New wish added successfully", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    View parentLayout = getActivity().findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Please enter detail to proceed", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton(getString(R.string.response_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(), getString(R.string.task_cancelled), Toast.LENGTH_SHORT).show();
                dialogInterface.cancel();
            }
        });

        return builder.create();
    }
}
