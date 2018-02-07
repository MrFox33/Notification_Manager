package com.example.wojci.notification_manager;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/*
    Create new group
 */
public class Add_Groups extends AppCompatActivity {


    ArrayList<Model> model;
    ArrayList<Model> _phones;
    String message = "";

    //selected options
    boolean[] checkedColors = new boolean[]{
            false, //
            false, //
            false, //
            false, //
            false, //


    };
    int[] timeHM = new int[] {
            -1,
            -1,
            -1,
            -1,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__groups);

        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_UNINSTALLED_PACKAGES;


        final TextView tv = (TextView) findViewById(R.id.editText);
        final PackageManager pm = getPackageManager();
        final List<ApplicationInfo> packages = pm.getInstalledApplications(flags);

        //If user want to change existing group
        Intent myIntent = getIntent();
        if(myIntent.hasExtra("Name")){
            Bundle args = myIntent.getBundleExtra(("apps"));
            model = (ArrayList<Model>) args.getSerializable("ArrayList");
            tv.setText(myIntent.getStringExtra("Name"));
            checkedColors = myIntent.getBooleanArrayExtra("Options");
            timeHM = myIntent.getIntArrayExtra("time");
            Bundle args2 = myIntent.getBundleExtra(("phones"));
            _phones = (ArrayList<Model>) args2.getSerializable("ArrayList2");
            message = myIntent.getStringExtra("mess");
        }



        String s = "";

        //check if model is null
        boolean isIt = false;
        if(model == null) {
            model = new ArrayList<Model>();
            isIt = true;
        }
        //model - list of installed applications
        ArrayList<Model> temp = new ArrayList<>();
        //create models 
        for (ApplicationInfo a : packages) {
            if((a.flags & ApplicationInfo.FLAG_SYSTEM)!=1) {
                s = ((String) (a != null ? pm.getApplicationLabel(a) : "(unknown)"));
                temp.add(new Model(s, Integer.toString(a.icon)));
                for(int i = 0; i < model.size(); i++) {
                    if (!isIt && model.get(i).getName().equals(pm.getApplicationLabel(a))) {
                        temp.remove(temp.size() - 1);
                        temp.add(new Model(s, Integer.toString(a.icon), true));

                        break;
                    }
                }
            }
        }

        model = temp;

