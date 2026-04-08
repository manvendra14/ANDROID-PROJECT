package com.example.audiovideo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button openFile, openURL, play, pause, stop, restart;
    VideoView videoView;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openFile = findViewById(R.id.openFile);
        openURL = findViewById(R.id.openURL);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        stop = findViewById(R.id.stop);
        restart = findViewById(R.id.restart);
        videoView = findViewById(R.id.videoView);

        // 🎵 Load Audio
        openFile.setOnClickListener(v -> {
            mediaPlayer = MediaPlayer.create(this, R.raw.sample);
            Toast.makeText(this, "Audio Loaded", Toast.LENGTH_SHORT).show();
        });

        // 🎬 Load Video from URL
        openURL.setOnClickListener(v -> {
            String url = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4";
            videoView.setVideoURI(Uri.parse(url));
            Toast.makeText(this, "Video Loaded", Toast.LENGTH_SHORT).show();
        });

        // ▶ Play
        play.setOnClickListener(v -> {
            if (mediaPlayer != null) mediaPlayer.start();
            videoView.start();
        });

        // ⏸ Pause
        pause.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
            if (videoView.isPlaying()) videoView.pause();
        });

        // ⏹ Stop
        stop.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            videoView.stopPlayback();
        });

        // 🔄 Restart
        restart.setOnClickListener(v -> {
            if (mediaPlayer != null) mediaPlayer.seekTo(0);
            videoView.seekTo(0);
        });
    }
}