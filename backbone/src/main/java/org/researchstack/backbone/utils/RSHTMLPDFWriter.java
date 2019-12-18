package org.researchstack.backbone.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.print.HtmlToPdfPrinter;
import android.print.PrintAttributes;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

public class RSHTMLPDFWriter {

    public interface PDFFileReadyCallback {
        void onPrintFileReady();
    }

    private static String SIGNED_PDF_FILENAME = "consent-signed.pdf";

    public static File getPDFFilePath(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    public static String getPDFFileName(String taskId) {
        return taskId+"_"+SIGNED_PDF_FILENAME;
    }

    public static String getPDFPath(Context context, String taskId) {
        return getPDFFilePath(context) + "/" + getPDFFileName(taskId);
    }

    protected void printPdfFile(final Activity context, final String taskId, String htmlConsentDocument, String assetsFolder,
                                final PDFFileReadyCallback callback) {

        // Create a WebView object specifically for printing
        WebView webView = new WebView(context);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                createWebPrintJob(context, view, taskId, callback);
            }
        });

        StringBuilder sb = new StringBuilder();
        sb.append("<HTML><HEAD><LINK href=\"consent.css\" type=\"text/css\" rel=\"stylesheet\"/>" +
                "<style type=\"text/css\">\n" +
                "table { page-break-inside:avoid }\n" +
                "tr { page-break-inside:avoid}\n" +
                "</style>" +
                "</HEAD><body>");
        sb.append(htmlConsentDocument);
        sb.append("</body></HTML>");
        
        String folder = "file://" + assetsFolder + "/";

        webView.loadDataWithBaseURL(folder, sb.toString(), "text/html", "utf-8", null);

    }

    private void createWebPrintJob(Context context, WebView webView, String taskId, final PDFFileReadyCallback callback) {
        String jobName = "JobDocument";
        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();

        // write document content to file
        File path = getPDFFilePath(context);
        String ouput = getPDFFileName(taskId);
        HtmlToPdfPrinter pdfPrint = new HtmlToPdfPrinter(attributes);

        HtmlToPdfPrinter.PrintReadyCallback printReadyCallback = () -> callback.onPrintFileReady();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pdfPrint.print(webView.createPrintDocumentAdapter(jobName), path, ouput, printReadyCallback);
        } else {
            pdfPrint.print(webView.createPrintDocumentAdapter(), path, ouput, printReadyCallback);
        }
    }
}
