package org.researchstack.skin.utils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Anita on 6/21/2016.
 */
/**
 * http://stackoverflow.com/questions/5888718/android-share-images-from-assets-folder
 */
public class AssetsProvider extends ContentProvider {

    public static final String PROVIDER_PATH = "content://org.researchstack.skin.utils/";
    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
        AssetManager am = getContext().getAssets();
        String file_name = uri.getLastPathSegment();
        if (file_name == null)
            throw new FileNotFoundException();
        AssetFileDescriptor afd = null;
        try {
            afd = am.openFd(file_name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return afd;//super.openAssetFile(uri, mode);
    }

    @Override
    public String getType(Uri p1) {
        // TODO: Implement this method
        return null;
    }

    @Override
    public int delete(Uri p1, String p2, String[] p3) {
        // TODO: Implement this method
        return 0;
    }

    @Override
    public Cursor query(Uri p1, String[] p2, String p3, String[] p4, String p5) {
        // TODO: Implement this method
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
        // TODO: Implement this method
        return super.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
    }

    @Override
    public Uri insert(Uri p1, ContentValues p2) {
        // TODO: Implement this method
        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this method
        return false;
    }

    @Override
    public int update(Uri p1, ContentValues p2, String p3, String[] p4) {
        // TODO: Implement this method
        return 0;
    }
}