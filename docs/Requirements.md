# iTrust2 Requirements
Version 1
September 2017

## Introduction
This project involves the development of an application through which health care providers can obtain and share essential patient information and can view aggregate patient data. Currently, access to a patient's history regarding previous medical problems, previous surgery, medications, allergies and other factors are often difficult or obtainable only from a patient's recollection. Now, as more hospitals and doctor's offices are automated, this information is available electronically. However, it is not accessible by other doctors and is often only viewed through some proprietary software so it can not be shared.

The final product is a site where health care workers can access important patient information, the non-emergency access can be controlled, and all access is tracked. Security and privacy of such a system are of paramount importance. HIPAA rules protect patients' information and also allow a patient to dictate who can access this information.

## Glossary
There are currently three roles in the iTrust2 Medical Records system.  The role of a user determines their viewing and editing capabilities.

  * Health Care Personnel (HCP): A health care professional who works with medical records.  There are several types of HCPs:
     * Licensed Health Care Professional (LHCP): A licensed health care professional that is allowed by a particular patient to view all approved medical records. In general, a patient does not know this non-designated health care professional, such as an emergency room doctor, and the set of approved records may be smaller than that granted to a designated licensed healthcare professional.
     * Designated Licensed Health Care Professional (DLHCP): A licensed health care professional that is allowed by a particular patient to view all approved medical records. Any LHCP can be a DLHCP to some patients (with whom he/she has an established relationship) and an LHCP to others (whom he/she has never/rarely seen before).
     * Unlicensed Authorized Personnel (UAP): A health care worker such as a medical secretary, case manager, care coordinator, or other authorized clerical-type personnel. An unlicensed personnel can enter and edit demographic information, diagnosis, office visit notes and other medical information, and can view records.
  * Patient: Upon their first visit to a practice using the iTrust2 medical records system a patient is assigned a medical identification (MID) name and password. Then, this person's electronic records are accessible via the iTrust2 Medical Records system.
  * Administrator: The administrator assigns medical identification numbers and passwords to LHCPs. [Note: for simplicity of the project, an administrator is added by directly entering the administrator into the database by an administrator that has access to the database.]

## Functional Requirements

  * [UC1 User Functionality](uc1)
  * [UC2 Authenticate Users](uc2)
  * [UC3 Log Transactions](uc3)
  * [UC4 Demographics](uc4)
  * [UC5 Hospitals](uc5)
  * [UC6 Appointments](uc6)
  * [UC7 Office Visit](uc7)
  * [UC8 Basic Health Metrics](uc8)

## Non-functional Requirements

  * NFR 1. Implementation must not violate HIPAA guidelines.
  * NFR 2. Exclusive Authentication - The system shall enable multiple simultaneous users, each with his/her own exclusive authentication.
  * NFR 3. Form Validation - The form validation of the system shall show the errors of all the fields in a form at the same time.
  * NFR 4. Privacy Policy - The system shall have a privacy policy linked off of the home page.  
  * NFR 5. Security of MID - MIDs are considered private, sensitive information and must not be displayed in URLs and on most pages.

## Constraints

  * C 1. Backend must use Java and Hibernate.
  * C 2. Frontend must use AngularJS or Thymeleaf.  All new pages must be implemented in AngularJS.
  * C 3. Maven will be used for build lifecycles and dependency management.
  * C 4. Testing frameworks are JUnit, Selenium, and Cucumber.
  * C 5. Tests must maintain at least 70% statement coverage of all Java classes.
  * C 6. Tests must pass on the deployed system.
  * C 7. All database connections must be released quickly after a transaction.
  * C 8. No CheckStyle notifications when using the provided configuration file


