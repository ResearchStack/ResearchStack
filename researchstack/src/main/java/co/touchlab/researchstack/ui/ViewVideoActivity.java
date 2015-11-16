package co.touchlab.researchstack.ui;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

import co.touchlab.researchstack.R;

public class ViewVideoActivity extends AppCompatActivity
{

    public static final String KEY_URI = "VideoViewActivity.URI";

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

        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setVideoURI(uri);
        videoView.start();
    }
}
