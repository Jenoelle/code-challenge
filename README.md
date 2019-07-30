# Open Trivia

A Yes or No Trivia game using question from OpenTriviaDB.

## Getting Started

This Project was created in Android Studio and tested on the Nexus 5X API 27 (Android 8.1.0, API 27) Emulator. Make sure you have an updated version of Android Studio installed. Find Android Studio install instructions here: https://developer.android.com/studio

Either clone the repository or download a zipfile of the project. Open Android studio, then go to file and open project. Locate the project file to open. Make sure to sync the project with the grade files under File > Sync Project with Gradle Files.  The compile sdk Version is 28 and the minimum sdk version is 21. Opening the project and playing the application in the proper emulator will start the opening activity.

### 

## Design

In the loop of the app, we fetch the data from the OpenTriviaDB, parse the data, and display the Questions on a sliding view of question cards. Once the user answers all the questions they can load a new question set.

### Requirements of Design

Pull True/False Trivia Questions from the Open Trivia DB

* User should be displayed a single question at a time
* Upon answering they should be notified if they were correct or not
* Upon answering the screen should swipe to the next question
* User should be able to swipe back to see all answered questions

Per the Requirements, the only information needed from a stored question object in the Open Trivia DB was the question and the answer and to only pull True/false questions. This fueled the DatabaseItem Creation to only store necessary values, the question and the right answer.  It's structure can optionally be scaled/customized for future implementations such as allowing the user to choose the category or difficulty.

I chose to separate the DatabaseItem as a parent and create a SlideItem that extends the parent. The Database Item would hold the immutable question information that can be extended into any type of item that wants to use it. The SlideItem is meant to be a UI item for the SliderAdapter(PagerAdapter).  Another Item class can be created and extend the database, for example, to fill a different type of UI element.  I chose to only use the SlideItem through out the code since we knew we would only have the one type of item.

The choice of ViewPager with a CardView was to fit the requirements to show only one question, all which have the same UI container and elements, at a time with the ability to auto scroll to the next question. A similar effect could be achieved by using fragments as well. We would only need to create a single fragment class and populate the fragment according to the question we are on. This is an option if you want more customizable behavior of each of the pages and to truly separate the Main Activity so the fragment itself can handle its own UI and take away the burden with its own fragment lifecycle.  This would be a good choice when scaling to different types of questions.

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

Example of a SQLiteOpenHelper Database helper, examples on database creation, querying a database, etc... A local database on the user's device could save information about the questions and save the user's personal progress. Having a local database takes up storage but, as apposed to fetching from a server, local storage avoids connectivity issues and faster access.

### UI

We have a simple UI cycle, A Welcome Page, The main Activity Layout which cycles through Question cards, and a popup to display the result of their answer.

#### Layouts
activity_main - holds our view pager to page through the questions

activity_opening - Welcome Screen

fragment_question_containter - the view inflated by the SliderAdapter. Holds the Question number, difficulty, question, true/false buttons, and a question number indicator.

#### Future Support

* Portrait and Landscape information persistence
* Support for pulling and storing more info from database
* Progress bar to display to user the time left to download next set of questions
* Keep the User's score + show the results at the end
* Allow user to pick question catgory, difficulty

