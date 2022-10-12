package com.example.musically.room.playlist;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.musically.room.song.Song;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface PlaylistDao {

    @Insert
    void insertAll(Playlist... playlists);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Playlist playlist);

    @Insert
    void insertAll(List<Playlist> playlists);

    @Query("SELECT * FROM playlists")
    List<Playlist> getAll();

    @Query("UPDATE playlists SET songs=:songs WHERE id = :playlistId")
    void updateSongs(long playlistId, ArrayList<Song> songs);

    @Query("DELETE FROM playlists")
    void deleteAll();

}
