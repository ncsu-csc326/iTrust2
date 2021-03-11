#Author kpresle

Feature: Personnel edit demographics
	As an iTrust Personnel
	I want to edit my demographics
	So that my information is stored for regulatory purposes

Scenario: Add my demographics
Given An Admin exists in iTrust2
When I log in as admin
When I navigate to the Personnel Edit My Demographics page
When I fill in new, updated personnel demographics
Then The personnel demographics are updated
And The admin can view new demographics