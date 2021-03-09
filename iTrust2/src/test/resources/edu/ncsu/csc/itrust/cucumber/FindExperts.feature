#author ejwoodho

Feature: Find an Expert
	As a user
	I want to find the page for Find an Expert
	So that I can find a specific type of specialist in my area

Scenario Outline: Invalid Inputs to Search
	Given there is at least one patient in the system
	And I login as a patient
	And I navigate to the Find an Expert page on iTrust2
	When I enter invalid values for specialty <specialty>; zip: <zipCode>; and radius: <radius>
	Then the Find Experts button is disabled
	And appropriate error messages <error> are displayed

Examples: 
	| specialty		| zipCode	| radius	| error								|
	| Dermatologist	| 27607		| -5		| Radius must be greater than 0.	|
	| Dermatologist	| 2760		| 10		| Invalid Zip Code.					|
	| Neurologist	| null		| 5			| Invalid Zip Code.					|
	| Urologist		| 27607		| null		| Radius must be greater than 0.	|
	| Pediatrician	| -27607	| 5			| Invalid Zip Code.					|
	| Dermatologist	| 27607		| 0			| Radius must be greater than 0.	|
	| Pediatrician	| 27a07		| 5			| Invalid Zip Code.					|

Scenario Outline: Valid Inputs to Search
	Given there is at least one patient in the system
	And I login as a patient
	And I navigate to the Find an Expert page on iTrust2
	When I enter valid values for specialty <specialty>; zip: <zipCode>; and radius: <radius>
	Then the Find Experts button is enabled
	And searching loads a table or message about experts <message>

Examples: 
	| specialty       | zipCode   | radius  | message													|
	| Cardiologist    |   27607   |  1    	| No experts found in your area.							|
	| Urologist       |   27607   |  1    	| No experts found in your area.							|
	| Pediatrician    |   27607   |   5   	| No experts found in your area.							|
	| Pediatrician    |   00600   |   5   	| Zip code not found.										|
	| Cardiologist    | 27607-1234|  5    	| Click/tap on the name of a hospital to view specialists.  |
	| Cardiologist    |   27606   |   10  	| Click/tap on the name of a hospital to view specialists.  |
	
Scenario Outline: Search with default radius
	Given there is at least one patient in the system
	And I login as a patient
	And I navigate to the Find an Expert page on iTrust2
	When I leave radius as the default value and enter a valid specialty <specialty> and zip <zipCode>
	Then the Find Experts button is enabled

Examples:
	| specialty       | zipCode   |
	| Cardiologist    |   27606   | 
	| Neurologist     |   27607   |
	| Urologist       |   27608   |
	| Pediatrician    |   27513   |
	
Scenario Outline: Search with default zip code
	Given there is at least one patient in the system
	And I login as a patient
	And I edit demographics to include my zip <zipCode>
	And I navigate to the Find an Expert page on iTrust2
	When I leave zip code as the default value and enter a valid specialty <specialty> and radius <radius>
	Then the Find Experts button is enabled
	
Examples:
	| zipCode   | specialty       | radius  | 
	|   27606   | Dermatologist   |  1    	| 
	| 27606-1234| Dermatologist   |  5    	|
	|   27607   | Neurologist     |   10  	|
	| 27607-4321| Urologist       |  1    	|
	|   98765   | Pediatrician    |   5   	|
	|   11111   | Pediatrician    |   10   	|