package projects.android.triviaapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Per Open Trivia DB: The API appends a "Response Code" to each API Call to help tell developers what the API is doing.

 Code 0: Success Returned results successfully.
 Code 1: No Results Could not return results. The API doesn't have enough questions for your query. (Ex. Asking for 50 Questions in a Category that only has 20.)
 Code 2: Invalid Parameter Contains an invalid parameter. Arguments passed in aren't valid. (Ex. Amount = Five)
 Code 3: Token Not Found Session Token does not exist.
 Code 4: Token Empty Session Token has returned all possible questions for the specified query. Resetting the Token is necessary.
 */

enum DownloadStatus {
    IDLE,
    NOT_INITIALIZED,
    PROCESSING,
    OK,
    FAILED_OR_EMPTY
}

/**
 * Class: GetTriviaData
 *
 * With this class, we download the Data returned by the link given
 *
 *  AsyncTask<String, Void, String>
 *      String: Http link
 *      Void: no progress to show
 *      String: return data
 */
public class GetTriviaData extends AsyncTask<String, Void, String> {
    private static final String TAG = "GetTriviaData";

    private DownloadStatus downloadStatus;
    private final OnDownloadComplete callback;

    /**
     * interface: OnDownloadComplete()
     * This interface is to be implemented by classes using the GetTriviaData AsyncTask
     * We use this interface in order to communicate with implementing classes
     * that our work is done.
     */
    interface OnDownloadComplete{
        void onDownloadComplete(String data, DownloadStatus status);
    }

    /**
     * Constructor: GetTriviaData
     * @param callback
     */
    public GetTriviaData(OnDownloadComplete callback) {
        this.downloadStatus = DownloadStatus.IDLE;
        this.callback = callback;
    }


    /**
     * Function: runInSameThread()
     * Doing what would have happened if we called the AsynchTasks Execute method
     * when you call the Asynch method of an Async task, it creates a new thread
     * and runs the do in background method. When that completes the onpost execute method
     * is called on the main thread. So we do the same thing without creating a background thread
     * call doInBackground and pass the return value from that to onPostExecute
     *
     * We removed the super call in on post execute, normally should not call mthods that invoke super from
     * your own code, when they are designed to be used as callbacks for another class
     * if it had a super that did anything then there woul dbe strange behavior
     *
     */
    void runInSameThread(String s){
        Log.d(TAG, "runInSameThread: Begin");
        if(callback!=null){
            String result = doInBackground(s);
            callback.onDownloadComplete(result, downloadStatus);
        }
        Log.d(TAG, "runInSameThread: End");

    }

    /**
     * Method: onPostExecute()
     * @param s
     */
    @Override
    protected void onPostExecute(String s) {
        if(callback != null){
            callback.onDownloadComplete(s, downloadStatus);
        }
        Log.d(TAG, "OnPostExecute: ends");

    }

    /**
     * Method: doInBackground()
     * In this method, we open an HTTP connection to the given link
     * We check if the link is well formed, if we have a connection, and if we are successful
     * at retrieving the data
     * @param strings
     * @return
     */
    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        if(strings == null){
            downloadStatus = DownloadStatus.NOT_INITIALIZED;
            return null;
        }

        try{
            downloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(strings[0]);

            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int response = connection.getResponseCode();
            Log.d(TAG,"doInBackground: The response code was " + response);

            StringBuilder result = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while(null != (line = reader.readLine())){
                result.append(line).append("\n");
            }

            downloadStatus = DownloadStatus.OK;
            return result.toString();


        } catch (MalformedURLException e){
            Log.e(TAG, "doInBackground: InvalidURL " + e.getMessage());
        } catch (IOException e){
            Log.e(TAG, "doInBackground: IO Exception reading data" + e.getMessage());
        } catch (SecurityException e){
            Log.e(TAG, "doInBackground: Security Exception. Needs permissions? " + e.getMessage());
        } finally {
            if(connection != null){
                connection.disconnect();
            }
            if(reader != null){
                try{
                    reader.close();
                } catch (IOException e){
                    Log.e(TAG, "doInBackground: Error closing Stream " + e.getMessage());
                }
            }
        }

        downloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }


}
