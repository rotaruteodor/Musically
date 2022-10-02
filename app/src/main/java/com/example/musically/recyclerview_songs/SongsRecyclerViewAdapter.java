package com.example.musically.recyclerview_songs;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musically.R;
import com.example.musically.classes.Song;
// todo extract background colors from customSongRow and mainActivity
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SongsRecyclerViewAdapter extends RecyclerView.Adapter<SongsRecyclerViewAdapter.SongsViewHolder> {

    ArrayList<Song> songs;
    RecyclerViewClickListener clickListener;

    public SongsRecyclerViewAdapter(ArrayList<Song> songs, RecyclerViewClickListener clickListener) {
        this.songs = songs;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_song_recyclerview_row, parent, false);
        return new SongsViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsViewHolder holder, int position) {
        holder.tvSongName.setText(songs.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public static class SongsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private WeakReference<RecyclerViewClickListener> clickListenerWeakReference;
        private final ConstraintLayout constraintLayoutCustomSongRow;
        private final TextView tvSongName;
        private final ImageButton buttonAddSongToFavorite;

        public SongsViewHolder(@NonNull View itemView, RecyclerViewClickListener clickListener) {
            super(itemView);
            clickListenerWeakReference = new WeakReference<>(clickListener);
            constraintLayoutCustomSongRow = itemView.findViewById(R.id.constraintLayoutCustomSongRow);
            tvSongName = itemView.findViewById(R.id.tvSongFilename);
            buttonAddSongToFavorite = itemView.findViewById(R.id.buttonAddSongToFavorite);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            buttonAddSongToFavorite.setOnClickListener(this);
        }

        @Override
        public void onClick(View clickedView) {
            clickListenerWeakReference.get().onPositionClicked(clickedView, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View clickedView) {
            clickListenerWeakReference.get().onLongPositionClicked(clickedView, getAdapterPosition());
            return true;
        }
    }
}
