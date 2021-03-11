Feature: Create and view appointment requests
	Patients should be able to request appointments
	So that HCPs can take patients for appointments

Scenario Outline: Patient has no appointment requests
	Given A Patient exists in iTrust2
	When I log in as patient
	When I navigate to the Manage Appointment Requests page
	Then <text> is displayed for appointment requests

Examples:
	| text 								|
	| No appointment requests found.	|

Scenario Outline: Create appointment request
	Given A Patient exists in iTrust2
	And An HCP exists in iTrust2
	When I log in as patient
	When I navigate to the Manage Appointment Requests page
	And I choose to request a medical appointment with type <type>, HCP <hcp>, date <date>, time <time>, and comments <comments>
	Then The appointment request with type <type>, date <date>, and time <time> is submitted successfully

Examples:
	| type							| hcp				| date			| time 		| comments 			    |		
	| General Checkup				| hcp 				| 09/27/2040	| 12:00 PM	| My brain hurt			|

Scenario Outline: Invalid appointment request
	Given A Patient exists in iTrust2
	And An HCP exists in iTrust2
	When I log in as patient
	When I navigate to the Manage Appointment Requests page
	And I choose to request a medical appointment with type <type>, HCP <hcp>, date <date>, time <time>, and comments <comments>
	Then The appointment request is not submitted

Examples:
	| type							| hcp				| date			| time 		| comments 						|
	| General Checkup				| hcp 		        | 09/27/2010	| 12:00 PM	| My eyes hurt					|

Scenario Outline: Patient views an appointment request
	Given A Patient exists in iTrust2
	And An HCP exists in iTrust2
	And The patient has requested a medical appointment with type <type>, HCP <hcp>, date <date>, time <time>, and comments <comments>
	When The patient navigates to the Manage Appointment Requests page
	Then The patient can view the appointment request with type <type>, date <date>, time <time>, and status <status>

Examples:
	| type							| hcp				| date			| time 		| comments 		| status 		| 
	| General Checkup    			| hcp    		 	| 10/31/2040	| 10:00 AM	| My eyes hurt	| Pending 		| 
	
Scenario Outline: Approve appointment request as an HCP
	Given A Patient exists in iTrust2
	And An HCP exists in iTrust2
	And The patient has requested a medical appointment with type <type>, HCP <hcp>, date <date>, time <time>, and comments <comments>
	When I log in as hcp
	And The HCP navigates to the Appointment Requests page
	And The HCP selects the appointment request with type <type>, HCP <hcp>, date <date>, time <time>, and comments <comments>
	And The HCP selects to approve the selected appointment request
	Then The appointment request with type <type>, HCP <hcp>, date <date>, time <time>, and comments <comments> is moved into the upcoming medical appointment column
	And The HCP behavior is logged on the iTrust2 homepage

Examples:
	| type							| hcp				| date			| time 		| comments 		|
	| General Checkup				| hcp		 		| 10/31/2040	| 10:00 AM	| My brain hurt	|


