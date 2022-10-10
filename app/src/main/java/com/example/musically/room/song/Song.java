package com.example.musically.room.song;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "songs")
public class Song {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "artist")
    private String artist;

    @ColumnInfo(name = "durationInMilisec")
    private Long durationInMilisec;

    @ColumnInfo(name = "path")
    private String path;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;

    public Song(String name, String artist, Long durationInMilisec, String path) {
        this.name = name;
        this.artist = artist;
        this.durationInMilisec = durationInMilisec;
        this.path = path;
        isFavorite = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Long getDurationInMilisec() {
        return durationInMilisec;
    }

    public void setDurationInMilisec(Long durationInMilisec) {
        this.durationInMilisec = durationInMilisec;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Song)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return this.path == ((Song) obj).path;
    }

    @Override
    public int hashCode() {
        return path.length();
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", durationInMilisec=" + durationInMilisec +
                ", path='" + path + '\'' +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
