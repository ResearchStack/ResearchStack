package org.researchstack.skin.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.utils.ResUtils;
import org.researchstack.skin.R;
import org.researchstack.skin.ResourceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import rx.Observable;

public class ConsentFormUtils {

    private ConsentFormUtils() {
    }

    public static void viewConsentForm(Context context) {
        String path = ResourceManager.getInstance().getConsentHtml().getAbsolutePath();
        String title = context.getString(R.string.rsb_consent);
        Intent intent = ViewWebDocumentActivity.newIntentForPath(context, title, path);
        context.startActivity(intent);
    }

    @Deprecated
    public static void shareConsentForm(Context context) {
        Observable.create(subscriber -> {
            File consentFile = ConsentFormUtils.getConsentFormFileFromExternalStorage(context);
            subscriber.onNext(consentFile);
        }).compose(ObservableUtils.applyDefault()).subscribe(o -> {
            int stringId = context.getApplicationInfo().labelRes;
            String appName = context.getString(stringId);
            String emailSubject = context.getResources()
                    .getString(R.string.rss_study_overview_email_subject, appName);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile((File) o));

            String title = context.getString(R.string.rss_send_email);
            context.startActivity(Intent.createChooser(intent, title));
        });
    }

    /**
     * @return Consent form pdf
     */
    @NonNull
    @Deprecated
    public static File getConsentFormFileFromExternalStorage(Context context) {
        LogExt.d(ConsentFormUtils.class, "getConsentFormFileFromExternalStorage() - - - - - - ");
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        String basepath = extStorageDirectory + "/" + ResUtils.getExternalSDAppFolder();

        ResourceManager.Resource consentPdf = ResourceManager.getInstance().getConsentPDF();
        String fileName = consentPdf.getName() + "." + consentPdf.getFileExtension();

        File consentFile = new File(basepath, fileName);
        LogExt.d(ConsentFormUtils.class, "File Path: " + consentFile.getAbsolutePath());

        if (!consentFile.exists()) {
            LogExt.d(ConsentFormUtils.class, "File does not exist");

            consentFile.getParentFile().mkdirs();
            LogExt.d(ConsentFormUtils.class, "Created file directory on external storage");

            try {
                copy(ResourceManager.getInstance().getConsentHtml().open(context), consentFile);
                LogExt.d(ConsentFormUtils.class, "File copied to external storage");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return consentFile;
    }

    @Deprecated
    private static void copy(InputStream in, File dst) throws IOException {
        FileOutputStream out = new FileOutputStream(dst);
        byte[] buf = new byte[1024];
        int len;

        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
    }
}
