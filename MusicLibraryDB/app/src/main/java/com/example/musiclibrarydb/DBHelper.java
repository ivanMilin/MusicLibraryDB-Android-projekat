package com.example.musiclibrarydb;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper
{
    private Context context;

    private static final String TAG = "DBHelper";

    public static final String DBNAME = "MusicLibraryDB";

    //= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
    private static final String TABLE_USERS = "users";
    private static final String TABLE_ARTISTS = "artists";
    private static final String TABLE_GENRES = "genres";
    private static final String TABLE_SONGS = "songs";
    private static final String TABLE_PLAYLISTS = "playlists";
    private static final String TABLE_USER_PLAYLISTS = "user_playlists";

    // Common Column Names
    private static final String KEY_ID = "id";

    // USERS Table - column names
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    // ARTISTS Table - column names
    private static final String KEY_ARTIST_NAME = "name";
    private static final String KEY_ARTIST_GENRE = "genre";

    // GENRES Table - column names
    private static final String KEY_GENRE_NAME = "name";

    // SONGS Table - column names
    private static final String KEY_SONG_NAME = "name";
    private static final String KEY_SONG_GENRE = "genre";
    private static final String KEY_SONG_ARTIST_NAME = "artist_name";

    // PLAYLISTS Table - column names
    private static final String KEY_PLAYLIST_NAME = "name";

    // USER_PLAYLISTS Table - column names
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PLAYLIST_ID = "playlist_id";
    private static final String KEY_SONG_ID = "song_id";
    //= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =

    public DBHelper(@Nullable Context context) {
        super(context, "MusicLibraryDB", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_table_users     = "CREATE TABLE " + TABLE_USERS + "("
                                      + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                      + KEY_USERNAME + " TEXT UNIQUE,"
                                      + KEY_PASSWORD + " TEXT" + ")";


        String create_table_artists   = "CREATE TABLE " + TABLE_ARTISTS + "("
                                      + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                      + KEY_ARTIST_NAME + " TEXT,"
                                      + KEY_ARTIST_GENRE + " TEXT" + ")";

        String create_table_genres    = "CREATE TABLE " + TABLE_GENRES + "("
                                      + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                      + KEY_GENRE_NAME + " TEXT" + ")";

        String create_table_songs     = "CREATE TABLE " + TABLE_SONGS + "("
                                      + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                      + KEY_SONG_NAME + " TEXT,"
                                      + KEY_SONG_GENRE + " TEXT,"
                                      + KEY_SONG_ARTIST_NAME + " TEXT" + ")";


        String create_table_playlists = "CREATE TABLE " + TABLE_PLAYLISTS + "("
                                      + KEY_ID + " INTEGER PRIMARY KEY,"
                                      + KEY_PLAYLIST_NAME + " TEXT,"
                                      + KEY_USER_ID + " INTEGER,"
                                      + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + ")"
                                      + ")";

        // Create the user_playlists table
        String create_table_users_playlists = "CREATE TABLE " + TABLE_USER_PLAYLISTS + "("
                                            + KEY_ID + " INTEGER PRIMARY KEY,"
                                            + KEY_PLAYLIST_ID + " INTEGER,"
                                            + KEY_SONG_ID + " INTEGER,"
                                            + "FOREIGN KEY(" + KEY_PLAYLIST_ID + ") REFERENCES " + TABLE_PLAYLISTS + "(" + KEY_ID + "),"
                                            + "FOREIGN KEY(" + KEY_SONG_ID + ") REFERENCES " + TABLE_SONGS + "(" + KEY_ID + ")"
                                            + ")";



        db.execSQL(create_table_users);
        db.execSQL(create_table_artists);
        db.execSQL(create_table_songs);
        db.execSQL(create_table_genres);
        db.execSQL(create_table_playlists);
        db.execSQL(create_table_users_playlists);


        }
    //==============================================================================================
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PLAYLISTS);
        onCreate(db);
    }
    //==============================================================================================
    public Boolean insertData(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("username", username);
        values.put("password", password);

        long result = db.insert("users", null, values);

        return result != -1;
    }
    //==============================================================================================
    public Boolean checkUsername(String username)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Boolean exists = false;

        String query = "SELECT 1 FROM " + TABLE_USERS + " WHERE " + KEY_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                exists = true;  // Record exists
            }
            cursor.close();
        }

        db.close();
        return exists;
    }
    //==============================================================================================
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
    //==============================================================================================
    public Boolean insertArtist(String name, String genre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ARTIST_NAME, name);
        values.put(KEY_ARTIST_GENRE, genre);

        long result = db.insert(TABLE_ARTISTS, null, values);
        db.close();

        return result != -1;
    }
    //==============================================================================================
    public Boolean insertGenre(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_GENRE_NAME, name);

        long result = db.insert(TABLE_GENRES, null, values);
        db.close();

        return result != -1;
    }
    //==============================================================================================
    public Boolean insertSong(String songName, String artist, String genre)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_SONG_NAME, songName);
        values.put(KEY_SONG_ARTIST_NAME, artist);
        values.put(KEY_SONG_GENRE, genre);

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
    //==============================================================================================
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
    //==============================================================================================
    Cursor readAllSongsName()
    {
        String query = "SELECT name FROM " + "songs";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null)
        {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
    //==============================================================================================
    public void updateData(String row_id,String songName, String artistName, String songGenre)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(KEY_SONG_NAME, songName);
        cv.put(KEY_SONG_ARTIST_NAME, artistName);
        cv.put(KEY_SONG_GENRE, songGenre);

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
    //==============================================================================================
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
    //==============================================================================================
    @SuppressLint("Range")
    public Long getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Long userId = null;

        String query = "SELECT " + KEY_ID + " FROM " + TABLE_USERS + " WHERE " + KEY_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                userId = cursor.getLong(cursor.getColumnIndex(KEY_ID));
            }
            cursor.close();
        }

        // Do not close the database here; it will be closed in the calling method
        return userId;
    }
    //==============================================================================================
    @SuppressLint("Range")
    public Long getPlaylistIdByName(String playlistName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Long playlistId = null;

        String query = "SELECT " + KEY_ID + " FROM " + TABLE_PLAYLISTS + " WHERE " + KEY_PLAYLIST_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{playlistName});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                playlistId = cursor.getLong(cursor.getColumnIndex(KEY_ID));
            }
            cursor.close();
        }

        //db.close();
        return playlistId;
    }
    //==============================================================================================
    @SuppressLint("Range")
    public Long getSongIdByName(String songName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Long songId = null;

        String query = "SELECT " + KEY_ID + " FROM " + TABLE_SONGS + " WHERE " + KEY_SONG_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{songName});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                songId = cursor.getLong(cursor.getColumnIndex(KEY_ID));
            }
            cursor.close();
        }

        //db.close();
        return songId;
    }
    //==============================================================================================
    public Boolean insertPlaylist(String playlistName, String username) {
        SQLiteDatabase db = null;
        Boolean success = false;

        try {
            db = this.getWritableDatabase();

            // Get userId based on username
            Long userId = getUserIdByUsername(username);
            if (userId == null) {
                return false;
            }

            // Insert the playlist
            ContentValues values = new ContentValues();
            values.put(KEY_PLAYLIST_NAME, playlistName);
            values.put(KEY_USER_ID, userId);

            long result = db.insert(TABLE_PLAYLISTS, null, values);

            success = result != -1;
        } finally {
            if (db != null && db.isOpen()) {
                //db.close();
            }
        }

        return success;
    }
    //==============================================================================================
    @SuppressLint("Range")
    public boolean addSongToPlaylist(String playlistName, String songName) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;

        try {
            // Retrieve the playlist ID using the playlist name
            Long playlistId = getPlaylistIdByName(playlistName);
            if (playlistId == null) {
                Log.e("DB_ERROR", "Playlist not found: " + playlistName);
                return false;
            }

            // Retrieve the song ID using the song name
            String songQuery = "SELECT " + KEY_ID + " FROM " + TABLE_SONGS + " WHERE " + KEY_SONG_NAME + " = ?";
            Cursor cursor = db.rawQuery(songQuery, new String[]{songName});

            Long songId = null;
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    songId = cursor.getLong(cursor.getColumnIndex(KEY_ID));
                }
                cursor.close();
            }

            if (songId == null) {
                Log.e("DB_ERROR", "Song not found: " + songName);
                return false;
            }

            // Prepare the values to be inserted
            ContentValues values = new ContentValues();
            values.put(KEY_PLAYLIST_ID, playlistId);
            values.put(KEY_SONG_ID, songId);

            // Insert the values into the table
            long rowId = db.insert(TABLE_USER_PLAYLISTS, null, values);
            success = (rowId != -1);  // Check if the insertion was successful

        } catch (Exception e) {
            Log.e("DB_ERROR", "Error inserting song into playlist", e);
        } finally {
            db.close();  // Ensure the database is closed after the operation
        }

        return success;
    }
    //==============================================================================================
    @SuppressLint("Range")
    public ArrayList<String> getPlaylistsByUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> playlists = new ArrayList<>();

        // Get userId based on username
        Long userId = getUserIdByUsername(username);
        if (userId == null) {
            //db.close();
            return playlists;
        }

        // Query to get playlists for the specific user
        String query = "SELECT " + KEY_PLAYLIST_NAME + " FROM " + TABLE_PLAYLISTS + " WHERE " + KEY_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    playlists.add(cursor.getString(cursor.getColumnIndex(KEY_PLAYLIST_NAME)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();
        return playlists;
    }
    //==============================================================================================
    @SuppressLint("Range")
    public Long getPlaylistIdByNameAndUser(String playlistName, Long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Long playlistId = null;

        // Query to find the playlist ID for the given playlist name and user ID
        String query = "SELECT " + KEY_ID + " FROM " + TABLE_PLAYLISTS
                + " WHERE " + KEY_PLAYLIST_NAME + " = ? AND " + KEY_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{playlistName, String.valueOf(userId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                playlistId = cursor.getLong(cursor.getColumnIndex(KEY_ID));
            }
            cursor.close();
        }

        //db.close();
        return playlistId;
    }
    //==============================================================================================
}
//https://www.youtube.com/watch?v=yJ02XTKiuAc
//https://www.youtube.com/playlist?list=PLSrm9z4zp4mGK0g_0_jxYGgg3os9tqRUQ
// ~ 10:00