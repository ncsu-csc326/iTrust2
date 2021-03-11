# UC4 Demographics

## 4.1 Preconditions
The iTrust2 user has authenticated themselves in the iTrust2 Medical Records system [UC2](uc2).

## 4.2 Main Flow
Demographic information is entered and/or edited [S1, S2, S3]. The user is presented with a success or failure message and the form is updated so that the user may correct the form or add more information.

## 4.3 Sub-flows

  * [S1] A patient may enter or edit their own demographic information.  Details about the data format for demographic information is in Section 4.6 [E1].
  * [S2] HCP must enter the MID of a patient and then enter or edit demographic information [E1].
  * [S3] An HCP may enter or edit their own demographic information according to the data format in Section 4.6 [E1]. 
  * [S4] An ER may enter or edit their own demographic information according to the data format in Section 4.6 [E1]. 
  * [S5] A Lab Tech may enter or edit their own demographic information according to the data format in Section 4.6 [E1]. 


## 4.4 Alternative Flows
  * [E1] The system prompts the patient or HCP to correct the format of a required data field because the input of that data field does not match that specified in Section 4.6.


## 4.5 Logging
| Transaction Code | Verbose Description | Logged In MID | Secondary MID | Transaction Type | Patient Viewable |
|------------------|---------------------|---------------|---------------|------------------|------------------|
| 400 | Demographics viewed by user | HCP or Patient or Lab Tech or Emergency Responder | Patient | View | Yes |
| 410| Demographics edited by user | HCP or Patient or Lab Tech or ER | Patient | Edit | Yes |

## 4.6 Data Format
| Field | Format |
|-------|--------|
|First Name  |Up to 20 alpha characters and symbols -, ', and space|
|Last Name  |Up to 30 alpha characters and symbols  -, ', and space|
|Preferred Name| Up to 30 alpha characters and symbol  -, ', and space (optional)|
|Mother (username) | Up to 20 alpha characters and existing user in the system (optional)|
|Father (username) | Up to 20 alpha characters and existing user in the system (optional)|
|Email  |Up to 30 alphanumeric characters and symbols ., _, and @|
|Street Address 1  |Up to 50 alphanumeric characters and symbols: . and space|
|Street Address 2  |Up to 50 alphanumeric characters and symbols: . and space (optional )|
|City  |Up to 15 alpha characters|
|State  |Approved 2-letter state abbreviation|
|Zip Code  |5 digits-4 digits (the latter part – 4 digits– is optional)|
|Phone  |3 digits-3 digits-4 digits|
|Date of Birth| Format: 2 digit month/2 digit day/4 digit year|
|Ethnicity|One of Caucasian, African American, Hispanic, Native American or Pacific Islander, Asian, Not Specified|
|Gender|One of Male, Female, Other, Not Speficified|


