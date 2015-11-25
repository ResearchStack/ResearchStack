package co.touchlab.researchstack.ui;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.R;
import co.touchlab.researchstack.ResearchStackApplication;
import co.touchlab.researchstack.common.Constants;

public class PassCodeActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{

    /**
     * Used to set a boolean value to ignore the pass-code when the Activity is initially created.
     * This will becomes useful when launching starting between an activity that does not and an
     * activity that does extend from {@link PassCodeActivity}
     */
    public static final String KEY_PASSCODE_IGNORE_FIRST_RUN = "PassCodeActivity.KEY_IGNORE_PASSCODE_FIRST";

    /**
     * Used to record the time when the activity has been paused. The variable is static to handle
     * cases when traveling between two Activities of type {@link PassCodeActivity}. When this is
     * the case, the lastPauseTime will be set by the caller activity. Whe the callee activity
     * accesses the value, not enough time will have passed to trigger showing the pass-code
     *
     * TODO figure our if we need to saveState for this variable -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
     * * If its static then that complicates things, but its useful when the activity in consumed by
     * * the system. Perhaps all we need to do is check if savedState.lastPauseTime >
     * * PassCodeActivity.lastPauseTime. If it is, set it. If not then ignore. Investigate Further
     *
     */
    private static long lastPauseTime;

    /**
     * The minimum time the activity needs to wait before showing the pass-code screen.
     */
    private long minTimeToIgnorePassCode;


    /**
     * Field ref to our pass-code layout.
     * TODO Use {@link co.touchlab.researchstack.ui.scene.SignUpPasscodeScene} instead of inflating from XML
     * TODO Make sure layout background is set to Style.windowBackgroundColor
     */
    private View passCodeLayout;


    /**
     * We check if the activity should ignore passcode on first run. This will become useful
     * when launching from an activity that does not extend from {@link PassCodeActivity}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        minTimeToIgnorePassCode = getMinTimeToIgnorePassCode(preferences);

        if (getIntent() != null)
        {
            if (getIntent().hasExtra(KEY_PASSCODE_IGNORE_FIRST_RUN))
            {
                boolean ignorePassCodeOnFirstRun = getIntent()
                        .getBooleanExtra(KEY_PASSCODE_IGNORE_FIRST_RUN, false);
                if(ignorePassCodeOnFirstRun)
                {
                    lastPauseTime = System.currentTimeMillis();
                    getIntent().removeExtra(KEY_PASSCODE_IGNORE_FIRST_RUN);
                }
            }

        }

    }

    private long getMinTimeToIgnorePassCode(SharedPreferences preferences)
    {
        String defaultAutoLock = getString(R.string.auto_lock_default);
        String stringValue = preferences.getString(Constants.KEY_SETTINGS_AUTO_LOCK,
                                                   defaultAutoLock);
        int minutes = Integer.parseInt(stringValue);
        return minutes * DateUtils.MINUTE_IN_MILLIS;
    }

    /**
     * We add our passCodeLayout on top of our UI via {@link WindowManager} if passcode exists
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        ResearchStackApplication application = ResearchStackApplication.getInstance();

        //TODO Passcode should not be saved anywhere. Just marking this so that we dont forget to change.
        if (application.getCurrentUser() != null && !TextUtils.isEmpty(application.getCurrentUser().getPasscode()))
        {
            //TODO Set setBackgroundColor to windowBackgroundColor
            passCodeLayout = getLayoutInflater().inflate(R.layout.item_passcode, null, false);
            passCodeLayout.setBackgroundColor(Color.WHITE);
            passCodeLayout.setVisibility(View.GONE);

            EditText editText = (EditText) passCodeLayout.findViewById(R.id.passcode);
            RxTextView.textChanges(editText).subscribe(s -> {
                String passcode = application.getCurrentUser().getPasscode();
                if(! TextUtils.isEmpty(s) && passcode.equals(s.toString()))
                {
                    passCodeLayout.setVisibility(View.GONE);
                    editText.setText("");
                }
            });

            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            getWindowManager().addView(passCodeLayout, params);
        }
    }

    /**
     * If the elapsed time from the {@link #lastPauseTime} to current time is greater than
     * {@link #minTimeToIgnorePassCode}, show PassCode Layout.
     */
    @Override
    protected void onResume()
    {
        if (passCodeLayout != null)
        {
            long now = System.currentTimeMillis();
            boolean isPastMinIgnoreTime = now - lastPauseTime > minTimeToIgnorePassCode;

            if(isPastMinIgnoreTime && passCodeLayout.getVisibility() != View.VISIBLE)
            {
                passCodeLayout.setVisibility(View.VISIBLE);
            }
        }

        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        lastPauseTime = System.currentTimeMillis();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key)
    {
        if(Constants.KEY_SETTINGS_AUTO_LOCK.equals(key))
        {
            minTimeToIgnorePassCode = getMinTimeToIgnorePassCode(preferences);
        }
    }
}
