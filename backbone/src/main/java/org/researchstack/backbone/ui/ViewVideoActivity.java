package org.researchstack.backbone.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.views.AssetVideoView;

import java.io.IOException;


public class ViewVideoActivity extends AppCompatActivity {
    public static final String KEY_RELATIVE_PATH = "VideoViewActivity.URI";

    private AssetVideoView videoView;

    public static Intent newIntent(Context context, String relativeVideoPath) {
        Intent intent = new Intent(context, ViewVideoActivity.class);
        intent.putExtra(KEY_RELATIVE_PATH, relativeVideoPath);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.rsb_activity_video_viewer);

        videoView = (AssetVideoView) findViewById(R.id.videoView);

        MediaController mediaController = new MediaController(this, false);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        try {
            String videoPath = getIntent().getStringExtra(KEY_RELATIVE_PATH);
            AssetFileDescriptor afd = getAssets().openFd(videoPath);
            videoView.setVideoDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            videoView.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }
}
