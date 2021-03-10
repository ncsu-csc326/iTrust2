# UC5 Hospitals

## 5.1 Preconditions
The administrator has authenticated themselves in the iTrust2 Medical Records system ([UC2](uc2)).

## 5.2 Main Flow
The administrator chooses to add [S1] or delete [S2] a hospital from iTrust2.

## 5.3 Sub-flows

  * [S1] The administrator enters the following information about the hospital that conforms to the data formats in Section 5.6 and presses the Add Hospital button:
     * Name
     * Address
     * State
     * Zip
  * [S2] An Admin selects a hospital from the list of possible hospitals, confirms the delete, and presses the button to delete the hospital.


## 5.4 Alternative Flows

  * [E1] The system prompts the Admin to correct the format of a required data field because the input of that data field does not match that specified in Section 5.6.


## 5.5 Logging
| Transaction Code | Verbose Description | Logged In MID | Secondary MID | Transaction Type | Patient Viewable |
|------------------|---------------------|---------------|---------------|------------------|------------------|
| 501 | New hospital created | Admin | N/A | Create | No |
| 502 | Hospital Viewed | Admin | N/A | View | No |
| 503 | Hospital edited | Admin | N/A | Edit| No |
| 504 | Hospital deleted | Admin | N/A | Delete| No |


## 5.6 Data Format
| Field | Format |
|-------|--------|
|Name  |Up to 20 alpha characters and symbols -, ', and space|
|Street Address 1  |Up to 50 alphanumeric characters and symbols: . and space|
|State| Two letter abbreviation for 50 US States |
|Zip Code  |5 digits-4 digits (the latter part – 4 digits– is optional)|