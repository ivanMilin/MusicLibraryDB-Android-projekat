package com.example.musiclibrarydb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>
{
    private Context context;
    private ArrayList songID, songName, artistName, songGenre;

    CustomAdapter(Context context, ArrayList songID, ArrayList songName,ArrayList artistName,ArrayList songGenre)
    {
        this.context  = context;
        this.songID   = songID;
        this.songName = songName;
        this.artistName = artistName;
        this.songGenre  = songGenre;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.songID.setText(String.valueOf(songID.get(position)));
        holder.songName.setText(String.valueOf(songName.get(position)));
        holder.artistName.setText(String.valueOf(artistName.get(position)));
        holder.songGenre.setText(String.valueOf(songGenre.get(position)));
    }

    @Override
    public int getItemCount() {
        return songID.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView songID, songName, artistName, songGenre;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            songID = itemView.findViewById(R.id.songID);
            songName = itemView.findViewById(R.id.songName);
            artistName = itemView.findViewById(R.id.artistName);
            songGenre = itemView.findViewById(R.id.songGenre);
        }
    }
}
