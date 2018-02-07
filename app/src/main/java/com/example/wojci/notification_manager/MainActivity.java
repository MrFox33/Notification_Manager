package com.example.wojci.notification_manager;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private AlertDialog enableNotificationListenerAlertDialog;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 99;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 98;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 97;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 96;


    static Context obj;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        obj = this;

        //Check for permissions
        if(!isNotificationServiceEnabled()){
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }
        CheckPermissions();


        ListView mListView;
        //groups to display in main activity
        final ArrayList<Group> groups = getGroups();

        ArrayList<String> Groups = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            Groups.add(groups.get(i).getName());

        }

        //if no created groups yet
        if(groups.size() == 0){
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.layout);
            TextView valueTV = new TextView(this);
            valueTV.setText("Add New Groups :)");
            valueTV.setId(5);
            valueTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
            linearLayout.addView(valueTV);
        }
        else{
            mListView = (ListView) findViewById(R.id.list_view);

        Custom_list_main adapter = new Custom_list_main(Groups, this);
        mListView.setAdapter(adapter);
        mListView.setClickable(true);
        //On list item click 
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Intent myIntent = new Intent(MainActivity.this, Add_Groups.class);
                Bundle args = new Bundle();
                args.putSerializable("ArrayList", (Serializable) groups.get(position).getList());
                myIntent.putExtra("apps", args);
                myIntent.putExtra("Name", groups.get(position).getName());
                myIntent.putExtra("Options",groups.get(position).getOpt());
                myIntent.putExtra("time",groups.get(position).getTime());
                myIntent.putExtra("mess",groups.get(position).getMessage());

                Bundle args2 = new Bundle();
                args2.putSerializable("ArrayList2", (Serializable) groups.get(position).getList2());
                myIntent.putExtra("phones",args2);
                startActivity(myIntent);

            }
        });
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(MainActivity.this, Add_Groups.class);
                startActivity(myIntent);
            }
        });
    }

    /*
     Check if needed permissions are granted for programme
     */
    public void CheckPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.WRITE_CONTACTS},
                    MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
        }
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {

                    // If request is cancelled, the result arrays are empty.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_CONTACTS},
                                MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
                    }
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                    }
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                    }
                    return;

            }
            case MY_PERMISSIONS_REQUEST_WRITE_CONTACTS: {
                // If request is cancelled, the result arrays are empty.

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                    }
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                    }
                    return;

            }

            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {

                // If request is cancelled, the result arrays are empty.
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
                return;

            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    
    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Notification Listener Service");
        alertDialogBuilder.setMessage("For the the app. to work you need to enable the Notification Listener Service. Enable it now?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                    }
                });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return(alertDialogBuilder.create());
    }
    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /*
        Read groups from files
    */
    public static ArrayList<Group> getGroups() {
        File path = new File(obj.getFilesDir(), "myFolder");
        path.mkdirs();
        File[] files = path.listFiles();


        ArrayList<Group> groups = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            try {
                ArrayList<Model> model = new ArrayList<>();
                ArrayList<Model> phones = new ArrayList<>();
                JSONObject obj = new JSONObject(loadJSONFromAsset(files[i]));
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
                groups.get(i).setMessage(message);
                groups.get(i).setPhones(phones);
                groups.get(i).setTime(obj.getInt("HourFrom"),obj.getInt("MinuteFrom"),obj.getInt("HourTo"),obj.getInt("MinuteTo"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    return groups;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
        Read file as json
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

}
