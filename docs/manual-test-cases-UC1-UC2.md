# iTrust2 Manual Test Cases — UC1 (User Functionality) & UC2 (Authenticate Users)

Format: Jira/Azure DevOps "Test Case" work item fields. One scenario per test case.
All steps describe manual actions in the browser UI — no API calls.

Pages referenced:
- **Login page** (`/login`) — fields: *Username*, *Password*; button: *Login*
- **Admin Manage Users page** (Admin > Manage Users) — fields: *Username*, *Password*, *Password (again)*, role checkboxes, *Enabled?* checkbox; buttons: *Add User*, *Delete*

---

## UC1 — User Functionality (Admin Manage Users page)

### TC-UC1-01: Admin successfully adds a new user
- **Requirement**: UC1 §1.3 [S1] — Admin creates a user
- **Priority**: High
- **Preconditions**: Tester is logged in as a user with the Admin role and is on the Manage Users page.
- **Test Data**: Username `patient_smith`, Password `Secret1`, Password (again) `Secret1`, Role: Patient, Enabled: checked
- **Steps**:
  1. In the "Add a User" panel, type `patient_smith` into the *Username* field.
  2. Type `Secret1` into the *Password* field.
  3. Type `Secret1` into the *Password (again)* field.
  4. Check the *ROLE_PATIENT* checkbox.
  5. Check the *Enabled?* checkbox.
  6. Click *Add User*.
- **Expected Result**:
  1. A green success message reading "User added successfully." appears.
  2. The new row for `patient_smith` appears in the "Existing Users" table above, showing role `ROLE_PATIENT` and "Enabled" = yes.
  3. The form fields are cleared after submission.

### TC-UC1-02: Adding a user with mismatched passwords is rejected
- **Requirement**: UC1 §1.4 [E2] — password and repeated password must match
- **Priority**: High
- **Preconditions**: Tester is on the Manage Users page.
- **Test Data**: Username `mismatch_user`, Password `Secret1`, Password (again) `Different2`
- **Steps**:
  1. Type `mismatch_user` into *Username*.
  2. Type `Secret1` into *Password*.
  3. Type `Different2` into *Password (again)*.
  4. Check any one role checkbox.
  5. Click *Add User*.
- **Expected Result**:
  1. A red error message reading "passwords do not match" appears below the form.
  2. No new row for `mismatch_user` appears in the "Existing Users" table.

### TC-UC1-03: Adding a user with a duplicate username is rejected
- **Requirement**: UC1 §1.3 [S1] (implicit uniqueness of MID/username)
- **Priority**: Medium
- **Preconditions**: A user named `existing_user` already appears in the "Existing Users" table.
- **Test Data**: Username `existing_user` (already in use), Password `Secret1`, Password (again) `Secret1`
- **Steps**:
  1. Type `existing_user` into *Username*.
  2. Type `Secret1` into both password fields.
  3. Check any one role checkbox.
  4. Click *Add User*.
- **Expected Result**:
  1. A red error message reading "Username already exists" appears below the form.
  2. No duplicate row is added to the "Existing Users" table.

### TC-UC1-04: Adding a user with a password shorter than 6 characters is rejected
- **Requirement**: UC1 §1.6 Data Format — password must be between 6 and 20 characters
- **Priority**: High
- **Preconditions**: Tester is on the Manage Users page.
- **Test Data**: Username `shortpw_user`, Password `abc12`, Password (again) `abc12`
- **Steps**:
  1. Type `shortpw_user` into *Username*.
  2. Type `abc12` (5 characters) into both password fields.
  3. Check any one role checkbox.
  4. Click *Add User*.
- **Expected Result**:
  1. A red error message reading "Password must be more than 6 characters long" appears below the form.
  2. No new row for `shortpw_user` appears in the "Existing Users" table.
- **Note**: this check only enforces a minimum length. There is no equivalent check anywhere in the UI for a maximum password length (spec says 20) or for username length/character format (spec says 6–20 alpha characters and `-`/`_`) — see TC-UC1-06.

### TC-UC1-05: Admin deletes an existing user
- **Requirement**: UC1 §1.3 [S2] — Admin selects a user, confirms the delete, and deletes them
- **Priority**: High
- **Preconditions**: A user named `to_delete_user` appears in the "Existing Users" table.
- **Test Data**: Username `to_delete_user`
- **Steps**:
  1. Locate the row for `to_delete_user` in the "Existing Users" table.
  2. Click the *Delete* button in that row.
- **Expected Result (per spec)**: The system should ask the Admin to confirm the deletion before it happens.
- **Actual current behavior**: Clicking *Delete* removes the user's row from the table immediately, with no confirmation prompt shown at any point.
- **Note / deviation to log**: this is a gap against UC1 §1.2 Main Flow, which states the Admin "confirms the delete" before it takes effect. Recommend filing as a defect: users can be deleted with a single accidental click.

