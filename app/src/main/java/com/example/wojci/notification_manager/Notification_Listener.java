package com.example.wojci.notification_manager;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.telecom.Call;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class Notification_Listener extends NotificationListenerService {
    private String TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate() {

        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*
        Read groups from files
     */
    public static ArrayList<Group> getGroups(File path) {

        path.mkdirs();
        File[] files = path.listFiles();
        ArrayList<Group> groups = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            try {
                ArrayList<Model> model = new ArrayList<>();
                JSONObject obj = new JSONObject(loadJSONFromAsset(files[i]));
                ArrayList<Model> phones = new ArrayList<>();
                String opts = obj.getString("Options");
                String name = obj.getString("Name");
                String mdls = obj.getString("Models");
                String numeros = obj.getString("Contacts");
                String message = obj.getString("message");
                JSONArray ar3 = new JSONArray(numeros);
                for (int j = 0; j < ar3.length(); j++) {
                    JSONObject row = ar3.getJSONObject(j);
                    String s = row.getString("name");
                    phones.add(new Model(s, row.getString("integer"), row.getBoolean("selected")));
                }
                JSONArray ar = new JSONArray(mdls);
                for (int j = 0; j < ar.length(); j++) {
                    JSONObject row = ar.getJSONObject(j);
                    model.add(new Model(row.getString("name"), Integer.toString(row.getInt("integer")), row.getBoolean("selected")));
                }
                JSONArray ar2 = new JSONArray(opts);
                boolean[] b = new boolean[ar2.length()];
                for(int j = 0; j < ar2.length(); j++)
                    b[j] = ar2.getBoolean(j);

                groups.add(new Group(model, name, b));
                groups.get(i).setPhones(phones);
                groups.get(i).setMessage(message);

                groups.get(i).setTime(obj.getInt("HourFrom"),obj.getInt("MinuteFrom"),obj.getInt("HourTo"),obj.getInt("MinuteTo"));


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return groups;
    }

    /*
        Read files as json strings
     */
    public static String loadJSONFromAsset(File filename) {
        String json = null;
        try {
            FileInputStream is = new FileInputStream(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    /*
        Perform actions based on selected options
     */
    public void Notification_handler(StatusBarNotification sbn, Group grp) throws ParseException {

        //if no time bounds are specified or check if current time is in bounds
        if(!grp.getOption(4) || (grp.getOption(4)&& CheckTime(grp))) {
            
            //delete
            if (grp.getOption(0))
                Notification_Listener.this.cancelNotification(sbn.getKey());
            //missed call
            if(grp.getOption(3) && sbn.getPackageName().equals("com.android.server.telecom"))
                sendSMS(grp,sbn);
            //cancel all notifications except last one
            if(grp.getOption(1)){
                Log.i(TAG,"********************************************************************");
                ArrayList<StatusBarNotification> ids = new ArrayList<>();
                StatusBarNotification[] sbns = getActiveNotifications();
                for(StatusBarNotification s : sbns){
                    ids.add(s);
                }
                Log.i(TAG,"Active Notifications: "+ sbns.length);
                //loop through all notifications in Notification Bar
                for(int i = 0; i < ids.size()- 1; i++){
                    StatusBarNotification temp0 = ids.get(i);
                    for(int j = 1; j < ids.size(); j++) {
                        StatusBarNotification temp1 = ids.get(j);
                        if (temp0.getPackageName().equals(temp1.getPackageName()) && temp0.getId() != temp1.getId()) {
                            Log.i(TAG,"EQUAL: " + temp0.getPackageName() +", "+ temp0.getPostTime() +  " AND " + temp1.getPackageName() +", "+ temp1.getPostTime());
                            if (temp0.getPostTime() > temp1.getPostTime()) {
                                Notification_Listener.this.cancelNotification(temp0.getKey());
                                temp0 = temp1;
                                ids.remove(i);
                                i--;
                                j--;
                            } else {
                                Notification_Listener.this.cancelNotification(temp1.getKey());
                                ids.remove(j);
                                j--;
                            }
                        }
                    }
                }

            }
            //delete all notifications in Notification Bar
            if(grp.getOption(2)){
                Notification_Listener.this.cancelAllNotifications();
            }

        }
    }
    /*
        Send sms if calling number was included in group
     */
    public void sendSMS(Group grp, StatusBarNotification sbn) {
        for(Model m : grp.getList2()) {
            //check if missed call was from number specified in group
            if (sbn.getNotification().tickerText.toString().toLowerCase().contains(m.getName().toLowerCase())) {

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    Log.i(TAG, "Group Name: " + grp.getName() + "Number of nums: " + grp.getList2().size() + "Message: " + grp.getMessage());
                    smsManager.sendTextMessage(m.getInteger(), null, grp.getMessage(), null, null);
                    Toast.makeText(getApplicationContext(), "Message Sent",
                            Toast.LENGTH_LONG).show();
                    Log.i(TAG, grp.getList2().get(0).getName());
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                            Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
                }
            }
        }
    }


    /*
        Check if current time is in time bounds specified in group
     */
    public boolean CheckTime(Group grp) throws ParseException {
        final Calendar c = Calendar.getInstance();
        //From
        Calendar c1 = Calendar.getInstance();
        String temp1 = Integer.toString(grp.getTimeId(0)) + ":"+Integer.toString(grp.getTimeId(1))+ ":00" ;
        Date time1 = new SimpleDateFormat("HH:mm:ss").parse(temp1);
        c1.setTime(time1);
        c1.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));

        //To
        Calendar c2 = Calendar.getInstance();
        String temp2 = Integer.toString(grp.getTimeId(2)) + ":"+Integer.toString(grp.getTimeId(3))+ ":00";
        Date time2 = new SimpleDateFormat("HH:mm:ss").parse(temp2);
        c2.setTime(time2);
        c2.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));

        Calendar tmp0 = Calendar.getInstance();
        tmp0.setTime(time1);
        tmp0.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));
        Calendar tmp = Calendar.getInstance();
        tmp.setTime(time2);
        tmp.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));

        //Current time
        Date x = c.getTime();

        //case if start time is after end time (e.g. from 22.00 to 5.00)
        if(c1.after(c2)) {
            tmp0.add(Calendar.DATE,-1);
            tmp.add(Calendar.DATE,1);
            if((x.after(c1.getTime()) && x.before(tmp.getTime()))||(x.after(tmp0.getTime()) && x.before(c2.getTime()))){

                return true;

            }
            Log.i(TAG,"FALSE:" + c1.getTime().toString() );
            Log.i(TAG,"FALSE:" + c2.getTime().toString() );
            Log.i(TAG,"FALSE:" + tmp0.getTime().toString() );
            Log.i(TAG,"FALSE:" + tmp.getTime().toString() );
            return false;
        }
        //Date x = c.getTime();
        if (x.after(c1.getTime()) && x.before(c2.getTime())) {
            Log.i(TAG,x.toString());
            return true;
        }
        Log.i(TAG,"FALSE:" + x.toString());
        if(x.after(c1.getTime()))
            Log.i(TAG,"FALSE:" + c1.getTime().toString() );
        if(x.before(c2.getTime()))
            Log.i(TAG,"FALSE:" + c2.getTime().toString() );
        return false;
    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
          Log.i(TAG,"**********  onNotificationPosted");
          Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());

        File path = new File(this.getFilesDir(), "myFolder");
        ArrayList<Group> groups = getGroups(path);
        final PackageManager pm = getApplicationContext().getPackageManager();
        Log.i(TAG,path.toString());

        //check if posted notification was from application included in one of groups
        for (Group g: groups) {
            for (Model m:g.getList()) {

                try {
                    if(sbn.getPackageName().equals("com.android.server.telecom") || (pm.getApplicationInfo(sbn.getPackageName(),0) != null ? pm.getApplicationLabel(pm.getApplicationInfo(sbn.getPackageName(),0)) : "(unknown)").equals( m.getName())) {
                        Notification_handler(sbn,g);
                        break;
                    }
                    else{
                        Log.i(TAG,(String) (pm.getApplicationInfo(sbn.getPackageName(),0) != null ? pm.getApplicationLabel(pm.getApplicationInfo(sbn.getPackageName(),0)) : "(unknown)"));
                        Log.i(TAG,m.getName());
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"********** onNOtificationRemoved");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName());

    }
}


