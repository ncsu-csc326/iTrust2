#Author kpresle
#Author rpcatalf
#Author ejwoodho

Feature: Edit demographics
	As a patient
	I want to edit my demographics
	So that my information is stored and HCPs can make the best decisions about my health
	
# test for #49 bug
Scenario Outline: Viewing logs with edits
	Given A patient exists in the system
	When I log in as a patient
	And I navigate to the Edit My Demographics page
	And I fill in new, updated demographics
	Then The demographics are updated
	And I go to the Edit My Demographics page and back to the log and check the last 3 messages, <first> and <second> and <third>

Examples:
	| first    						| second   							|  third   							| 
	|Demographics viewed by user	| HCP edits patient's demographics 	| Demographics viewed by user	    | 

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

Scenario: Attempt to update patient demographics with multi-word city.
	Given A patient exists in the system
	When I log in as a specific patient
	When I navigate to the Edit My Demographics page
	When I fill in updated demographics with a multi-word city
	Then The demographics are updated
	And The edited demographics can be viewed

