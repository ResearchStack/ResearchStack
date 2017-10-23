package org.researchstack.skin.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.researchstack.backbone.utils.ResUtils;
import org.researchstack.backbone.utils.TextUtils;
import org.researchstack.backbone.utils.ThemeUtils;
import org.researchstack.skin.R;
import java.util.ArrayList;
import java.util.List;

@Deprecated // use ShareTheAppStep which loads ShareTheAppStepLayout instead
public class ShareFragment extends Fragment
{
    protected static final String FACEBOOK_SHARE_URL = "https://www.facebook.com/sharer/sharer.php?u=";
    protected static final String TWITTER_SHARE_URL = "https://twitter.com/intent/tweet?source=webclient&text=";
    protected static final String SMS_MIME_TYPE = "vnd.android-dir/mms-sms";
    protected static final String TEXT_MIME_TYPE = "text/plain";
    protected static final String SMS_BODY_KEY = "sms_body";
    protected static final String MAILTO_SCHEME = "mailto";

    public static enum Type
    {
        TWITTER,
        FACEBOOK,
        SMS,
        EMAIL
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.rss_fragment_share, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        ImageView logoView = (ImageView) view.findViewById(R.id.share_logo_view);
        // look for a logo to show, otherwise hide it
        int logoId = ResUtils.getDrawableResourceId(getActivity(), "logo_disease");
        if(logoId > 0)
        {
            logoView.setImageResource(logoId);
            logoView.setVisibility(View.VISIBLE);
        }

        RecyclerView recyclerView = (RecyclerView) view.findViewById(org.researchstack.skin.R.id.share_recycler_view);
        recyclerView.setAdapter(new ShareFragment.ShareAdapter(getContext(), loadItems()));
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

    }

    /**
     * Return a list of Share Type Item objects.
     *
     * @return list of share items to be displayed in adapter
     */
    protected List<ShareItem> loadItems() {
        List<ShareItem> items = new ArrayList<>();

        ShareItem twitter = new ShareItem(ResUtils.TWITTER_ICON, getString(R.string.rsb_share_twitter), Type.TWITTER);
        items.add(twitter);

        ShareItem facebook = new ShareItem(ResUtils.FACEBOOK_ICON, getString(R.string.rsb_share_facebook), Type.FACEBOOK);
        items.add(facebook);

        ShareItem sms = new ShareItem(ResUtils.SMS_ICON, getString(R.string.rsb_share_sms), Type.SMS);
        items.add(sms);

        ShareItem email = new ShareItem(ResUtils.EMAIL_ICON, getString(R.string.rsb_share_email), Type.EMAIL);
        items.add(email);

        return items;
    }

    private class ShareItem {
        public String icon;
        public String text;
        public Type type;

        public ShareItem(String i, String t, Type ty) {
            icon = i;
            text = t;
            type = ty;
        }
    }

    public class ShareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {

        private Context context;
        private List<ShareItem>   items;
        private LayoutInflater inflater;

        public ShareAdapter(Context ctx, List<ShareItem> itemList)
        {
            super();
            context = ctx;
            items = itemList;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = inflater.inflate(R.layout.rss_item_row_share, parent, false);
            return new ShareFragment.ShareAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder hldr, int position)
        {

            ShareFragment.ShareAdapter.ViewHolder holder = (ShareFragment.ShareAdapter.ViewHolder) hldr;
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
                switch(item.type)
                {
                    case TWITTER:
                        intent = getShareTwitterIntent(message);
                        break;
                    case FACEBOOK:
                        intent = getShareFacebookIntent(message);
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
        public int getItemCount()
        {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView title;
            ImageView icon;

            public ViewHolder(View itemView)
            {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.share_item_title);
                icon = (ImageView) itemView.findViewById(R.id.share_item_icon);
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
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.rsb_share_email_subject));
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
     *
     * @param message The message to share.
     * @return  The share intent.
     */
    protected Intent getShareFacebookIntent(String message) {
        String urlToShare = getString(R.string.rsb_share_app_url);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(TEXT_MIME_TYPE);
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

        // See if official Facebook app is found
        boolean facebookAppFound = false;
        List<ResolveInfo> matches = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches)
        {
            String facebookKey = getString(R.string.rsb_share_facebook_key);
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
