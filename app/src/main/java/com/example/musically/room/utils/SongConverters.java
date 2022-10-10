package com.example.musically.room.utils;

import androidx.room.TypeConverter;

import com.example.musically.room.song.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SongConverters {
    @TypeConverter
    public static ArrayList<Song> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Song>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Song> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
