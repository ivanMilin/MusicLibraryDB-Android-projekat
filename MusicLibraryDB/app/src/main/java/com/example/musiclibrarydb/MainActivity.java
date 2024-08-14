package com.example.musiclibrarydb;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    String username;
    EditText et_username, et_password;
    Button btn_login, btn_register;

    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_username = findViewById(R.id.username);
        et_password = findViewById(R.id.password);

        btn_login = findViewById(R.id.login);
        btn_register = findViewById(R.id.register);

        DB = new DBHelper(this);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = et_username.getText().toString();
                String pass = et_password.getText().toString();

                if(TextUtils.isEmpty(user) || TextUtils.isEmpty(pass))
                {
                    Toast.makeText(MainActivity.this,"All fields reguired", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Boolean checkuserpass = DB.checkUsernamePassword(user,pass);

                    if(checkuserpass == true)
                    {
                        username = et_username.getText().toString();
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra("username",username);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"Login Failed!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = et_username.getText().toString();
                String pass = et_password.getText().toString();

                if(TextUtils.isEmpty(user) || TextUtils.isEmpty(pass))
                {
                    Toast.makeText(MainActivity.this,"All fields reguired", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Boolean checkuser = DB.checkUsername(user);

                    if(checkuser == false)
                    {
                            Boolean insert = DB.insertData(user,pass);

                            if(insert == true)
                            {
                                Toast.makeText(MainActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Registered Failed", Toast.LENGTH_SHORT).show();
                            }
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "User already exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}