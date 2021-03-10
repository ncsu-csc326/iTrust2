# UC6 Appointments

## 6.1 Preconditions
The iTrust2 user has authenticated themselves in iTrust2 [UC2](uc2).

## 6.2 Main Flow
The patient can request to schedule an appointment with an HCP [S1].  The patient can view and delete their appointment requests [S2].  

The HCP can view appointment requests and approve or decline them [S3].  The HCP can view upcoming approved appointments [S4].

## 6.3 Sub-flows

  * [S1]The patient enters information about the appointment request and presses the Submit Request button [E1].  A message confirms the request.
     * HCP
     * Date
     * Time
     * Comments
     * Type
  * [S2] The patient views their appointment request.  The patient can select a request and delete the request.  A message confirms the request.
  * [S3] The HCP views appointment requests.  The HCP can select to approve or decline the requestion.  A message confirms the action.  The appointment will no longer be visible in the patient's appointment request view.
  * [S4] The HCP view upcoming appointments.

## 6.4 Alternative Flows

  * [E1] The system prompts the patient or HCP to correct the format of a required data field because the input of that data field does not match that specified in Section 6.6.


## 6.5 Logging
| Transaction Code | Verbose Description | Logged In MID | Secondary MID | Transaction Type | Patient Viewable |
|------------------|---------------------|---------------|---------------|------------------|------------------|
|611|Upcoming appointment viewed| HCP or Patient | Patient or HCP | View | Yes|
|640|Appointment requested by patient |Patient|HCP | Create | Yes |
|641|Appointment request(s) viewed|Patient| HCP|View |Yes|
|642|Appointment request deleted by patient|Patient|HCP|Delete|Yes|
|650|Appointment request approved by HCP|HCP |Patient|Edit|Yes|
|651|Appointment request denied by HCP|HCP|Patient|Edit|Yes|
|652|Appointment request was updated|HCP|Patient|Edit|Yes|


## 6.6 Data Format

| Field | Format |
|-------|--------|
| HCP | HCP User name from system |
| Date| 2 digit month/2 digit day/4 digit year|
|Time| 2 digit hour:2 digit minutes[space][am or pm]|
|Comments| Up to 50 characters |
|Type| One of General Checkup |