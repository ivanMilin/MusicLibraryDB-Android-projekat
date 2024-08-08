package com.example.musiclibrarydb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddRemoveActivity extends AppCompatActivity {

    EditText editText_songName;
    EditText editText_artist;
    EditText editText_genre;

    Button btn_add;
    Button btn_goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_remove);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editText_songName = findViewById(R.id.editText_songName2);
        editText_artist   = findViewById(R.id.editText_artist2);
        editText_genre    = findViewById(R.id.editText_genre2);
        btn_add           = findViewById(R.id.btn_update);
        btn_goBack        = findViewById(R.id.btn_goBack2);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper DB = new DBHelper(AddRemoveActivity.this);

                Boolean checkIsItDone = DB.insertSong(editText_songName.getText().toString().trim(), editText_artist.getText().toString().trim(), editText_genre.getText().toString().trim());
                if(checkIsItDone == true)
                {
                    Toast.makeText(AddRemoveActivity.this,"Input DONE!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(AddRemoveActivity.this,"Input Failed! "+ editText_songName.getText().toString().trim() +" "+ editText_artist.getText().toString().trim() +" "+ editText_genre.getText().toString().trim(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddRemoveActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }


}