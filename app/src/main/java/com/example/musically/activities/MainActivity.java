package com.example.musically.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musically.R;
import com.example.musically.Song;
import com.example.musically.general_utils.FilesManager;
import com.example.musically.general_utils.PermissionsChecker;
import com.example.musically.recyclerview_files.RecyclerViewClickListener;
import com.example.musically.recyclerview_files.SongsRecyclerViewAdapter;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener {

    private RecyclerView recyclerViewSongs;
    private RecyclerView.Adapter recyclerViewSongsAdapter;
    private RecyclerView.LayoutManager recyclerViewSongsLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private TextView tvCurrentSongPlaying;

    private AppCompatButton buttonFavorites;
    private AppCompatButton buttonPlaylists;
    private AppCompatImageButton buttonPlayPause;
    private AppCompatImageButton buttonNextSong;
    private AppCompatImageButton buttonPreviousSong;

    private ArrayList<Song> songs;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
    }

    private void initializeComponents() {
        mediaPlayer = new MediaPlayer();
        songs = new ArrayList<>();
        if (PermissionsChecker.checkPermissionREAD_EXTERNAL_STORAGE(getApplicationContext())) {
            songs = FilesManager.getSongsFilenames(getApplicationContext());
        }
        recyclerViewSongs = findViewById(R.id.recyclerViewSongs);
        buttonFavorites = findViewById(R.id.buttonFavorites);
        buttonPlaylists = findViewById(R.id.buttonPlaylists);
        buttonPlayPause = findViewById(R.id.buttonPlayPause);
        buttonNextSong = findViewById(R.id.buttonNextSong);
        buttonPreviousSong = findViewById(R.id.buttonPreviousSong);
        tvCurrentSongPlaying = findViewById(R.id.tvCurrentSongPlaying);
        tvCurrentSongPlaying = findViewById(R.id.tvCurrentSongPlaying);
        dividerItemDecoration = new DividerItemDecoration(recyclerViewSongs.getContext(), DividerItemDecoration.VERTICAL);
        configureComponents();
    }

    private void configureComponents() {
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycler_view_divider));
        recyclerViewSongs.addItemDecoration(dividerItemDecoration);
        recyclerViewSongsLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewSongsAdapter = new SongsRecyclerViewAdapter(songs, this);
        recyclerViewSongs.setHasFixedSize(true);
        recyclerViewSongs.setLayoutManager(recyclerViewSongsLayoutManager);
        recyclerViewSongs.setAdapter(recyclerViewSongsAdapter);
        updateTasksRecyclerView();

        mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(getApplicationContext(), "Error. Can't play song from path", Toast.LENGTH_LONG).show();
            return false;
        });

        tvCurrentSongPlaying.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvCurrentSongPlaying.setSelected(true);
        tvCurrentSongPlaying.setSingleLine(true);

        buttonPlayPause.setOnClickListener(view -> {
            if(mediaPlayer.isPlaying()){
                buttonPlayPause.setImageResource(R.drawable.play_icon);
                mediaPlayer.pause();
            } else {
                buttonPlayPause.setImageResource(R.drawable.pause_icon);
                mediaPlayer.start();
            }
        });
    }

    private void updateTasksRecyclerView() {
        //todo image saying there's no mp3 files on the device
        ((SongsRecyclerViewAdapter) recyclerViewSongsAdapter).setSongs(songs);
        recyclerViewSongsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPositionClicked(View clickedView, int clickedRecyclerItemPosition) {
        if (clickedView.getId() == R.id.buttonAddSongToFavorite) {
            // todo
        } else {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(songs.get(clickedRecyclerItemPosition).getPath());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                buttonPlayPause.setImageResource(R.drawable.pause_icon);
                tvCurrentSongPlaying.setText(songs.get(clickedRecyclerItemPosition).getName());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error. Can't prepare song from path", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onLongPositionClicked(View clickedView, int clickedRecyclerItemPosition) {

    }

}