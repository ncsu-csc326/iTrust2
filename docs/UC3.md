# UC3 Log Transactions
Medical information is highly personal. Maintaining confidentiality and integrity of patient data is paramount. Complete log files are critical for performing forensics on inappropriate access (create, read, update, delete) of patient data and on the inappropriate granting of system privileges to users.

## 3.1 Preconditions
None

## 3.2 Main Flow
Any event which creates, views, edits, or deletes information is logged [S1]. Login failures, valid authentication, and log outs are also logged [S2].

## 3.3 Sub-flows

  * [S1] For creating, viewing, modifying, or deleting information, the following information is recorded: the MID of the logged in user, any appropriate secondary MID of the user whose information is being accessed, a transaction type corresponding to the given action, and the current timestamp. Individual audit codes related to specific use cases are presented within each Use Case description. The subflow and transaction values are based on Use Case. For example, any in the range of 100-199 are for UC1, any in the range of 300-399 are in UC3, etc.  The exception is for authentication [S2].
  * [S2] The values from range 1-99 are logging events which do not exist in any use case but are concerned with the system as a whole. Logging associated with authentication [UC2] is also in this range. Miscellaneous transaction codes 1-99 are presented in Section 3.5 below. 


## 3.4 Alternative Flows
None


## 3.5 Logging
| Transaction Code | Verbose Description | Logged In MID | Secondary MID | Transaction Type | Patient Viewable |
|------------------|---------------------|---------------|---------------|------------------|------------------|
| 1 | Failed login | IP Address | N/A | Other | Yes |
| 2 | Successful login | MID | N/A | Other | Yes |
| 3 | Logged Out | MID | N/A | Other | Yes |
| 10 | View Home Page | IP Address | N/A | View | No |