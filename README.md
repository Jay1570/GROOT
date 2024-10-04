# GROOT

## Project Description
GROOT is a project aimed at providing detailed repository management and user interaction functionalities. This application offers features such as viewing repository details, starring/un-starring repositories, and fetching README files and contributions to offer a comprehensive repository experience.

## Technologies Used
- **Kotlin**: For developing the Android application.
- **Firebase Storage**: Used for storing and retrieving repository files, including the README files.
- **LiveData**: Utilized for observing data changes within the application.
- **ViewModel**: Employed for managing UI-related data in a lifecycle-conscious way.
- **Gradle**: For project build automation.

## Download From Here
https://objects.githubusercontent.com/github-production-release-asset-2e65be/833581838/663981c8-e8c1-44ea-826e-05d470848147?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=releaseassetproduction%2F20241004%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20241004T060030Z&X-Amz-Expires=300&X-Amz-Signature=744cab56a12489785edd666f55c6adbb323f3a9b7542a0b4a4f3189b223e6bbf&X-Amz-SignedHeaders=host&response-content-disposition=attachment%3B%20filename%3Dapp-debug.apk&response-content-type=application%2Fvnd.android.package-archive

## Activity Details

1. **RepoDetailsActivity**
   - **Purpose**: Displays the details of a repository, including the README file, star count, and language contributions.
   - **Components**: 
     - TextViews for username and repository name.
     - MaterialButton for starring the repository and showing the star count.
     - ProgressBar for loading indication.
     - MaterialCardView for viewing code.
     - Toolbar for navigation.
     - ScrollView for main content.
     - LanguageBarChartView for displaying language contributions.
   - **ViewModel**: `RepoDetailsViewModel` handles fetching repository details, starring/un-starring repositories, and managing loading states.

2. **StarredActivity**
   - **Purpose**: Displays a list of repositories starred by the user.
   - **Components**:
     - RecyclerView for displaying the list of starred repositories.
     - MaterialToolbar for navigation.
     - ProgressBar for loading indication.
     - TextView for displaying messages when the list is empty.
   - **ViewModel**: `StarRepoViewModel` handles fetching the list of starred repositories and managing loading states.

3. **RoutingActivity**
   - **Purpose**: Routes the user to the appropriate screen based on their authentication status.
   - **Components**:
     - SplashScreen for initial loading.
   - **ViewModel**: `AuthViewModel` checks if the user is authenticated and routes to either `LoginActivity` or `HomeActivity`.

4. **RegistrationActivity**
   - **Purpose**: Handles user registration.
   - **Components**:
     - EditTexts for username, email, password, and confirm password.
     - Button for registration.
     - CircularProgressIndicator for loading indication.
   - **ViewModel**: `AuthViewModel` handles user signup and manages loading states.

5. **HomeFragment**
   - **Purpose**: Serves as the home screen of the application.
   - **Components**:
     - MaterialButton for navigating to repository and starred repository screens.
   - **ViewModel**: `ProfileViewModel` provides user profile information.

6. **IntroActivity**
   - **Purpose**: Displays an introductory screen.
   - **Components**:
     - Adjusts layout parameters based on window insets.