package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.ShareTheAppStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.AlertFrameLayout;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.researchstack.backbone.utils.ResUtils;
import org.researchstack.backbone.utils.TextUtils;
import org.researchstack.backbone.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by TheMDP on 1/26/17.
 */

public class ShareTheAppStepLayout extends FixedSubmitBarLayout implements StepLayout {

    protected static final String FACEBOOK_SHARE_URL = "https://www.facebook.com/sharer/sharer.php?u=";
    protected static final String TWITTER_SHARE_URL = "https://twitter.com/intent/tweet?source=webclient&text=";
    protected static final String SMS_MIME_TYPE = "vnd.android-dir/mms-sms";
    protected static final String TEXT_MIME_TYPE = "text/plain";
    protected static final String SMS_BODY_KEY = "sms_body";
    protected static final String MAILTO_SCHEME = "mailto";

    protected TextView titleTextView;
    protected TextView textTextView;
    protected RecyclerView recyclerView;

    protected ShareTheAppStep step;
    protected StepCallbacks callbacks;

    public ShareTheAppStepLayout(Context context) {
        super(context);
    }

    public ShareTheAppStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShareTheAppStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public ShareTheAppStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getContentResourceId() {
        return R.layout.rsb_step_layout_share_the_app;
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateStep(step);

        titleTextView = (TextView)contentContainer.findViewById(R.id.rsb_share_title_view);
        titleTextView.setText(step.getTitle());
        textTextView  = (TextView)contentContainer.findViewById(R.id.rsb_share_view_text);
        textTextView.setText(step.getText());

        ImageView logoView = (ImageView)contentContainer.findViewById(R.id.rsb_share_logo_view);
        // look for a logo to show, otherwise hide it
        int logoId = ResUtils.getDrawableResourceId(getContext(), ResUtils.LOGO_DISEASE);
        if(logoId > 0) {
            logoView.setImageResource(logoId);
            logoView.setVisibility(View.VISIBLE);
        } else {
            logoView.setVisibility(View.GONE);
        }

        recyclerView = (RecyclerView)contentContainer.findViewById(R.id.rsb_share_recycler_view);
        recyclerView.setAdapter(new ShareAdapter(getContext(), loadItems()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        submitBar.setPositiveTitle(R.string.rsb_done);
        submitBar.setPositiveAction(o -> { callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, null); });
        submitBar.getNegativeActionView().setVisibility(View.GONE);
    }

    protected void validateStep(Step step) {
        if (!(step instanceof ShareTheAppStep)) {
            throw new IllegalStateException("ShareTheAppStepLayout only works with ShareTheAppStep");
        }
        this.step = (ShareTheAppStep)step;
    }

    @Override
    public View getLayout() {
        return this;
    }

    @Override
    public boolean isBackEventConsumed() {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, null);
        return true;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    protected List<ShareItem> loadItems() {
        List<ShareItem> items = new ArrayList<>();

        ShareItem twitter = new ShareItem(ResUtils.TWITTER_ICON,
                getContext().getString(R.string.rsb_share_twitter), ShareTheAppStep.ShareType.TWITTER);
        items.add(twitter);

        ShareItem facebook = new ShareItem(ResUtils.FACEBOOK_ICON,
                getContext().getString(R.string.rsb_share_facebook), ShareTheAppStep.ShareType.FACEBOOK);
        items.add(facebook);

        ShareItem sms = new ShareItem(ResUtils.SMS_ICON,
                getContext().getString(R.string.rsb_share_sms), ShareTheAppStep.ShareType.SMS);
        items.add(sms);

        ShareItem email = new ShareItem(ResUtils.EMAIL_ICON,
                getContext().getString(R.string.rsb_share_email), ShareTheAppStep.ShareType.EMAIL);
        items.add(email);

        return items;
    }

    private class ShareItem {
        public String icon;
        public String text;
        public ShareTheAppStep.ShareType type;

        private ShareItem(String i, String t, ShareTheAppStep.ShareType ty) {
            icon = i;
            text = t;
            type = ty;
        }
    }

    public class ShareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private List<ShareItem> items;
        private LayoutInflater inflater;

        private ShareAdapter(Context ctx, List<ShareItem> itemList) {
            super();
            context = ctx;
            items = itemList;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.rsb_row_icon_text, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder hldr, int position) {

            ViewHolder holder = (ViewHolder) hldr;
            ShareItem item = items.get(position);

            holder.title.setText(item.text);

            int resId = ResUtils.getDrawableResourceId(context, item.icon);
            holder.icon.setImageResource(resId);

            // use accent color for the icons
            int colorId = ThemeUtils.getAccentColor(context);
            holder.icon.setColorFilter(colorId, PorterDuff.Mode.SRC_IN);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = null;
                String message = context.getString(R.string.rsb_share_the_app_message);
                switch(item.type) {
                    case TWITTER:
                        intent = getShareTwitterIntent(message);
                        break;
                    case FACEBOOK:
                        intent = getShareFacebookIntent();
                        break;
                    case SMS:
                        intent = getShareSmsIntent(message);
                        break;
                    case EMAIL:
                        intent = getShareEmailIntent(message);
                        break;
                }

                v.getContext().startActivity(intent);

            });

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            ImageView icon;

            private ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.rsb_share_item_title);
                icon = (ImageView) itemView.findViewById(R.id.rsb_share_item_icon);
            }
        }
    }

    /**
     * Return an Intent for sharing by email.
     *
     * @param message The message to share.
     * @return  The share intent.
     */
    protected Intent getShareEmailIntent(String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                MAILTO_SCHEME, "", null));
        intent.putExtra(Intent.EXTRA_SUBJECT, getContext().getString(R.string.rsb_share_email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, message);
        return intent;
    }

    /**
     * Return an intent for sharing by SMS.
     *
     * @param message The message to share.
     * @return  The share intent.
     */
    protected Intent getShareSmsIntent(String message) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType(SMS_MIME_TYPE);
        intent.putExtra(SMS_BODY_KEY,message);
        return intent;
    }

    /**
     * Return an Intent for sharing by Twitter.
     *
     * @param message The message to share.
     * @return  The share intent.
     */
    protected Intent getShareTwitterIntent(String message) {
        String url = TWITTER_SHARE_URL + TextUtils.urlEncode(message);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        return intent;
    }

    /**
     * Return an intent for sharing by Facebook.
     * Facebook API does not let you post a message to share, user must type it themselves
     * @return  The share intent.
     */
    protected Intent getShareFacebookIntent() {
        String urlToShare = getContext().getString(R.string.rsb_share_app_url);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(TEXT_MIME_TYPE);
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

        // See if official Facebook app is found
        boolean facebookAppFound = false;
        List<ResolveInfo> matches = getContext().getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches)
        {
            String facebookKey = getContext().getString(R.string.rsb_share_facebook_key);
            if (info.activityInfo.packageName.toLowerCase().contains(facebookKey) ||
                    info.activityInfo.name.toLowerCase().contains(facebookKey))
            {
                intent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }

        // As fallback, launch sharer.php in a browser
        if (!facebookAppFound)
        {
            String sharerUrl = FACEBOOK_SHARE_URL + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }

        return intent;
    }
}
