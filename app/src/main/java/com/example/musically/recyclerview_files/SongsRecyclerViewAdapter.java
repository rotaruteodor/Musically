package com.example.musically.recyclerview_files;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musically.R;
import com.example.musically.Song;
// todo extract background colors from customMp3Row and mainActivity
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SongsRecyclerViewAdapter extends RecyclerView.Adapter<SongsRecyclerViewAdapter.Mp3FilesViewHolder>{

    ArrayList<Song> songs;
    RecyclerViewClickListener clickListener;

    public SongsRecyclerViewAdapter(ArrayList<Song> songs, RecyclerViewClickListener clickListener) {
        this.songs = songs;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public Mp3FilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_song_recyclerview_row, parent, false);
        return new Mp3FilesViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Mp3FilesViewHolder holder, int position) {
        holder.tvMp3Filename.setText(songs.get(position).getName());
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

    public static class Mp3FilesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private WeakReference<RecyclerViewClickListener> clickListenerWeakReference;
        private final ConstraintLayout constraintLayoutCustomMp3FileRow;
        private final TextView tvMp3Filename;
        private final ImageButton buttonAddSongToFavorite;

        public Mp3FilesViewHolder(@NonNull View itemView, RecyclerViewClickListener clickListener) {
            super(itemView);
            clickListenerWeakReference = new WeakReference<>(clickListener);
            constraintLayoutCustomMp3FileRow = itemView.findViewById(R.id.constraintLayoutCustomMp3FileRow);
            tvMp3Filename = itemView.findViewById(R.id.tvSongFilename);
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
