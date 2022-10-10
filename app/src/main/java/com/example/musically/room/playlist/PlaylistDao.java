package com.example.musically.room.playlist;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlaylistDao {

    @Insert
    void insertAll(Playlist... playlists);

    @Insert
    void insertAll(List<Playlist> playlists);

    @Query("SELECT * FROM playlists")
    List<Playlist> getAll();
}
