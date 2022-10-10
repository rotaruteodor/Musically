package com.example.musically.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.musically.room.playlist.Playlist;
import com.example.musically.room.song.Song;
import com.example.musically.room.playlist.PlaylistDao;
import com.example.musically.room.song.SongDao;
import com.example.musically.room.utils.SongConverters;

@Database(entities = {Song.class, Playlist.class}, version = 3)
@TypeConverters({SongConverters.class})
public abstract class MusicallyDatabase extends RoomDatabase {

    public abstract SongDao songDao();
    public abstract PlaylistDao playlistDao();

    private static MusicallyDatabase musicallyDatabase;

    public static MusicallyDatabase getInstance(Context context){
        if(musicallyDatabase == null){
            musicallyDatabase = Room.databaseBuilder(context,
                    MusicallyDatabase.class,
                    "Musically_Database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return musicallyDatabase;
    }
}
