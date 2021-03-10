# Developer's Guide
The developer's guide details how to setup, test, and run iTrust2.

# Setup iTrust2 Development Environment
Follow the [CSC326 Technology Installation Instructions](https://pages.github.ncsu.edu/engr-csc326-staff/326-course-page/install/) to set up your development environment.  

## Obtaining iTrust2
Only one team member should complete the following tasks (with the support of the other team members).  The other team members should wait to get their copy of iTrust2 until all the setup tasks are complete.

### Clone Repository
You and your team have been assigned a repository for working with iTrust2.  Clone the repository, either using [eGit in Eclipse](https://pages.github.ncsu.edu/engr-csc216-staff/CSC216-SE-Materials/git-tutorial/git-clone.html#cloning-in-egit) or through [Git Bash](https://pages.github.ncsu.edu/engr-csc216-staff/CSC216-SE-Materials/git-tutorial/git-clone.html#cloning-in-git-bash), to your local machine.

#### Download Project from iTrust2 Repository
Navigate to the [iTrust2 repository](https://github.ncsu.edu/engr-csc326-staff/iTrust2-v3/) in your favorite browser.  Click the **Clone or download** button and select the bottom right option to **Download ZIP**.

### Extract iTrust2 into Repo
Extract the contents of the ZIP, including `README.txt` and `.gitignore` into the local copy of your repository.  When you are done, the directory structure should look like the following:

```
csc326-20X-Y-Z #Where X is your lab section, Y is the project abbreviation, and Z is your team number
 |--- iTrust2
       |--- iTrust2 sub folders
 |--- .gitignore
 |--- README.txt
```
Your directory structure should **not** have iTrust2 or any of the other files inside of some other folder.  Your project structure needs to look exactly like the one above does, or _Jenkins will be unable to build your project_.

### Import iTrust2 into Eclipse Workspace
If you cloned your repository on the command line, you'll need to add a reference to the repository in Eclipse by adding a local repository in the *Git Repositories* view.  

If you cloned your repository using eGit, the repository will be listed in the *Git Repositories* view.  

In the *Git Repositories* view, select the Refresh button in the top level tool bar.  This will update the Eclipse view of the repositories as updated on your file system.

**YOU MUST DO THE FOLLOWING EXACTLY OR YOUR PROJECT WON'T LAUNCH IN A BROWSER!**

To import your project: 

  * Right click in the **Package Explorer** and select **Import... > Maven > Existing Maven Projects**.  
  * Browse to your repository location
  * Select `/iTrust2/pom.xml`
  * Click **Finish**

Give your workspace time to build.  If you run into issues with your project building, you may want to shut down Eclipse, delete the `.m2` directory in your user directory and restart Eclipse with a fresh Maven setup.

## iTrust2 Properties
There are two properties files that need to be set up for iTrust2 to talk with your database and an email account.  For the first two files, make a copy of the `*.properites.template` file and edit the file to contain your database's password.  `.gitignore` has been set up so that the two files will not be pushed to GitHub, but double check that they are not in your first push.

  * `src/main/java/db.properties.template` copied to `src/main/java/db.properties`
  * `src/main/java/email.properties.template` copied to `src/main/java/email.properties`

Edit the _second_ file (`email.properties`) and add in the credentials of a throwaway Gmail account.  Your team can create one; we suggest naming it something like `csc326f18-20x-y`.  The first two lines of the properties file get the email account; the third one gets the password for your account.  You will also need to [enable less secure access](https://support.google.com/accounts/answer/6010255?hl=en) on your email account.

## Setting up Admin User
An Admin user is required to start using iTrust2.  It's also helpful to have a HCP and Patient to work with.

Run `edu.ncsu.csc.itrust2.utils.HibernateDataGenerator` in the `src/test/java/` folder to create a starting set of users. This should create all the users required for running the test suite later, and refreshes the database into "good to test" state.

## Run iTrust2 on Jetty
See [`Phase 5: Test Jetty` from the CSC326 Install instructions](https://pages.github.ncsu.edu/engr-csc326-staff/326-course-page/install/#phase-5-eclipse-setup).

Once the server is running, open your favorite browser and go to http://localhost:8080/iTrust2.  You'll see our favorite Dr. Jenkins' smiling face!

## Run Tests
Just like with CoffeeMaker, the server must be running for Selenium and Cucmber tests to run.  You can run all of the tests at once by right clicking on the `src/test/java` folder and selecting **Run As > JUnit** or you can run the tests by type by selecting the `unit`, `apitest`, `selenium`, or `cucmber` folders.

If tests seem to fail for no reason, try running `edu.ncsu.csc.itrust2.utils.HibernateDataGenerator` in the `src/test/java/` folder before running the tests again. Its possible that your database is in a state that is incompatible with the current test suite.

## Run CheckStyle
iTrust2 has been set up with the CSC Checkstyle configuration in `src/test/resources/reporting/csc_checkstyle.xml`.  The requirement for the `@author` tag has been removed, so you can update your local Checkstyle to use the updated configuration file in your iTrust2 project.

## Run Maven Build
Maven is set up to run locally so you can start testing what the build will look like on Jenkins (which will be ready by Part 2).  The goals are `clean test verify checkstyle:checkstyle`. 

**The Jetty server is started/stopped as part of the build.  Do NOT have a local instance of Jetty running, or you'll receive errors during the build.**

## Push to GitHub
After the initial configuration is complete and your iTrust2 is working locally, one team member should push the project to [GitHub](http://github.ncsu.edu).  Ensure that the project structure is correct.

## Share Project
The other team members should now clone the repository and import iTrust2 to their workspace by following the instructions starting with "Import iTrust2 into Eclipse Workspace", above.  Don't forget to import as a Maven project and set up your database properties!

# iTrust2 REST API Information
1. Make a `GET` request to `/login` and grab the `_csrf` token from the hidden input at the top of the Login form

2. Make a `POST` request to `/login` with the `_csrf` token from above and a valid username + password.  Add these as key/value pairs and set the body type to `x-www-form-urlencoded` as so:
![Postman example](http://i.imgur.com/O3EceR2.png)

3. Make any requests to API resources (`/api/v1/**`).  You do _not_ need to supply a username and password for these requests -- Spring will keep track of the session and authenticate automatically as long as you remain in the current session.

4. (Optionally) make a `POST` request to `/logout` with the CSRF token when you're done.

# Test Naming Conventions

Unit tests must have `Test` in their name.  `*Test` or `Test*` are valid names.  This will ensure that unit tests are run via the `surefire` plugin.

Cucumber step definitions must have `StepDefs` in their name. `*StepDefs` or `StepDefs*` are valid names.

Integration tests (Cucumber and Selenium) must have `IT` in their name `*IT` or `IT*` are valid names.  This will ensure that integration tests are run via the `failsafe` plugin.

# Notes and Quirks

The current implementation of "Personal Representatives" only works when logged in as a patient with a Patient object. If the patient you are logged in as prompts strange errors for Personal Representatives, go to their Edit Demographics page and fill in a set of demographics for the patient. This should create a Patient object for the user, and no longer prompt errors on Personal Representatives.