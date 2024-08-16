package com.example.musiclibrarydb;

import static java.lang.reflect.Array.get;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    ArrayList<String> selectedSongsForPlaylist = new ArrayList<>();

    Button button_add, button_showPlaylist, button_remove;
    Spinner spinner;
    ArrayAdapter<String> spinnerAdapter;

    DBHelper dbHelper;
    String usernameFromIntent;
    String playListName;
    ArrayList<String> songsInOnePlaylist = new ArrayList<>();

    ArrayList<String> SpinnerList;

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
                if(!playListName.isEmpty())
                {
                    spinnerAdapter.add(playListName);
                    addPlaylistWithSongs();

                    songsList.clear();
                    for (int i = 0; i < selectedSongs.length; i++) {
                        selectedSongs[i] = false;
                    }
                }
                else
                {
                    Toast.makeText(PlaylistActivity.this, "Playlist name is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_showPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spinner.getAdapter() != null && spinner.getAdapter().getCount() > 0) {
                    String selectedItem = spinner.getSelectedItem().toString();

                    songsInOnePlaylist = dbHelper.getAllSongsInPlaylist(usernameFromIntent, selectedItem);
                    showSongsInPlaylist(songsInOnePlaylist);
                }
                else
                {
                    Toast.makeText(PlaylistActivity.this, "Spinner is empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.removePlaylist(spinner.getSelectedItem().toString());

                int selectedPosition = spinner.getSelectedItemPosition();
                if (selectedPosition >= 0 && selectedPosition < SpinnerList.size())
                {
                    String playlistName = SpinnerList.get(selectedPosition);
                    SpinnerList.remove(selectedPosition);
                    spinnerAdapter.notifyDataSetChanged();
                    loadPlaylistsIntoSpinner();
                }
            }
        });

        loadPlaylistsIntoSpinner(); // Load playlists into spinner
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
        // Create an EditText for the playlist name
        final EditText editTextPlaylistName = new EditText(PlaylistActivity.this);
        editTextPlaylistName.setHint("Playlist name :");
        editTextPlaylistName.setInputType(InputType.TYPE_CLASS_TEXT);

        // Create a container to hold the EditText and the MultiChoiceItems
        LinearLayout layout = new LinearLayout(PlaylistActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);
        layout.addView(editTextPlaylistName);  // Add the EditText to the layout

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(PlaylistActivity.this);
        builder.setTitle("Select Songs for playlist");
        builder.setCancelable(false);

        // Add the layout containing EditText to the dialog
        builder.setView(layout);

        // Add the MultiChoiceItems (songs) to the dialog
        builder.setMultiChoiceItems(songsArray, selectedSongs, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    songsList.add(which);
                }
                else
                {
                    songsList.remove((Integer) which);
                }
            }
        });

        // Set the Positive Button for the dialog
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playListName = editTextPlaylistName.getText().toString().trim();

                if (!playListName.isEmpty()) {
                    // You can use the playlistName variable for further processing
                    updateAvailableSongs();
                }
            }
        });

        // Show the dialog
        builder.show();
    }
    //==============================================================================================
    private void updateAvailableSongs() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < songsList.size(); i++) {
            stringBuilder.append(songsArray[songsList.get(i)]);

            selectedSongsForPlaylist.add(songsArray[songsList.get(i)]);


            if (i != songsList.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        availableSongs.setText(stringBuilder.toString()); // Update the TextView with selected songs
    }
    //==============================================================================================
    private void addPlaylistWithSongs() {
        StringBuilder playlistBuilder = new StringBuilder();
        DBHelper DB = new DBHelper(PlaylistActivity.this);

        playlistBuilder.append(":");
        for (int index : songsList)
        {
            playlistBuilder.append(songsArray[index]).append("\n");
        }

        playlistBuilder.setLength(playlistBuilder.length() - 1);

        DB.insertPlaylist(playListName, usernameFromIntent);

        spinnerAdapter.notifyDataSetChanged();

        for (String song : selectedSongsForPlaylist)
        {
            DB.addSongToPlaylist(playListName, song);
        }
        selectedSongsForPlaylist.clear();


        songsList.clear();
        availableSongs.setText("");
    }
    //==============================================================================================
    private void loadPlaylistsIntoSpinner() {
        ArrayList<String> playlists = dbHelper.getAllPlaylistsByUsername(usernameFromIntent);
        SpinnerList = playlists;

        // Set up the spinner adapter with the playlist names
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, playlists);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        // Update the spinner with the loaded playlists
        if (!playlists.isEmpty()) {
            spinnerAdapter.notifyDataSetChanged();
        }
    }
    //==============================================================================================
    private void showSongsInPlaylist(ArrayList<String> songsInOnePlaylist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlaylistActivity.this);
        builder.setTitle("Songs in Playlist :\n\n");

        // Create a LinearLayout to hold the list of songs
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // Add each song to the LinearLayout
        for (String song : songsInOnePlaylist) {
            TextView textView = new TextView(this);
            textView.setText("○ "+song);
            textView.setPadding(120, 16, 16, 16);
            textView.setTextSize(16);
            linearLayout.addView(textView);
        }

        // Set the LinearLayout as the content view of the AlertDialog
        builder.setView(linearLayout);

        // Add a button to close the dialog
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        }).setNeutralButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showEditDialog(playListName, songsArray);

            }
        });

        // Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //==============================================================================================
    private void showEditDialog(String playlistName, String[] allSongs) {
        // Retrieve the currently selected songs for the playlist
        ArrayList<String> currentSongsList = songsInOnePlaylist; // Assuming this is an ArrayList<String>
        if (currentSongsList == null) {
            currentSongsList = new ArrayList<>();
        }

        // Prepare an array to keep track of selected items
        boolean[] checkedItems = new boolean[allSongs.length];

        // Mark items as checked if they are in the current playlist
        for (int i = 0; i < allSongs.length; i++) {
            if (currentSongsList.contains(allSongs[i].trim())) {
                checkedItems[i] = true;
            }
        }

        AlertDialog.Builder editDialogBuilder = new AlertDialog.Builder(PlaylistActivity.this);
        editDialogBuilder.setTitle("Edit " + playlistName);

        editDialogBuilder.setMultiChoiceItems(allSongs, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index, boolean isChecked) {
                checkedItems[index] = isChecked;
            }
        });

        editDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dbHelper.removePlaylist(playListName);
                dbHelper.insertPlaylist(playListName,usernameFromIntent);

                ArrayList<String> updatedPlaylist = new ArrayList<>();
                for (int i = 0; i < allSongs.length; i++) {
                    if (checkedItems[i]) {
                        updatedPlaylist.add(allSongs[i]);
                        dbHelper.addSongToPlaylist(playListName,allSongs[i]);
                    }
                }
                updatedPlaylist.clear();
                songsInOnePlaylist.clear();

                // Update the playlist map with the new song list
                //playlists.put(playlistName, String.join("\n", updatedPlaylist));

                Toast.makeText(PlaylistActivity.this, "Playlist updated", Toast.LENGTH_SHORT).show();
            }
        });

        editDialogBuilder.setNegativeButton("Cancel", null);
        editDialogBuilder.show();
    }
    //==============================================================================================
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        finish();
    }
}


//How to Implement MultiSelect DropDown List in Android | MultiSelectDropDownList
//https://www.youtube.com/watch?v=4GdbCl-47wE