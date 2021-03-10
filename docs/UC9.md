# UC9 Prescriptions

## 9.1 Preconditions

An HCP, a patient, and an administrator are all registered in the iTrust2 system. An iTrust2 user is logged into the system. The HCP must have begun documenting an office visit.

## 9.2 Main Flow

The HCP adds a new prescription to the office visit. The HCP selects the name of the prescription from a list and enters other prescription information. Zero or more prescriptions may be added to the visit.

The patient views a table of their current and past prescriptions and information about each prescription.

The administrator updates the list of NDCs in the system. The user can add new NDCs and modify existing ones.

## 9.3 Sub-flows
* [S1] The HCP selects the name of the prescription from a list. There is no option to enter names not tracked by the system.
* [S2] The HCP enters the prescription dosage in milligrams.
* [S3] The HCP enters the start date for the prescription.
* [S4] The HCP enters the end date for the prescription.
* [S5] The HCP enters the number of renewals that can be made during the prescription period.
* [S6] The patient views a table containing current and past prescriptions.
* [S7] The administrator selects an option to add a new drug. The administrator enters a NDC, a name, and a description.
* [S8] The administrator selects an existing drug to edit. The administrator modifies the existing information and presses submit.

## 9.4 Alternative Flows
* [E1] An error message is displayed if the HCP or administrator enters invalid data.
* [E2] If the patient has no current or past prescriptions, the system displays a error message.

## 9.5 Logging

| Transaction Code | Verbose Description | Logged In MID | Secondary MID | Transaction Type | Patient Viewable |
|------------------|---------------------|---------------|---------------|------------------|------------------|
| 900 | Add NDC | Administrator | None | Create | No |
| 901 | Edit NDC | Administrator | None | Edit | No |
| 902 | Delete NDC | Administrator | None | Delete | No |
| 903 | View NDC | Administrator | None | View | No |
| 910 | Create prescription | HCP | Patient | Create | Yes |
| 911 | Modify prescription | HCP | Patient | Edit | Yes |
| 912 | Delete prescription | HCP | Patient | Delete | Yes |
| 913 | View prescriptions | Patient | HCP | View | Yes |
| 914 | Patient Views Prescriptions | Patient | None | View | Yes |

## 9.6 Data Format

### Drug

| Field | Format |
|-------|--------|
| name | Non-empty string, up to 50 characters |
| code | String of four digits, a dash, four digits, a dash, and two more digits |
| description | Up to 1024 characters |

### Prescription

| Field | Format |
|-------|--------|
| drug | String for NDC code |
| dose | Positive integer | 
| start | Month Day, Year |
| end | Month Day, Year |
| renewals | Positive integer |

## 9.7 Acceptance Scenarios

### Scenario 1 :: Add prescription to office visit

HCP Shelly Vang authenticates into iTrust2. Dr. Vang chooses to document a new office visit for Jim Bean (born April 12, 1995) on December 15, 2017 at Central Hospital with the note, "Jim appears to be depressed. He also needs to eat more vegetables." Dr. Vang enters Jim's weight as 130.4 lbs, height as 73.1 in, blood pressure as 160/80 mmHg, household smoking status of "non-smoking household", a patient smoking status of "Never smoker", HDL as 50, LDL as 123, and Triglycerides as 148. Dr. Vang selects "Enter a new prescription" and selects "Quetiane Fumarate - 61786-926-01" from the list. She then 100 mg for the dose, December 15, 2017 for the start date, December 14, 2018 for the end date, and 12 for the number of renewals. She clicks submit and a message notifies her that the entry was recorded successfully.

### Scenario 2 :: View prescriptions

Jim Bean authenticates into iTrust2. He clicks "View Prescriptions". He is presented with a list of current and past prescriptions. The table contains one entry: Drug: Quetiane Fumarate, Dose: 100 mg, Start: December 15, 2017, End: December 14, 2018, Renewals: 12

### Scenario 3 :: Add new prescription

Administrator Al Minister authenticates into iTrust2. He clicks "Add new NDC". He enters "1234-5678-90" for the NDC, "Test Product" for the name, and "Strong antiseptic" for the description. He clicks submit. A message notifies him the NDC was recorded successfully.