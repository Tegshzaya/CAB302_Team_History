#!/bin/bash

# Historical Art Guesser - Build Script
# This script builds and tests the application

set -e  # Exit on any error

echo "🎨 Historical Art Guesser - Build Script"
echo "========================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Java is installed
check_java() {
    print_status "Checking Java installation..."
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
        print_success "Java found: $JAVA_VERSION"
    else
        print_error "Java is not installed. Please install Java 21 or higher."
        exit 1
    fi
}

# Check if Maven is installed
check_maven() {
    print_status "Checking Maven installation..."
    if command -v mvn &> /dev/null; then
        MAVEN_VERSION=$(mvn -version | head -n 1 | cut -d' ' -f3)
        print_success "Maven found: $MAVEN_VERSION"
        USE_MAVEN=true
    else
        print_warning "Maven not found. Using Maven wrapper..."
        if [ -f "./mvnw" ]; then
            print_success "Maven wrapper found"
            USE_MAVEN=false
        else
            print_error "Neither Maven nor Maven wrapper found."
            exit 1
        fi
    fi
}

# Clean previous builds
clean_build() {
    print_status "Cleaning previous builds..."
    if [ "$USE_MAVEN" = true ]; then
        mvn clean
    else
        ./mvnw clean
    fi
    print_success "Clean completed"
}

# Compile the application
compile_app() {
    print_status "Compiling application..."
    if [ "$USE_MAVEN" = true ]; then
        mvn compile
    else
        ./mvnw compile
    fi
    print_success "Compilation completed"
}

# Run tests
run_tests() {
    print_status "Running tests..."
    if [ "$USE_MAVEN" = true ]; then
        mvn test
    else
        ./mvnw test
    fi
    print_success "Tests completed"
}

# Generate test coverage report
generate_coverage() {
    print_status "Generating test coverage report..."
    if [ "$USE_MAVEN" = true ]; then
        mvn jacoco:report
    else
        ./mvnw jacoco:report
    fi
    print_success "Coverage report generated at target/site/jacoco/index.html"
}

# Package the application
package_app() {
    print_status "Packaging application..."
    if [ "$USE_MAVEN" = true ]; then
        mvn package -DskipTests
    else
        ./mvnw package -DskipTests
    fi
    print_success "Application packaged"
}

# Run the application
run_app() {
    print_status "Starting application..."
    if [ "$USE_MAVEN" = true ]; then
        mvn javafx:run
    else
        ./mvnw javafx:run
    fi
}

# Main build process
main() {
    echo
    print_status "Starting build process..."
    
    check_java
    check_maven
    clean_build
    compile_app
    run_tests
    generate_coverage
    package_app
    
    echo
    print_success "🎉 Build completed successfully!"
    echo
    echo "📊 Build Summary:"
    echo "  - Compilation: ✅"
    echo "  - Tests: ✅"
    echo "  - Coverage: ✅"
    echo "  - Packaging: ✅"
    echo
    echo "📁 Generated files:"
    echo "  - JAR: target/plan_B_302-1.0-SNAPSHOT.jar"
    echo "  - Coverage: target/site/jacoco/index.html"
    echo
    echo "🚀 To run the application:"
    echo "  ./build.sh run"
    echo
}

# Handle command line arguments
case "${1:-build}" in
    "build")
        main
        ;;
    "run")
        check_java
        check_maven
        run_app
        ;;
    "test")
        check_java
        check_maven
        run_tests
        generate_coverage
        ;;
    "clean")
        check_maven
        clean_build
        ;;
    "help")
        echo "Usage: $0 [command]"
        echo
        echo "Commands:"
        echo "  build  - Full build process (default)"
        echo "  run    - Run the application"
        echo "  test   - Run tests only"
        echo "  clean  - Clean build artifacts"
        echo "  help   - Show this help"
        ;;
    *)
        print_error "Unknown command: $1"
        echo "Use '$0 help' for available commands"
        exit 1
        ;;
esac
