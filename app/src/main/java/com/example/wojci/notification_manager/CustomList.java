package com.example.wojci.notification_manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
/*
    Custom list to show phone names or installed applications
 */

public class CustomList extends BaseAdapter{

    private final Activity context;
    private final ArrayList<Model> list;
    boolean checkAll_flag = false;
    boolean checkItem_flag = false;
    private final Drawable[] imageId;


    public CustomList(Activity context ,ArrayList<Model> model, Drawable[] imageId)
    {
        this.context = context;
        this.list = model;
        this.imageId = imageId;
    }

    static class ViewHolder {
        protected TextView text;
        protected CheckBox checkbox;
        protected  ImageView imageview;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {
        ViewHolder viewHolder = null;


        if(rowView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = context.getLayoutInflater();
            rowView= inflater.inflate(R.layout.list_single, null, true);
            viewHolder.text =  (TextView) rowView.findViewById(R.id.txt);
            viewHolder.checkbox = (CheckBox) rowView.findViewById(R.id.checkbox);
            viewHolder.imageview = (ImageView) rowView.findViewById(R.id.img);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    list.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            rowView.setTag(viewHolder);
            rowView.setTag(R.id.txt, viewHolder.text);
            rowView.setTag(R.id.checkbox, viewHolder.checkbox);
            rowView.setTag(R.id.img,viewHolder.imageview);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        viewHolder.checkbox.setTag(position); // This line is important.

        if(list.get(position).getName().length() > 35)
            viewHolder.text.setText(list.get(position).getName().substring(0,35) + "...");
        else
            viewHolder.text.setText(list.get(position).getName());
        viewHolder.checkbox.setChecked(list.get(position).isSelected());
        viewHolder.imageview.setImageDrawable(imageId[position]);
        return rowView;
    }


}

