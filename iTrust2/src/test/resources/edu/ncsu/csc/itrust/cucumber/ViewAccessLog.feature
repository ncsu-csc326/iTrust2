Feature: View Access Logs
    As an registered user wants to view his/her access log

Scenario Outline: Landing screen
Given the required users exist
When <tuser> has logged in with password and chosen to view the access log
Then The first ten record should appear on the screen

Examples:
    |tuser|
    |svang|
    
Scenario Outline: Patient Views Prescription (Issue 106)
Given the required users exist
When <tuser> has logged in with password and chosen to view the access log
And The patient goes to the prescriptions page
And The user goes to the HomePage
Then The patient sees a PATIENT_PRESCRIPTION_VIEW log

Examples:
    |tuser  |
    |patient|


Scenario Outline: Choose access log within timeframe
Given the required users exist
When <tuser> has logged in with password and chosen to view the access log
When She selects the start date and end date
Then She sees the access log within this time frame.

Examples:
    |tuser|
    |svang|



Scenario Outline: Start Date later than End Date
Given the required users exist
When <tuser> has logged in with password and chosen to view the access log
And She enter the date in the wrong text box
Then The Search By Date button is disabled

Examples:
    |tuser|
    |svang|

Scenario Outline: No End Date
Given the required users exist
When <tuser> has logged in with password and chosen to view the access log
And She didn't enter the end date
Then The Search By Date button is disabled

Examples:
    |tuser|
    |svang|

Scenario Outline: No Start Date
Given the required users exist
When <tuser> has logged in with password and chosen to view the access log
And She didn't enter the start date
Then The Search By Date button is disabled

Examples:
    |tuser|
    |svang|

