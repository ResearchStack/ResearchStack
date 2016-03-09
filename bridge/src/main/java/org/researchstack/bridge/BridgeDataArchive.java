package org.researchstack.bridge;
import android.util.Base64;

import com.google.gson.Gson;

import org.researchstack.backbone.helpers.LogExt;
import org.researchstack.backbone.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by bradleymcdermott on 2/11/16.
 */
public class BridgeDataArchive
{
    public static final String INFO_JSON_FILENAME = "info.json";

    Info   info;
    String filename;
    private ZipOutputStream outputStream;
    private File            tempFile;

    public BridgeDataArchive(String reference)
    {
        info = new Info(reference);
        this.filename = UUID.randomUUID().toString() + "_" + reference + ".zip";
    }

    public void start(File baseDir) throws FileNotFoundException
    {
        tempFile = new File(baseDir, filename + ".temp");
        FileOutputStream dest = new FileOutputStream(tempFile);
        outputStream = new ZipOutputStream(new BufferedOutputStream(dest));
    }

    public UploadRequest finishAndEncrypt(int publicKeyId, File baseDir) throws IOException
    {
        try
        {
            // all sage bridge file uploads need an info.json describing the files in the zip
            addZipEntry(INFO_JSON_FILENAME,
                    new ByteArrayInputStream(new Gson().toJson(info).getBytes()));
        }
        finally
        {
            if(outputStream != null)
            {
                outputStream.close();
            }
        }

        LogExt.e(getClass(), "SAVED temp file: " + tempFile.getAbsolutePath());

        try
        {
            // TODO turn this back on when we include spongyCastle
            //            InputStream encryptedInputStream = getEncryptedInputStream(context,
            //                    tempFile,
            //                    publicKeyId);
            InputStream encryptedInputStream = new FileInputStream(tempFile);

            File encryptedFile = new File(baseDir, filename);

            // might as well get the md5 hash while we're doing this
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            DigestInputStream inputStream = new DigestInputStream(encryptedInputStream, md5);

            FileUtils.copy(inputStream, encryptedFile);

            LogExt.e(getClass(), "SAVED Encrypted: " + encryptedFile.getAbsolutePath());

            String md5Hash = Base64.encodeToString(md5.digest(), Base64.NO_WRAP);

            return new UploadRequest(encryptedFile.getName(),
                    encryptedFile.length(),
                    md5Hash,
                    "application/zip");
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new RuntimeException("MD5 hashing not supported on this device", e);
        }
        finally
        {
            if(tempFile != null && tempFile.exists())
            {
                tempFile.delete();
                LogExt.e(getClass(), "DELETED TEMP FILE");
            }
        }
    }


    public void addFile(String filename, byte[] data, String date) throws IOException
    {
        addFile(filename, new ByteArrayInputStream(data), date);
    }


    public void addFile(String filename, InputStream data, String date) throws IOException
    {
        info.addFileInfo(new FileInfo(filename, date));
        addZipEntry(filename, data);
    }


    private void addZipEntry(String filename, InputStream in) throws IOException
    {
        ZipEntry entryOne = new ZipEntry(filename);

        BufferedInputStream origin = null;

        try
        {

            outputStream.putNextEntry(entryOne);

            origin = new BufferedInputStream(in);
            byte[] bytes = new byte[1024];

            int read1;
            while((read1 = origin.read(bytes)) != - 1)
            {
                outputStream.write(bytes, 0, read1);
            }
        }
        finally
        {
            if(origin != null)
            {
                origin.close();
            }

        }
    }

    static
    {
        //        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }
    //
    //    @NonNull
    //    private InputStream getEncryptedInputStream(Context context, File tempFile, int publicKeyId)
    //    {
    //        // Creating a CMS encrypted input stream that only recipients can decrypt
    //        CMSEnvelopedDataGenerator gen = new CMSEnvelopedDataGenerator();
    //
    //        // Load bridge public key certificate from R.raw and add to recipients list
    //        try
    //        {
    //            CertificateFactory factory = new CertificateFactory();
    //            InputStream keyInputStream = context.getResources().openRawResource(publicKeyId);
    //            X509Certificate cert = (X509Certificate) factory.engineGenerateCertificate(
    //                    keyInputStream);
    //            JceKeyTransRecipientInfoGenerator recipientInfoGenerator = new JceKeyTransRecipientInfoGenerator(
    //                    cert).setProvider("SC");
    //            gen.addRecipientInfoGenerator(recipientInfoGenerator);
    //
    //            // Generate encrypted input stream in AES-256-CBC format, output is DER, not S/MIME or PEM
    //            CMSProcessableFile content = new CMSProcessableFile(tempFile);
    //            OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES256_CBC).setProvider(
    //                    "SC").build();
    //            CMSEnvelopedData envelopedData = gen.generate(content, encryptor);
    //            return new BufferedInputStream(new ByteArrayInputStream(envelopedData.getEncoded()));
    //        }
    //        catch(CertificateException | IOException | CMSException e)
    //        {
    //            throw new RuntimeException("Error encrypting with CMS", e);
    //        }
    //    }
}
