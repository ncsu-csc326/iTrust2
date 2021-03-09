# iTrust2-v7 (Fall 2020)

## Background
iTrust2 was founded as a class project for [CSC326 Software Engineering](http://courses.ncsu.edu/csc326) in the [Computer Science Department of NC State University](http://www.csc.ncsu.edu) and is maintained primarily by [Kai Presler-Marshall](https://kpresler.github.io/) and [Sarah Heckman](https://www.csc.ncsu.edu/people/sesmith5). The goal of iTrust2 is to engage students with software engineering practices on a significant, relevant, and growing application that includes security and privacy requirements.  Interested in using iTrust2 for your software engineering course?  Feel free to contact us.

iTrust2 is an electronics health records system (EHR) that provides patients with a means to maintain their health records and communicate with their health care providers (HCPs).  HCPs can record information about office visits including basic health metrics, diagnoses, prescriptions, eye care, and pregnancy care. iTrust2 follows [HIPAA statue](http://www.hhs.gov/ocr/hipaa/) for ensuring security and privacy of patient records.

## Technical Info
iTrust2 is written with Java EE and JavaScript.  It uses Spring to handle the backend and AngularJS on the frontend (currently AngularJS v1.6; work is underway to migrate to AngularJS v7) and runs with a MySQL/MariaDB database.  Testing is performed using Spring for the API and a combination of [Cucumber](https://cucumber.io/docs) and [Selenium](https://www.seleniumhq.org/) on the frontend.  

Setup instructions are located in the Developer's Guide in the Wiki.

iTrust2 is a successor to the original [iTrust](https://github.com/ncsu-csc326/iTrust) also developed at NC State University.

## Publications

iTrust and iTrust2 have resulted in two paper publications:
* [10+ years of teaching software engineering with iTrust: the good, the bad, and the ugly](https://dl.acm.org/citation.cfm?id=3183393)
* [Wait wait. No, tell me: analyzing Selenium configuration effects on test flakiness](https://dl.acm.org/citation.cfm?id=3338661)
