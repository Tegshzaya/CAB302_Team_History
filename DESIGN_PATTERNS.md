# Design Patterns Documentation
Historical Art Guesser Application

## Overview
This document describes the design patterns used in the Historical Art Guesser application. We implemented several patterns to organize the code better and make it easier to maintain.

## 1. Model-View-Controller (MVC) Pattern

Used the MVC pattern to separate the different parts of the application:

### Model Layer
- User.java - stores user information and handles user data
- ArtImage.java - represents artwork data and checks if answers are correct
- DatabaseManager.java - handles all database operations

### View Layer
- FXML Files - define what the user interface looks like
  - login.fxml - login screen
  - signup.fxml - registration screen
  - Menu.fxml - main menu
  - game.fxml - year guessing game
  - Location.fxml - location guessing game
- CSS Files - make the interface look good
  - style.css - all the styling

### Controller Layer
- AuthController.java - handles login and signup
- MenuController.java - manages the main menu
- GameController.java - controls the year guessing game
- LocationController.java - controls the location guessing game

### Why I Used MVC
- Each part has its own job
- Easy to change the UI without breaking the logic
- Can test each part separately
- Models can be reused in different views

## 2. Base Controller Pattern

Created a BaseController class that all my controllers extend. This saves us from writing the same navigation code over and over.

```java
public abstract class BaseController {
    protected void navigate(ActionEvent event, String fxmlPath, 
                          Consumer<Object> controllerSetup) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            if (controllerSetup != null) {
                controllerSetup.accept(loader.getController());
            }
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("../css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

All our controllers extend this base class:
- AuthController extends BaseController
- MenuController extends BaseController
- GameController extends BaseController
- LocationController extends BaseController

This pattern helps because:
- We don't have to write the same navigation code multiple times
- All controllers work the same way
- If we need to change how navigation works, we only change it in one place

## 3. Factory Pattern

Used the factory pattern in a couple of places to create objects in a consistent way.

### Game Factory
Created a GameFactory class to make game controllers:

```java
public class GameFactory {
    public static GameController createYearGame(User user) {
        GameController controller = new GameController();
        controller.setCurrentUser(user);
        return controller;
    }
    
    public static LocationController createLocationGame(User user) {
        LocationController controller = new LocationController();
        controller.setCurrentUser(user);
        return controller;
    }
}
```

### Database Factory
The DatabaseManager class acts like a factory for database operations:

```java
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:artgame.db";
    
    public static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    public static List<ArtImage> getAllImages() {
        // Creates ArtImage objects from database data
    }
}
```

Using the factory pattern helps because:
- All object creation happens in one place
- Easy to add new game types later
- All objects are created the same way
- Makes testing easier

## 4. Strategy Pattern

Used the strategy pattern to handle different types of games. This lets me easily add new game modes without changing existing code.

### Game Strategy Interface
Created an interface that all game strategies must follow:

```java
public interface GameStrategy {
    void startGame();
    void processAnswer(String answer);
    int calculateScore();
    boolean isGameComplete();
}
```

### Different Game Strategies
- YearGuessingStrategy - handles the year guessing game
- LocationGuessingStrategy - handles the location guessing game

### How It Works
The GameController can use any strategy:

```java
public class GameController {
    private GameStrategy currentStrategy;
    
    public void setStrategy(GameStrategy strategy) {
        this.currentStrategy = strategy;
    }
    
    public void playGame() {
        currentStrategy.startGame();
        // Game logic using current strategy
    }
}
```

This pattern is useful because:
- Easy to add new game types
- Don't need to change existing code when adding new games
- Different strategies can be swapped easily

## 5. Observer Pattern

Used the observer pattern to update the UI when the game state changes. This keeps the UI and game logic separate.

### Subject Interface
The game controller can notify observers when things change:

```java
public interface GameSubject {
    void addObserver(GameObserver observer);
    void removeObserver(GameObserver observer);
    void notifyObservers();
}
```

### Observer Interface
UI components can watch for changes:

```java
public interface GameObserver {
    void update(GameState state);
}
```

### How It Works
The GameController notifies observers when the game state changes:

```java
public class GameController implements GameSubject {
    private List<GameObserver> observers = new ArrayList<>();
    private GameState currentState;
    
    @Override
    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }
    
    @Override
    public void notifyObservers() {
        for (GameObserver observer : observers) {
            observer.update(currentState);
        }
    }
    
    private void updateGameState() {
        // Update game state
        notifyObservers();
    }
}
```

This pattern helps because:
- UI and game logic stay separate
- Can add or remove observers while the program is running
- One change can update multiple UI components
- Easy to add new UI components that watch for changes

## 6. Singleton Pattern

I used the singleton pattern for the DatabaseManager to make sure there's only one database connection throughout the application.

```java
public class DatabaseManager {
    private static DatabaseManager instance;
    private static final String DB_URL = "jdbc:sqlite:artgame.db";
    
    private DatabaseManager() {
        // Private constructor
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    public void initializeDatabase() {
        // Database initialization logic
    }
}
```

This pattern is useful because:
- Only one database manager exists
- Can access it from anywhere in the application
- Prevents multiple database connections
- Keeps the database state consistent

## 7. Template Method Pattern

Used the template method pattern to define the basic structure of how games work, while letting each game customize specific parts.

```java
public abstract class BaseGameController {
    // Template method defining the game flow
    public final void playGame() {
        initializeGame();
        while (!isGameComplete()) {
            presentQuestion();
            String answer = getPlayerAnswer();
            processAnswer(answer);
            updateScore();
        }
        endGame();
    }
    
    // Abstract methods to be implemented by subclasses
    protected abstract void initializeGame();
    protected abstract void presentQuestion();
    protected abstract String getPlayerAnswer();
    protected abstract void processAnswer(String answer);
    protected abstract boolean isGameComplete();
    protected abstract void endGame();
    
    // Concrete method
    protected void updateScore() {
        // Common score update logic
    }
}
```

This pattern helps because:
- All games follow the same basic structure
- Common game flow is written once
- Each game can customize specific parts
- Easy to change the overall game flow

## Summary

Using these design patterns has made my code much better organized and easier to work with. Here's what I learned:

### Maintainability
- Each part of the code has a clear job
- Easy to change one part without breaking others
- Code is organized consistently

### Scalability
- Easy to add new features
- Code is broken into logical modules
- Can extend the application easily

### Testability
- Can test each part separately
- Easy to create mock objects for testing
- Clear dependencies between components

### Code Quality
- Follows good programming principles
- Less repeated code
- Easier to read and understand

### Team Development
- Other developers can understand the structure
- Consistent patterns throughout the code
- Easy for new team members to get started