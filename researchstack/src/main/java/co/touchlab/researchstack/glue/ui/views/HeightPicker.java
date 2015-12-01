package co.touchlab.researchstack.glue.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.glue.R;
import rx.Observable;

/**
 * Created by bradleymcdermott on 11/17/15.
 */
public class HeightPicker extends LinearLayout
{
    private AppCompatEditText feet;
    private AppCompatEditText inches;
    private Observable<Integer> changes;

    public HeightPicker(Context context)
    {
        super(context);
    }

    public HeightPicker(Context context, AttributeSet attrs)
    {
        super(context,
                attrs);
    }

    public HeightPicker(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context,
                attrs,
                defStyleAttr);
    }

    @TargetApi(21)
    public HeightPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context,
                attrs,
                defStyleAttr,
                defStyleRes);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        feet = (AppCompatEditText) findViewById(R.id.height_feet);
        inches = (AppCompatEditText) findViewById(R.id.height_inches);

        changes = Observable.merge(RxTextView.textChanges(feet),
                RxTextView.textChanges(inches))
                .map(charSequence -> getUserHeight())
                .filter(totalInches -> totalInches > 0);
    }

    public void setUserHeight(int totalInches)
    {
        if (totalInches <= 0)
        {
            return;
        }

        feet.setText(String.valueOf(totalInches / 12));
        inches.setText(String.valueOf(totalInches % 12));
    }

    public int getUserHeight()
    {

        String feetString = feet.getText()
                .toString();
        String inchesString = inches.getText()
                .toString();

        if (feetString.isEmpty() || inchesString.isEmpty())
        {
            return 0;
        }

        return Integer.valueOf(feetString) * 12 +
                Integer.valueOf(inchesString);
    }

    public Observable<Integer> getObservable()
    {
        return changes;
    }

}
