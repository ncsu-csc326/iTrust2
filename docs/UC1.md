# UC1 User Functionality

## 1.1 Preconditions
The iTrust2 Admin has authenticated themselves in the iTrust2 Medical Records system [UC3](uc3).

## 1.2 Main Flow
The admin selects the option to create a user [S1] or delete a user [S2].  A success message is displayed and the action is logged.

## 1.3 Sub-flows

  * [S1]: An Admin enters a user name, a password, confirm password, the role, and if the user is enabled (or active) in the system.  The Admin presses the button to add the user [E1][E2]. The user name serves as the User's Medical ID (MID). The possible roles are:
     * Patient
     * Health Care Provider (HCP)
	 * Optometrist HCP
	 * Ophthalmologist HCP
     * Admin
     * Emergency Responder (ER)
     * Lab Tech

  * [S2]: An Admin selects a user from the list of possible users, confirms the delete, and presses the button to delete the user.

## 1.4 Alternative Flows

  * [E1] The system prompts the Admin to correct the format of a required data field because the input of that data field does not match that specified in Section 1.6.
  * [E2] The password and repeated password must match or an error is displayed.

## 1.5 Logging

| Transaction Code | Verbose Description | Logged In MID | Secondary MID | Transaction Type | Patient Viewable |
|------------------|---------------------|---------------|---------------|------------------|------------------|
| 100 | New user created | Admin | User | Create | No |
| 101 | View user | Admin | User | View | No |
| 102 | View users | Admin | N/A | View | No |
| 103 | Delete user | Admin | User | Delete | No |
| 104 | Update user | Admin | User | Edit | No |

## 1.6 Data Format
| Field | Format |
|-------|--------|
|User Name  |Between 6 and 20 alpha characters and symbols - or _|
|Password | Between 6 and 20 characters |
|Repeated password | Between 6 and 20 characters |