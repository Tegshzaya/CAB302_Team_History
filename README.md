# Historical Art Guesser

This is a JavaFX desktop application game where users try to guess when famous artworks were created or where they're currently located. There are two main game modes - one for guessing years and another for guessing locations on a world map.

## What it does

The app has two games:
- Guess the Year: Users see a famous painting and pick what year it was made from multiple choice options
- Guess the Location: Users see a painting and click on a world map where they think it's currently displayed

Users can create accounts, log in, and their scores get saved. The location game uses an interactive map that provides an engaging user experience.

## How to run it

You need Java 21 and Maven installed.

1. Download the project
2. Open terminal in the project folder
3. Run: `mvn javafx:run`

That's it! The app should start up.

## Project structure

```
CAB302_Team_History/
├── src/main/java/com/learn/plan_b_302/     # All the Java code
├── src/main/resources/
│   ├── css/                                # Styling files
│   ├── fxml/                               # UI layouts
│   ├── images/                             # Pictures of artworks
│   └── web/                                # Map functionality
├── src/test/                               # Test files
├── docs/                                   # Documentation
├── data/                                   # Database files
└── pom.xml                                 # Maven config
```

## How the games work

### Year Game
1. Click "Begin Journey" on the main menu
2. Look at the artwork shown
3. Pick the year you think it was created
4. Get feedback and move to next question
5. See your final score after 10 questions

### Location Game  
1. Click "Start Adventure" on the main menu
2. Look at the artwork
3. Click on the world map where you think it's located
4. See how close you were
5. Keep going through the rounds

## Technical implementation

The project uses several design patterns:
- MVC pattern to separate the UI from the game logic
- Factory pattern for creating different game controllers
- Strategy pattern for handling different game types
- Observer pattern for managing user state
- Singleton for the database manager

The database has tables for users, artwork data, game records, and detailed game history.

The project includes 51 unit tests to ensure functionality works properly. You can run them with `mvn test`.

## Building and testing

To compile: `mvn clean compile`
To build: `mvn package`  
To test: `mvn test`
To see test coverage: `mvn jacoco:report`

## Notes

This was built for CAB302 coursework. The artwork images are from public domain sources, and the project uses Leaflet.js for the map functionality. The UI uses JavaFX with custom CSS styling.

The code follows Java conventions and includes proper documentation. All the important design decisions are documented in the docs folder.

## Licence
This project is licensed under the MIT License. See the LICENSE file for details.
