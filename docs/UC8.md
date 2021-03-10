# UC8 Basic Health Metrics

## 8.1 Preconditions
An HCP is a registered user of the iTrust2 Medical Records system ([UC1](uc1)).  The HCP has authenticated themselves in the iTrust2 Medical Records system ([UC2](uc2)).  The HCP has started documenting an office visit for the patient ([UC7](uc7)).

## 8.2 Main Flow
The HCP enters health metrics appropriate for the age of the patient: under 3 years [S1], between 3 years and under 12 years [S2], and 12 and over [S3]. All ages are calculated as calendar years between the date of the office visit and the patient’s birthdate.  The HCP can edit the basic health metrics.  For creating or editing an office visit, the HCP enters the appropriate information and submits.  A success message is displayed and the HCP sees the saved information.

The patient can view the basic health metrics recorded for an office visit.

## 8.3 Sub-flows

  * [S1] If the patient is under three calendar years of age, the HCP enters the length, weight, head circumference, and household smoking status [S4][E1].
  * [S2] If the patient is three calendar years of age or older and under 12 calendar years of age, the HCP enters the height, weight, blood pressure, and household smoking status [S4][E1].
  * [S3] If the patient is 12 calendar years of age or older, the HCP enters the height, weight, blood pressure, household smoking status [S4], patient smoking status [S5], high density lipoproteins (HDL) cholesterol, low density lipoproteins (LDL) cholesterol, and triglycerides [E1].
  * [S4] The HCP selects one of the following menu options to specify the smoking status of household members: 1 - non-smoking household, 2 - outdoor smokers, 3 - indoor smokers.
  * [S5] The HCP selects one of the following menu options to specify the smoking status of the patient: 1 - Never smoker; 2 - Former smoker; 3 - Current some days smoker; 4 - Current every day smoker; 5 - Smoker, current status unknown; 9 - unknown if ever smoked.

## 8.4 Alternative Flows

  * [E1] An error message is displayed describing what entries do not conform to appropriate data formats.

## 8.5 Logging
| Transaction Code | Verbose Description | Logged In MID | Secondary MID | Transaction Type | Patient Viewable |
|------------------|---------------------|---------------|---------------|------------------|------------------|
| 800 | Create basic health metrics | HCP | Patient | Create | Yes |
| 801 | HCP views basic health metrics | HCP | Patient | View | Yes |
| 802 | HCP edits basic health metrics | HCP | Patient | Edit | Yes |
| 810 | Patient views basic health metrics for an office visit | Patient | HCP | View | Yes |

## 8.6 Data Format
| Field | Format |
|-------|--------|
|Height/Length  |Up to 3-digit number + up to 1 decimal place (inches) **greater than 0**.|
|Weight  |Up to 4-digit number + up to 1 decimal place (pounds) **greater than 0**.|
|Head Circumference  |up to 3-digit number + up to 1 decimal place (inches) **greater than 0**.|
|Blood Pressure  |Systolic is the top value. Diastolic is the bottom value.  Both are positive integers that should be at most three digits. (mmHg)|
|Cholesterol  |HDL [integer between 0 and 90, inclusive], LDL [integer between 0 and 600, inclusive], triglyceride [integer between 100 and 600, inclusive] (mg/dL)|
|Household Smoking Status  | 1 - non-smoking household, 2 - outdoor smokers, 3 - indoor smokers  |
|Patient Smoking Status  |1 - Never smoker; 2 - Former smoker; 3 - Current some days smoker; 4 - Current every day smoker; 5 - Smoker, current status unknown; 9 - unknown if ever smoked  |

## 8.7 Acceptance Scenarios

**Scenario 1:: Add Basic Health Metrics to 4-month-old well check office visit**

HCP Shelly Vang authenticates into iTrust2. Dr. Vang chooses to document a new office visit for Brynn McClain (born May 1, 2017) on October 1, 2017 at Central Hospital with the note, "Brynn can start eating rice cereal mixed with breast milk or formula once a day.". Dr. Vang enters Brynn's weight as 16.5lbs, length as 22.3in, head circumference as 16.1in, and household smoking status of "non-smoking household". The system displays a message that the office visit details were updated successfully.

**Scenario 2:: Add Basic Health Metrics to 2-year-old office visit for strep throat**

HCP Shelly Vang authenticates into iTrust2. Dr. Vang chooses to document a new office visit for Caldwell Hudson (born September 29, 2015) on October 28, 2017, at Central Hospital with the note, "Diagnosed with strep throat. Avoid contact with others for first 24 hours of antibiotics.". Dr. Vang enters Caldwell's weight as 30.2lbs, length as 34.7in, head circumference as 19.4in, and household smoking status of "indoor smokers". The system displays a message that the office visit details were updated successfully. 

**Scenario 3:: Add Basic Health Metrics to 5-year-old well check office visit**

HCP Shelly Vang authenticates into iTrust2. Dr. Vang chooses to document a new office visit for Fulton Gray (born October 10, 2012) on October 13, 2017, at Central Hospital with the note, "Fulton has all required immunizations to start kindergarten next year.". Dr. Vang enters Fulton's weight as 37.9lbs, height as 42.9in, blood pressure of 95/65 mmHg, and household smoking status of "outdoor smokers". The system displays a message that the office visit details were updated successfully.

**Scenario 4:: Add Basic Health Metrics to 20-year-old well check office visit**

HCP Shelly Vang authenticates into iTrust2. Dr. Vang chooses to document a new office visit for Daria Griffin (born October 25, 1997) on October 25, 2017, at Central Hospital with the note, "Patient is healthy". Dr. Vang enters Daria's weight as 124.3lbs, height as 62.3in, blood pressure of 110/75 mmHg, household smoking status of "non-smoking household", a patient smoking status of "3 - Former smoker", HDL as 65, LDL as 102, and Triglycerides as 147. The system displays a message that the office visit details were updated successfully.

**Scenario 5:: Add Basic Health Metrics to 24-year-old well check office visit**

HCP Shelly Vang authenticates into iTrust2. Dr. Vang chooses to document a new office visit for Thane Ross (born January 3, 1993) on October 25, 2017, at Central Hospital with the note, "Thane should consider modifying diet and exercise to avoid future heart disease". Dr. Vang enters Thane's weight as 210.1lbs, height as 73.1in, blood pressure of 160/100 mmHg, household smoking status of "non-smoking household", a patient smoking status of "4 - Never smoker", HDL as 37, LDL as 141, and Triglycerides as 162. The system displays a message that the office visit details were updated successfully.
