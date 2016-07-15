package org.researchstack.skin.ui.adapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.researchstack.backbone.ui.ViewVideoActivity;
import org.researchstack.backbone.ui.views.LocalWebView;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ResUtils;
import org.researchstack.backbone.utils.TextUtils;
import org.researchstack.skin.R;
import org.researchstack.skin.ResourceManager;
import org.researchstack.skin.model.StudyOverviewModel;
import org.researchstack.skin.utils.AssetsProvider;
import org.w3c.dom.Text;

import java.io.File;
import java.util.List;


public class OnboardingPagerAdapter extends PagerAdapter
{
    public static final String TAG = "OnboardingPagerAdapter";
    private final List<StudyOverviewModel.Question> items;
    private final LayoutInflater                    inflater;
    private Context context;

    public OnboardingPagerAdapter(Context context, List<StudyOverviewModel.Question> items)
    {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        Log.i(TAG, "context found: " + context.getPackageName());
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return items.get(position).getTitle();
    }

    @Override
    public int getCount()
    {
        return items.size();
    }

    /**
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

        if(! TextUtils.isEmpty(item.getVideoName()))
        {
            View layout = inflater.inflate(R.layout.rss_layout_study_html, container, false);
            container.addView(layout);

            StringBuilder builder = new StringBuilder("<h3>" + item.getTitle() + "</h3>");
            builder.append("<p>" + item.getDetails() + "</p>");

            TextView simpleView = (TextView) layout.findViewById(R.id.text);
            simpleView.setText(Html.fromHtml(builder.toString()));
            simpleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rss_ic_video);
            simpleView.setOnClickListener(v -> {
                String videoPath = ResourceManager.getInstance()
                        .generatePath(ResourceManager.Resource.TYPE_MP4, item.getVideoName());
                Intent intent = ViewVideoActivity.newIntent(container.getContext(), videoPath);
                container.getContext().startActivity(intent);
            });

            return layout;
        }
        else if (item.getDetails().equals("CONSENT TAG"))
        {
            return formatConsentPage(container, item);
        }
        else
        {
            LocalWebView layout = new LocalWebView(container.getContext());
            String path = ResourceManager.getInstance().generateAbsolutePath(
                    ResourceManager.Resource.TYPE_HTML, item.getDetails());
            layout.loadUrl(path);
            container.addView(layout);
            return layout;
        }
    }

    @NonNull
    private Object formatConsentPage(ViewGroup container, StudyOverviewModel.Question item) {
        View layout = inflater.inflate(R.layout.rss_layout_consent_onboarding, container, false);
        container.addView(layout);

        TextView text = (TextView) layout.findViewById(R.id.consent_text);
        text.setText(item.getConsentText());

        Button emailButton = (Button) layout.findViewById(R.id.email_consent);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("application/pdf");
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Consent Form");

                File externalCopy;
                try {
                    externalCopy = ResourceManager.getInstance().saveResourceToExternalStorage(context, ResourceManager.getInstance().getConsentPDF());
                    Uri uri = Uri.fromFile(externalCopy);
                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                } catch (Exception e) {
                    LogExt.e(this.getClass(), "Could not write resource to external storage");
                    e.printStackTrace();
                }

                context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
                Log.i(TAG, "onClick: Sending consent via email");
            }
        });

        Button viewButton = (Button) layout.findViewById(R.id.view_consent);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                File externalCopy;
                try{
                    externalCopy = ResourceManager.getInstance().saveResourceToExternalStorage(context, ResourceManager.getInstance().getConsentPDF());
                    Uri uri = Uri.fromFile(externalCopy);
                    viewIntent.setDataAndType(uri, "application/pdf");
                } catch (Exception e) {
                    LogExt.e(this.getClass(), "Could not write resource to external storage");
                    e.printStackTrace();
                }

                context.startActivity(Intent.createChooser(viewIntent, "Open PDF..."));
                Log.i(TAG, "onClick: Showing consent in pdf viewer");

            }
        });
        return layout;
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
