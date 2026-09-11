# iTrust2 Test Cases — UC1, UC2, UC3

Format: Jira/Azure DevOps "Test Case" work item fields. One scenario per test case.
Automation reference points to the JUnit method implementing each case.

---

## UC1 — User Functionality

### TC-UC1-01: Admin deletes an existing user
- **Requirement**: UC1 §1.3 [S2] — Admin selects a user, confirms delete, presses delete button
- **Priority**: High
- **Preconditions**: Tester is authenticated as a user with the Admin role. At least one User (e.g. a HCP) already exists in the system.
- **Test Data**: Existing username `hcp_test_1`
- **Steps**:
  1. Send `DELETE /api/v1/users/hcp_test_1` as an authenticated Admin.
  2. Query the user list / attempt to retrieve `hcp_test_1` by name.
- **Expected Result**:
  1. Response status is `200 OK`.
  2. `hcp_test_1` no longer exists in the system (user count decreases by one; lookup by name returns nothing).
- **Automation reference**: `APIUserTest.testDeleteUser` (part 1)

### TC-UC1-02: Deleting a user that does not exist
- **Requirement**: UC1 §1.3 [S2] (negative path)
- **Priority**: Medium
- **Preconditions**: Tester is authenticated as Admin. No user named `ghost_user` exists.
- **Test Data**: Non-existent username `ghost_user`
- **Steps**:
  1. Send `DELETE /api/v1/users/ghost_user` as an authenticated Admin.
- **Expected Result**:
  1. Response status is `404 Not Found`.
  2. No user records are affected.
- **Automation reference**: `APIUserTest.testDeleteUser` (part 2)

### TC-UC1-03: User creation rejected when password confirmation does not match
- **Requirement**: UC1 §1.4 [E2] — password and repeated password must match
- **Priority**: High
- **Preconditions**: None (unauthenticated user-creation endpoint).
- **Test Data**: username `new_patient_1`, password `Secret123`, confirm password `DIFFERENT456`
- **Steps**:
  1. Submit `POST /api/v1/users` with the above username/password/confirm-password and role Patient.
- **Expected Result**:
  1. Response status is a `4xx` client error.
  2. No User record is created (user count unchanged).
- **Automation reference**: `APIUserTest.testCreateUser_passwordConfirmationMismatch`

### TC-UC1-04: Creating and deleting a user generates the correct audit log entries
- **Requirement**: UC1 §1.5 Logging — codes 100 (New user created) and 103 (User deleted)
- **Priority**: High
- **Preconditions**: Tester is authenticated as Admin (username `admin`).
- **Test Data**: New username `audit_user_1`
- **Steps**:
  1. Create the user via `POST /api/v1/users`.
  2. Query the audit log for transaction code 100 attributed to `admin`.
  3. Delete the user via `DELETE /api/v1/users/audit_user_1`.
  4. Query the audit log for transaction code 103 attributed to `admin`.
- **Expected Result**:
  1. Exactly one log entry with code 100 exists, logged-in MID = `admin`, secondary MID = `audit_user_1`.
  2. Exactly one log entry with code 103 exists, logged-in MID = `admin`.
- **Automation reference**: `APIUserTest.testCreateAndDeleteUser_logTransactions`

### TC-UC1-05: Viewing and updating a user generates the correct audit log entries
- **Requirement**: UC1 §1.5 Logging — codes 101 (View user), 102 (View users), 104 (Update user)
- **Priority**: Medium
- **Preconditions**: Tester is authenticated as Admin (username `admin`). User `view_user_1` already exists.
- **Test Data**: Username `view_user_1`
- **Steps**:
  1. `GET /api/v1/users/view_user_1` (view single user).
  2. `GET /api/v1/users` (view full list).
  3. `PUT /api/v1/users/view_user_1` with an added role (update).
  4. Query the audit log for codes 101, 102, and 104.
- **Expected Result**:
  1. Exactly one code-101 entry is logged for the view-single-user action.
  2. Exactly one code-102 entry is logged for the view-list action, attributed to `admin`.
  3. Exactly one code-104 entry is logged for the update action, attributed to `admin`.
