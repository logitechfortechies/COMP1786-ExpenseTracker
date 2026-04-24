# COMP1786 Expense Tracker

A comprehensive mobile application for tracking project-related expenses, developed as part of the COMP1786 Mobile App Development coursework. This project includes an Android Native(Admin) app , Node.js backend and a simple MAUI app(User)

## 📱 Features

- **Project Management**: Create, view, and manage different projects.
- **Expense Tracking**: Record detailed expenses for each project, including category, amount, date, and location.
- **Database**: Local storage using SQLite (`DatabaseHelper`) for offline access.
- **Network Sync**: Synchronize local data with a remote backend server using Retrofit.
- **Search & Filter**: Search through expenses and projects efficiently.
- **Location Services**: Capture and store the location where expenses were incurred using Google Play Services.
- **User Interface**: Modern Material Design interface with `RecyclerView` and `CardView`.

## 🛠 Tech Stack

- **Android App (Frontend)**:
  - Language: Java
  - Architecture: Activity-based with SQLite local database
  - Libraries: Retrofit 2, GSON, OkHttp, Android Jetpack (AppCompat, ConstraintLayout, RecyclerView), Play Services Location.
- **Backend**:
  - Runtime: Node.js
  - Framework: Express.js
  - Features: RESTful API for projects and expenses, CORS support, and request logging.

## 📂 Project Structure

- `/app`: The Android Studio project source code.
- `/NodeBackend`: The Node.js server source code.
- `server.js`: Main entry point for the backend server.
- `DatabaseHelper.java`: Local SQLite database management.

## 🚀 Getting Started

### Backend Setup
1. Navigate to the `NodeBackend` directory.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the server:
   ```bash
   npm start
   ```
   The server will run on `http://localhost:3000`.

### Android Setup
1. Open the root folder in **Android Studio**.
2. Wait for Gradle sync to complete.
3. **Important**: Update the API base URL in the Android app to point to your machine's IP address (use `10.0.2.2` if running on the Android Emulator).
4. Build and Run the app on an emulator or physical device.

## 🧪 API Endpoints

- `GET /projects`: Retrieve all projects.
- `POST /projects`: Create a new project.
- `GET /projects/:id/expenses`: Get all expenses for a specific project.
- `POST /expenses`: Upload a new expense.

---
**Course**: COMP1786 Mobile App Coursework
