package com.followitupapp.followitup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.followitupapp.followitup.R;

/**
 * Created by zic on 7/27/17.
 */

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.hello);
        Intent intent = getIntent();
        textView.setText("Hello " + intent.getStringExtra("login from"));
    }
}
