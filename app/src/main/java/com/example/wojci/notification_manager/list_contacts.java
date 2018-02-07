package com.example.wojci.notification_manager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * List of contacts if user wants to send text message on missed call
  */

public class list_contacts extends AppCompatActivity {


    ListView lv ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_apps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent myIntent = getIntent();

        Bundle args = myIntent.getBundleExtra(("apps"));
        final ArrayList<Model> model = (ArrayList<Model>) args.getSerializable("ArrayList");

        Drawable[] d = new Drawable[model.size()];
        for(int i = 0; i < d.length; i++)
            d[i] = getDrawable(R.mipmap.ic_contacts);

        lv =  (ListView) findViewById(R.id.list);

        Collections.sort(model,new Model.CustomComparator());

        //remove copies
        for(int i = 0; i < model.size() - 1; i++){
            if(model.get(i).getName().equals(model.get(i+1).getName()) )
            {
                model.remove(i);
                i--;
            }
        }

        CustomList adapter = new
                CustomList(this, model,d);

        lv.setAdapter(adapter);
        Button button = (Button) findViewById(R.id.button1) ;


        //send back results (selected phone numbers)
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent data = new Intent();
                int n = 0;
                //data.putExtra("Data1", model);
                data.putExtra("data1", model);
                for (Model m: model
                     ) {
                    if(m.isSelected()) {
                        n = 1;
                        break;
                    }
                }
                if(n == 0)
                    finish();
                setResult(RESULT_OK, data);
                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(false );

    }
}