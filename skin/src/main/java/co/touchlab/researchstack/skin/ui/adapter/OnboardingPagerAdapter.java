package co.touchlab.researchstack.skin.ui.adapter;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import co.touchlab.researchstack.backbone.ui.LocalImageGetter;
import co.touchlab.researchstack.backbone.utils.ResUtils;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.model.StudyOverviewModel;
import co.touchlab.researchstack.skin.ui.ViewVideoActivity;
import co.touchlab.researchstack.skin.ui.views.StudyLandingLayout;


public class OnboardingPagerAdapter extends PagerAdapter
{
    private final List<StudyOverviewModel.Question> items;
    private final LayoutInflater                    inflater;

    public OnboardingPagerAdapter(Context context, List<StudyOverviewModel.Question> items)
    {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return items.size();
    }

    /**
     * TODO Clean this code up -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
     * Each case is the same block of code. Figure out what the layout-id needs to be, then upcast
     * to the super class that the layouts each share. If the layouts cant extend from the same parent
     * class, maybe have each implement an interface?
     * <p>
     * interface {
     * public getView()
     * public setFormSteps(Data d)
     * }
     * <p>
     * To discuss.
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        StudyOverviewModel.Question item = items.get(position);

        if(position == 0)
        {
            //            APCStudyLandingCollectionViewCell *landingCell = (APCStudyLandingCollectionViewCell *)[collectionView dequeueReusableCellWithReuseIdentifier:kAPCStudyLandingCollectionViewCellIdentifier forIndexPath:indexPath];
            //            landingCell.delegate = self;
            //            landingCell.titleLabel.text = studyDetails.caption;
            //            landingCell.subTitleLabel.text = studyDetails.detailText;
            //            landingCell.readConsentButton.hidden = YES;
            //            landingCell.emailConsentButton.hidden = [((APCAppDelegate *)[UIApplication sharedApplication].delegate) hideEmailOnWelcomeScreen];
            //
            //            if ([MFMailComposeViewController canSendMail]) {
            //            [landingCell.emailConsentButton setTitleColor:[UIColor appPrimaryColor] forState:UIControlStateNormal];
            //            [landingCell.emailConsentButton setUserInteractionEnabled:YES];
            //        }else{
            //            [landingCell.emailConsentButton setTitleColor:[UIColor grayColor] forState:UIControlStateNormal];
            //            [landingCell.emailConsentButton setUserInteractionEnabled:NO];
            //        }
            //
            //        if (studyDetails.showsConsent) {
            //            landingCell.readConsentButton.hidden = NO;
            //        }

            StudyLandingLayout layout = new StudyLandingLayout(container.getContext());
            layout.setData(item);
            container.addView(layout);
            return layout;

        }
        else if(! TextUtils.isEmpty(item.getVideoName()))
        {
            View layout = inflater.inflate(R.layout.layout_study_html, container, false);
            container.addView(layout);

            StringBuilder builder = new StringBuilder("<h1>" + item.getTitle() + "</h1>");
            builder.append("<p>" + item.getDetails() + "</p>");

            TextView simpleView = (TextView) layout.findViewById(R.id.text);
            simpleView.setText(Html.fromHtml(builder.toString()));
            simpleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.video_icon);
            simpleView.setOnClickListener(v -> {
                Intent intent = ViewVideoActivity.newIntent(container.getContext(), item.getVideoName());
                container.getContext().startActivity(intent);
            });

            return layout;


            //            APCStudyVideoCollectionViewCell *videoCell = (APCStudyVideoCollectionViewCell *)[collectionView dequeueReusableCellWithReuseIdentifier:kAPCStudyVideoCollectionViewCellIdentifier forIndexPath:indexPath];
            //            videoCell.delegate = self;
            //            videoCell.titleLabel.text = studyDetails.caption;
            //            videoCell.videoMessageLabel.text = studyDetails.detailText;

        }
        else
        {
            View layout = inflater.inflate(R.layout.layout_study_html, container, false);
            container.addView(layout);

            TextView simpleView = (TextView) layout.findViewById(R.id.text);
            simpleView.setMovementMethod(LinkMovementMethod.getInstance());
            int id =  ResUtils.getRawResourceId(container.getContext(), item.getDetails());
            String html = getHtmlText(container.getContext(), id);
            LocalImageGetter imageGetter = new LocalImageGetter(simpleView);
            simpleView.setText(Html.fromHtml(html, imageGetter, null));

            return layout;

            //            NSString *filePath = [[NSBundle mainBundle] pathForResource: studyDetails.detailText ofType:@"html" inDirectory:@"HTMLContent"];
            //            NSURL *targetURL = [NSURL URLWithString:filePath];
            //            NSURLRequest *request = [NSURLRequest requestWithURL:targetURL];
            //            [webViewCell.webView loadRequest:request];

        }
    }

    private String getHtmlText(Context context, int id)
    {
        InputStream inputStream = context.getResources().openRawResource(id);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        try
        {
            i = inputStream.read();
            while(i != - 1)
            {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toString();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

}
