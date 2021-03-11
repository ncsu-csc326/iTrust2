#Author kpresle

Feature: Add a Hospital
	As an Admin
	I want to add a new hospital
	So that patients can use one of our new facilities

Scenario: Add new hospital
Given An Admin exists in iTrust2
When I log in as admin
When I navigate to the Add Hospital page
When I fill in the values in the Add Hospital form
Then The hospital is created successfully
