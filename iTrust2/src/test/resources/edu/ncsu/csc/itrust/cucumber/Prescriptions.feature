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
	
	
Scenario Outline: Add New Drug
    Given An Admin exists in iTrust2
    When I log in as admin
	When I choose to add a new drug
	And submit the values for NDC <ndc>, name <name>, and description <description>
	Then the drug <name> is successfully added to the system
	
Examples:
	| ndc          | name         | description       |
	| 1234-5678-90 | Test Product | Strong antiseptic |
	

# these are implemented in the OfficeVisitStepDefs file	
Scenario Outline: Add Prescription to an Office Visit
    Given A hospital exists in iTrust2
    Given An HCP exists in iTrust2
    Given A Patient exists in iTrust2
    Given A drug named <prescription> exists in iTrust2
    Given The patient has name: <name> and date of birth: <dob>
    When I log in as hcp
    When I navigate to the Document Office Visit page
    When I fill in information on the office visit for people 12 and over with date: <date>, weight: <weight>, height: <height>, systolic blood pressure: <sys>, diastolic blood pressure: <dia>, household smoking status: <houseSmoking>, patient smoking status: <patientSmoking>, HDL cholesterol: <HDL>, LDL cholesterol: <LDL>, triglycerides: <triglycerides>, and note: <note>
    And I add a prescription for <prescription> with a dosage of <dosage> starting on <start> and ending on <end> with <renewals> renewals
    And I submit the office visit 
    Then The office visit is documented successfully

Examples:
	| user    | name     | dob        | date         |notes                                                               | weight | height | sys  | dia  | houseSmoking | patientSmoking | HDL | LDL | triglycerides | prescription      | dosage | start      | end        | renewals |
	| patient | Jim Bean | 04/12/1995 | 12/15/2017   | Jim appears to be depressed. He also needs to eat more vegetables. | 130.4  | 73.1   | 110  | 175  | 1            | 3              | 65  | 102 | 147           | Quetiane Fumarate | 100    | 12/15/2017 | 12/14/2018 | 12       |



Scenario Outline: View Prescriptions
	Given A hospital exists in iTrust2
    Given An HCP exists in iTrust2
    Given A Patient exists in iTrust2
    Given A drug named <prescription> exists in iTrust2
    Given I have been prescribed the drug <prescription> with a dosage of <dosage> starting on <start> and ending on <end> with <renewals> renewals
	When I log in as patient
	When I choose to view my prescriptions
	Then I see a prescription for <prescription> with a dosage of <dosage> starting on <start> and ending on <end> with <renewals> renewals

Examples:
	| user  | prescription      | dosage | start      | end        | renewals |
	| patient | Quetiane Fumarate | 100    | 12/15/2017 | 12/14/2018 | 12       |