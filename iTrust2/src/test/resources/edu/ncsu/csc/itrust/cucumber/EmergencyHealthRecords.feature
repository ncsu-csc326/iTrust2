#Author tadicke3
Feature: Emergency Health Records
	As an HCP or ER
	I want to access emergency health records
	So that I can help a patient in an emergency
	
Scenario: Emergency health records by name
Given I am logged in as an HCP
When I navigate to Emergency Health Records
When I search a full patient name
Then The patient appears as a result in the list of matching patients
And The patients information can be displayed

Scenario: Emergency health records by MID
Given I am logged in as an ER
When I navigate to Emergency Health Records
When I search a full patient MID
Then The patient appears as a result in the list of matching patients
And The patients information can be displayed

Scenario: Emergency health records by name substring
Given I am logged in as an HCP
When I navigate to Emergency Health Records
When I search a partial patient MID
Then The patient appears among other names containing the substring
And The patient can be selected from the others, and has accurate information