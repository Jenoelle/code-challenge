package projects.android.triviaapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: GetJsonData
 * Given a String link, (in this case we have our hardcoded link) this class fetches the Json data
 * returned by this link from the OpenTrivia DB and returns a list of Questions with the relevant data
 * of each question
 *
 * This class implements GetTriviaData.OnDownloadComplete interface as
 *
 * AsyncTask<String, Integer, List<SlideItem>>
 *     String: Link we fetch from
 *     Integer: Help to show integer progress
 *     List<SlideItem>: our list returned from this task
 */
public class GetJsonData extends AsyncTask<String, Integer, List<SlideItem>> implements GetTriviaData.OnDownloadComplete {
    private static final String TAG = "GetJsonData";

    ProgressBar progressBar;

    private List<SlideItem> questionList = null;
    String link = "https://opentdb.com/api.php?amount=20&type=boolean";

    private final OnDataAvailable callback;
    private boolean runningOnSameThread = false;

    /**
     * interface: OnDataAvailable()
     * With this interface, we can have classes that use this AsyncTask
     * implement this interface to ensure they have an onDataAvailable function
     * The call back onDataAvailable is where the implementing class would
     * retrieve the returned data.
     */
    interface  OnDataAvailable{
        void onDataAvailable(List<SlideItem> dataList, DownloadStatus status);
    }

    /**
     * Constructor: GetJsonData
     * @param callback
     */
    public GetJsonData(OnDataAvailable callback){
        Log.d(TAG, "GetJsonData called");
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    /**
     * Method: onPostExecute()
     * Here we alert the using class that the data fetched and ready to use
     * @param slideItems
     */
    @Override
    protected void onPostExecute(List<SlideItem> slideItems) {
        Log.d(TAG, "onPostExecute: Begin");
        if(callback!= null){
            callback.onDataAvailable(questionList, DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: End");
        
    }

    /**
     * Method: onProgressUpdate()
     * Optionally use this method, especially for long running tasks, to display progress to the
     * user
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    /**
     * Method: doInBackground()
     * We use GetTriviaData instance to fetch the raw data in string form from the link
     * @param strings
     * @return
     */
    @Override
    protected List<SlideItem> doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: Begin");
        GetTriviaData getTriviaData = new GetTriviaData(this);
        getTriviaData.runInSameThread(link);

        Log.d(TAG, "doInBackground: End");
        return questionList;
    }

    /**
     * Method: onDownloadComplete()
     * From the GetTriviaData inner interface to alert the implementing class when GetTriviaData is done.
     * When it receives the data, check the status of the returned result, we parse the Json and
     * break it up into a collection of usable objects
     * @param data
     * @param status
     */
    @Override
    public void onDownloadComplete(String data, DownloadStatus status){
        if(status == DownloadStatus.OK){
            questionList = new ArrayList<>();
            try{
                JSONObject jsonData = new JSONObject(data);
                JSONArray qArray = jsonData.getJSONArray("results");

                for(int i = 0; i < qArray.length(); i++){

                    JSONObject jsonQuestion = qArray.getJSONObject(i);
                    String category = jsonQuestion.getString("category");
                    String difficulty = jsonQuestion.getString("difficulty");
                    String questionRaw = jsonQuestion.getString("question");

                    String question = questionRaw.replaceAll("&quot;", "\"").replaceAll("&#039;", "\'");


                    String correctAnswer = jsonQuestion.getString("correct_answer");

                    questionList.add(new SlideItem(category, difficulty, question, ((correctAnswer.equals("True") ? 1 : 0))));
                }
            } catch (JSONException e){
                e.printStackTrace();
                Log.e(TAG, "OnDownLoadComplete: Error processing Json data " + e.getMessage());
                status = DownloadStatus.FAILED_OR_EMPTY;
            }
        } else {
            Log.e(TAG, "onDownloadComplete failed with status " + status);
        }

        if(runningOnSameThread && callback != null){
            callback.onDataAvailable(questionList, status);
        }
        Log.d(TAG, "onDownloadComplete: End");

    }

}
