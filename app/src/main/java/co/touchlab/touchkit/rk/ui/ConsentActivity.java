package co.touchlab.touchkit.rk.ui;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import co.touchlab.touchkit.rk.R;

public class ConsentActivity extends AppCompatActivity
{
    public static final String CONSENT_RESULT = "CONSENT_RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);

        findViewById(R.id.disagree).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        findViewById(R.id.agree).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onAgree();
            }
        });
    }

    private void onAgree()
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(CONSENT_RESULT, true);
        setResult(RESULT_OK,
                resultIntent);
        finish();
    }

    public static Intent newIntent(Context context)
    {
        return new Intent(context, ConsentActivity.class);
    }
}
