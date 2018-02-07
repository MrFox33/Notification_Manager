package com.example.wojci.notification_manager;


import android.graphics.drawable.Drawable;
import java.io.Serializable;
import java.util.Comparator;
/*
    Used for displaying phones
 */

public class Model2  {

    private Drawable drawable;
    private String name;
    private boolean selected;
    private Integer _int;

    public Model2(String name, Drawable drawable, int _int) {
        this.name = name;
        this.drawable = drawable;
        this._int = _int;
    }
    public Model2(String name, int integer, boolean b,int _int) {
        this.name = name;
        this.drawable = drawable;
        this.selected = b;
        this._int = _int;
    }
    public static class CustomComparator implements Comparator<Model2> {
        @Override
        public int compare(Model2 o1, Model2 o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    public String getName() {
        return name;
    }

    public Drawable getInteger(){
        return drawable;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
