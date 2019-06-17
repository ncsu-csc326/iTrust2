#Author kpresle

Feature: Test Logins
	As a User
	I wat to make sure that I can log in
	So that I can make sure that basic functionality works


Scenario Outline: Valid Logins
    Given All user types exist in the system
    When I log into iTrust2 as a: <user>
    Then I should be able to view my homepage
	Examples:
	| user      |
	| admin     |
	| hcp       |
	| patient   |
	| er        |
	| lt        |
	| bobbyOD   |
	| robortOPH |
	
	
Scenario Outline: Invalid Logins
    Given All user types exist in the system
    When I log into iTrust2 as a: <user>
    Then I should not be logged in
	Examples:
	| user    |
	| vladimir|