### TC-UC1-06 (BLOCKED — feature gap): Username length/format and password maximum length are not validated
- **Requirement**: UC1 §1.6 Data Format — username 6–20 characters (alpha + `-`/`_`); password 6–20 characters
- **Priority**: High
- **Status**: **Blocked — not implemented.** No validation exists in the UI (or the underlying API) for username length/character set, or for a password exceeding 20 characters.
- **Preconditions**: Tester is on the Manage Users page.
- **Test Data**:
  - Case A — username too short: Username `ab`, Password `Secret1`, Password (again) `Secret1`
  - Case B — username contains disallowed characters: Username `bad user!`, Password `Secret1`, Password (again) `Secret1`
  - Case C — password too long: Username `longpw_user`, Password `ThisPasswordIsWayTooLongForTheSpec`, Password (again) same
- **Steps**: For each case, fill in the fields as above, check a role, and click *Add User*.
- **Expected Result (per spec)**: Each case should be rejected with an on-screen validation error, and no user should be created.
- **Actual current behavior**: All three cases succeed — the user is created and appears in the table with no warning.
- **Recommendation**: File as a defect/backlog item against UC1 rather than treating as a passing test case.

---

## UC2 — Authenticate Users (Login page)

### TC-UC2-01: Successful login with a correct username and password
- **Requirement**: UC2 §2.2 Main Flow
- **Priority**: High
- **Preconditions**: A registered, enabled user exists (e.g. an HCP account), and is not currently locked or banned.
- **Test Data**: Username `hcp`, Password `123456`
- **Steps**:
  1. Navigate to the Login page.
  2. Type `hcp` into the *Username* field.
  3. Type `123456` into the *Password* field.
  4. Click *Login*.
- **Expected Result**:
  1. The user is redirected away from the login page to their role-appropriate home page (no error banner is shown).

### TC-UC2-02: Login is rejected with an incorrect username or password
- **Requirement**: UC2 §2.2 Main Flow [E1] (single failed attempt, not yet locked out)
- **Priority**: High
- **Preconditions**: A registered user exists. No prior failed attempts are on record for this user or this machine.
- **Test Data**: Username `hcp`, Password `wrongPassword`
- **Steps**:
  1. Navigate to the Login page.
  2. Type `hcp` into *Username* and `wrongPassword` into *Password*.
  3. Click *Login*.
- **Expected Result**:
  1. The page reloads showing the message "Invalid username and password."
  2. The Username/Password fields are empty again, ready for another attempt.

### TC-UC2-03: Both Username and Password are required to submit the login form
- **Requirement**: UC2 §2.2 Main Flow (implicit — both credentials are needed to authenticate)
- **Priority**: Medium
- **Preconditions**: Tester is on the Login page.
- **Test Data**: N/A (fields left blank)
- **Steps**:
  1. Leave both *Username* and *Password* blank.
  2. Click *Login*.
- **Expected Result (typical expectation)**: The browser should block submission and indicate that both fields are required, before any request is sent.
- **Actual current behavior**: Neither field is marked as required in the page; clicking *Login* submits the empty form and the page reloads showing the generic "Invalid username and password." message rather than a field-specific "this field is required" prompt.
- **Note / deviation to log**: consider filing as a minor UX defect — a dedicated required-field message would be clearer than the generic invalid-credentials message for this case.

### TC-UC2-04: User is locked out after 3 consecutive failed login attempts
- **Requirement**: UC2 §2.7 Acceptance Scenario 1
- **Priority**: High
- **Preconditions**: A registered user exists (e.g. `shellyVang`). No prior failed attempts on record.
- **Test Data**: Username `shellyVang`, Password `wrongPassword` (intentionally incorrect)
- **Steps**:
  1. On the Login page, attempt to log in as `shellyVang` with the wrong password (attempt 1). Observe the "Invalid username and password." message.
  2. Repeat with the wrong password (attempt 2). Observe the same message.
  3. Repeat with the wrong password (attempt 3).
- **Expected Result**:
  1. After attempts 1 and 2, only the generic "Invalid username and password." message appears.
  2. After attempt 3, the page instead shows "Too many invalid logins. Account locked for 1 hour."
  3. Attempting to log in again immediately afterward with the *correct* password still shows the locked-out message rather than succeeding.

### TC-UC2-05: User is banned after being locked out 3 times within 24 hours
- **Requirement**: UC2 §2.7 Acceptance Scenario 2
- **Priority**: High
- **Preconditions**: The same user has already been locked out twice within the last 24 hours (see TC-UC2-04), and each 1-hour lockout window has since elapsed.
- **Test Data**: Username `shellyVang`, Password `wrongPassword`
- **Steps**:
  1. After the 2nd lockout's 1-hour window has passed, fail to log in 3 more times with the wrong password.
