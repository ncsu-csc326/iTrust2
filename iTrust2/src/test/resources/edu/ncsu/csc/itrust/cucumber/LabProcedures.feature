#Author tadicke3
Feature: Lab procedures
	As an HCP
	I want to add lab procedures to an office visit
	So that I can keep track of what lab procedures a patient needs
	
Scenario: Add lab procedure to office visit
Given I log in to iTrust2 as an HCP
When I create a new Office Visit
And I add a lab procedure to that visit
Then I recieve a message that office visit details were changed successfully

Scenario: View assigned lab procedures
Given I log in to iTrust2 as a Lab Tech
When I navigate to Assigned Procedures
And I change my newest procedures status to In-Progress
Then I recieve a message that lab procedure details were changed successfully

Scenario: Add new lab procedure
Given I log in to iTrust2 as an Admin
When I navigate to Admin Procedures
And I add a new lab procedure
Then I recieve a message that the procedure list was changed successfully