- **Known deviation to verify**: the code-101 entry is currently logged with the *viewed* user as the primary MID rather than `admin`, which differs from the documented Logging table (Logged In MID: Admin). Confirm whether this is intentional before treating a mismatch as a defect.
- **Automation reference**: `APIUserTest.testViewAndUpdateUser_logTransactions`

### TC-UC1-06 (NOT AUTOMATED — feature gap): Username/password format is rejected when out of range
- **Requirement**: UC1 §1.4 [E1] / §1.6 Data Format — username and password must be 6–20 characters
- **Priority**: High
- **Status**: **Blocked — not implemented.** No length or character-set validation currently exists on the User creation form or model.
- **Preconditions**: None.
- **Test Data**: username `abc` (too short, 3 chars), password `12` (too short, 2 chars)
- **Steps**:
  1. Submit `POST /api/v1/users` with the undersized username/password above.
- **Expected Result (per spec)**: Response should be a `4xx` client error with a validation message; no User created.
- **Actual current behavior**: Request succeeds and the User is created regardless of length.
- **Recommendation**: File as a defect/backlog item against UC1 rather than a test case to automate as-is.

---

## UC2 — Authenticate Users

### TC-UC2-01: Successful login records success and clears prior failed attempts
- **Requirement**: UC2 §2.2 Main Flow
- **Priority**: High
- **Preconditions**: A registered user exists (e.g. `main_flow_user` / `password1`). The user has exactly one prior failed login attempt on record.
- **Test Data**: username `main_flow_user`, correct password `password1`
- **Steps**:
  1. Submit `POST /login` with the correct username and password.
  2. Check the failed-attempt counter for this user.
  3. Query the audit log for a LOGIN_SUCCESS (code 2) entry.
- **Expected Result**:
  1. Login succeeds (redirected to the role-appropriate home page).
  2. The user's failed-attempt counter is reset to 0.
  3. Exactly one LOGIN_SUCCESS (code 2) entry is logged for this user.
- **Automation reference**: `LoginSecurityTest.testMainFlow_successfulLoginLogsAndClearsFailedAttempts`

### TC-UC2-02: User is locked out after 3 consecutive failed login attempts
- **Requirement**: UC2 §2.7 Acceptance Scenario 1
- **Priority**: High
- **Preconditions**: A registered user exists (e.g. `shellyVang`). No prior failed attempts on record.
- **Test Data**: username `shellyVang`, incorrect password `wrongPassword`
- **Steps**:
  1. Submit `POST /login` with the wrong password (attempt 1).
  2. Repeat (attempt 2). Confirm the account is still not locked.
  3. Repeat (attempt 3).
- **Expected Result**:
  1. After attempt 2, the account is not yet locked out.
  2. After attempt 3, the account is locked out for 60 minutes.
  3. A USER_LOCKOUT (code 4) entry is logged for `shellyVang`.
- **Automation reference**: `LoginSecurityTest.testScenario1_userLockedOutAfterThreeFailedAttempts`

### TC-UC2-03: User is banned after being locked out 3 times within 24 hours
- **Requirement**: UC2 §2.7 Acceptance Scenario 2
- **Priority**: High
- **Preconditions**: A registered user exists (e.g. `shellyVangRepeatOffender`). No prior lockout history.
- **Test Data**: 3 separate cycles of 3 consecutive failed login attempts (9 total), all within a 24-hour window
- **Steps**:
  1. Fail login 3 times → user is locked out (cycle 1).
  2. Fail login 3 more times → user is locked out again (cycle 2).
  3. Fail login 3 more times (cycle 3).
- **Expected Result**:
  1. After the 3rd lockout cycle, the user is banned from the system rather than merely re-locked.
  2. The ban persists until an Administrator re-authorizes the account.
  3. A USER_BANNED (code 6) entry is logged for the user.
- **Automation reference**: `LoginSecurityTest.testScenario2_userBannedAfterThreeLockoutsWithin24Hours`

