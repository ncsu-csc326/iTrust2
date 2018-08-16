#Authors tadicke3, nslandsb
Feature: Personal representatives
	As a Patient or HCP
	I want to be able to declare people representatives for myself
	So that others can make my health decisions for me
	
Scenario: Add Personal Representative
Given The relevant users exist
Given I am a patient with no representatives
When I navigate to Personal Representatives
And I add a new Personal Representative
Then A success message is displayed that I added a representative

Scenario: View Personal Representative
Given The relevant users exist
Given I am a patient who represents another patient
When I navigate to Personal Representatives
Then I see the patient who I represent

Scenario: Remove Self as Personal Representative
Given The relevant users exist
Given I am a patient who represents another patient
When I navigate to Personal Representatives
And I unassign the patient who I represent
Then A success message is displayed that I removed myself as representative
And I do not see the patient who I represented

Scenario: Add Personal Representative as HCP
Given The relevant users exist
Given I am an HCP
When I navigate to the HCP Personal Representatives
And I search for a user
And I select a new representative to add
Then A success message is displayed that I added another representative