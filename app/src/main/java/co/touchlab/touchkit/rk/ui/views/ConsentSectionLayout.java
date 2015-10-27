package co.touchlab.touchkit.rk.ui.views;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.model.ConsentSection;
import co.touchlab.touchkit.rk.ui.ViewWebDocumentActivity;

public class ConsentSectionLayout extends RelativeLayout
{
    private ViewGroup infoContainer;
    private ImageView imageView;
    private TextView titleView;
    private TextView summaryView;
    private TextView moreInfo;
    private Button next;

    public ConsentSectionLayout(Context context)
    {
        super(context);
        init();
    }

    public ConsentSectionLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ConsentSectionLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_consent_section, this, true);

        int accentColor = fetchAccentColor(getContext());

        infoContainer = (ViewGroup) findViewById(R.id.layout_consent_info_container);
        imageView = (ImageView) findViewById(R.id.layout_consent_image);
        imageView.setColorFilter(accentColor, PorterDuff.Mode.ADD);
        titleView = (TextView) findViewById(R.id.layout_consent_title);
        summaryView = (TextView) findViewById(R.id.layout_consent_summary);
        moreInfo = (TextView) findViewById(R.id.layout_consent_more_info);
        moreInfo.setTextColor(accentColor);
        next = (Button) findViewById(R.id.layout_consent_next);
    }

    public void setData(ConsentSection data, OnClickListener onClickListener)
    {

        boolean isOverview = data.getType() == ConsentSection.Type.Overview;

        LayoutParams nextParams = (LayoutParams) next.getLayoutParams();
        nextParams.addRule(BELOW, isOverview ? R.id.layout_consent_info_container : 0);
        nextParams.addRule(ALIGN_PARENT_BOTTOM, isOverview ? 0 : TRUE);

        LayoutParams infoParams = (LayoutParams) infoContainer.getLayoutParams();
        infoParams.addRule(CENTER_VERTICAL, isOverview ? TRUE : 0);

        String title = data.getTitle();
        if (TextUtils.isEmpty(title))
        {
            title = getResources().getString(data.getType().getTitleResId());
        }
        titleView.setText(title);
        summaryView.setVisibility(TextUtils.isEmpty(data.getSummary()) ? View.GONE : View.VISIBLE);
        summaryView.setText(data.getSummary());


        String imageName = !TextUtils.isEmpty(data.getCustomImageName()) ? data.getCustomImageName() :
                data.getType().getImageName();
        imageView.setVisibility(TextUtils.isEmpty(imageName) ? View.GONE : View.VISIBLE);
        if (imageView.getVisibility() == View.VISIBLE)
        {
            int imageId = getResources().getIdentifier(imageName, "drawable", getContext().getPackageName());
            imageView.setImageResource(imageId);
        }

        moreInfo.setText(data.getType().getMoreInfoResId());
        moreInfo.setOnClickListener(v -> {
            String path = data.getHtmlContent();
            String webTitle = getResources().getString(R.string.consent_section_more_info);
            Intent webDoc = ViewWebDocumentActivity.newIntent(getContext(), webTitle, path);
            getContext().startActivity(webDoc);
        });

        next.setOnClickListener(onClickListener);

//        TODO self.continueSkipContainer.continueEnabled = YES;
//        TODO [self.continueSkipContainer updateContinueAndSkipEnabled];
    }


//    TODO Make into static helper method (ViewUtils?)
    private int fetchAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

//    static NSString *localizedLearnMoreForType(ORKConsentSectionType sectionType) {
//    NSString *str = ORKLocalizedString(@"BUTTON_LEARN_MORE", nil);
//    switch (sectionType) {
//        case ORKConsentSectionTypeOverview:
//            str = ORKLocalizedString(@"LEARN_MORE_WELCOME", nil);
//            break;
//        case ORKConsentSectionTypeDataGathering:
//            str = ORKLocalizedString(@"LEARN_MORE_DATA_GATHERING", nil);
//            break;
//        case ORKConsentSectionTypePrivacy:
//            str = ORKLocalizedString(@"LEARN_MORE_PRIVACY", nil);
//            break;
//        case ORKConsentSectionTypeDataUse:
//            str = ORKLocalizedString(@"LEARN_MORE_DATA_USE", nil);
//            break;
//        case ORKConsentSectionTypeTimeCommitment:
//            str = ORKLocalizedString(@"LEARN_MORE_TIME_COMMITMENT", nil);
//            break;
//        case ORKConsentSectionTypeStudySurvey:
//            str = ORKLocalizedString(@"LEARN_MORE_STUDY_SURVEY", nil);
//            break;
//        case ORKConsentSectionTypeStudyTasks:
//            str = ORKLocalizedString(@"LEARN_MORE_TASKS", nil);
//            break;
//        case ORKConsentSectionTypeWithdrawing:
//            str = ORKLocalizedString(@"LEARN_MORE_WITHDRAWING", nil);
//            break;
//        case ORKConsentSectionTypeOnlyInDocument:
//            assert(0); // assert and fall through to custom
//        case ORKConsentSectionTypeCustom:
//            break;
//    }
//    return str;
//}
}
