package com.example.musically.classes;

public class Song {
    private String name;
    private String artist;
    private Long durationInMilisec;
    private String path;

    public Song(String name, String artist, Long durationInMilisec, String path) {
        this.name = name;
        this.artist = artist;
        this.durationInMilisec = durationInMilisec;
        this.path = path;
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

    @Override
    public String toString() {
        return "Song{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", durationInMilisec=" + durationInMilisec +
                ", path='" + path + '\'' +
                '}';
    }
}
