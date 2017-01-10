package com.internationalmoneygetters.jeevand.firebasetester;

import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;

/**
 * Created by JeevanD on 16-09-17.
 */
public class firebasetester extends android.app.Application {



    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

}
