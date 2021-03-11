# iTrust2

## Background
iTrust2 was founded as a class project for [CSC326 Software Engineering](http://courses.ncsu.edu/csc326) in the [Computer Science Department of NC State University](http://www.csc.ncsu.edu) and is maintained primarily by [Kai Presler-Marshall](https://kpresler.github.io/) and [Sarah Heckman](https://www.csc.ncsu.edu/people/sesmith5). The goal of iTrust2 is to engage students with software engineering practices on a significant, relevant, and growing application that includes security and privacy requirements.  Interested in using iTrust2 for your software engineering course?  Feel free to contact us.

iTrust2 is an electronics health records system (EHR) that provides patients with a means to maintain their health records and communicate with their health care providers (HCPs).  HCPs can record information about office visits including basic health metrics, diagnoses, prescriptions, eye care, and pregnancy care. iTrust2 follows [HIPAA statue](http://www.hhs.gov/ocr/hipaa/) for ensuring security and privacy of patient records.

## Technical Info
iTrust2 is written with Java EE and JavaScript.  It uses Spring to handle the backend and AngularJS on the frontend (currently AngularJS v1.6; work is underway to migrate to AngularJS v7) and runs with a MySQL/MariaDB database.  Testing is performed using Spring for the API and a combination of [Cucumber](https://cucumber.io/docs) and [Selenium](https://www.seleniumhq.org/) on the frontend. 

iTrust2 is a successor to the original [iTrust](https://github.com/ncsu-csc326/iTrust) also developed at NC State University.

Each semester, the teaching staff releases a new version of iTrust2, taking a project from one team that followed particularly good practise and implemented a particularly good version, and cleans it up a bit. 

Starting with `v8`, iTrust2 received a significant rewrite of the Java layers of the system.  This was done to upgrade to Spring Boot 2.x (`2.3.7.RELEASE` as of the time of this writing) and use a more moduler, component-based architecture.  This brought several benefits, such as transaction-based rollback in tests and separate test/production databases through Application Profiles.  However, from a functional perspective, iTrust2-v8 marks a significant regression from iTrust2-v7, losing about half of the usecases.  Efforts are underway to rewrite them.

## Setup & Documentation

If you want to take a trip back into time, you can check out the branches, `v1`, `v2`, ... to view older versions of iTrust2 as they stood when they were used.

Each branch contains a `docs` folder with setup instructions and a set of requirements (in the form of usecases) for the project as it existed at the time.

Due to some technical difficulties faced during the development process that could not be resolved in time for the start of the semester, iTrust2-v5 was never released, and iTrust2-v4 was used for two semesters in a row.



## Publications

iTrust and iTrust2 have resulted in two paper publications:
* [10+ years of teaching software engineering with iTrust: the good, the bad, and the ugly](https://dl.acm.org/citation.cfm?id=3183393)
* [Wait wait. No, tell me: analyzing Selenium configuration effects on test flakiness](https://dl.acm.org/citation.cfm?id=3338661)
