# Developer's Guide
The developer's guide details how to setup, test, and run iTrust2.

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
Similar to CoffeeMaker, there's a properties file that need to be set up for iTrust2 to talk with your database.  Copy `iTrust2/src/main/resources/application.yml.template` to `iTrust2/src/main/resources/application.yml` and add in your database credentials.  **Do not rename the file or it will break the gitignore setup**



## Run iTrust2 on Tomcat
To run iTrust2:
1. Right-click on iTrust2 in Eclipse
2. Select Run As -> `Maven Build...`
3. In the "Goals" field, type `spring-boot:run`
4. Click "Apply"
5. Click "Run"
6. If you see an error like:
```
[INFO] -------------------------------------------------------------
[ERROR] COMPILATION ERROR : 
[INFO] -------------------------------------------------------------
[ERROR] No compiler is provided in this environment. Perhaps you are running on a JRE rather than a JDK?
[INFO] 1 error
[INFO] -------------------------------------------------------------
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
```
make sure that you add a JDK to your Eclipse workspace: Window -> Preferences -> Java -> Installed JREs -> Remove the JRE selected and click Add -> Standard VM -> JRE Home -> Browse to where your JDK is located (for me, C:\Program Files\Java\jdk1.8.0_181) -> Click Finish -> Click the check next to the JDK -> click "Apply and Close". Then, try running again. Note that you do not need to keep entering the Maven goal; you can just do "Run As -> Maven Build" and Eclipse will remember what you did last

Once the server is running, open your favorite browser and go to http://localhost:8080/iTrust2.  You'll see our favorite Dr. Jenkins' smiling face!

## Setting up Admin User
An Admin user is required to start using iTrust2.  It's also helpful to have a HCP and Patient to work with.  Once iTrust2 launches, click the Generate Sample Users button on the login screen.


## Run Tests
Just like with CoffeeMaker, the server must be running for Cucmber tests to run.  You can run all of the tests at once by right clicking on the `src/test/java` folder and selecting **Run As > JUnit** or you can run the tests by type by selecting the `unit`, `apitest`, or `cucmber` folders.

If tests seem to fail for no reason, drop your database (through MySQL Workbench or similar) and rerun.  


## Run CheckStyle
iTrust2 has been set up with the CSC Checkstyle configuration in `src/test/resources/reporting/csc_checkstyle.xml`.  The requirement for the `@author` tag has been removed, so you can update your local Checkstyle to use the updated configuration file in your iTrust2 project.

## Run Maven Build
Maven is set up to run locally so you can start testing what the build will look like on Jenkins (which will be ready by Part 2).  The goals are `clean test integration-test checkstyle:checkstyle`. 

**The Tomcat application server is started/stopped as part of the build.  Do NOT have a local instance of Tomcat running, or you'll receive errors during the build.**

If you don't want to use Eclipse, you can also run iTrust2 in development mode directly from the command line.  To run the data generator, you can run `mvn -f pom-data.xml process-test-classes`; to run the main iTrust2 server, run `mvn jetty:run`.  Note that to run any of these, you'll need to have Maven on your path.  If you do not, instructions are available [here](https://maven.apache.org/install.html).

## Push to GitHub
After the initial configuration is complete and your iTrust2 is working locally, one team member should push the project to [GitHub](http://github.ncsu.edu).  Ensure that the project structure is correct.

## Share Project
The other team members should now clone the repository and import iTrust2 to their workspace by following the instructions starting with "Import iTrust2 into Eclipse Workspace", above.  Don't forget to import as a Maven project and set up your database properties!

# iTrust2 REST API Information
1. Make a `GET` request to `/login` and grab the `_csrf` token from the hidden input at the top of the Login form

2. Make a `POST` request to `/login` with the `_csrf` token from above and a valid username + password.  Add these as key/value pairs and set the body type to `x-www-form-urlencoded` as so:
[[images/postman.png]]


3. Make any requests to API resources (`/api/v1/**`).  You do _not_ need to supply a username and password for these requests -- Spring will keep track of the session and authenticate automatically as long as you remain in the current session.

4. (Optionally) make a `POST` request to `/logout` with the CSRF token when you're done.

# Test Naming Conventions

Unit tests must have `Test` in their name.  `*Test` or `Test*` are valid names.  This will ensure that unit tests are run via the `surefire` plugin.

Cucumber step definitions must have `StepDefs` in their name. `*StepDefs` or `StepDefs*` are valid names.

Integration tests (Cucumber and Selenium) must have `IT` in their name `*IT` or `IT*` are valid names.  This will ensure that integration tests are run via the `failsafe` plugin.

# [](#date-time-handling)Date/Time Handling in iTrust2

Dates and date-times in iTrust are stored in the backend as LocalDate and ZonedDateTime objects respectively. These objects allow for the easy conversion and storage of date strings and timezone information.

From the frontend, all date input fields use the `date` type, which automatically is parsed to JavaScript Date objects. When sending date information to the API, [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601) strings are used as the standard format. For dates, this is a string such as `2018-12-05` (yyyy-MM-dd), and for date-times this is a string such as `2018-12-05T14:10:00.000-05:00` (yyyy-MM-dd'T'hh:mm:ss.SSSZ). Converting JavaScript Date objects to these formats is easy! For date-times, the built in Date.toISOString() functionality can be used. For date-only strings, the dateTimeService in iTrust (dateTimeService.js) provides a toDateString() function for conversion.

For displaying dates on the frontend, we use the [AngularJS date filter](https://docs.angularjs.org/api/ng/filter/date). In most cases, dates and times are displayed separately, but they can be easily displayed in one field. For example, `visit.date | date : 'MM/dd/yyyy'` for dates, and `visit.date | date : 'shortTime'` for times.

On the backend, models using the LocalDate object use the following annotations for proper conversion:
```
@Basic
// Allows the field to show up nicely in the database
@Convert(converter = LocalDateConverter.class)
@JsonAdapter( LocalDateAdapter.class )
private LocalDate startDate;
```
and similarly for ZonedDateTime objects:
```
@Basic
// Allows the field to show up nicely in the database
@Convert( converter = ZonedDateTimeAttributeConverter.class )
@JsonAdapter( ZonedDateTimeAdapter.class )
private ZonedDateTime date;
```
