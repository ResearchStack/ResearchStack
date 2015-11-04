package co.touchlab.touchkit.rk.ui.scene;
import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.StepResult;
import rx.functions.Action1;

/**
 * TODO Create builder object to clean up setters for title, summary, etc..
 */
public abstract class Scene extends ScrollView
{

    public static final String TAG = Scene.class.getSimpleName();

    private TextView title;
    private TextView summary;
    private TextView moreInfo;
    private TextView next;
    private TextView skip;
    private LinearLayout container;

    public Scene(Context context)
    {
        super(context);
        init();
    }

    public Scene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public Scene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(getRootLayoutResourceId(), this, true);


        View filler = findViewById(R.id.filler);

        container = (LinearLayout) findViewById(R.id.content_container);
        container.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int sceneHeight = Scene.this.getHeight();
            int infoContainerHeight = container.getHeight();

            if(sceneHeight > 0 && infoContainerHeight > 0 && sceneHeight > infoContainerHeight)
            {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        sceneHeight - infoContainerHeight);
                filler.setLayoutParams(params);
            }
        });


        title = (TextView) findViewById(R.id.title);
        summary = (TextView) findViewById(R.id.text);
        moreInfo = (TextView) findViewById(R.id.more_info);
        next = (TextView) findViewById(R.id.next);
        skip = (TextView) findViewById(R.id.skip);

        int bodyIndex = getPositionToInsertBody();
        int bodyResId = getBodyLayoutResourceId();
        if (bodyIndex >= 0 && bodyResId > 0)
        {
            View bodyView = inflater.inflate(bodyResId, container, false);
            container.addView(bodyView, bodyIndex);
        }

        onLayoutAttachedToRoot();
    }

    protected int getRootLayoutResourceId()
    {
        return R.layout.fragment_step;
    }

    protected int getBodyLayoutResourceId()
    {
        return -1;
    }

    protected void onLayoutAttachedToRoot() { }

    //TODO Not sure how i feel about this method. Part of me says "OK", another says just return an
    //TODO integer (aka magic number).
    //TODO Consult Brad.
    protected int getPositionToInsertBody()
    {
        LinearLayout stepViewContainer = (LinearLayout) findViewById(R.id.content_container);
        return stepViewContainer.indexOfChild(moreInfo) + 1;
    }

    protected abstract StepResult getResult();


    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Setters for UI TODO Create builder object to clean these methods up?
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public void setTitle(@StringRes int stringRes)
    {
        title.setText(stringRes);
    }

    public void setSummary(@StringRes int stringRes)
    {
        if (summary.getVisibility() != View.VISIBLE)
        {
            summary.setVisibility(View.VISIBLE);
        }

        summary.setText(stringRes);
    }

    public void setMoreInfo(@StringRes int stringRes, Action1<? super Object> action)
    {
        if (moreInfo.getVisibility() != View.VISIBLE)
        {
            moreInfo.setVisibility(View.VISIBLE);
        }

        moreInfo.setText(stringRes);

        if (action != null)
        {
            RxView.clicks(moreInfo).subscribe(action);
        }
    }

    public void setSkip(boolean isOptional, @StringRes int stringRes,  Action1<? super Object> action)
    {
        skip.setVisibility(isOptional ? View.VISIBLE : View.GONE);

        if (stringRes != 0)
        {
            skip.setText(stringRes);
        }

        if (action != null)
        {
            RxView.clicks(skip).subscribe(action);
        }
    }

    public static class StepSceneBuilder
    {
        private String titleText;
        private String summaryText;
        private String moreInfoText;
        private Action1 moreInfoAction;
    }

}
