Feature: Create and view appointment requests
	Patients should be able to request appointments
	So that HCPs can take patients for appointments

Scenario Outline: Patient has no appointment requests
	Given There exists a patient in the system
	Then I log on as a patient
	When I navigate to the Manage Appointment Requests page
	Then <text> is displayed for appointment requests

Examples:
	| text 								|
	| No appointment requests found.	|

Scenario Outline: Create appointment request
	Given There exists a patient in the system
	Then I log on as a patient
	When I navigate to the Manage Appointment Requests page
	And I choose to request a medical appointment with type <type>, HCP <hcp>, date <date>, time <time>, and comments <comments>
	Then The appointment request with type <type>, date <date>, and time <time> is submitted successfully

Examples:
	| type							| hcp				| date			| time 		| comments 														|
	| General Checkup				| hcp 				| 09/27/2040	| 12:00 PM	| My brain hurt													|								|
	| General Ophthalmology			| robortOPH 		| 09/27/2040	| 12:00 PM	| My eyes hurt													|
	| General Ophthalmology			| bobbyOD 			| 09/27/2040	| 1:30 PM	| I'm having trouble seeing										|
	| Ophthalmology Surgery			| robortOPH			| 12/27/2040	| 11:15 AM	| Is something wrong with my eyes, or AM I just getting old?	|
	| Ophthalmology Surgery			| robortOPH			| 12/06/2040	| 9:30 AM	| I think I have something stuck in my left eye					|

Scenario Outline: Invalid appointment request
	Given There exists a patient in the system
	Then I log on as a patient
	When I navigate to the Manage Appointment Requests page
	And I choose to request a medical appointment with type <type>, HCP <hcp>, date <date>, time <time>, and comments <comments>
	Then The appointment request is not submitted

Examples:
	| type							| hcp				| date			| time 		| comments 						|
	| General Checkup				| hcp 				| 09/27/2010	| 12:00 PM	| My brain hurt					|
	| General Checkup				| robortOPH 		| 09/27/2010	| 12:00 PM	| My eyes hurt					|
	| General Ophthalmology			| bobbyOD 			| 09/27/2010	| 12:00 PM	| My eyes hurt					|
	| General Checkup 				| robortOPH 		| 09/27/2040	| 1:30 PM	| I'm having trouble seeing		|
	| General Checkup 				| bobbyOD 			| 09/27/2040	| 1:30 PM	| I'm having trouble seeing		|

Scenario Outline: Patient views an appointment request
	Given There exists a patient in the system
	And The patient has requested a medical appointment with type <type>, HCP <hcp>, date <date>, time <time>, and comments <comments>
	When The patient navigates to the Manage Appointment Requests page
	Then The patient can view the appointment request with type <type>, date <date>, time <time>, and status <status>

Examples:
	| type							| hcp				| date			| time 		| comments 		| status 		| 
	| General Ophthalmology			| bobbyOD		 	| 10/31/2040	| 10:00 AM	| My eyes hurt	| Pending 		| 
	
Scenario Outline: View appointment requests as an ophthalmologist HCP
	Given There exists a patient in the system
	And There exists an HCP in the system
	And The patient has requested a medical appointment with type <type>, HCP <hcp>, date <date>, time <time>, and comments <comments>
	When The HCP <hcp> logs in
	And The HCP navigates to the Appointment Requests page
	Then The HCP can view the appointment request with type <type>, date <date>, and time <time>

Examples:
	| type							| hcp				| date			| time 		| comments 		|
	| General Ophthalmology 		| bobbyOD		 	| 10/31/2040	| 10:00 AM	| My eyes hurt	|
	| Ophthalmology Surgery			| robortOPH		 	| 10/31/2040	| 10:00 AM	| My eyes hurt	|

Scenario Outline: Approve appointment request as an HCP
	Given There exists a patient in the system
	And There exists an HCP in the system
	And The patient has requested a medical appointment with type <type>, HCP <hcp>, date <date>, time <time>, and comments <comments>
	When The HCP <hcp> logs in
	And The HCP navigates to the Appointment Requests page
	And The HCP selects the appointment request with type <type>, HCP <hcp>, date <date>, time <time>, and comments <comments>
	And The HCP selects to approve the selected appointment request
	Then The appointment request with type <type>, HCP <hcp>, date <date>, time <time>, and comments <comments> is moved into the upcoming medical appointment column
	And The HCP behavior is logged on the iTrust2 homepage

Examples:
	| type							| hcp				| date			| time 		| comments 		|
	| General Checkup				| hcp		 		| 10/31/2040	| 10:00 AM	| My brain hurt	|
	| General Ophthalmology			| bobbyOD		 	| 10/31/2040	| 10:00 AM	| My eyes hurt	|
	| Ophthalmology Surgery			| robortOPH		 	| 10/31/2040	| 10:00 AM	| My eyes hurt	|

Scenario Outline: Decline appointment request as an ophthalmologist HCP
	Given There exists a patient in the system
	And There exists an HCP in the system
	And The patient has requested a medical appointment with type <type>, HCP <hcp>, date <date>, time <time>, and comments <comments>
	When The HCP <hcp> logs in
	And The HCP navigates to the Appointment Requests page
	And The HCP selects the appointment request with type <type>, HCP <hcp>, date <date>, time <time>, and comments <comments>
	And The HCP selects to reject the selected appointment request
	Then The appointment request is deleted from the update medical appointment requests column
	And The HCP behavior is logged on the iTrust2 homepage

Examples:
	| type							| hcp				| date			| time 		| comments 		|
	| General Checkup				| hcp		 		| 10/31/2040	| 10:00 AM	| My brain hurt	|
	| General Ophthalmology			| bobbyOD		 	| 10/31/2040	| 10:00 AM	| My eyes hurt	|
	| Ophthalmology Surgery			| robortOPH		 	| 10/31/2040	| 10:00 AM	| My eyes hurt	|