### TC-UC2-04: IP address is locked out after 6 failed login attempts across different users
- **Requirement**: UC2 §2.7 Acceptance Scenario 3
- **Priority**: High
- **Preconditions**: Two registered users exist (e.g. `shellyVangIpTest`, `jimBean`). All login attempts originate from the same source IP address. No prior attempt history for this IP.
- **Test Data**: 3 failed attempts as User A, then 3 failed attempts as User B (6 total from the same IP)
- **Steps**:
  1. Fail login as User A three times (locks User A individually, per Scenario 1 behavior).
  2. Fail login as User B twice. Confirm the IP is not yet locked out.
  3. Fail login as User B a third time (6th failure overall from this IP).
- **Expected Result**:
  1. After 5 total failed attempts from the IP, the IP is not locked.
  2. After the 6th failed attempt, the source IP address is locked out for 60 minutes.
  3. User B is not additionally, individually locked out — the 6th attempt is consumed by the IP-level check.
  4. An IP_LOCKOUT (code 5) entry is logged against the IP address.
- **Automation reference**: `LoginSecurityTest.testScenario3_ipLockedOutAfterSixFailedAttemptsAcrossUsers`

### TC-UC2-05: IP address is banned after being locked out 3 times within 24 hours
- **Requirement**: UC2 §2.7 Acceptance Scenario 4
- **Priority**: High
- **Preconditions**: No prior attempt/lockout history exists for the source IP address under test. Attempts may use a non-existent username, consistent with an "unregistered user" per the spec narrative.
- **Test Data**: 3 separate cycles of 6 consecutive failed login attempts (18 total) from the same IP, each cycle separated by the 60-minute lockout window elapsing
- **Steps**:
  1. Fail login 6 times from the IP → IP is locked out (cycle 1).
  2. Wait for the 60-minute lockout window to elapse (or equivalent), then fail login 6 more times → IP is locked out again (cycle 2).
  3. Wait for the lockout window to elapse again, then fail login 6 more times (cycle 3).
- **Expected Result**:
  1. After the 3rd IP-lockout cycle within 24 hours, the IP is banned rather than merely re-locked.
  2. The IP ban persists until an Administrator re-authorizes it.
  3. An IP_BANNED (code 7) entry is logged against the IP address.
- **Automation reference**: `LoginSecurityTest.testScenario4_ipBannedAfterThreeLockoutsWithin24Hours` (automation simulates the elapsed hour by adjusting lockout timestamps directly, rather than waiting in real time)

### TC-UC2-06 (NOT AUTOMATED — feature gap): Session terminates after 10 minutes of inactivity
- **Requirement**: UC2 §2.3 [S1] — electronic sessions must terminate after 10 minutes of inactivity
- **Priority**: High
- **Status**: **Blocked — not implemented.** No session-timeout configuration was found in the application config or security config.
- **Preconditions**: A user is logged in and has an active session.
- **Test Data**: N/A
- **Steps**:
  1. Log in successfully.
  2. Remain idle (no requests) for 10 minutes and 1 second.
  3. Attempt to access an authenticated page/endpoint.
- **Expected Result (per spec)**: The session should be invalidated; the user should be redirected to re-authenticate.
- **Actual current behavior**: No 10-minute timeout is configured; default container/session timeout behavior applies instead.
- **Recommendation**: File as a defect/backlog item against UC2 rather than a test case to automate as-is.

---

## UC3 — Log Transactions

### TC-UC3-01: Create and edit actions are logged with the correct MID and timestamp
- **Requirement**: UC3 §3.3 [S1] — creating/viewing/editing/deleting logs the logged-in MID, transaction type, and timestamp
- **Priority**: Medium
- **Preconditions**: Tester is authenticated as Admin (username `admin`). No hospital named "UC3 Test Hospital" currently exists.
- **Test Data**: Hospital name `UC3 Test Hospital`, address `1 iTrust Test Street` → edited to `2 iTrust Test Street`
- **Steps**:
  1. Create the hospital via `POST /api/v1/hospitals`.
  2. Inspect the resulting audit log entry for the create action.
  3. Edit the hospital's address via `PUT /api/v1/hospitals/UC3 Test Hospital`.
  4. Inspect the resulting audit log entry for the edit action.
