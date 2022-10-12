package com.example.musically.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musically.R;
import com.example.musically.enums.PlaylistAction;
import com.example.musically.enums.RepeatTypes;
import com.example.musically.room.playlist.Playlist;
import com.example.musically.room.song.Song;
import com.example.musically.room.MusicallyDatabase;
import com.example.musically.general_utils.FilesManager;
import com.example.musically.recyclerview_songs.RecyclerViewClickListener;
import com.example.musically.recyclerview_songs.SongsRecyclerViewAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener {

    private static final String SHARED_PREFERENCES_KEY = "SHARED_PREF";
    private static final String LOOP_TYPE_KEY = "LOOP_TYPE";

    private RecyclerView recyclerViewSongs;
    private RecyclerView.Adapter recyclerViewSongsAdapter;
    private RecyclerView.LayoutManager recyclerViewSongsLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private TextView tvCurrentSongPlaying;

    private AppCompatButton buttonAllSongs;
    private AppCompatButton buttonFavorites;
    private AppCompatButton buttonPlaylists;
    private TextView tvTotalSongDuration;

    private AppCompatImageButton buttonPlayPause;
    private AppCompatImageButton buttonNextSong;
    private AppCompatImageButton buttonPreviousSong;
    private TextView tvCurrentSongDuration;
    private SeekBar seekBarSongDuration;

    private EditText editTextSearchSongs;
    private AppCompatImageButton buttonSearchSongs;
    private AppCompatImageButton buttonRefreshSongsList;
    private TextView tvLoopType;

    private Handler songDurationHandler;
    private ArrayList<Song> playingSongs;
    private ArrayList<Song> allSongs;
    private ArrayList<Song> visibleSongs;
    private ArrayList<Song> searchedSongs;
    private ArrayList<Playlist> playlists;
    private MediaPlayer mediaPlayer;
    private Integer currentSongIndex;
    private AlertDialog.Builder songOptionsDialog;
    private AlertDialog.Builder selectPlaylistDialog;
    private AlertDialog addPlaylistDialog;
    private EditText editTextAddPlaylistName;
    private TextView tvFinishAddingPlaylist;
    private Playlist currentPlaylist;
    private RepeatTypes loopType;
    private SharedPreferences sharedPreferences;
    private MusicallyDatabase database;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializeComponents() {
        initializeVariables();
        findViews();
        initializeGraphicalComponents();
        configureComponents();
    }

    private void initializeVariables() {
        database = MusicallyDatabase.getInstance(getApplicationContext());
        mediaPlayer = new MediaPlayer();
        songDurationHandler = new Handler();
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        loopType = RepeatTypes.valueOf(sharedPreferences.getString(LOOP_TYPE_KEY, RepeatTypes.REPEAT_ALL.toString()));
        allSongs = new ArrayList<>(database.songDao().getAll());
        playingSongs = allSongs;
        visibleSongs = allSongs;
        searchedSongs = new ArrayList<>();
        playlists = new ArrayList<>(database.playlistDao().getAll());
        currentPlaylist = null;
    }

    private void findViews() {
        recyclerViewSongs = findViewById(R.id.recyclerViewSongs);
        buttonAllSongs = findViewById(R.id.buttonAllSongs);
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
        editTextSearchSongs = findViewById(R.id.editTextSearchSongs);
        buttonSearchSongs = findViewById(R.id.buttonSearchSongs);
        buttonRefreshSongsList = findViewById(R.id.buttonRefreshSongsList);
        tvLoopType = findViewById(R.id.tvLoopType);
    }

    private void initializeGraphicalComponents() {
        dividerItemDecoration = new DividerItemDecoration(recyclerViewSongs.getContext(), DividerItemDecoration.VERTICAL);
        recyclerViewSongsLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewSongsAdapter = new SongsRecyclerViewAdapter(visibleSongs, this);
        songOptionsDialog = new AlertDialog.Builder(this).setTitle("Options");
        selectPlaylistDialog = new AlertDialog.Builder(this).setTitle("Playlists");
        View addPlaylistDialogView = this.getLayoutInflater()
                .inflate(R.layout.add_playlist_dialog_layout, null);
        addPlaylistDialog = new AlertDialog.Builder(this)
                .setView(addPlaylistDialogView)
                .create();
        editTextAddPlaylistName = addPlaylistDialogView.findViewById(R.id.editTextAddPlaylistName);
        tvFinishAddingPlaylist = addPlaylistDialogView.findViewById(R.id.tvFinishAddingPlaylist);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void configureComponents() {
        configureSongsRecyclerView();
        configureMediaPlayer();
        configureCurrentSongPlayingTextView();
        configurePlayPauseButton();
        configureNextSongButton();
        configurePreviousSongButton();
        configureSongDurationSeekBar();
        configureLoopType();
        configureSeeAllSongsButton();
        configureSeeFavoriteSongsButton();
        configureSeePlaylistsButton();
        configureRefreshSongsListButton();
        configureSearchSongsEditText();
        configureFinishAddingPlaylistButton();
    }

    private void configureFinishAddingPlaylistButton() {
        tvFinishAddingPlaylist.setOnClickListener(view1 -> {
            if (editTextAddPlaylistName.getText().length() >= 3) {
                Playlist newPlaylist = new Playlist(editTextAddPlaylistName.getText().toString());
                long newPlaylistId = database.playlistDao().insert(newPlaylist);
                newPlaylist.setId(newPlaylistId);
                playlists.add(newPlaylist);

                addPlaylistDialog.dismiss();
                editTextAddPlaylistName.getText().clear();
            }
        });
    }

    private void configureSearchSongsEditText() {
        editTextSearchSongs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    searchedSongs = visibleSongs.stream()
                            .filter(s -> s.getName().toLowerCase(Locale.ROOT).contains(charSequence.toString().toLowerCase(Locale.ROOT)))
                            .collect(Collectors.toCollection(ArrayList::new));
                    updateSongsRecyclerView(searchedSongs);
                } else {
                    searchedSongs = new ArrayList<>();
                    updateSongsRecyclerView(visibleSongs);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void configureRefreshSongsListButton() {
        buttonRefreshSongsList.setOnClickListener(view -> {
            ArrayList<Song> newSongs = FilesManager.getSongsFromDevice(this, allSongs);
            allSongs.addAll(newSongs);
            database.songDao().insertAll(newSongs);
            updateSongsRecyclerView(visibleSongs);
            String toastMessage = "Successfully updated songs list!\nNr. of new songs found: "
                    .concat(String.valueOf(newSongs.size()));

            Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void configureSeePlaylistsButton() {
        buttonPlaylists.setOnClickListener(view -> {
            displayPlaylistsDialog(null, PlaylistAction.SEE_SONGS);
        });

        buttonPlaylists.setOnLongClickListener(view -> {
            addPlaylistDialog.show();
            addPlaylistDialog.getWindow()
                    .setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            return true;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void configureSeeFavoriteSongsButton() {
        buttonFavorites.setOnClickListener(view -> {
            visibleSongs = (ArrayList<Song>) allSongs.stream()
                    .filter(Song::isFavorite)
                    .collect(Collectors.toList());

            updateSongsRecyclerView(visibleSongs);
            currentPlaylist = null;
        });
    }

    private void configureSeeAllSongsButton() {
        buttonAllSongs.setOnClickListener(view -> {
            visibleSongs = allSongs;
            updateSongsRecyclerView(visibleSongs);
            currentPlaylist = null;
        });
    }

    private void configureLoopType() {
        if (loopType == RepeatTypes.SHUFFLE) {
            tvLoopType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.shuffle_icon, 0);
        } else if (loopType == RepeatTypes.REPEAT_CURRENT) {
            tvLoopType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.repeat_one_icon, 0);
        } else {
            tvLoopType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.repeat_icon, 0);
        }

        // repeat -> repeat current -> shuffle
        tvLoopType.setOnClickListener(view -> {
            if (loopType == RepeatTypes.SHUFFLE) {
                tvLoopType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.repeat_icon, 0);
                loopType = RepeatTypes.REPEAT_ALL;
            } else if (loopType == RepeatTypes.REPEAT_CURRENT) {
                tvLoopType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.shuffle_icon, 0);
                loopType = RepeatTypes.SHUFFLE;
            } else {
                tvLoopType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.repeat_one_icon, 0);
                loopType = RepeatTypes.REPEAT_CURRENT;
            }
        });
    }

    private void configureSongDurationSeekBar() {
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
    }

    private void configurePreviousSongButton() {
        buttonPreviousSong.setOnClickListener(view -> {
            if (currentSongIndex == null) {
                currentSongIndex = 0;
            } else {
                if (currentSongIndex == 0) {
                    currentSongIndex = playingSongs.size() - 1;
                } else {
                    --currentSongIndex;
                }
            }
            prepareSongPlay(currentSongIndex);
            setSongDuration(currentSongIndex);
        });
    }

    private void configureNextSongButton() {
        buttonNextSong.setOnClickListener(view -> {
            if (currentSongIndex == null) {
                currentSongIndex = 0;
            } else {
                if (currentSongIndex == playingSongs.size() - 1) {
                    currentSongIndex = 0;
                } else {
                    ++currentSongIndex;
                }
            }
            prepareSongPlay(currentSongIndex);
            setSongDuration(currentSongIndex);
        });
    }

    private void configurePlayPauseButton() {
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
                currentSongIndex = 0;
                prepareSongPlay(currentSongIndex);
                setSongDuration(currentSongIndex);
            }
        });
    }

    private void configureCurrentSongPlayingTextView() {
        tvCurrentSongPlaying.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvCurrentSongPlaying.setSelected(true);
        tvCurrentSongPlaying.setSingleLine(true);
    }

    private void configureMediaPlayer() {
        mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(getApplicationContext(), "Error. Can't play song", Toast.LENGTH_LONG).show();
            return false;
        });

        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            if (loopType == RepeatTypes.SHUFFLE) {
                Integer songIndex = currentSongIndex;
                if (playingSongs.size() > 1) {
                    Random rd = new Random();
                    songIndex = rd.nextInt(playingSongs.size());

                    while (songIndex.equals(currentSongIndex)) {
                        songIndex = rd.nextInt(playingSongs.size());
                        // todo this for nextbutton also?
                    }
                }
                prepareSongPlay(songIndex);
            } else if (loopType == RepeatTypes.REPEAT_ALL) {
                if (currentSongIndex == playingSongs.size() - 1) {
                    currentSongIndex = 0;
                } else {
                    ++currentSongIndex;
                }
                prepareSongPlay(currentSongIndex);
            } else {
                prepareSongPlay(currentSongIndex);
            }
        });
    }

    private void configureSongsRecyclerView() {
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.recycler_view_divider));
        recyclerViewSongs.addItemDecoration(dividerItemDecoration);
        recyclerViewSongs.setHasFixedSize(true);
        recyclerViewSongs.setLayoutManager(recyclerViewSongsLayoutManager);
        recyclerViewSongs.setAdapter(recyclerViewSongsAdapter);
        updateSongsRecyclerView(visibleSongs);
    }

    private void updateSongsRecyclerView(ArrayList<Song> songs) {
        //todo image saying there's no mp3 files on the device?
        ((SongsRecyclerViewAdapter) recyclerViewSongsAdapter).setSongs(songs);
        recyclerViewSongsAdapter.notifyDataSetChanged();
    }

    private void setSongDuration(int position) {
        tvTotalSongDuration.setText(getDurationAsString(playingSongs.get(position).getDurationInMilisec()));
        seekBarSongDuration.setMax(Math.toIntExact(mediaPlayer.getDuration()));
    }

    private String getDurationAsString(long duration) {
        long minutes = (duration / 60000) % 60000;
        long seconds = duration % 60000 / 1000;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private String getDurationAsString(int position) {
        long duration = playingSongs.get(position).getDurationInMilisec();
        long minutes = (duration / 60000) % 60000;
        long seconds = duration % 60000 / 1000;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void prepareSongPlay(int position) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(playingSongs.get(position).getPath());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            UpdateSongDurationSeekBar updateSongDurationSeekBar = new UpdateSongDurationSeekBar();
            songDurationHandler.post(updateSongDurationSeekBar);
            currentSongIndex = position;
            buttonPlayPause.setImageResource(R.drawable.pause_icon);
            tvCurrentSongPlaying.setText(playingSongs.get(position).getName());
            setSongDuration(position);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error. Can't prepare song", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPositionClicked(View clickedView, int position) {
        if (clickedView.getId() == R.id.buttonAddSongToFavorite) {
            if (!visibleSongs.get(position).isFavorite()) {
                makeSongFavorite(clickedView, position);
            } else {
                deleteSongFromFavorites(clickedView, position);
            }
        } else {
            if (searchedSongs.size() > 0) {
                playingSongs = searchedSongs;
            } else {
                playingSongs = visibleSongs;
            }

            prepareSongPlay(position);
        }
    }

    private void deleteSongFromFavorites(View clickedView, int position) {
        visibleSongs.get(position).setFavorite(false);
        database.songDao().update(false, visibleSongs.get(position).getId());
        ((AppCompatImageButton) clickedView.findViewById(R.id.buttonAddSongToFavorite))
                .setImageResource(R.drawable.heart_icon_shallow_24);
    }

    private void makeSongFavorite(View clickedView, int position) {
        visibleSongs.get(position).setFavorite(true);
        database.songDao().update(true, visibleSongs.get(position).getId());
        ((AppCompatImageButton) clickedView.findViewById(R.id.buttonAddSongToFavorite))
                .setImageResource(R.drawable.heart_icon_filled_24);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displaySongOptionsDialog(Song song) {
        int optionsArrayId = currentPlaylist == null ? R.array.generalSongOptions : R.array.playlistSongOptions;
        songOptionsDialog.setItems(optionsArrayId, (dialogInterface, i) -> {
                    if (i == 0) { // add/delete from playlist
                        if (currentPlaylist == null) {
                            displayPlaylistsDialog(song, PlaylistAction.ADD);
                        } else {
                            removeSongFromPlaylist(song);
                        }

                    } else if (i == 1) { // delete
                        allSongs.remove(song);
                        database.songDao().deleteSong(song.getId());
                        updateSongsRecyclerView(visibleSongs);
                    }
                })
                .create().show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayPlaylistsDialog(Song song, PlaylistAction desiredAction) {
        if (playlists.size() == 0) {
            Toast.makeText(getApplicationContext(),
                    "No playlists yet! Long click playlists button to add one",
                    Toast.LENGTH_LONG).show();
        } else {
            CharSequence[] playlistsNames = playlists.stream()
                    .map(Playlist::getName)
                    .collect(Collectors.toList())
                    .toArray(new CharSequence[playlists.size()]);

            final PlaylistAction action = desiredAction;
            selectPlaylistDialog.setItems(playlistsNames, (dialogInterface, i) -> {
                        if (action == PlaylistAction.ADD) {
                            addSongToPlaylist(song, i);
                        } else if (action == PlaylistAction.SEE_SONGS) {
                            currentPlaylist = playlists.get(i);
                            visibleSongs = playlists.get(i).getSongs();
                            updateSongsRecyclerView(visibleSongs);
                        }
                    })
                    .create().show();
        }
    }

    private void removeSongFromPlaylist(Song song) {
        currentPlaylist.removeSong(song);
        database.playlistDao().insert(currentPlaylist);
        updateSongsRecyclerView(visibleSongs);
    }

    private void addSongToPlaylist(Song song, int i) {
        playlists.get(i).addSong(song);
        database.playlistDao().insert(playlists.get(i));
        Toast.makeText(getApplicationContext(),
                song.getName().concat(" added to playlist!"),
                Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onLongPositionClicked(View clickedView, int clickedRecyclerItemPosition) {
        displaySongOptionsDialog(visibleSongs.get(clickedRecyclerItemPosition));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            for (Playlist playlist : playlists) {
                Log.e("PLAYLISTS:", playlist.getSongs().size() + " - " + playlist);
            }
            Log.e("SONGS:", allSongs.size() + " - " + allSongs);
        }
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