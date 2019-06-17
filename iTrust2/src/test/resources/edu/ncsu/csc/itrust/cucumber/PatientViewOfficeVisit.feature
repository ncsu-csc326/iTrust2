Feature: Patient View Office Visits
	As an Patient
	I want to view my past office visits
	So that I can see my office visit history


Scenario Outline: View Patients Office Visits
  Given there exists a patient in the iTrust system
	Given there are office visits of all types for the patient
	Then the patient logs in and navigates to the view patient office visits page
	And all of the office visit types are options to select
	When the patient selects <office_visit_type> office visit it shows the correct visit information
	
Examples:
	| office_visit_type     |
	| General Checkup       | 
	| General Ophthalmology |
	| Ophthalmology Surgery | 
