package com.example.musiclibrarydb;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton add_remove_button;

    DBHelper DB;
    ArrayList <String> songID, songName, artistName, songGenre;
    CustomAdapter customAdapter;

    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recycleView);
        add_remove_button = findViewById(R.id.floatingActionButton);

        spinner = findViewById(R.id.searchType);

        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Song Name");
        spinnerItems.add("Song Genre");
        spinnerItems.add("Artist Name");

        // Create an ArrayAdapter using a simple spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, // or getContext() if inside a fragment
                android.R.layout.simple_spinner_item,
                spinnerItems
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        DB = new DBHelper(HomeActivity.this);
        songID     = new ArrayList<>();
        songName   = new ArrayList<>();
        artistName = new ArrayList<>();
        songGenre  = new ArrayList<>();

        storeDataInArrays();

        customAdapter = new CustomAdapter(HomeActivity.this, this,songID,songName,artistName,songGenre);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1)
            recreate();
    }

    void storeDataInArrays()
    {
        Cursor cursor = DB.readAllData();

        if(cursor.getCount() == 0)
        {
            Toast.makeText(HomeActivity.this,"Baza je prazna!", Toast.LENGTH_LONG).show();
        }
        else
        {
            while(cursor.moveToNext())
            {
                songID.add(cursor.getString(0));
                songName.add(cursor.getString(1));
                artistName.add(cursor.getString(2));
                songGenre.add(cursor.getString(3));
            }
        }

    }
}