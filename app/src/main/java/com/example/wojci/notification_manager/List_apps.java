package com.example.wojci.notification_manager;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/*
    List installed applications
 */

public class List_apps extends AppCompatActivity {

    //private static final String TAG = "MainActivity";

    ListView lv ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_apps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent myIntent = getIntent();

        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_UNINSTALLED_PACKAGES;

        final PackageManager pm = getPackageManager();
        final List<ApplicationInfo> packages = pm.getInstalledApplications(flags);

        Bundle args = myIntent.getBundleExtra(("apps"));
        final ArrayList<Model> model = (ArrayList<Model>) args.getSerializable("ArrayList");
        final ArrayList<Model2> model2  = new ArrayList<>();
        final ArrayList<Boolean> listItems = new ArrayList<Boolean>();
        String s = "";

        lv =  (ListView) findViewById(R.id.list);
        for(ApplicationInfo p : packages){
            if((p.flags & ApplicationInfo.FLAG_SYSTEM)!=1) {
                model2.add(new Model2((String) pm.getApplicationLabel(p), pm.getApplicationIcon(p), p.icon));
            }

        }

        Collections.sort(model,new Model.CustomComparator());
        Collections.sort(model2, new Model2.CustomComparator());

        Drawable[] d = new Drawable[model.size()];
        for(int i = 0; i < model.size(); i++){
            d[i] = model2.get(i).getInteger();
        }
        CustomList adapter = new
                CustomList(this, model,d);

        lv.setAdapter(adapter);

        Button button = (Button) findViewById(R.id.button1) ;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Model i:model) {
                    listItems.add(i.isSelected());
                }
                Intent data = new Intent();
                //data.putExtra("Data1", model);
                data.putExtra("data1", model);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(false );

    }

}