- **Expected Result**:
  1. Exactly one CREATE_HOSPITAL log entry exists, with logged-in MID = `admin` and a timestamp at/after the moment the action occurred.
  2. Exactly one EDIT_HOSPITAL log entry exists for the address change.
- **Automation reference**: `LogTransactionTest.testCreateAndEditActions_logMidAndTimestamp`

### TC-UC3-02: Delete action is logged for both a successful deletion and a failed (already-deleted) deletion
- **Requirement**: UC3 §3.3 [S1] (delete path, including a non-happy-path attempt)
- **Priority**: Medium
- **Preconditions**: Tester is authenticated as Admin. A hospital named "UC3 Test Hospital" exists.
- **Test Data**: Hospital name `UC3 Test Hospital`
- **Steps**:
  1. Delete the hospital via `DELETE /api/v1/hospitals/UC3 Test Hospital`.
  2. Inspect the audit log for the delete action.
  3. Repeat the same delete request against the now-nonexistent hospital.
  4. Inspect the audit log again.
- **Expected Result**:
  1. First deletion succeeds (`200 OK`); exactly one DELETE_HOSPITAL entry is logged.
  2. Second deletion fails (`404 Not Found`), but a second DELETE_HOSPITAL entry is still logged, per UC3's requirement that all such actions are recorded even on failure.
- **Automation reference**: `LogTransactionTest.testDeleteAction_logsCorrectCodeForSuccessAndFailure`

### TC-UC3-03: Authentication-related transaction codes fall within the reserved 1–99 range
- **Requirement**: UC3 §3.3 [S2] — codes 1–99 are reserved for system-wide/authentication events; UC-specific codes (e.g. UC1 = 100s) fall outside that range
- **Priority**: Low
- **Preconditions**: None (static verification of transaction code assignments).
- **Test Data**: `LOGIN_FAILURE`, `LOGIN_SUCCESS`, `LOGOUT`, `USER_LOCKOUT`, `IP_LOCKOUT`, `USER_BANNED`, `IP_BANNED`
- **Steps**:
  1. For each authentication-related transaction type above, read its assigned numeric code.
- **Expected Result**:
  1. Every code is between 1 and 99 inclusive.
- **Automation reference**: `LogEntryTest.testAuthenticationTransactionCodes_fallWithinReservedRange`

---

## Summary Table

| ID | Title | UC | Priority | Status |
|----|-------|----|----|--------|
| TC-UC1-01 | Admin deletes an existing user | UC1 | High | Automated |
| TC-UC1-02 | Delete a non-existent user | UC1 | Medium | Automated |
| TC-UC1-03 | Reject mismatched password confirmation | UC1 | High | Automated |
| TC-UC1-04 | Create/delete logs codes 100 & 103 | UC1 | High | Automated |
| TC-UC1-05 | View/update logs codes 101, 102, 104 | UC1 | Medium | Automated |
| TC-UC1-06 | Username/password length validation | UC1 | High | **Blocked — gap** |
| TC-UC2-01 | Successful login clears failed attempts | UC2 | High | Automated |
| TC-UC2-02 | User locked out after 3 failed attempts | UC2 | High | Automated |
| TC-UC2-03 | User banned after 3 lockouts / 24h | UC2 | High | Automated |
| TC-UC2-04 | IP locked out after 6 failed attempts | UC2 | High | Automated |
| TC-UC2-05 | IP banned after 3 lockouts / 24h | UC2 | High | Automated |
| TC-UC2-06 | 10-minute inactivity session timeout | UC2 | High | **Blocked — gap** |
| TC-UC3-01 | Create/edit logs MID & timestamp | UC3 | Medium | Automated |
| TC-UC3-02 | Delete logged on success and failure | UC3 | Medium | Automated |
| TC-UC3-03 | Auth codes reserved to 1–99 range | UC3 | Low | Automated |
