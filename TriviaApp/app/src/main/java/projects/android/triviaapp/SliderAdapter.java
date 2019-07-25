package projects.android.triviaapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: SliderAdapter
 * Adapter to fill the QuestionCard
 */
public class SliderAdapter extends PagerAdapter {

    private static String QUESTION_CARD_TITLE= "Question ";

    Context context;
    LayoutInflater layoutInflater;
    int latestQuestion = 1;
    ItemClicked activity;

    List<SlideItem> questionList;


    /**
     * interface: ItemClicked()
     * This interface is implemented by classes using the SliderAdapter
     * so it will have a callback method to be alerted that the question
     * was answered
     */
    public interface ItemClicked{
        /**
         * Method: onQuestionAnswered()
         * @param index the current card
         * @param value the user's answer
         */
        void onQuestionAnswered(int index, int value);
    }

    /**
     * Constructor
     * @param context
     * @param questionList
     */
    public  SliderAdapter(Context context, List<SlideItem> questionList){
        this.context = context;
        this.questionList = questionList;
        activity = (ItemClicked)context;

    }

    /**
     * Method: setLatestQuestion()
     * with this we keep track of the last question the user answered
     * @param latestQuestion
     */
    public void setLatestQuestion(int latestQuestion) {
        this.latestQuestion = latestQuestion;

    }

    /**
     * Method: refreshList()
     * Clears the saved List in the adapter and repopulates it with
     * a new data set
     * @param refresh
     */
    public void refreshList(List<SlideItem> refresh) {
        this.questionList.clear();
        this.questionList.addAll(refresh);
        notifyDataSetChanged();
    }


    /**
     * Funciton:getCount()
     * To ensure the player can't move to the next question until they have answered the current one
     * @return
     */
    @Override
    public int getCount() {
        return latestQuestion;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == (ConstraintLayout)o);
    }


    /**
     * Method: instantiateItem()
     * We populate the Question Card
     * @param container
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.fragment_question_container, container, false);

        // Grab views
        TextView questionText = (TextView)view.findViewById(R.id.slide_txt_question);
        TextView difficultyText = (TextView)view.findViewById(R.id.slide_txt_difficulty);
        TextView titleText = (TextView)view.findViewById(R.id.slide_txt_title);
        TextView indicatorText = (TextView)view.findViewById(R.id.slide_txt_question_indicator);
        Button trueButton = (Button)view.findViewById(R.id.slide_btn_true);
        Button falseButton = (Button)view.findViewById(R.id.slide_btn_false);


        // Populate views
        int currentPage = position+1;
        questionText.setText(questionList.get(position).getQuestion());
        difficultyText.setText(questionList.get(position).getDifficulty());
        titleText.setText(QUESTION_CARD_TITLE + currentPage);
        indicatorText.setText(currentPage + "/" + questionList.size());

        SlideItem currentQuestion = questionList.get(position);

        //Setting the saved button state
        if(currentQuestion.isQuestionAnswered()){
            if(currentQuestion.isUserCorrect()){
                if(currentQuestion.getUserChoice() == 1){ // if they chose true
                    trueButton.setBackgroundColor(context.getResources().getColor(R.color.colorCorrect));
                } else {
                    falseButton.setBackgroundColor(context.getResources().getColor(R.color.colorCorrect));
                }
            } else {
                if(currentQuestion.getUserChoice() == 1){ // if they chose true
                    trueButton.setBackgroundColor(context.getResources().getColor(R.color.colorIncorrect));
                } else {
                    falseButton.setBackgroundColor(context.getResources().getColor(R.color.colorIncorrect));
                }
            }
        } // otherwise the question hasn't been answered


        trueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!questionList.get(position).isQuestionAnswered()) {
                    activity.onQuestionAnswered(position, DatabaseItem.TRUE);
                }

            }
        });
        falseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!questionList.get(position).isQuestionAnswered()) {
                    activity.onQuestionAnswered(position, DatabaseItem.FALSE);
                }
            }
        });


        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
