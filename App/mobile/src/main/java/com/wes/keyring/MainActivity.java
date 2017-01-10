package com.wes.keyring;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseHandler dbh;
    CoordinatorLayout cLayout;
    FloatingActionButton addCardButton;
    TextView noCards;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbh = new DatabaseHandler(this, null, null, 1);

        cLayout = (CoordinatorLayout)findViewById(R.id.fab_layout);
        cLayout.bringToFront();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter(getData());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String nc = getData().get(position).getmText1();

                        Intent in = new Intent(MainActivity.this, OpenCard.class);
                        in.putExtra("nc", nc);
                        startActivity(in);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));

                noCards = (TextView) findViewById(R.id.label_no_cards);

        addCardButton = (FloatingActionButton)findViewById(R.id.fab);
        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddCardActivity.class));
            }
        });

        Cursor res = dbh.getAllData();

        if (checkDatabase(MainActivity.this, "data.db")) {
            noCards.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter(getData());
        mRecyclerView.setAdapter(mAdapter);

        Cursor res = dbh.getAllData();

        if (checkDatabase(MainActivity.this, "data.db")) {
            noCards.setVisibility(View.INVISIBLE);
        }
    }

    private ArrayList<DataObject> getData() {
        ArrayList results = new ArrayList<DataObject>();

        Cursor res = dbh.getAllData();
        String nc;
        String nh;
        String sn;

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            nc = res.getString(1).toString();
            nh = res.getString(2).toString();
            sn = res.getString(4).toString();
            DataObject obj = new DataObject(nc, nh, sn);
            results.add(obj);
        }

        return results;
    }

    private static boolean checkDatabase(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }
}
