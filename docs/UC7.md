# UC7 Office Visit

## 7.1 Preconditions
The iTrust2 user has authenticated themselves in iTrust2 [UC2](uc2).

## 7.2 Main Flow
The patient can view a documented office visit [S1].

The HCP can document an office visit [S2] and provides details about the visit [S3].

## 7.3 Sub-flows

  * [S1]: The patient selects to view an office visit.  They see all information the HCP recorded for the office visit.  They cannot edit the information.
  * [S2]: The HCP selects to document an office visit by recording if the office visit was prescheduled, any notes about the visit, the patient, appointment type, hospital the patient was seen at, the date, and time of the visit.  They fill out a form and press Submit Office Visit [E1].  A message shows if the office visit was entered correctly.
  * [S3]: The HCP provides additional details about the office visit as appropriate for the appointment type:
     * General Checkup
          * Basic Health Metrics [(UC8)](uc8)
     * [Ophthalmology Office Visit](uc21)
     * [Ophthalmology Surgeries](uc22)

## 7.4 Alternative Flows

 * [E1] The system prompts the HCP to correct the format of a required data field because the input of that data field does not match that specified in Section 7.7 for the office visit data or for any other collected data [S3].


## 7.5 Logging
| Transaction Code | Verbose Description | Logged In MID | Secondary MID | Transaction Type | Patient Viewable |
|------------------|---------------------|---------------|---------------|------------------|------------------|

## 7.6 Data Format
| Field | Format |
|-------|--------|
| Notes | Up to 500 characters |
| Date| 2 digit month/2 digit day/4 digit year|
|Time| 2 digit hour:2 digit minutes[space][am|pm]|