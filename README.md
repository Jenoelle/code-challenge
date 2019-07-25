# Open Trivia

A Yes or No Trivia game using question from OpenTriviaDB.

## Getting Started

This Project was created in Android Studio and tested on the Nexus 5X API 27 (Android 8.1.0, API 27) Emulator. Make sure you have an updated version of Android Studio installed. Find Android Studio install instructions here: https://developer.android.com/studio

Either clone the repository or download a zipfile of the project. Open Android studio, then go to file and open project. Locate the project file to open. Make sure to sync the project with the grade files under File > Sync Project with Gradle Files.  The compile sdk Version is 28 and the minimum sdk version is 21. Opening the project and playing the application in the proper emulator will start the opening activity.

### 

## Design

In the loop of the app, we fetch the data from the OpenTriviaDB, parse the data, and display the Questions on a sliding view of question cards. Once the user answers all the questions they can load a new question set.

### Active Classes

#### OpenTriviaDB
This class is responsible for opening the HTTP link to a given URL and returns the Json Data at the url. It extends AsyncTask.

#### GetJsonData
Once the data is retrieved from the URL we can pass it to the GetJsonData class to parse out the information stored at the url. It extends AsyncTask.

#### MainActivity
Responsible for keeping track of the current question, the Question set, and logic (aka checking if the user answered the question right or not. We separate the fetching of data from the Main Activity since it doesn't have to be responsible for the Async Tasks and other Classes in the future may also use the OpenTriviaDB and GetJsonData as well.

#### SliderAdapter
This Adapter class extends from View Pager. We use this to have an easy way to inflate identical cards to display the questions

See each class for more implementation details and documentation.

#### Unused : DatabaseHelper 

Example of a SQLiteOpenHelper Database helper, examples on database creation, querying a database, etc...

### UI

We have a simple UI cycle, A Welcome Page, The main Activity Layout which cycles through Question cards, and a popup to display the result of their answer.

#### Layouts
activity_main - holds our view pager to page through the questions

activity_opening - Welcome Screen

fragment_question_containter - the view inflated by the SliderAdapter. Holds the Question number, difficulty, question, true/false buttons, and a question number indicator.


