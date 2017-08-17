package com.wanching.birthdayreminder.Adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.wanching.birthdayreminder.R;

/**
 * Created by WanChing on 17/8/2017.
 */

public class SpinnerAdapter extends BaseAdapter {

    Context context;
    private LayoutInflater inflater;
    String [] messages;

    public SpinnerAdapter (Context context, String[] messages){
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View spinnerItem = view;

        if(spinnerItem == null){
            inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            spinnerItem = inflater.inflate(R.layout.spinner_row, viewGroup, false);
        }

        TextView tvMessage = (TextView) spinnerItem.findViewById(R.id.message_choices);
        tvMessage.setText(messages[position]);

        return spinnerItem;
    }
}