        //list applications
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(Add_Groups.this, List_apps.class);
                Bundle args = new Bundle();
                args.putSerializable("ArrayList",(Serializable)model);
                myIntent.putExtra("apps",  args);
                startActivityForResult(myIntent,1);
            }
        });
        Button add_button = (Button) findViewById(R.id.button);



        add_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Build an AlertDialog
                        final AlertDialog.Builder builder = new AlertDialog.Builder(Add_Groups.this);

                        // String array for alert dialog multi choice items
                        // colors == options
                        String[] colors = new String[]{
                                "Delete",
                                "Display only last notification",
                                "Delete all notifications",
                                "Send Message after recieving missed call",
                                "Apply options in given time interval",

                        };

                        // Boolean array for initial selected items


                        // Convert the color array to list
                        final List<String> colorsList = Arrays.asList(colors);

                        // Set multiple choice items for alert dialog
                /*
                    AlertDialog.Builder setMultiChoiceItems(CharSequence[] items, boolean[]
                    checkedItems, DialogInterface.OnMultiChoiceClickListener listener)
                        Set a list of items to be displayed in the dialog as the content,
                        you will be notified of the selected item via the supplied listener.
                 */
                /*
                    DialogInterface.OnMultiChoiceClickListener
                    public abstract void onClick (DialogInterface dialog, int which, boolean isChecked)

                        This method will be invoked when an item in the dialog is clicked.

                        Parameters
                        dialog The dialog where the selection was made.
                        which The position of the item in the list that was clicked.
                        isChecked True if the click checked the item, else false.
                 */


                        //Actions for groups
                        builder.setMultiChoiceItems(colors, checkedColors, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                                // Update the current focused item's checked status
                                checkedColors[which] = isChecked;
                                if(which == 4 && isChecked){
                                    timePicker("To");
                                    timePicker("From");
                            }

                            if(which == 3 && isChecked){
                                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
                                ArrayList<Model> contacten = new ArrayList<>();
                                int i = 0;

                                while (phones.moveToNext())
                                {
                                    String csds = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    int k = 0;
                                    if(_phones != null) {
                                        for (Model m : _phones) {
                                            if (phones != null && m.getName().equals(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)))) {
                                                contacten.add(m);
                                                k = 1;
                                                break;
                                            }
                                        }
                                    }
                                    if(k == 0)
                                        contacten.add(new Model(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)), csds));
                                }
                                phones.close();
                                Intent mydIntent = new Intent(Add_Groups.this, list_contacts.class);
                                Bundle args = new Bundle();
                                args.putSerializable("ArrayList",(Serializable)contacten);
                                mydIntent.putExtra("apps",  args);
                                startActivityForResult(mydIntent,2);
                            }

                                // Get the current focused item
                                String currentItem = colorsList.get(which);

                                // Notify the current action
                                Toast.makeText(getApplicationContext(),
                                        currentItem + " " + isChecked, Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Specify the dialog is not cancelable
                        builder.setCancelable(false);

                        // Set a title for alert dialog
                        builder.setTitle("Choose wisely");

                        // Set the positive/yes button click listener
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ArrayList<Model> temp = new ArrayList<>();
                                for (Model m :model) {
                                    if(m.isSelected())
                                        temp.add(m);
                                }
                                ArrayList<Model> temp2 = new ArrayList<>();
                                if(_phones != null) {
                                    for (Model m : _phones) {
                                        if (m.isSelected())
                                            temp2.add(m);
                                    }
                                }
                                Group group = new Group(temp,tv.getText().toString(),checkedColors);
                                group.setMessage(message);
                                group.setPhones(temp2);
                                if(checkedColors[4] == true){
                                    group.setTime(timeHM[0],timeHM[1],timeHM[2],timeHM[3]);
                                }
                                Toast.makeText(Add_Groups.this, "Name: "+group.getName() + " No. of apps: " + group.getNumber().toString() + group.getOpt() ,Toast.LENGTH_SHORT).show();
                                JSONObject jsonObject = new JSONObject();
                                try{
                                    JsonArray jsonArray,jsonArray2,jsonArray3;
                                    Gson gson = new GsonBuilder().create();
                                    jsonArray = gson.toJsonTree(group.getArr()).getAsJsonArray();
                                    jsonArray2 = gson.toJsonTree(group.getOpt()).getAsJsonArray();
                                    jsonArray3 = gson.toJsonTree(group.getContacts()).getAsJsonArray();
                                    jsonObject.put("Models",jsonArray);
                                    jsonObject.put("Name",group.getName());
                                    jsonObject.put("message",group.getMessage());
                                    jsonObject.put("Options",jsonArray2);
                                    jsonObject.put("Contacts",jsonArray3);
                                    jsonObject.put("HourFrom",timeHM[0]);
                                    jsonObject.put("MinuteFrom",timeHM[1]);
                                    jsonObject.put("HourTo",timeHM[2]);
                                    jsonObject.put("MinuteTo",timeHM[3]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    Writer output = null;

                                    File file = new File(getFilesDir() + "/myFolder/"+ group.getName() + ".json");
                                    output = new BufferedWriter(new FileWriter(file));
                                    output.write(jsonObject.toString());
                                    output.close();
                                    Toast.makeText(getApplicationContext(), "Composition saved", Toast.LENGTH_LONG).show();

                                } catch (Exception e) {
                                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do something when click the neutral button
                            }
                        });

                        AlertDialog dialog = builder.create();
                        // Display the alert dialog on interface
                        dialog.show();
                    }
                });
    }

    /*
        Set time bounds
     */
    private void timePicker(final String name){
        final int[] time = new int[2];
        final Calendar c = Calendar.getInstance();
        time[0] = c.get(Calendar.HOUR_OF_DAY);
        time[1] = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        final TimePickerDialog timePickerDialog = new TimePickerDialog(this,AlertDialog.THEME_HOLO_LIGHT,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,int minute) {

                            time[0] = hourOfDay;
                            time[1] = minute;
                            if(name.equals("From")){
                                timeHM[0] = hourOfDay;
                                timeHM[1] = minute;
                            }
                            else{
                                timeHM[2] = hourOfDay;
                                timeHM[3] = minute;
                            }

                    }
                }, time[0],time[1], true);

        timePickerDialog.setTitle(name);
        if(timeHM[0] != -1) {
            if(name.equals("From")){
                timePickerDialog.updateTime(timeHM[0], timeHM[1]);
            }
            else{
                timePickerDialog.updateTime(timeHM[2], timeHM[3]);
            }
        }
        timePickerDialog.show();

    }

//Get list of applications/ phones from activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        final String[] m_Text = {""};
        if (resultCode == RESULT_OK && requestCode == 1) {
            /*
                save list of selected applications
             */
            if (data.hasExtra("data1")) {
                model = (ArrayList<Model>) data.getSerializableExtra("data1");
                int n = 0;
                for (Model  l:model) {
                    if(l.isSelected())
                        n++;
                }
                Toast.makeText(this, "You've Chosen " + n + " Applications",Toast.LENGTH_SHORT).show();
            }

        }
        else if (resultCode == RESULT_OK && requestCode == 2){
            /*
                save list of selected phone numbers && set sms message
             */
            if (data.hasExtra("data1")) {
                _phones = (ArrayList<Model>) data.getSerializableExtra("data1");
                AlertDialog.Builder builder = new AlertDialog.Builder(Add_Groups.this);
                builder.setTitle("Message Text");

                final EditText input = new EditText(Add_Groups.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT );
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text[0] = input.getText().toString();
                        message = input.getText().toString();
                    }
                });

                builder.show();

                int n = 0;
                for (Model l : _phones) {
                    if (l.isSelected())
                        n++;
                }
                Toast.makeText(this, "You've Chosen " + n + " phone numbers", Toast.LENGTH_SHORT).show();

            }
        }
    }




}
