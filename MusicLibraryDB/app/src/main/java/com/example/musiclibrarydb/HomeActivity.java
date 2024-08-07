package com.example.musiclibrarydb;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton add_remove_button;

    DBHelper DB;
    ArrayList <String> songID, songName, artistName, songGenre;
    CustomAdapter customAdapter;

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

        add_remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddRemoveActivity.class);
                startActivity(intent);
            }
        });

        DB = new DBHelper(HomeActivity.this);
        songID     = new ArrayList<>();
        songName   = new ArrayList<>();
        artistName = new ArrayList<>();
        songGenre  = new ArrayList<>();

        storeDataInArrays();

        customAdapter = new CustomAdapter(HomeActivity.this,songID,songName,artistName,songGenre);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
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