package com.example.wojci.notification_manager;
import android.content.pm.ApplicationInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wojciech on 22.12.2017.
 *
 */

public class Group  implements Serializable{
    private ArrayList<Model> model;
    private ArrayList<Model> phones;
    private String name;
    private String message = "";
    private int FromHour;
    private int ToHour;
    private int FromMinute;
    private int ToMinute;
    private boolean[] options;

    public Group(ArrayList<Model> model, String name, boolean[] options){
        this.model = model;
        this.name = name;
        this.options = options;
    }
    public void setPhones(ArrayList<Model> _phones){
        phones = _phones;
    }

    public String getName(){
        return this.name;
    }
    public void setMessage(String _message){
        message = _message;
    }
    public String getMessage(){
        return  message;
    }

    public Integer getNumber(){
        return model.size();
    }

    public boolean[] getOpt(){
        return options;


    }
    public void setTime(int h1, int h2,int m1, int m2){
        FromHour = h1;
        FromMinute= h2;
        ToHour  = m1;
        ToMinute = m2;
    }

    public int[] getTime(){
        int[] temp = new int[]{
            FromHour,
            FromMinute,
            ToHour,
            ToMinute
        };
        return temp;
    }

    public int getTimeId(int i){
        int[] temp = new int[]{
                FromHour,
                FromMinute,
                ToHour,
                ToMinute
        };
        return temp[i];

    }
    public boolean getOption(int i){
        return options[i];
    }
    public Model[] getArr(){
        Model[] m = new Model[this.model.size()];
        for(int i = 0; i < m.length ; i++){
            m[i] = new Model(model.get(i).getName(),model.get(i).getInteger(),model.get(i).isSelected());

        }
        return m;
    }
    public Model[] getContacts(){
        Model[] m = new Model[this.phones.size()];
        for(int i = 0; i < m.length ; i++){
            m[i] = new Model(phones.get(i).getName(),phones.get(i).getInteger(),phones.get(i).isSelected());

        }
        return m;

    }
    public List<Model> getList(){
        return this.model;
    }
    public List<Model> getList2(){
        return this.phones;
    }
}
