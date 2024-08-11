package com.example.musiclibrarydb;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateActivity extends AppCompatActivity {


    EditText songName2, artist2, genre2;
    Button btn_update;
    Button btn_delete;

    String songID, songName, artistName, songGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        songName2  = findViewById(R.id.editText_songName2);
        artist2    = findViewById(R.id.editText_artist2);
        genre2     = findViewById(R.id.editText_genre2);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);

        getAndSetIntentData();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper DB = new DBHelper(UpdateActivity.this);
                Toast.makeText(UpdateActivity.this, "Kod ispravljen "  + songName2.getText().toString() + " " + artist2.getText().toString() + " " + genre2.getText().toString() + " ", Toast.LENGTH_SHORT).show();
                DB.updateData(songID, songName2.getText().toString(), artist2.getText().toString(), genre2.getText().toString());
                finish();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });
    }

    void getAndSetIntentData()
    {
        if(getIntent().hasExtra("id") && getIntent().hasExtra("songName") &&
           getIntent().hasExtra("artistName") && getIntent().hasExtra("songGenre"))
        {
            //GETTING INTENT DATA
            songID = getIntent().getStringExtra("id");
            songName = getIntent().getStringExtra("songName");
            artistName = getIntent().getStringExtra("artistName");
            songGenre = getIntent().getStringExtra("songGenre");

            //SETTING INTENT DATA
            songName2.setText(songName);
            artist2.setText(artistName);
            genre2.setText(songGenre);
        }
        else
        {
            Toast.makeText(this, "Zero data received!", Toast.LENGTH_SHORT).show();
        }

    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DBHelper DB = new DBHelper(UpdateActivity.this);
                DB.deleteOneRow(songID);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
    }
}