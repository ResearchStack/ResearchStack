package org.researchstack.backbone.result.logger;

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
     * The AsyncTask that performs the file writing on another thread
     */
    private DataLoggerAsyncTask dataLoggerAsyncTask;
    private DataWriteListener dataWriteListener;

    public DataLogger(File file, DataWriteListener listener) {
        this.file = file;
        this.dataWriteListener = listener;
    }

    /**
     * @param fileHeader the header of the file to write
     * @param fileFooter the footer of the file to write
     * @param estimatedDataWriteFrequency the estimated frequency in Hz that you will be writing data
     *                                    this is used to give the thread a break from checking
     *                                    if there is data to be written to the file stream
     */
    public void start(String fileHeader, String fileFooter, double estimatedDataWriteFrequency) {
        if (dataLoggerAsyncTask != null) {
            throw new IllegalStateException("Thread was started while another was running, " +
                    "check your application logic, because this is not allowed");
        }

        dataLoggerAsyncTask = new DataLoggerAsyncTask(
                file, fileHeader, fileFooter,
                estimatedDataWriteFrequency, new DataWriteListener()
        {
            @Override
            public void onWriteError(Throwable throwable) {
                dataLoggerFailed(throwable);
            }

            @Override
            public void onWriteComplete() {
                DataLoggerManager.getInstance().dataLoggerTaskFinished(DataLogger.this, null);
                dataWriteListener.onWriteComplete();
                dataLoggerAsyncTask = null;
            }
        });

        DataLoggerManager.getInstance().startNewDataLoggerTask(this, dataLoggerAsyncTask);
    }

    /**
     * Call when you are done writing to the data logger
     */
    public void stop() {
        if (dataLoggerAsyncTask == null) {
            throw new IllegalStateException("You need to call start() first");
        }
        dataLoggerAsyncTask.stop();
    }

    /**
     * Cancels the data logger because of an error that happened with a class using this class
     * @param throwable the error to return to the listener, that happened above this class
     */
    public void cancelDueToError(Throwable throwable) {
        dataLoggerAsyncTask.cancel(true);
        dataLoggerFailed(throwable);
    }

    private void dataLoggerFailed(Throwable throwable) {
        DataLoggerManager.getInstance().dataLoggerTaskFinished(DataLogger.this, throwable);
        dataWriteListener.onWriteError(throwable);
        dataLoggerAsyncTask = null;
    }

    /**
     * @param data to append to the file on a different async task thread
     */
    public void appendData(byte[] data) {
        if (dataLoggerAsyncTask != null) {
            dataLoggerAsyncTask.appendData(data);
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
        void onWriteComplete();
    }
}
