#Author kpresle

Feature: Request Appointment
	As a Patient
	I want to request an appointment
	So that I have a scheduled time to visit the doctor

Scenario: Valid appointment requested
Given There is a sample HCP and sample Patient in the database
When I log in as patient
When I navigate to the Request Appointment page
When I fill in values in the Appointment Request Fields
Then The appointment is requested successfully
And The appointment can be found in the list


Scenario: Approve appointment request
Given An appointment request exists
When I log in as hcp
And I navigate to the View Requests page
And I approve the Appointment Request
Then The request is successfully updated
And The appointment is in the list of upcoming events 