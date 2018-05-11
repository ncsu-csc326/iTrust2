Feature: HCP Edit Patient Demographics
	As an HCP
	I want to edit a patient's demographics before an office visit
	So that all of their pertinent information is up to date


Scenario Outline: Edit Demographics
	Given the required users exist
	And Dr Shelly Vang has logged in and chosen to edit a patient
	When she selects the patient with first name: <first> and last name: <last>
	And she changes the zip code to: <zip>
	And she submits the changes
	Then a success message is displayed
	
Examples:
	| first | last | zip   |
	| Jim   | Bean | 98765 |


Scenario Outline: Edit Invalid Demographics
	Given the required users exist
	And Dr Shelly Vang has logged in and chosen to edit a patient
	When she selects the patient with first name: <first> and last name: <last>
	And she changes the zip code to: <badZip>
	And she submits the changes
	Then an error message is displayed
	When she changes the zip code to: <validZip>
	And she submits the changes
	Then a success message is displayed
	
Examples:
	| first | last | badZip | validZip |
	| Jim   | Bean | abc    | 12345    |


Scenario Outline: Change Patient While Editing
	Given the required users exist
	And Dr Shelly Vang has logged in and chosen to edit a patient
	When she selects the patient with first name: <first1> and last name: <last1>
	And she changes the zip code to: <zip1>
	Then if she changes to patient to <first2> <last2>, a popup indicates her changes will be lost
	When she chooses to continue
	And she changes the zip code to: <zip2>
	And she submits the changes
	Then a success message is displayed
	When she selects the patient with first name: <first1> and last name: <last1>
	Then the zip code has the value: <zip3>

Examples:
	| first1 | last1 | zip1  | first2 | last2     | zip2  | zip3  |
	| Jim    | Bean  | 35678 | Nellie | Sanderson | 34567 | 12345 |