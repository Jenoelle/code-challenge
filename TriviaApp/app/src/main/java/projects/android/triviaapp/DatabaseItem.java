package projects.android.triviaapp;

/**
 * Class: DatabaseItem
 * This class is the basic formula of the database item.
 * Since we only need a couple categories from the Open Trivia DB
 * We only have the 4 here.  For use to store a data Item from
 * the database or be extended into more specific items (Like SlideItem)
 */
public class DatabaseItem {
   public static final int TRUE = 1;
   public static final int FALSE = 0;

    private final String category;
    private final String difficulty;
    private final String question;
    private final int correctAnswer;

    public DatabaseItem(String category, String difficulty, String question, int correctAnswer){
        this.category = category;
        this.difficulty = difficulty;
        this.question = question;
        this.correctAnswer = correctAnswer;

    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    @Override
    public String toString() {
        return "DatabaseItem{" +
                "category='" + category + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", question='" + question + '\'' +
                ", correctAnswer=" + correctAnswer +
                '}';
    }
}
