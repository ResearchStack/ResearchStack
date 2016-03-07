package org.researchstack.skin.ui;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import org.researchstack.skin.R;

public class ViewVideoActivity extends AppCompatActivity
{
    public static final String KEY_URI = "VideoViewActivity.URI";

    private VideoView videoView;

    public static Intent newIntent(Context context, String videoName)
    {
        Intent intent = new Intent(context, ViewVideoActivity.class);
        intent.putExtra(KEY_URI, videoName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_video_viewer);

        String videoName = getIntent().getStringExtra(KEY_URI);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + videoName);

        videoView = (VideoView) findViewById(R.id.videoView);

        MediaController mediaController = new MediaController(this, false);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.start();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(videoView.isPlaying())
        {
            videoView.pause();
        }
    }
}
