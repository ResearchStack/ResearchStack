package co.touchlab.touchkit.rk.ui.views;
import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.List;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.answerformat.AnswerFormat;
import rx.functions.Action1;

/**
 * TODO Extract shared elements into {@link StepLayout} and have FormStepLayout extend from it
 */
public class FormStepLayout extends RelativeLayout
{

    private TextView title;
    private TextView text;
    private TextView moreInfo;
    private TextView next;
    private TextView skip;

    public FormStepLayout(Context context)
    {
        super(context);
        init();
    }

    public FormStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public FormStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.fragment_step, this, true);

        title = (TextView) findViewById(R.id.title);
        text = (TextView) findViewById(R.id.text);
        moreInfo = (TextView) findViewById(R.id.more_info);
        next = (TextView) findViewById(R.id.next);
        skip = (TextView) findViewById(R.id.skip);

//        LinearLayout stepViewContainer = (LinearLayout) findViewById(R.id.step_view_container);
//        View bodyView = getBodyView(inflater, stepViewContainer);
//        stepViewContainer.addView(bodyView, getBodyIndex());
    }

    public void setFormItems(List<FormItem> items)
    {
        LinearLayout stepViewContainer = (LinearLayout) findViewById(R.id.step_view_container);
        int startIndex = getBodyIndex();
        for(int i = 0; i < items.size(); i++)
        {
            FormItem item = items.get(i);

            View formItem =  LayoutInflater.from(getContext())
                    .inflate(R.layout.fragment_step_form, this, false);

            TextView label = (TextView) formItem.findViewById(R.id.text);
            label.setText(item.text);

            EditText value = (EditText) formItem.findViewById(R.id.value);
            value.setHint(item.placeholder);

            stepViewContainer.addView(formItem, startIndex + i);
        }
    }

    //TODO Not sure how i feel about this method. Part of me says "OK", another says just return an
    //TODO integer (aka magic number).
    //TODO Consult Brad.
    public int getBodyIndex()
    {
        LinearLayout stepViewContainer = (LinearLayout) findViewById(R.id.step_view_container);
        return stepViewContainer.indexOfChild(moreInfo) + 1;
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Setters for UI TODO Create builder object to clean these methods up?
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public void setTitle(@StringRes int stringRes)
    {
        title.setText(stringRes);
    }

    public void setText(@StringRes int stringRes)
    {
        if (text.getVisibility() != View.VISIBLE)
        {
            text.setVisibility(View.VISIBLE);
        }

        text.setText(stringRes);
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

//    public void setNext(@StringRes int stringRes, Action1<? super Object> action)
//    {
//        if (next.getVisibility() != View.VISIBLE)
//        {
//            next.setVisibility(View.VISIBLE);
//        }
//
//        next.setText(stringRes);
//
//        if (action != null)
//        {
//            RxView.clicks(next).subscribe(action);
//        }
//    }

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

    public static class FormItem
    {
        private final String givenNameIdentifier;
        private final String text;
        private final AnswerFormat format;
        private final String placeholder;

        public FormItem(String givenNameIdentifier, String text, AnswerFormat format, String placeholder)
        {

            this.givenNameIdentifier = givenNameIdentifier;
            this.text = text;
            this.format = format;
            this.placeholder = placeholder;
        }
    }

}
