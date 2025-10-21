# Design Patterns Used in Historical Art Guesser

This document explains the design patterns I used in my application and why I chose them.

## 1. Model-View-Controller (MVC)

I used MVC to keep the UI separate from the game logic. This makes the code much easier to work with.

### Models (Data)
- User.java - stores user info like username and scores
- ArtImage.java - holds artwork data and checks if answers are right
- DatabaseManager.java - does all the database stuff

### Views (UI)
- FXML files define what the screens look like
- CSS files make everything look nice
- login.fxml, signup.fxml, Menu.fxml, game.fxml, Location.fxml

### Controllers (Logic)
- AuthController.java - handles login/signup
- MenuController.java - main menu stuff
- GameController.java - year guessing game logic
- LocationController.java - location game logic

This separation means I can change the UI without breaking the game logic, and vice versa.

## 2. Base Controller

I made a BaseController class that all my controllers extend. This stops me from writing the same navigation code over and over.

```java
public abstract class BaseController {
    protected void navigate(ActionEvent event, String fxmlPath, 
                          Consumer<Object> controllerSetup) {
        // navigation code here
    }
}
```

Every controller extends this, so they all get the same navigation behavior without duplicating code.

## 3. Factory Pattern

I used this to create different types of game controllers. Instead of having messy if-else statements everywhere, I have a clean way to create the right controller for each game type.

```java
public class GameControllerFactory {
    public static BaseController createController(String gameType) {
        switch (gameType) {
            case "YEAR_GAME":
                return new GameController();
            case "LOCATION_GAME":
                return new LocationController();
            default:
                throw new IllegalArgumentException("Unknown game type");
        }
    }
}
```

## 4. Strategy Pattern

This handles the different scoring systems for each game type. The year game and location game score differently, so I made separate strategies for each.

```java
public interface ScoringStrategy {
    int calculateScore(boolean isCorrect, double accuracy);
}

public class YearGameScoring implements ScoringStrategy {
    public int calculateScore(boolean isCorrect, double accuracy) {
        return isCorrect ? 1 : 0;
    }
}
```

## 5. Observer Pattern

I used this to manage user state changes. When a user logs in or out, other parts of the app need to know about it.

```java
public class UserManager {
    private static User currentUser;
    private static List<UserStateObserver> observers = new ArrayList<>();
    
    public static void setCurrentUser(User user) {
        currentUser = user;
        notifyObservers();
    }
}
```

## 6. Singleton Pattern

I made DatabaseManager a singleton because I only need one instance managing the database connection.

```java
public class DatabaseManager {
    private static DatabaseManager instance;
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
}
```

## 7. Template Method Pattern

I used this in my controllers to define the common flow while allowing each game to customize specific steps.

```java
public abstract class GameController extends BaseController {
    public final void startGame() {
        initializeGame();
        loadQuestion();
        updateUI();
    }
    
    protected abstract void loadQuestion();
    protected abstract void updateUI();
}
```

## Why These Patterns?

I chose these patterns because they solve real problems in my application:

- MVC keeps things organized and testable
- Base Controller reduces code duplication
- Factory makes it easy to add new game types
- Strategy handles different scoring systems cleanly
- Observer keeps the UI in sync with user state
- Singleton ensures only one database connection
- Template Method provides consistent game flow

These patterns made my code much more maintainable and easier to understand. They also made it easier to add new features without breaking existing code.