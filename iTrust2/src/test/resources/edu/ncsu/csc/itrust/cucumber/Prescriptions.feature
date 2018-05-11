Feature: Manage Prescriptions
	As an HCP
	I want to record prescriptions for patients
	So that I have record of past prescriptions and they can be fulfilled
	As a patient
	I want to view my past prescriptions
	So that I can make sure they match my expectations
	As an admin
	I want to manage the list of available medications
	So that all records are up to date and HCPs can prescribe the latest medications
	
Scenario Outline: Add Prescription to an Office Visit
    Given I have logged in with username: <user>
    When I start documenting an office visit for the patient with name: <first> <last> and date of birth: <dob>
    And fill in the office visit with date: <date>, hospital: <hospital>, notes: <notes>, weight: <weight>, height: <height>, blood pressure: <pressure>, household smoking status: <hss>, patient smoking status: <pss>, hdl: <hdl>, ldl: <ldl>, and triglycerides: <triglycerides>
    And add a prescription for <prescription> with a dosage of <dosage> starting on <start> and ending on <end> with <renewals> renewals
    And submit the office visit 
   	Then A message indicates the visit was submitted successfully

Examples:
	| user  | first | last | dob        | date       | hospital         | notes                                                              | weight | height | pressure | hss     | pss   | hdl | ldl | triglycerides | prescription      | dosage | start      | end        | renewals |
	| svang | Jim   | Bean | 04/12/1995 | 12/15/2017 | General Hospital | Jim appears to be depressed. He also needs to eat more vegetables. | 130.4  | 73.1   | 160/80   | OUTDOOR | NEVER | 50  | 123 | 148           | Quetiane Fumarate | 100    | 12/15/2017 | 12/14/2018 | 12       |



Scenario Outline: View Prescriptions
	Given I have logged in with username: <user>
	When I choose to view my prescriptions
	Then I see a prescription for <prescription> with a dosage of <dosage> starting on <start> and ending on <end> with <renewals> renewals

Examples:
	| user  | prescription      | dosage | start      | end        | renewals |
	| jbean | Quetiane Fumarate | 100    | 12/15/2017 | 12/14/2018 | 12       |



Scenario Outline: Add New Drug
	Given I have logged in with username: <user>
	When I choose to add a new drug
	And submit the values for NDC <ndc>, name <name>, and description <description>
	Then the drug <name> is successfully added to the system
	
Examples:
	| user  | ndc          | name         | description       |
	| admin | 1234-5678-90 | Test Product | Strong antiseptic |