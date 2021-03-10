# UC2 Authenticate Users

## 2.1 Preconditions
A User has been created in iTrust2 [UC1](uc1).

## 2.2 Main Flow
A user enters their user name and password to gain role-based entry into the iTrust2 Medical Records system [E1].  A session that has been inactive for more than ten minutes is terminated [S1].  Upon successful authentication, the user will be directed to a personalized home page based on their role.  An authenticate session ends with the user logs out or closes the iTrust2 application.

## 2.3 Sub-flows

  * [S1] Electronic sessions must terminate after ten minutes of inactivity. Ensure that authentication is reset after a period of inactivity that exceeds ten minutes.


## 2.4 Alternative Flows

   * [E1] The user may try three times. After three failed attempts with a user id, lock out that username for 60 minutes. If the last 6 login attempts from a given IP address fail, across any number of users or non-existent usernames, that IP address will be locked out for 60 minutes. After the 60 minute lockout-period, a user gets 3 more attempts, and an IP address gets 6 more attempts. [E2]
   * [E2] If a user or IP address is locked out 3 times in a 24-hour period, the offending user or IP address will be banned from the system until re-authorized by a system administrator.



## 2.5 Logging
| Transaction Code | Verbose Description | Logged In MID | Secondary MID | Transaction Type | Patient Viewable |
|------------------|---------------------|---------------|---------------|------------------|------------------|
| 1 | Failed login | IP Address | N/A | Other | Yes |
| 2 | Successful login | MID | N/A | Other | Yes |
| 3 | Logged Out | MID | N/A | Other | Yes |
| 4 | User Locked Out | Attempted User | N/A | Other | Yes |
| 5 | IP Locked Out | IP Address | N/A | Other | Yes|
| 6 | User Banned | Attempted User | N/A | Other | Yes|
| 7 | IP Banned | IP Address | N/A | Other | Yes|


## 2.6 Data Format
|Field|Format|
|----|-----|
|time| YYYY-MM-DD HH:MM:SS|
|ip| A valid IPv4 or IPv6 address. A String in the form "X.X.X.X" where each X is a integer (base 10) from 0 to 255, or "X:X:X:X:X:X:X:X" where each X is a hexadecimal number from 0000 to FFFF (no 0x- prefix), and extraneous zeros may be omitted.|
|user| A valid user within the system|

## 2.7 Acceptance Scenarios

### Scenario 1 :: User locked out after 3 attempts
HCP Shelly Vang attempts to authenticate into iTrust2, but uses an incorrect password. A message informs her of her mistake. She tried again 2 more times, after which a message informs her she is locked out of the system. An hour later, she attempts to login with the correct credentials and successfully logs in.

### Scenario 2 :: User banned after 3 lockouts
HCP Shelly Vang attempts to authenticate into iTrust2, but uses an incorrect password. A message informs her of her mistake. She tried again 2 more times, after which a message informs her she is locked out of the system. An hour later, she repeats the entries, still incorrect, and is locked out again. After another hour, she tries again, and a message informs her that her account has been banned from the system, and she must contact an administrator.

### Scenario 3 :: IP locked out after 6 attempts
HCP Shelly Vang attempts to authenticate into iTrust2, but uses an incorrect password. A message informs her of her mistake. She tried again 2 more times, after which a message informs her she is locked out of the system. From the same machine, Patient Jim Bean attempts to authorize, but with the incorrect password. After his third attempt, a message is displayed indicating the IP address has been blocked for 1 hour. After 1 hour, Shelly Vang attempts to authenticate with the correct password and succeeds.

### Scenario 4 :: IP banned after 3 lockouts
An unregistered user attempts to log into iTrust. After six failed attempts, the system informs him his IP address has been locked for one hour. One hour later, he tries again, and after six attempts is locked out. After another hour, he tries again, and the system displays a message informing him his IP has been banned and he must contact an administrator to un-block his IP.