package org.researchstack.skin;
import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.utils.LogExt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public abstract class ResourceManager extends ResourcePathManager
{

    public static ResourceManager getInstance()
    {
        return (ResourceManager) ResourcePathManager.getInstance();
    }

    public abstract Resource getStudyOverview();

    public abstract Resource getConsentHtml();

    public abstract Resource getConsentPDF();

    public abstract Resource getConsentSections();

    public abstract Resource getLearnSections();

    public abstract Resource getPrivacyPolicy();

    public abstract Resource getSoftwareNotices();

    public abstract Resource getTasksAndSchedules();

    public abstract Resource getTask(String taskFileName);

}
