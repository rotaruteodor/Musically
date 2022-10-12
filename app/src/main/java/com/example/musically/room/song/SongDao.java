package com.example.musically.room.song;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SongDao {

    @Query("SELECT * FROM songs")
    List<Song> getAll();

    @Insert
    void insertAll(Song... songs);

    @Insert
    void insertAll(List<Song> songs);

    @Query("UPDATE songs SET is_favorite=:isFavorite WHERE id = :id")
    void update(boolean isFavorite, long id);

    @Query("DELETE FROM songs")
    void deleteAll();

    @Query("DELETE FROM songs WHERE id = :id")
    void deleteSong(long id);
}
