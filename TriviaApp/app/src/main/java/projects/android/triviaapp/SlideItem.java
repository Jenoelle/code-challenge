package projects.android.triviaapp;

/**
 * Class: SlideItem
 * This class is used to save the information of a question, formatted specifically
 * for use in the sliderAdapter
 */
public class SlideItem extends DatabaseItem{
    private boolean isQuestionAnswered = false;
    private int userChoice = -1;


    public SlideItem(String category, String difficulty, String question, int correctAnswer) {
        super(category, difficulty, question, correctAnswer);
    }

    public boolean isQuestionAnswered() {
        return isQuestionAnswered;
    }

    public void setQuestionAnswered(boolean questionAnswered) {
        isQuestionAnswered = questionAnswered;
    }

    public int getUserChoice() {
        return userChoice;
    }

    public void setUserChoice(int userChoice) {
        this.userChoice = userChoice;
    }

    public boolean isUserCorrect(){
        if(isQuestionAnswered()){
            if(userChoice == getCorrectAnswer()){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String str = super.toString();
        return str + "SlideItem{" +
                "isQuestionAnswered=" + isQuestionAnswered +
                ", userChoice=" + userChoice +
                '}';
    }
}
