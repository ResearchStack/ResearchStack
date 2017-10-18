package org.researchstack.backbone.result.logger;

import org.researchstack.backbone.utils.LogExt;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Created by TheMDP on 2/6/17.
 *
 * The DataLogger class can be used to stream data to a file on another thread
 * It should only be used when the data may become too large to store in app memory
 */

public class DataLogger {

    protected static final String UTF_8 = "UTF-8";

    /**
     * The file that the data is being written to
     */
    private File file;

    /**
     * The Thread that performs the file writing
     */
    private DataLoggerFileWriterThread dataLoggerWriterThread;
    private DataWriteListener dataWriteListener;

    public DataLogger(File file, DataWriteListener listener) {
        this.file = file;
        this.dataWriteListener = listener;
    }

    /**
     * @param fileHeader the header of the file to write
     * @param fileFooter the footer of the file to write
     */
    public void start(String fileHeader, String fileFooter) {
        if (dataLoggerWriterThread != null) {
            throw new IllegalStateException("Thread was started while another was running, " +
                    "check your application logic, because this is not allowed");
        }

        dataLoggerWriterThread = new DataLoggerFileWriterThread(
                file, fileHeader, fileFooter,
                new DataWriteListener()
        {
            @Override
            public void onWriteError(Throwable throwable) {
                dataLoggerFailed(throwable);
            }

            @Override
            public void onWriteComplete(File file) {
                DataLoggerManager.getInstance().dataLoggerTaskFinished(DataLogger.this, null);
                dataWriteListener.onWriteComplete(file);
                dataLoggerWriterThread = null;
            }
        });

        DataLoggerManager.getInstance().startNewDataLoggerTask(this);
        dataLoggerWriterThread.start();
    }

    /**
     * Call when you are done writing to the data logger
     */
    public void stop() {
        if (dataLoggerWriterThread == null) {
            throw new IllegalStateException("You need to call start() first");
        }
        dataLoggerWriterThread.stop();
    }

    /**
     * Call when you want the data logger file to immediately stop and the file deleted
     */
    public void cancel() {
        if (dataLoggerWriterThread != null) {
            dataLoggerWriterThread.cancel();
        }
    }

    /**
     * Cancels the data logger because of an error that happened with a class using this class
     * @param throwable the error to return to the listener, that happened above this class
     */
    public void cancelDueToError(Throwable throwable) {
        dataLoggerWriterThread.cancel();
        dataLoggerFailed(throwable);
    }

    private void dataLoggerFailed(Throwable throwable) {
        LogExt.e(getClass(), "Data logger failed " +
                throwable.getLocalizedMessage() + throwable.getStackTrace().toString());
        DataLoggerManager.getInstance().dataLoggerTaskFinished(DataLogger.this, throwable);
        dataWriteListener.onWriteError(throwable);
        dataLoggerWriterThread = null;
    }

    /**
     * @param data to append to the file on a different async task thread
     */
    public void appendData(byte[] data) {
        if (dataLoggerWriterThread != null) {
            dataLoggerWriterThread.appendData(data);
        }
    }

    /**
     * @param data string byte data to append to the file on a different async task thread
     */
    public void appendData(String data) {
        try {
            appendData(data.getBytes(UTF_8));
        } catch (UnsupportedEncodingException e) {
            dataWriteListener.onWriteError(e);
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public interface DataWriteListener {
        void onWriteError(Throwable throwable);
        void onWriteComplete(File file);
    }
}
