# Testing Documentation

## Overview
This document describes the testing approach used in the Historical Art Guesser application. The project includes 51 unit tests that cover the main functionality and ensure the code works properly.

## Testing Setup

### Tools Used
- JUnit 5 for unit testing
- TestFX for testing JavaFX UI components
- JaCoCo for code coverage reports
- Maven for running tests

### Test Structure
```
src/test/java/com/learn/plan_b_302/
├── UserTest.java                    # Tests for User class
├── ArtImageTest.java                # Tests for ArtImage class
├── UserManagerTest.java             # Tests for UserManager
├── UserGameRecordTest.java          # Tests for UserGameRecord
├── GameHistoryTest.java             # Tests for GameHistory
├── AuthControllerTest.java          # UI tests for login/signup
├── DatabaseManagerTest.java         # Tests for database operations
├── SimpleValidationTest.java        # Basic validation tests
├── UtilityTest.java                 # Utility function tests
└── IntegrationTest.java             # Integration tests
```

## Test Categories

### 1. User Management Tests
Tests for user-related functionality:
- User creation and data handling
- UserManager state management
- User authentication flow

### 2. Game Data Tests
Tests for game-related classes:
- ArtImage creation and option handling
- UserGameRecord creation and formatting
- GameHistory data management

### 3. Database Tests
Tests for database operations:
- Database initialization
- Data insertion and retrieval
- Connection handling

### 4. UI Tests
Tests for user interface components:
- Login and signup functionality
- Form validation
- User interaction handling

### 5. Integration Tests
Tests that verify components work together:
- User flow from login to game
- Data persistence across sessions
- Component interaction

## Running Tests

### Command Line
```bash
mvn test                    # Run all tests
mvn jacoco:report          # Generate coverage report
```

### Test Results
- All 51 tests pass
- Good coverage of core functionality
- Tests run quickly and reliably

## Test Quality

### What the Tests Cover
- Basic functionality of all main classes
- Edge cases and error conditions
- User interface interactions
- Database operations
- Data validation

### Test Design Principles
- Tests are simple and focused
- Each test verifies one specific behavior
- Tests use descriptive names
- Tests are independent and can run in any order

## Coverage Report

The JaCoCo plugin generates coverage reports showing:
- Which lines of code are tested
- Percentage of code covered by tests
- Areas that might need more testing

Reports are generated in the target/site/jacoco-report/ directory.

## Maintenance

### Adding New Tests
When adding new features:
1. Write tests for the new functionality
2. Ensure tests pass before committing
3. Update this documentation if needed

### Test Best Practices
- Keep tests simple and readable
- Test both success and failure cases
- Use meaningful test names
- Don't test implementation details, test behavior

## Notes

The testing approach focuses on ensuring the application works correctly rather than achieving 100% code coverage. The tests cover the most important functionality and help catch bugs during development.