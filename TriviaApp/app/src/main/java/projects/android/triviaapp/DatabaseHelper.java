package projects.android.triviaapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "open_trivia.db";
    private static final int DATABASE_VERSION = 1;

    private static final int NUM_TABLES = 1;

    // Table name
    private static final String TABLE_QUESTIONS = "questions";

    // Column Names
    private static final String KEY_QUESTION_ID = "_id";
    private static final String KEY_QUESTION_CATEGORY = "category";
    private static final String KEY_QUESTION_DIFFICULTY = "difficulty";
    private static final String KEY_QUESTION_QUESTION = "question";
    private static final String KEY_QUESTION_CORRECT_ANSWER = "correct_answer";

    // Singleton Instance
    private static DatabaseHelper dbInstance;


    /************************* SETUP/CONFIG ****************************/

    /**
     * Constructor DatabaseHelper
     * This database helper will only use the database with the name matching DATABASE_NAME
     * @param context
     */
    private DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Function: onConfigure
     * called when the database connection is being configured
     * Configure database settings for things like foreign key support
     * @param db
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }


    /**
     * Called when the database is created for the first time;
     * if a database already exists on disk with the same named database, this will not be called
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate: CREATE DATABASE");

        String CREATE_QUESTION_TABLE = "CREATE TABLE " + TABLE_QUESTIONS +
                "(" +
                KEY_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_QUESTION_CATEGORY + " TEXT NOT NULL, " +
                KEY_QUESTION_DIFFICULTY + " TEXT DEFAULT 'undefined'," +
                KEY_QUESTION_QUESTION + " TEXT NOT NULL, " +
                KEY_QUESTION_CORRECT_ANSWER + " INTEGER DEFAULT 0" +
                ")";

        Log.d(TAG, "onCreate: Create Table - " + TABLE_QUESTIONS);
        sqLiteDatabase.execSQL(CREATE_QUESTION_TABLE);

    }

    /**
     * Function: onUpgrade
     * Called when the database needs to be upgraded.
     * Only called if a database already exists on disk with the same database name,
     * but the database_version is different than the version of the database that is on disk
     *
     * Use: Automatically called by the class
     * @param sqLiteDatabase
     * @param i Old version
     * @param i1 New version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(i != i1){
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
            onCreate(sqLiteDatabase);
        }
    }

    /**
     * Function: getInstance
     * This function returns an instance of the database so it can be used as a
     * singleton across all activities in the app. To ensure there is only one copy
     * of the instance.
     *
     * USE: DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
     * @param context
     * @return an instance of the database
     */
    public static synchronized DatabaseHelper getInstance(Context context){

        Log.d(TAG, "getInstance: GET DATABASE INSTANCE");
        if(dbInstance == null){
            dbInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return dbInstance;
    }

    @Override
    public String toString() {
        String result = "";
        for(int i = 0; i < NUM_TABLES; i++){

        }
        return result;
    }

    /************************ INSERTING ****************************/

    /**
     * Function: addQuestion
     * This function adds the given database item to the database.
     * @param newItem
     */
    public void addQuestion(DatabaseItem newItem){
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_QUESTION_CATEGORY, newItem.getCategory());
            values.put(KEY_QUESTION_DIFFICULTY, newItem.getDifficulty());
            values.put(KEY_QUESTION_QUESTION, newItem.getQuestion());
            values.put(KEY_QUESTION_CORRECT_ANSWER, newItem.getCorrectAnswer());
        } catch (Exception e){
            Log.e(TAG, "addQuestion: Error adding new question", e);
        } finally {
            db.endTransaction();
        }

    }

    /************************ GETTING ****************************/

    /**
     * Function: getAllQuestions
     * This function will return all of the questions in the question table
     * @return
     */
    public List<DatabaseItem> getAllQuestions(){
        List<DatabaseItem> questions = new ArrayList<>();
        String QUESTIONS_SELECT_QUERY = String.format("SELECT * FROM %s", TABLE_QUESTIONS);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(QUESTIONS_SELECT_QUERY, null);
        try {
            if(cursor.moveToFirst()){
                do{
                    DatabaseItem question = new DatabaseItem(cursor.getString(cursor.getColumnIndex(KEY_QUESTION_CATEGORY)),
                            cursor.getString(cursor.getColumnIndex(KEY_QUESTION_DIFFICULTY)),
                            cursor.getString(cursor.getColumnIndex(KEY_QUESTION_QUESTION)),
                            cursor.getInt(cursor.getColumnIndex(KEY_QUESTION_CORRECT_ANSWER)));
                    questions.add(question);
                } while(cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e(TAG, "getAllQuestions: Error getting all questions", e);
        } finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return questions;
    }

    /**
     * Function: getQuestionsOfCategory
     * Given a category, return all questions of that specific difficulty
     * @param category
     * @return
     */
    public List<DatabaseItem> getQuestionsOfCategory(String category){
        List<DatabaseItem> questions = new ArrayList<>();
        String CATEGORY_SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s.%s = '%s';",
                TABLE_QUESTIONS,
                TABLE_QUESTIONS,
                KEY_QUESTION_CATEGORY,
                category);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(CATEGORY_SELECT_QUERY, null);
        cursor.moveToFirst();

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    DatabaseItem question = new DatabaseItem(cursor.getString(cursor.getColumnIndex(KEY_QUESTION_CATEGORY)),
                            cursor.getString(cursor.getColumnIndex(KEY_QUESTION_DIFFICULTY)),
                            cursor.getString(cursor.getColumnIndex(KEY_QUESTION_QUESTION)),
                            cursor.getInt(cursor.getColumnIndex(KEY_QUESTION_CORRECT_ANSWER)));
                    questions.add(question);
                } while(cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e(TAG, "getQuestionsOfCategory: Error while trying to retrieve questions", e);
        }finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return questions;
    }

    /**
     * Function: getQuestionsOfDifficulty
     * Given a difficulty, return all questions of that specific difficulty
     * @param difficulty
     * @return
     */
    public List<DatabaseItem> getQuestionsOfDifficulty(String difficulty){
        List<DatabaseItem> questions = new ArrayList<>();

        // populate

        return questions;
    }

    /**
     * Function: getQuestionsCount
     * Returns the total number of questions stored in the database
     * @return
     */
    public int getQuestionsCount(){
        SQLiteDatabase db = getReadableDatabase();
        int result = (int)DatabaseUtils.queryNumEntries(db, TABLE_QUESTIONS);
        // populate

        return result;
    }


    /************************ DELETING ****************************/

    /**
     * Function: deleteAllQuestions()
     * Erases all the entries in the questions table
     */
    public void deleteAllQuestions(){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            //order of deletions is important when foreign key relationships exist
            db.execSQL("DELETE FROM " + TABLE_QUESTIONS);
            db.setTransactionSuccessful();
        } catch (Exception e){
            Log.e(TAG, "deleteAllQuestions: Error while trying to delete all questions", e);
        } finally {
            db.endTransaction();
        }
    }


}
