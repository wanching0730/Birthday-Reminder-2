package com.wanching.birthdayreminder;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomDrawerAdapter extends ArrayAdapter<DrawerItem>{

    Context context;
    List<DrawerItem> drawerItems;
    int layoutId;

    public CustomDrawerAdapter(Context context, int layoutId, List<DrawerItem> drawerItems){
        super(context, layoutId, drawerItems);
        this.context = context;
        this.drawerItems = drawerItems;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DrawerItemHolder drawerItemHolder;
        View view = convertView;

        if(view == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            drawerItemHolder = new DrawerItemHolder();

            view = inflater.inflate(layoutId, parent, false);
            drawerItemHolder.itemName = view.findViewById(R.id.drawer_itemName);
            drawerItemHolder.icon = view.findViewById(R.id.drawer_icon);

            view.setTag(drawerItemHolder);
        }else{
            drawerItemHolder = (DrawerItemHolder) view.getTag();
        }

        DrawerItem item = this.drawerItems.get(position);

        drawerItemHolder.icon.setImageDrawable(view.getResources().getDrawable(item.getImgResID()));
        drawerItemHolder.itemName.setText(item.getItemName());

        return view;
    }

    private static class DrawerItemHolder {
        TextView itemName;
        ImageView icon;
    }
}
