package com.wes.keyring;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class OpenCard extends AppCompatActivity {

    DatabaseHandler dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_card);

        Bundle extras = getIntent().getExtras();
        String thisNC = extras.getString("nc");
        Card c = dbh.findCard(thisNC);

        this.setTitle(thisNC);


    }
}