- **Expected Result**:
  1. On the 3rd failed attempt of this final cycle, the page shows "This account has been locked. Please contact a system administrator to re-enable." instead of the 1-hour lockout message.
  2. The account remains blocked even after waiting — it no longer self-clears after an hour, and requires an Administrator to re-enable it.

### TC-UC2-06: IP address is locked out after 6 failed login attempts, across different users, from the same machine
- **Requirement**: UC2 §2.7 Acceptance Scenario 3
- **Priority**: High
- **Preconditions**: Two registered users exist (e.g. `shellyVang`, `jimBean`). Both login attempts will be made from the same browser/machine. No prior failed-attempt history on this machine.
- **Test Data**: Username `shellyVang` / wrong password (x3), then username `jimBean` / wrong password (x3)
- **Steps**:
  1. On this machine, fail to log in as `shellyVang` 3 times (this locks `shellyVang`'s account individually — see TC-UC2-04).
  2. From the same machine, attempt to log in as `jimBean` with the wrong password (attempt 1 of 2 remaining before 6 total). Observe the generic invalid-credentials message.
  3. Repeat for `jimBean` (attempt 2, 5th total from this machine).
  4. Repeat for `jimBean` once more (3rd attempt for Jim, 6th total from this machine).
- **Expected Result**:
  1. After 5 total failed attempts from this machine, only the generic "Invalid username and password." message has appeared.
  2. On the 6th failed attempt, the page instead shows "Too many invalid logins. This IP is blocked for 1 hour." — even though this was only Jim's 3rd individual attempt.
  3. Jim's account is not shown as individually locked (a subsequent correct-password attempt for Jim from a *different* machine, if available, would succeed once the IP block clears).

### TC-UC2-07: IP address is banned after being locked out 3 times within 24 hours
- **Requirement**: UC2 §2.7 Acceptance Scenario 4
- **Priority**: High
- **Preconditions**: The same machine has already triggered the "IP is blocked for 1 hour" message twice within the last 24 hours (see TC-UC2-06), and each 1-hour window has since elapsed.
- **Test Data**: Any username (can be nonexistent, e.g. `unregistered_user`), wrong/any password
- **Steps**:
  1. After the 2nd IP lockout's 1-hour window has passed, fail to log in 6 more times from the same machine (any username).
- **Expected Result**:
  1. On the 6th failed attempt of this final cycle, the page shows "This IP has been banned. Please contact a system administrator to re-enable." instead of the 1-hour block message.
  2. The machine remains blocked from logging in even after waiting an hour, until an Administrator re-enables it.

### TC-UC2-08 (BLOCKED — feature gap): Session terminates after 10 minutes of inactivity
- **Requirement**: UC2 §2.3 [S1] — electronic sessions must terminate after 10 minutes of inactivity
- **Priority**: High
- **Status**: **Blocked — not implemented.** No 10-minute session timeout is configured anywhere in the application.
- **Preconditions**: Tester is logged in and viewing any authenticated page.
- **Test Data**: N/A
- **Steps**:
  1. Log in successfully.
  2. Leave the browser tab idle (no clicks, no navigation) for at least 10 minutes.
  3. After the wait, click any link or refresh the page.
- **Expected Result (per spec)**: The tester should be redirected to the Login page, as if logged out, because the session expired from inactivity.
- **Actual current behavior**: The tester remains logged in and can continue navigating normally past the 10-minute mark.
- **Recommendation**: File as a defect/backlog item against UC2 rather than a passing test case.

---

## Summary Table

| ID | Title | Priority | Status |
|----|-------|----------|--------|
| TC-UC1-01 | Add a new user successfully | High | Pass expected |
| TC-UC1-02 | Reject mismatched passwords | High | Pass expected |
| TC-UC1-03 | Reject duplicate username | Medium | Pass expected |
| TC-UC1-04 | Reject password under 6 characters | High | Pass expected |
| TC-UC1-05 | Delete a user (no confirmation shown) | High | **Deviation — log defect** |
| TC-UC1-06 | Username format / password max length | High | **Blocked — gap** |
| TC-UC2-01 | Successful login | High | Pass expected |
| TC-UC2-02 | Reject incorrect credentials | High | Pass expected |
| TC-UC2-03 | Both fields required | Medium | **Deviation — log defect** |
| TC-UC2-04 | User locked out after 3 failed attempts | High | Pass expected |
| TC-UC2-05 | User banned after 3 lockouts / 24h | High | Pass expected |
| TC-UC2-06 | IP locked out after 6 failed attempts | High | Pass expected |
| TC-UC2-07 | IP banned after 3 lockouts / 24h | High | Pass expected |
| TC-UC2-08 | 10-minute inactivity session timeout | High | **Blocked — gap** |
