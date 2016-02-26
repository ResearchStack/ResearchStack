package co.touchlab.researchstack.skin.utils;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import co.touchlab.researchstack.backbone.helpers.LogExt;
import co.touchlab.researchstack.backbone.utils.ObservableUtils;
import co.touchlab.researchstack.backbone.utils.ResUtils;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.ResourceManager;
import rx.Observable;

public class ConsentFormUtils
{
    public static void viewConsentForm(Context context)
    {
        Observable.create(subscriber -> {
            File consentFile = ConsentFormUtils.getConsentFormFileFromExternalStorage(context);
            subscriber.onNext(consentFile);
        }).compose(ObservableUtils.applyDefault()).subscribe(o -> {
            //TODO This may fail and throw an ActivityNotFoundException for type "application/pdf"
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile((File) o), "application/pdf");
            context.startActivity(intent);
        });
    }

    public static void shareConsentForm(Context context)
    {
        Observable.create(subscriber -> {
            File consentFile = ConsentFormUtils.getConsentFormFileFromExternalStorage(context);
            subscriber.onNext(consentFile);
        }).compose(ObservableUtils.applyDefault()).subscribe(o -> {
            String appName = ResUtils.getApplicationName(context);
            String emailSubject = context.getResources()
                    .getString(R.string.study_overview_email_subject, appName);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile((File) o));

            String title = context.getString(R.string.send_email);
            context.startActivity(Intent.createChooser(intent, title));
        });
    }

    /**
     * TODO Fix crash when file has been deleted
     * TODO Check if SDCard is mounted
     *
     * @return Consent form pdf
     */
    @NonNull
    public static File getConsentFormFileFromExternalStorage(Context context)
    {
        LogExt.d(ConsentFormUtils.class, "getConsentFormFileFromExternalStorage() - - - - - - ");
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        String basepath = extStorageDirectory + "/" + ResUtils.getExternalSDAppFolder();

        int fileResId = ResourceManager.getInstance().getConsentPDF();
        String formName = context.getResources().getResourceEntryName(fileResId);
        String fileName = formName + ".pdf";

        File consentFile = new File(basepath, fileName);
        LogExt.d(ConsentFormUtils.class, "File Path: " + consentFile.getAbsolutePath());

        if(! consentFile.exists())
        {
            LogExt.d(ConsentFormUtils.class, "File does not exist");

            consentFile.getParentFile().mkdirs();
            LogExt.d(ConsentFormUtils.class, "Created file directory on external storage");

            try
            {
                copy(context.getResources().openRawResource(fileResId), consentFile);
                LogExt.d(ConsentFormUtils.class, "File copied to external storage");
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return consentFile;
    }

    private static void copy(InputStream in, File dst) throws IOException
    {
        FileOutputStream out = new FileOutputStream(dst);
        byte[] buf = new byte[1024];
        int len;

        while((len = in.read(buf)) > 0)
        {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
    }
}
