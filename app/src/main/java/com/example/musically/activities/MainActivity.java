package com.example.musically.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musically.R;
import com.example.musically.classes.RepeatTypes;
import com.example.musically.classes.Song;
import com.example.musically.general_utils.FilesManager;
import com.example.musically.general_utils.PermissionsChecker;
import com.example.musically.recyclerview_songs.RecyclerViewClickListener;
import com.example.musically.recyclerview_songs.SongsRecyclerViewAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener {

    private static final String SHARED_PREFERENCES_KEY = "SHARED_PREF";
    private static final String LOOP_TYPE_KEY = "LOOP_TYPE";

    private RecyclerView recyclerViewSongs;
    private RecyclerView.Adapter recyclerViewSongsAdapter;
    private RecyclerView.LayoutManager recyclerViewSongsLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private TextView tvCurrentSongPlaying;

    private AppCompatButton buttonFavorites;
    private AppCompatButton buttonPlaylists;
    private TextView tvTotalSongDuration;

    private AppCompatImageButton buttonPlayPause;
    private AppCompatImageButton buttonNextSong;
    private AppCompatImageButton buttonPreviousSong;
    private TextView tvCurrentSongDuration;
    private SeekBar seekBarSongDuration;
    private TextView tvSongsVisibleTag;
    private TextView tvLoopType;

    private Handler songDurationHandler;
    private ArrayList<Song> songs;
    private MediaPlayer mediaPlayer;
    private Integer currentSongIndex;

    private RepeatTypes loopType;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
    }

    private void initializeComponents() {
        mediaPlayer = new MediaPlayer();
        songs = new ArrayList<>();
        songDurationHandler = new Handler();
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        loopType = RepeatTypes.valueOf(sharedPreferences.getString(LOOP_TYPE_KEY, RepeatTypes.REPEAT_ALL.toString()));
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
        tvCurrentSongDuration = findViewById(R.id.tvCurrentSongDuration);
        seekBarSongDuration = findViewById(R.id.seekBarSongDuration);
        tvTotalSongDuration = findViewById(R.id.tvTotalSongDuration);
        tvSongsVisibleTag = findViewById(R.id.tvSongsVisibleTag);
        tvLoopType = findViewById(R.id.tvLoopType);
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
        updateSongsRecyclerView();

        mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(getApplicationContext(), "Error. Can't play song", Toast.LENGTH_LONG).show();
            return false;
        });

        tvCurrentSongPlaying.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvCurrentSongPlaying.setSelected(true);
        tvCurrentSongPlaying.setSingleLine(true);

        buttonPlayPause.setOnClickListener(view -> {
            if (currentSongIndex != null) {
                if (mediaPlayer.isPlaying()) {
                    buttonPlayPause.setImageResource(R.drawable.play_icon);
                    mediaPlayer.pause();
                } else {
                    buttonPlayPause.setImageResource(R.drawable.pause_icon);
                    mediaPlayer.start();
                }
            } else {
                Toast.makeText(getApplicationContext(), "No song selected", Toast.LENGTH_SHORT).show();
            }
        });

        buttonNextSong.setOnClickListener(view -> {
            if (currentSongIndex == songs.size() - 1) {
                currentSongIndex = 0;
            } else {
                ++currentSongIndex;
            }
            prepareSongPlay(currentSongIndex);
            setSongDuration(currentSongIndex);
        });

        buttonPreviousSong.setOnClickListener(view -> {
            if (currentSongIndex == 0) {
                currentSongIndex = songs.size() - 1;
            } else {
                --currentSongIndex;
            }
            prepareSongPlay(currentSongIndex);
            setSongDuration(currentSongIndex);
        });

        seekBarSongDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int currentDuration, boolean changedByUser) {
                if (changedByUser) {
                    mediaPlayer.seekTo(currentDuration);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if(loopType == RepeatTypes.SHUFFLE){
            tvLoopType.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.shuffle_icon,0);
        } else if (loopType == RepeatTypes.REPEAT_CURRENT){
            tvLoopType.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.repeat_one_icon,0);
        } else {
            tvLoopType.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.repeat_icon,0);
        }

        // rep - rep one - shuffle
        tvLoopType.setOnClickListener(view -> {
            if(loopType == RepeatTypes.SHUFFLE){
                tvLoopType.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.repeat_icon,0);
                loopType = RepeatTypes.REPEAT_ALL;
            } else if (loopType == RepeatTypes.REPEAT_CURRENT){
                tvLoopType.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.shuffle_icon,0);
                loopType = RepeatTypes.SHUFFLE;
            } else {
                tvLoopType.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.repeat_one_icon,0);
                loopType = RepeatTypes.REPEAT_CURRENT;
            }
        });

    }

    private void updateSongsRecyclerView() {
        //todo image saying there's no mp3 files on the device
        ((SongsRecyclerViewAdapter) recyclerViewSongsAdapter).setSongs(songs);
        recyclerViewSongsAdapter.notifyDataSetChanged();
    }

    private void setSongDuration(int position) {
        tvTotalSongDuration.setText(getDurationAsString(songs.get(position).getDurationInMilisec()));
        seekBarSongDuration.setMax(Math.toIntExact(mediaPlayer.getDuration()));
    }

    private String getDurationAsString(long duration) {
        long minutes = (duration / 60000) % 60000;
        long seconds = duration % 60000 / 1000;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private String getDurationAsString(int position) {
        long duration = songs.get(position).getDurationInMilisec();
        long minutes = (duration / 60000) % 60000;
        long seconds = duration % 60000 / 1000;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void onPositionClicked(View clickedView, int clickedRecyclerItemPosition) {
        if (clickedView.getId() == R.id.buttonAddSongToFavorite) {
            // todo
        } else {
            prepareSongPlay(clickedRecyclerItemPosition);
        }
    }

    private void prepareSongPlay(int clickedRecyclerItemPosition) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songs.get(clickedRecyclerItemPosition).getPath());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            UpdateSongDurationSeekBar updateSongDurationSeekBar = new UpdateSongDurationSeekBar();
            songDurationHandler.post(updateSongDurationSeekBar);
            currentSongIndex = clickedRecyclerItemPosition;
            buttonPlayPause.setImageResource(R.drawable.pause_icon);
            tvCurrentSongPlaying.setText(songs.get(clickedRecyclerItemPosition).getName());
            setSongDuration(clickedRecyclerItemPosition);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error. Can't prepare song", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLongPositionClicked(View clickedView, int clickedRecyclerItemPosition) {
        //todo alertdialog
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        spEditor.putString(LOOP_TYPE_KEY, loopType.toString());
        spEditor.apply();
    }

    public class UpdateSongDurationSeekBar implements Runnable {

        @Override
        public void run() {
            seekBarSongDuration.setProgress(mediaPlayer.getCurrentPosition());
            tvCurrentSongDuration.setText(getDurationAsString((long) mediaPlayer.getCurrentPosition()));
            songDurationHandler.postDelayed(this, 1000);
        }
    }
}