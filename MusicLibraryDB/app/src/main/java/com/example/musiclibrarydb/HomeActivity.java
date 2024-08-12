package com.example.musiclibrarydb;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
    Button button_playlist;
    FloatingActionButton add_remove_button;


    DBHelper DB;
    ArrayList <String> songID, songName, artistName, songGenre;
    CustomAdapter customAdapter;

    EditText dataForSearch;

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
        button_playlist = findViewById(R.id.button_playlist);

        spinner = findViewById(R.id.searchType);

        dataForSearch = findViewById(R.id.dataForSearch);

        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("songName");
        spinnerItems.add("songGenre");
        spinnerItems.add("artistName");

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


        button_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PlaylistActivity.class);
                startActivity(intent);
            }
        });

        add_remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddRemoveActivity.class);
                startActivity(intent);
            }
        });

        dataForSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Get the search query and search type
                    String query = dataForSearch.getText().toString().trim().toLowerCase();
                    String searchType = spinner.getSelectedItem().toString();

                    // Create a list to store matched results
                    ArrayList<String> matchedResults = new ArrayList<>();

                    // Perform search based on the selected type
                    switch (searchType) {
                        case "songName":
                            for (int i = 0; i < songName.size(); i++) {
                                if (songName.get(i).toLowerCase().contains(query)) {
                                    matchedResults.add("Song: " + songName.get(i) + "\nArtist: " + artistName.get(i) + "\nGenre: " + songGenre.get(i));
                                }
                            }
                            break;
                        case "artistName":
                            for (int i = 0; i < artistName.size(); i++) {
                                if (artistName.get(i).toLowerCase().contains(query)) {
                                    matchedResults.add("Song: " + songName.get(i) + "\nArtist: " + artistName.get(i) + "\nGenre: " + songGenre.get(i));
                                }
                            }
                            break;
                        case "songGenre":
                            for (int i = 0; i < songGenre.size(); i++) {
                                if (songGenre.get(i).toLowerCase().contains(query)) {
                                    matchedResults.add("Song: " + songName.get(i) + "\nArtist: " + artistName.get(i) + "\nGenre: " + songGenre.get(i));
                                }
                            }
                            break;
                    }

                    // Display the results in an AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    if (matchedResults.isEmpty()) {
                        builder.setTitle("No Results Found");
                        builder.setMessage("No matches found for the search query.");
                    } else {
                        builder.setTitle("Search Results");
                        StringBuilder resultMessage = new StringBuilder();
                        for (String result : matchedResults) {
                            resultMessage.append(result).append("\n\n");
                        }
                        builder.setMessage(resultMessage.toString().trim());
                    }
                    builder.setPositiveButton("OK", null);
                    builder.show();

                    return true; // Consume the action
                }
                return false; // Let others handle it
            }
        });
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