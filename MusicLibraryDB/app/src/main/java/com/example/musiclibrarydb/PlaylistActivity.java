package com.example.musiclibrarydb;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaylistActivity extends AppCompatActivity {

    MaterialCardView selectSongs;
    TextView availableSongs,textView2;
    boolean[] selectedSongs;
    ArrayList<Integer> songsList = new ArrayList<>();
    String[] songsArray;

    Button button_add, button_showPlaylist, button_remove;
    Spinner spinner;
    ArrayList<String> playlistNames = new ArrayList<>();
    ArrayAdapter<String> spinnerAdapter;
    Map<String, String> playlists = new HashMap<>(); // Map to store playlist names and their songs

    DBHelper dbHelper; // Declare DBHelper instance
    String selectedPlaylist;
    String usernameFromIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playlist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this); // Initialize DBHelper
        loadSongsFromDatabase(); // Load songs into songsArray

        selectSongs = findViewById(R.id.selectCard);
        availableSongs = findViewById(R.id.availableSongs);
        textView2 = findViewById(R.id.textView2);
        selectedSongs = new boolean[songsArray.length];

        spinner = findViewById(R.id.spinner);

        button_add = findViewById(R.id.button_add);
        button_remove = findViewById(R.id.button_remove);
        button_showPlaylist = findViewById(R.id.button_showPlaylist);

        // Initialize the spinner adapter
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, playlistNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        usernameFromIntent = getIntent().getStringExtra("username").toString();

        selectSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCoursesDialog();
            }
        });

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlaylist();
                //DB.insertSongForPlaylist(playlistNames);

                songsList.clear();
                for (int i = 0; i < selectedSongs.length; i++) {
                    selectedSongs[i] = false;
                }
            }
        });

        button_showPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectedPlaylist();
            }
        });

        button_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelectedPlaylist();
            }
        });
    }
    //==============================================================================================
    private void loadSongsFromDatabase() {
        Cursor cursor = dbHelper.readAllSongsName();
        if (cursor != null) {
            ArrayList<String> songsList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String songName = cursor.getString(0); // Assuming the first column is songName
                songsList.add(songName);
            }
            cursor.close();
            songsArray = songsList.toArray(new String[0]);
        }
    }
    //==============================================================================================
    private void showCoursesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlaylistActivity.this);

        builder.setTitle("Select Songs for playlist");
        builder.setCancelable(false);

        builder.setMultiChoiceItems(songsArray, selectedSongs, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    songsList.add(which);
                } else {
                    songsList.remove((Integer) which);
                }
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateAvailableSongs();
            }
        });
        builder.show();
    }
    //==============================================================================================
    private void updateAvailableSongs() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < songsList.size(); i++) {
            stringBuilder.append(songsArray[songsList.get(i)]);


            if (i != songsList.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        availableSongs.setText(stringBuilder.toString()); // Update the TextView with selected songs
    }
    //==============================================================================================
    private void addPlaylist() {
        StringBuilder playlistBuilder = new StringBuilder();
        DBHelper DB = new DBHelper(PlaylistActivity.this);
        String playlistName;

        playlistBuilder.append(":");
        for (int index : songsList) {
            playlistBuilder.append(songsArray[index]).append("\n");
        }
        //if (playlistBuilder.length() > 0) {
            playlistBuilder.setLength(playlistBuilder.length() - 1);

            playlistName = "Playlist" + (playlistNames.size() + 1);
            DB.insertPlaylist(playlistName, usernameFromIntent);
            playlists.put(playlistName, playlistBuilder.toString());
            playlistNames.add(playlistName);
            spinnerAdapter.notifyDataSetChanged();
            DB.addSongToPlaylist(playlistName, "Danza Kuduro");
        //}
        songsList.clear();
        availableSongs.setText(""); // Clear the TextView
    }
    //==============================================================================================
    private void removeSelectedPlaylist() {
        int selectedPosition = spinner.getSelectedItemPosition();
        if (selectedPosition >= 0 && selectedPosition < playlistNames.size()) {
            String playlistName = playlistNames.get(selectedPosition);
            playlists.remove(playlistName); // Remove from map
            playlistNames.remove(selectedPosition); // Remove from list
            spinnerAdapter.notifyDataSetChanged(); // Update the Spinner
        }
    }
    //==============================================================================================
    private void showSelectedPlaylist() {
        int selectedPosition = spinner.getSelectedItemPosition();
        if (selectedPosition >= 0 && selectedPosition < playlistNames.size()) {
            String playlistName = playlistNames.get(selectedPosition);
            selectedPlaylist = playlists.get(playlistName);

            // Remove leading ":" if it exists
            if (selectedPlaylist.startsWith(":")) {
                selectedPlaylist = selectedPlaylist.substring(1);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(PlaylistActivity.this);
            builder.setTitle("Songs in " + playlistName + ":"); // Concatenate playlist name to title
            builder.setMessage(selectedPlaylist);
            builder.setPositiveButton("OK", null);

            builder.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Show a dialog to edit the playlist
                    showEditDialog(playlistName, selectedPlaylist.split("\n"));
                }
            });
            builder.show();
        }
    }
    //==============================================================================================
    private void showEditDialog(String playlistName, String[] songs) {
        boolean[] checkedItems = new boolean[songs.length];
        AlertDialog.Builder editDialogBuilder = new AlertDialog.Builder(PlaylistActivity.this);
        editDialogBuilder.setTitle("Edit " + playlistName);

        editDialogBuilder.setMultiChoiceItems(songs, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index, boolean isChecked) {
                checkedItems[index] = isChecked;
            }
        });

        editDialogBuilder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder updatedPlaylist = new StringBuilder();
                for (int i = 0; i < songs.length; i++) {
                    if (!checkedItems[i]) {
                        updatedPlaylist.append(songs[i]).append("\n");
                    }
                }
                if (updatedPlaylist.length() > 0) {
                    updatedPlaylist.setLength(updatedPlaylist.length() - 1); // Remove the trailing newline
                }

                // Update the playlist map with the new song list
                playlists.put(playlistName, updatedPlaylist.toString());

                Toast.makeText(PlaylistActivity.this, "Playlist updated", Toast.LENGTH_SHORT).show();
            }
        });

        editDialogBuilder.setNegativeButton("Cancel", null);
        editDialogBuilder.show();
    }

}


//How to Implement MultiSelect DropDown List in Android | MultiSelectDropDownList
//https://www.youtube.com/watch?v=4GdbCl-47wE