package org.researchstack.bridge;
import android.os.Build;

import org.researchstack.skin.BuildConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by bradleymcdermott on 2/8/16.
 */
public class Info
{
    private List<FileInfo> files;
    private String         item;
    private String         surveyGuid;
    private String         surveyCreatedOn;
    private int    schemaRevision = 1;
    // since this buildconfig is in skin, this won't be correct
    private String appVersion     = BuildConfig.VERSION_NAME;
    private String phoneInfo      = Build.MANUFACTURER + " " + Build.MODEL;

    public Info(String item, int schemaRevision)
    {
        this.item = item;
        this.schemaRevision = schemaRevision;
        this.files = new ArrayList<>();
    }

    public Info(String surveyGuid, String surveyCreatedOn)
    {
        this.surveyGuid = surveyGuid;
        this.surveyCreatedOn = surveyCreatedOn;
        this.files = new ArrayList<>();
    }

    public void addFileInfo(FileInfo fileInfo)
    {
        files.add(fileInfo);
    }

    public String getFileName()
    {
        return UUID.randomUUID().toString() + "_" + (item == null ? surveyGuid : item) + ".zip";
    }
}
