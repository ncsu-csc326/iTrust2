Feature: HCP View Patient Office Visits
	As an HCP
	I want to view patients' office visits
	So that I can see their office visit history


Scenario Outline: View Patients Office Visits
	Given There exists a patient in the system
	Given there are office visits of all types
	And there exists an <hcp_type> HCP in the iTrust system
	Then the <hcp_type> HCP logs in and navigates to the view patient office visits page
	And all of the office visit types are options to select
	When the HCP selects <office_visit_type> office visit it shows the correct visit information
	
Examples:
	| hcp_type        | office_visit_type     |
	| general         | General Checkup       | 
	| ophthalmologist | General Checkup       |
	| optometrist     | General Checkup       |
	| general         | General Ophthalmology | 
	| ophthalmologist | General Ophthalmology |
	| optometrist     | General Ophthalmology |
	| general         | Ophthalmology Surgery | 
	| ophthalmologist | Ophthalmology Surgery |
	| optometrist     | Ophthalmology Surgery |