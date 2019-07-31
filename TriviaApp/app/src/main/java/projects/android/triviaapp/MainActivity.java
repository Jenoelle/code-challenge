package projects.android.triviaapp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SliderAdapter.ItemClicked, GetJsonData.OnDataAvailable{
    private static final String TAG = "MainActivity";

    private ArrayList<SlideItem> questionSet = new ArrayList<>();
    private ViewPager viewPager;
    private SliderAdapter sliderAdapter;
    private int latestQuestionNumber = 1;
    private int numQuestions;

    ConstraintLayout constraintLayout;

    Dialog answerStatusDialog;
    Button popupCancelButton;
    TextView popupTextStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        constraintLayout = (ConstraintLayout)findViewById(R.id.slide_parent);
        viewPager = (ViewPager)findViewById(R.id.main_view_pager);

        answerStatusDialog = new Dialog(this);

    }

    /**
     * Funtion: onQuestionAnsweredNotify()
     * This function is used to display our popup of the user's status after they answer
     * a question.  If they are done with a set of questions
     * we also use this popup to start the load of the next set
     * @param status
     */
    public void onQuestionAnsweredNotify(boolean status){
        answerStatusDialog.setContentView(R.layout.popup_answer_notify);

        popupCancelButton = (Button)answerStatusDialog.findViewById(R.id.popup_btn_close);
        popupTextStatus = (TextView)answerStatusDialog.findViewById(R.id.popup_txt_answer_state);

        popupCancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                answerStatusDialog.dismiss();
                if(latestQuestionNumber == numQuestions){
                    loadNextQuestionSet();
                }
            }

        });
        String popupText = "";

        if(status){
            popupText = "You're Right!";
            popupTextStatus.setTextColor(getResources().getColor(R.color.colorCorrect));
        } else {
            popupText = "Whoops, wrong answer...";
            popupTextStatus.setTextColor(getResources().getColor(R.color.colorIncorrect));
        }

        if(latestQuestionNumber == numQuestions) {// at the end
            popupCancelButton.setText(R.string.button_popup_load_more);
            popupText += "\nWant to test yourself again?";
        }

        popupTextStatus.setText(popupText);

        answerStatusDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        answerStatusDialog.show();
    }

    /**
     * Method: onResume()
     * In our on Resume we fetch the dataset
     * We don't want to disrupt the UI and have strange behavior
     */
    @Override
    protected void onResume() {
        Log.d(TAG, "onStart: Begin");
        super.onResume();

        GetJsonData getJsonData = new GetJsonData(this);
        getJsonData.execute();

        Log.d(TAG, "onStart: End");
    }

    //Helper for testing
    void initQuestions(){
        Log.d(TAG, "initQuestions: Loading questions");
        questionSet.add(new SlideItem("History", "medium", "The two atomic bombs dropped on Japan by the United States in August 1945 were named &#039;Little Man&#039; and &#039;Fat Boy&#039;.", DatabaseItem.FALSE));
        questionSet.add(new SlideItem("History", "hard", "Heres another question", DatabaseItem.TRUE));
        questionSet.add(new SlideItem("History", "hard", "Heres a third question", DatabaseItem.TRUE));
        questionSet.add(new SlideItem("History", "hard", "Heres a Fourth question", DatabaseItem.TRUE));
        questionSet.add(new SlideItem("History", "Easy", "Heres a fifth question", DatabaseItem.TRUE));
        questionSet.add(new SlideItem("History", "Easy", "Heres a 6th question", DatabaseItem.FALSE));
        numQuestions = questionSet.size();
    }


    /**
     * Function: loadNextQuestionSet()
     * Called when user has reached the end of the current Question Set and decides to move on to
     * the next set of questions
     */
    void loadNextQuestionSet(){
        questionSet.clear();
        GetJsonData getJsonData = new GetJsonData(this);
        getJsonData.execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt("latestPage", latestQuestionNumber);
    }

    /**
     * Function: onQuestionAnswered()
     * From the SliderAdapter's inner interface we can see the user's answer
     * and respond accordingly
     * @param index
     * @param userAnswer
     */
    @Override
    public void onQuestionAnswered(int index, int userAnswer) {
        String result;
        if(userAnswer == DatabaseItem.TRUE){
            result = "You clicked on index " + index + "and chose TRUE";
        } else {
            result = "You clicked on index " + index + "and chose FALSE";
        }

        Log.d(TAG, "onQuestionAnswered: " + result);

        // 2) Save the answer to set the UI accordingly
        questionSet.get(index).setQuestionAnswered(true);
        questionSet.get(index).setUserChoice(userAnswer);
        boolean userStatus = questionSet.get(index).isUserCorrect();

        // 3) increment the current page
        latestQuestionNumber++;
        if(latestQuestionNumber <= numQuestions){
            sliderAdapter.setLatestQuestion(latestQuestionNumber);
            sliderAdapter.notifyDataSetChanged();

            Log.d(TAG, "onQuestionAnswered: AutoScroll");
            // 4) Auto Scroll to next question
            viewPager.setCurrentItem(latestQuestionNumber-1, true);
        }
        onQuestionAnsweredNotify(userStatus);

    }

    /**
     * Function: onDataAvailable()
     * The callback method from the GetJsonData Task
     * We receive the status of the download, and the dataset.
     * We update our adapter to display the new cards
     *
     * @param data
     * @param status
     */
    @Override
    public void onDataAvailable(List<SlideItem> data, DownloadStatus status){
        if(status == DownloadStatus.OK){
            Log.d(TAG, "onDataAvailable: data SIZE: " + data.size());

            questionSet.addAll(data);
            sliderAdapter = new SliderAdapter(this, questionSet);
            viewPager.setAdapter(sliderAdapter);
            numQuestions = questionSet.size();
            latestQuestionNumber = 1;

            Log.d(TAG, "onDataAvailable: question set SIZE: " + questionSet.size());
        } else {
            Log.e(TAG, "onDataAvailable failed with status " + status);
        }
    }
}

