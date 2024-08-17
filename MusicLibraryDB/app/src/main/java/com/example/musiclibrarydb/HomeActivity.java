package com.example.musiclibrarydb;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
    FloatingActionButton add_remove_button,floatingActionButton_delete;

    String username;
    DBHelper DB;
    ArrayList <String> songID, songName, artistName, songGenre, musicNote;
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


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            username = intent.getStringExtra("username");
        }

        if (username != null) {
            username = username.toString();
        } else {
            // Handle the case where username is null, e.g., show a message or assign a default value
            username = "defaultUsername";  // Replace with your logic
            }

        recyclerView = findViewById(R.id.recycleView);

        add_remove_button = findViewById(R.id.floatingActionButton);
        floatingActionButton_delete = findViewById(R.id.floatingActionButton_delete);
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

        songID     = new ArrayList<>();
        songName   = new ArrayList<>();
        artistName = new ArrayList<>();
        songGenre  = new ArrayList<>();
        musicNote  = new ArrayList<>();

        DB = new DBHelper(HomeActivity.this);
        storeDataInArrays();

        customAdapter = new CustomAdapter(HomeActivity.this, this,songID,songName,artistName,songGenre,musicNote);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));


        button_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PlaylistActivity.class);
                intent.putExtra("username",username);
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

        floatingActionButton_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String searchData = dataForSearch.getText().toString().trim();

                // Create an AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                switch (spinner.getSelectedItem().toString()) {
                                    case "songGenre":
                                        DB.removeGenreAndAssociatedData(searchData);
                                        break;

                                    case "artistName":
                                        DB.removeArtistAndAssociatedSongs(searchData);
                                        break;

                                    case "songName":
                                        DB.removeSongByName(searchData);
                                        break;
                                }

                                storeDataInArrays();
                                customAdapter.notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
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
    //==============================================================================================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1)
            recreate();
    }
    //==============================================================================================
    @SuppressLint("Range")
    void storeDataInArrays()
    {
        Cursor cursor = DB.readAllData();

        songID.clear();
        songName.clear();
        artistName.clear();
        songGenre.clear();
        musicNote.clear();

        if(cursor.getCount() == 0)
        {
            Toast.makeText(HomeActivity.this,"Baza je prazna!", Toast.LENGTH_LONG).show();
        }
        else
        {
            while(cursor.moveToNext())
            {
                musicNote.add("♪");
                songID.add(cursor.getString(cursor.getColumnIndex("id"))); // ID
                songName.add(cursor.getString(cursor.getColumnIndex("name"))); // Song Name
                artistName.add(cursor.getString(cursor.getColumnIndex("artist_name"))); // Artist Name
                songGenre.add(cursor.getString(cursor.getColumnIndex("genre"))); // Song Genre
            }
        }
    }
    //==============================================================================================
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Create an AlertDialog.Builder
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setCancelable(false) // Prevent the dialog from being dismissed by clicking outside or pressing the back button
                .setPositiveButton("Yes", (dialog, which) -> {
                    // If the user clicks "Yes", finish the activity
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // If the user clicks "No", just dismiss the dialog
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload the data from the database
        storeDataInArrays();
        // Notify the adapter that the data has changed
        customAdapter.notifyDataSetChanged();
    }
}