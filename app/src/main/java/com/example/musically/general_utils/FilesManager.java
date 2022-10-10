package com.example.musically.general_utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.example.musically.room.song.Song;

import java.util.ArrayList;

public class FilesManager {


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayList<Song> getSongsFromDevice(Context context, ArrayList<Song> existingSongs) {
        if (PermissionsChecker.checkPermissionREAD_EXTERNAL_STORAGE(context)) {
            return getSongs(context, existingSongs);
        } else {
            return new ArrayList<>();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static ArrayList<Song> getSongs(Context context, ArrayList<Song> existingSongs) {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";
        ArrayList<Song> songs = new ArrayList<>();

        Cursor cursor = null;
        try {
            Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            cursor = context.getContentResolver().query(uri, projection, selection, null, sortOrder);
            if (cursor != null) {
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    String title = cursor.getString(0);
                    String artist = cursor.getString(1);
                    String path = cursor.getString(2);
                    Long songDurationInMilisec = Long.parseLong(cursor.getString(4));
                    cursor.moveToNext();
                    if (path != null &&
                            path.endsWith(".mp3") &&
                            songDurationInMilisec > 15 * 1000 &&
                            !songExists(existingSongs, title)) {
                        songs.add(new Song(title, artist, songDurationInMilisec, path));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return songs;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static boolean songExists(ArrayList<Song> songs, String name) {
        return songs.stream().anyMatch(s -> s.getName().equals(name));
    }
}
