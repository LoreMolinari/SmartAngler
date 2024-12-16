# SmartAngler

SmartAngler is an Android app designed to enhance the fishing experience with features like session tracking, fish species recognition, and fishing statistics management.

## Core Features

- Fish Database: A comprehensive library of fish species with detailed information
- Fishing Tracker: Logs fishing sessions, including caught fish, locations, and additional details
- Fish Recognition: Identifies species based on manually entered data or images
- Personal Statistics: Analyzes session data to provide insights

## Installation and Setup

1. Clone the repository:
  ```git clone https://github.com/LoreMolinari/SmartAngler.git```

2. Open the project in Android Studio
3. Set up an emulator or connect a physical device
4. Ensure Gradle is correctly configured:
  ```./gradlew build```

5. Run the app:
- From the IDE: Click Run
- From the terminal:
  ```./gradlew installDebug```

**NB**: The app won't build without an apiKey in the local.properties file. This is needed for the LLM

## Technologies Used

- Language: Java
- Architecture: MVVM (Model-View-ViewModel)
- Platform: Android
- Database: Local storage
- Additional Libraries: Material Components, ConstraintLayout, AnyChart Android, OSMDroid, CameraX, Google Play Services Location, Glide, Secrets Gradle Plugin, Generative AI, Guava

## Project Structure
 - Activities:
    - `MainActivity.java`: The main entry point of the app
-  UI Fragments:
    - `HomeFragment`: Displays the main menu.
    - `HomeFragment`: Entry interface
    - `FishingFragment`: Fishing session interface
    - `StatisticsFragment`: Displays statistics
    - `SessionFragment`: Interface for viewing past sessions
    - `FishDBFragment`: Interface for viewing fish in the database
- Helpers:
    - `SmartAnglerOpenHelper`: Manages interactions with the fishDB and fish location DB
  - `SmartAnglerSessionHelper`: Handles interactions with the session database
- Listeners:
  - `StepCountListener`: Detects steps using device sensors
  - `CastDetectorListener`: Detects casting movements using the device's sensors
-  Other resources:
    - `res/layout/`: Contains screen layouts
    - `res/drawable/`: Contains images, icons, and graphics

## Future Improvements

- Cleaner UI design
- Expanded fish database, locations, gear, and techniques
- General performance enhancements
- Better picture management (local storage)

