Feature: Add and view a new food diary entry
	I want to be able to add new diary entries
	So that we can view them afterwards

Scenario Outline: Patient has no food diary entries
	Given There exists a patient in the system.
	Then I log on as a patient for diaries.
	When I navigate to the view food diary entries page.
	Then <text> is displayed.
Examples:
	| text 								|
	| There are no Food Diary entries.	|

Scenario Outline: Add entry as patient
	Given There exists a patient in the system.
	Then I log on as a patient for diaries.
	When I navigate to the Add Diary Entry page.
	And I choose to add a new diary entry with <date>, <type>, <food>, <servings>, <calories>, <fat>, <sodium>, <carbs>, <sugars>, <fiber>, <protein>.
	Then The diary entry is added successfully.

Examples:
	| date			| type			| food				| servings	| calories | fat	| sodium	| carbs	| sugars | fiber	| protein	|
	| 09/27/2018	| Breakfast		| bacon and eggs	| 1			| 400	   | 10		| 20		| 15	| 20	 | 0		| 40		|
	| 09/27/2018	| Lunch			| bacon and eggs	| 1			| 400	   | 10		| 20		| 15	| 20	 | 0		| 40		|
	| 09/27/2018	| Dinner		| bacon and eggs	| 1			| 400	   | 10		| 20		| 15	| 20	 | 0		| 40		|
	| 09/27/2018	| Snack			| bacon and eggs	| 1			| 400	   | 10		| 20		| 15	| 20	 | 0		| 40		|

Scenario Outline: Invalid add entry as patient
	Given There exists a patient in the system.
	Then I log on as a patient for diaries.
	When I navigate to the Add Diary Entry page.
	And I choose to incorrectly add a new diary entry with <date>, <type>, <food>, <servings>, <calories>, <fat>, <sodium>, <carbs>, <sugars>, <fiber>, <protein>.
	Then The diary entry is not added.

Examples:
	| date			| type			| food				| servings	| calories | fat	| sodium	| carbs	| sugars | fiber	| protein	|
	| 05/20/2050	| Breakfast		| bacon and eggs	| 1			| 400	   | 10		| 20		| 15	| 20	 | 0		| 40		|
	| 09/27/2018	| Lunch			| bacon and eggs	| -20		| 400	   | 10		| 20		| 15	| 20	 | 0		| 40		|
	| 09/27/2018	| Dinner		| bacon and eggs	| 1			| -400	   | 10		| 20		| 15	| 20	 | 0		| 40		|
	| 09/27/2018	| Snack			| bacon and eggs	| 1			| 400	   | null	| 20		| 15	| 20	 | 0		| 40		|
	| 09/27/2018	| Lunch			| bacon and eggs	| 1			| 400	   | 10		| -1		| 15	| 20	 | 0		| 40		|
	| 09/27/2018	| Dinner		| bacon and eggs	| 1			| 400	   | 10		| 20		| **	| 20	 | 0		| 40		|
	| 09/27/2018	| Snack			| bacon and eggs	| 1			| 400	   | 10		| 20		| 15	| null	 | 0		| 40		|
	| 09/27/2018	| Dinner		| bacon and eggs	| 1			| 400	   | 10		| 20		| 15	| 20	 | @@		| 40		|
	| 09/27/2018	| Snack			| bacon and eggs	| 1			| 400	   | 10		| 20		| 15	| 20	 | 0		| why		|

Scenario Outline: View entry as a Patient
	Given There exists a patient in the system.
	And The patient has added a entry <date>, <type>, <food>, <servings>, <calories>, <fat>, <sodium>, <carbs>, <sugars>, <fiber>, <protein>.
	Then The patient has navigated to food diary dashboard.
	And The patient selects <date>.
	Then The patient can view the entry <date>, <type>, <food>, <servings>, <calories>, <fat>, <sodium>, <carbs>, <sugars>, <fiber>, <protein>.
	And This patient behavior is logged on the iTrust2 homepage.

Examples:
	| date			| type			| food				| servings	| calories | fat	| sodium	| carbs	| sugars | fiber	| protein	| 
	| 09/27/2018	| Breakfast		| bacon and eggs	| 1			| 400	   | 10		| 20		| 15	| 20	 | 0		| 40		| 
	
Scenario Outline: View entry as an HCP
	Given There exists a patient in the system.
	And There exists an HCP in the system.
	And The patient has added a entry <date>, <type>, <food>, <servings>, <calories>, <fat>, <sodium>, <carbs>, <sugars>, <fiber>, <protein>.
	Then The HCP logs in and has navigated to the food diary entry dashboard.
	And The HCP selects the entries for the patient on <date>.
	Then The HCP can view the entry <date>, <type>, <food>, <servings>, <calories>, <fat>, <sodium>, <carbs>, <sugars>, <fiber>, <protein>.
	And The HCP behavior is logged on the iTrust2 homepage.

Examples:
	| date			| type			| food				| servings	| calories | fat	| sodium	| carbs	| sugars | fiber	| protein	| 
	| 09/27/2018	| Breakfast		| bacon and eggs	| 1			| 400	   | 10		| 20		| 15	| 20	 | 0		| 40		| 

Scenario Outline: Totals are calculated correctly.
	Given There exists a patient in the system.
	Then I log on as a patient for diaries.
	When I navigate to the Add Diary Entry page.
	And I choose to add a new diary entry with <date>, <type>, <food>, <servings>, <calories>, <fat>, <sodium>, <carbs>, <sugars>, <fiber>, <protein>.
	Then The diary entry is added successfully.
	When I navigate to the Add Diary Entry page.
	And I choose to add a new diary entry with <date>, <type>, <food>, <servings>, <calories>, <fat>, <sodium>, <carbs>, <sugars>, <fiber>, <protein>.
	Then The diary entry is added successfully.
	Then The patient has navigated to food diary dashboard.
	And The patient selects <date>.
	Then The patient can view the proper totals based on <calories>, <fat>, <sodium>, <carbs>, <sugars>, <fiber>, <protein>.

Examples:
	| date			| type			| food				| servings	| calories | fat	| sodium	| carbs	| sugars | fiber	| protein	| 
	| 08/10/2018	| Breakfast		| bacon and eggs	| 1			| 400	   | 10		| 20		| 15	| 20	 | 0		| 40		| 
