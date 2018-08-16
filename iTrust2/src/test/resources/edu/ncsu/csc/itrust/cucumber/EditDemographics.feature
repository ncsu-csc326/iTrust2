#Author kpresle
Feature: Edit demographics
	As a patient
	I want to edit my demographics
	So that my information is stored and HCPs can make the best decisions about my health

Scenario: Add my demographics
Given A patient exists in the system
When I log in as a patient
When I navigate to the Edit My Demographics page
When I fill in new, updated demographics
Then The demographics are updated
And The new demographics can be viewed

Scenario: Add bad phone demographics
Given A patient exists in the system
When I log in as a patient
When I navigate to the Edit My Demographics page
When I fill in a bad phone number
Then An error message occurs for my phone number
