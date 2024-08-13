package com.example.musiclibrarydb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper
{
    private Context context;
    public static final String DBNAME = "MusicLibraryDB";

    public DBHelper(@Nullable Context context) {
        super(context, "MusicLibraryDB", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists users(username TEXT primary key, password TEXT)");                                               //u zagradu ide koji su elementi te tabele - mozda je problem "if not exists"
        db.execSQL("CREATE TABLE if not exists songs(id INTEGER PRIMARY KEY AUTOINCREMENT, songName TEXT, authorName TEXT, genreName TEXT)");   //u zagradu ide koji su elementi te tabele - mozda je problem "if not exists"
        db.execSQL("CREATE TABLE if not exists playlists(id INTEGER PRIMARY KEY AUTOINCREMENT, songNames TEXT)");                               //u zagradu ide koji su elementi te tabele - mozda je problem "if not exists"
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists users");
        db.execSQL("drop table if exists songs");
        db.execSQL("drop table if exists playlists");
        onCreate(db);
    }

    public Boolean insertData(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("username", username);
        values.put("password", password);

        long result = db.insert("users", null, values);

        return result != -1;
    }

    public Boolean checkUsername(String username)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where username=?", new String[]{username});

        if(cursor.getCount()>0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public Boolean checkUsernamePassword(String username, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where username=? and password=?", new String[]{username,password});

        if(cursor.getCount() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public Boolean insertSong(String songName, String artist, String genre)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("songName", songName);
        values.put("authorName", artist);
        values.put("genreName", genre);

        try
        {
            long result = db.insert("songs", null, values);
            if (result == -1)
            {
                Log.e("DBHelper", "Failed to insert song");
                return false;
            }
            else
            {
                Log.d("DBHelper", "Song inserted successfully");
                return true;
            }
        }
        catch (Exception e)
        {
            Log.e("DBHelper", "Error inserting song", e);
            return false;
        }
    }

    Cursor readAllData()
    {
        String query = "SELECT * FROM " + "songs";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null)
        {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    Cursor readAllSongsName()
    {
        String query = "SELECT songName FROM " + "songs";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null)
        {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void updateData(String row_id,String songName, String artistName, String songGenre)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("songName", songName);
        cv.put("authorName", artistName);
        cv.put("genreName", songGenre);

        long result = db.update("songs", cv, "id=?", new String[]{row_id});

        if(result == -1)
        {
            Toast.makeText(context, "Failed updated!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Successfully updated!", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteOneRow(String row_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("songs","id=?", new String[]{row_id});

        if(result == -1)
        {
            Toast.makeText(context, "Failed removal!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean insertSongForPlaylist(String songs)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("songNames", songs);

        try
        {
            long result = db.insert("playlists", null, values);
            if (result == -1)
            {
                Log.e("DBHelper", "Failed to insert playlist");
                return false;
            }
            else
            {
                Log.d("DBHelper", "Songs inserted successfully");
                return true;
            }
        }
        catch (Exception e)
        {
            Log.e("DBHelper", "Error inserting playlist", e);
            return false;
        }
    }
}

//https://www.youtube.com/watch?v=yJ02XTKiuAc
//https://www.youtube.com/playlist?list=PLSrm9z4zp4mGK0g_0_jxYGgg3os9tqRUQ
// ~ 10:00