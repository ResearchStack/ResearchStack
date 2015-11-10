package co.touchlab.touchkit.rk.ui.scene;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.ui.fragment.StepFragment;
import rx.functions.Action1;

public abstract class Scene extends RelativeLayout
{

    public static final String TAG = Scene.class.getSimpleName();

    private ImageView image;
    private TextView title;
    private TextView summary;
    private TextView moreInfo;
    private TextView next;
    private TextView skip;
    private LinearLayout container;
    private Step step;
    private StepFragment.StepCallbacks callbacks;

    public Scene(Context context)
    {
        super(context);
        initScene();
    }

    public Scene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initScene();
    }

    public Scene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initScene();
    }

    protected void initScene()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View scene = onCreateScene(inflater, this);
        onSceneCreated(scene);
    }

    public View onCreateScene(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(getRootLayoutResourceId(), parent, true);
    }

    public void onSceneCreated(View scene)
    {
        View filler = findViewById(R.id.filler);

        container = (LinearLayout) findViewById(R.id.content_container);
        container.getViewTreeObserver().addOnPreDrawListener(() -> {
            int sceneHeight = Scene.this.getHeight();
            int infoContainerHeight = container.getHeight();

            if(sceneHeight > 0 && infoContainerHeight > 0 && sceneHeight > infoContainerHeight)
            {
                int fillerHeight = sceneHeight - infoContainerHeight;
                if (fillerHeight >= 0 && fillerHeight != filler.getHeight())
                {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fillerHeight);
                    filler.setLayoutParams(params);
                    return false;
                }
            }

            return true;

        });

        image = (ImageView) findViewById(R.id.image);
        title = (TextView) findViewById(R.id.title);
        summary = (TextView) findViewById(R.id.text);
        moreInfo = (TextView) findViewById(R.id.more_info);
        next = (TextView) findViewById(R.id.next);
        next.setOnClickListener(v -> onNextClicked());
        skip = (TextView) findViewById(R.id.skip);

        initBody();
    }

    public void onNextClicked()
    {
        if (isAnswerValid())
        {
            if (callbacks != null && step != null)
            {
                callbacks.onNextPressed(step);
            }
        }
    }

    private void initBody()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        int bodyResId = getBodyLayoutResourceId();
        if (bodyResId > 0)
        {
            View body = onCreateBody(inflater, bodyResId, container);

            int bodyIndex = getPositionToInsertBody();
            container.addView(body, bodyIndex);

            onBodyCreated(body);
        }
    }

    public View onCreateBody(LayoutInflater inflater, int bodyResId, ViewGroup parent)
    {
        return inflater.inflate(bodyResId, parent, false);
    }

    public void onBodyCreated(View body) { }

    protected int getRootLayoutResourceId()
    {
        return R.layout.fragment_step;
    }

    protected int getBodyLayoutResourceId()
    {
        return -1;
    }

    //TODO Not sure how i feel about this method. Part of me says "OK", another says just return an
    //TODO integer (aka magic number).
    //TODO Consult Brad.
    protected int getPositionToInsertBody()
    {
        LinearLayout stepViewContainer = (LinearLayout) findViewById(R.id.content_container);
        return stepViewContainer.indexOfChild(moreInfo) + 1;
    }

    public abstract StepResult getResult();


    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Setters for UI
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public void setImage(@DrawableRes int drawableResid)
    {
        if (image.getVisibility() != View.VISIBLE)
        {
            image.setVisibility(View.VISIBLE);
        }

        image.setImageResource(drawableResid);
    }

    public ImageView getImageView()
    {
        return image;
    }

    public void setTitle(@StringRes int stringRes)
    {
        String title = getResources().getString(stringRes);
        setTitle(title);
    }

    public void setTitle(String string)
    {
        title.setText(string);
    }

    public void setSummary(@StringRes int stringRes)
    {
        String summary = getResources().getString(stringRes);
        setSummary(summary);
    }

    public void setSummary(String string)
    {
        if (summary.getVisibility() != View.VISIBLE)
        {
            summary.setVisibility(View.VISIBLE);
        }

        summary.setText(string);
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

    public TextView getMoreInfo()
    {
        return moreInfo;
    }

    public void setSkip(boolean isOptional)
    {
        setSkip(isOptional, 0, null);
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

    public void setNextButtonText(@StringRes int stringRes)
    {
        String string = getResources().getString(stringRes);
        setNextButtonText(string);
    }

    public void setNextButtonText(String string)
    {
        next.setText(string);
    }

    /**
     * @return true to call through to Fragment and start next scene.
     */
    public boolean isAnswerValid()
    {
        return true;
    }


    public StepFragment.StepCallbacks getCallbacks()
    {
        return callbacks;
    }

    public void setCallbacks(Step step, StepFragment.StepCallbacks callbacks)
    {
        this.step = step;
        this.callbacks = callbacks;
    }

    public static class StepSceneBuilder
    {
        private String titleText;
        private String summaryText;
        private String moreInfoText;
        private Action1 moreInfoAction;
    }

}
