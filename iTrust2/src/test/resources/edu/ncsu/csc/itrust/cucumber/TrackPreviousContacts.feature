#author ejwoodho

Feature: Tracking Previous Contacts 
    As a virologist
    I want to find the page for Tracking Previous Contacts 
    So that I can view disease spread within the system 

Scenario Outline: No Passenger Data  
    Given there is no passenger data in the system 
    Given the user navigates to Tracking Previous Contacts
    Then the missing passenger data message <message> appears on the page

Examples: 
   | message                    		 	| 
   | Error: No passengers in the database. 	|

Scenario Outline: Existing Passengers 
    Given there are already passengers <passengers>
    And the user navigates to Tracking Previous Contacts 
    When the user selects Choose File and adds contact data from <contacts> 
    And the user validly fills out the form with <id>, <date>, and <depth> 
    Then they can select Track Contacts to view contacts by depth 

Examples: 
   | passengers  		        | contacts   			      	| id       	| date       | depth |
   | abcpassengers.csv 			| abccontacts.csv 				| A 		| 02/04/2020 | 3     |
   | passenger-data-short.csv 	| passenger-contacts-short.csv 	| 3b9acb62 	| 02/04/2020 | 2     |

Scenario Outline: Existing Passengers and Contacts 
    Given there are already passengers <passengers>
    And the user navigates to Tracking Previous Contacts 
    When there are already contacts <contacts>
    And the user validly fills out the form with <id>, <date>, and <depth> 
    Then they can select Track Contacts to view contacts by depth 

Examples: 
   | passengers  		        | contacts   			      	| id       	| date       | depth |
   | abcpassengers.csv 			| abccontacts.csv 				| A 		| 02/04/2020 | 3     |
   | passenger-data-short.csv 	| passenger-contacts-short.csv 	| 3b9acb62 	| 02/04/2020 | 2     |

Scenario Outline: Invalid Form Inputs 
    Given the user navigates to Tracking Previous Contacts 
    And there are already passengers <passengers>
    When there are already contacts <contacts>
    And the user invalidly fills out the form with <id>, <date>, and <depth> 
    Then they cannot select Track Contacts to view contacts by depth 

Examples: 
   | passengers  		        | contacts   			      	| id       	| date       | depth |
   | abcpassengers.csv 			| abccontacts.csv 				| null   	| 02/04/2020 | 3     |
   | passenger-data-short.csv 	| passenger-contacts-short.csv 	| 3b9acb62 	| null 	     | 2     |
   | passenger-data-short.csv 	| passenger-contacts-short.csv 	| 3b9acb62 	| 02/04/2020 | null  |
   | abcpassengers.csv 			| abccontacts.csv 				| A 		| 02/04/2020 | -2  	 |
   | passenger-data-short.csv 	| passenger-contacts-short.csv 	| null	 	| null		 | null  |