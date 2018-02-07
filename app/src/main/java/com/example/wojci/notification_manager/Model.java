package com.example.wojci.notification_manager;


import java.io.Serializable;
import java.util.Comparator;

/*
*   Represents application 
*/

public class Model implements Serializable {

    private String integer;
    private String name;
    private boolean selected;

    public Model(String name, String integer) {
        this.name = name;
        this.integer = integer;
    }
    public Model(String name, String integer, boolean b) {
        this.name = name;
        this.integer = integer;
        this.selected = b;
    }
    public static class CustomComparator implements Comparator<Model> {
        @Override
        public int compare(Model o1, Model o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    public String getName() {
        return name;
    }

    public String getInteger(){
        return integer;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
