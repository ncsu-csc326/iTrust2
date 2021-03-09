#author lli34 
#author ejwoodho

Feature: Upload Patient Data
	As a virologist
	I want to find the page for Upload Patient Data
	So that I can add passengers to the system for Disease Control functionality

Scenario Outline: Invalid File Type
	Given I navigate to the Upload Patient Data page on iTrust2
	When I select Choose File and upload an invalid file type: <file>
	Then clicking Upload CSV makes an incorrect file extension <message> appear

Examples:
	| file | message |
	| passenger-data.csv.txt | Error: The file passenger-data.csv.txt has an incorrect file extension. |

Scenario Outline: Invalid File Format  
	Given I navigate to the Upload Patient Data page on iTrust2  
	When I select Choose File and upload a valid file type: <file> with invalid formatting
	Then Upload CSV button is enabled
	And uploading displays the incorrect format <message>

Examples:
	| file | message |
	| WrongIDs.csv | Error: No successful uploads, check file formatting and content |

Scenario Outline: Valid File Format
	Given I navigate to the Upload Patient Data page on iTrust2
	When I select Choose File and upload a valid file type: <file> with valid formatting
	Then Upload CSV button is enabled
	And uploading displays messages for passengers <skipped> and <added>

Examples:
	| file | skipped | added |
	| duplicate-data.csv | null | 3 Passengers were added to the database. |
	| DuplicatesWithinFile.csv | 1 duplicate Passengers were not added. | 11 Passengers were added to the database. |
	| passenger-data.csv | null | 1209 Passengers were added to the database. |
	
Scenario Outline: Upload File While Database Is Not Empty
	Given I navigate to the Upload Patient Data page on iTrust2
	When I select Choose File and upload a valid file type: <file> with valid formatting
	Then Upload CSV button is enabled
	And the messages displayed for passengers are <skipped> and <added>
	And I select Choose File and upload another valid file type: <file2> with valid formatting
	And upload the file which displays messages for passengers <skipped2> and <added2> or <error>
Examples:
	| file | file2 | skipped | added | skipped2 | added2 | error |
	| duplicate-data.csv | 2-duplicate-data.csv | null | 3 Passengers were added to the database. | 2 duplicate Passengers were not added. | 1 Passengers were added to the database. | null |
	| duplicate-data.csv | duplicate-data.csv | null | 3 Passengers were added to the database. | null  | null | Error: All passengers in this file are already in the system |
