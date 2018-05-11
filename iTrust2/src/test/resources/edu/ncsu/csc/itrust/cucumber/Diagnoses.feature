#Author tvbarnet
Feature: Diagnoses in iTrust2
	As an iTrust2 HCP
	I want to add a diagnosis to an office visit
	So that a record exists of when a Patient is diagnosed with illnesses
	As a Patient
	I want to view my current and previous diagnoses
	So that I know my history
	As an Admin
	I want to add new diagnoses to the system
	So that they can be tracked

#Add valid diagnosis to 24 year old during an office visit
Scenario Outline: Document an Office Visit with a Diagnosis
Given The required diagnosis facilities exist
And A patient exists with the name: <first> <last> and DOB: <dob>
When I log into iTrust2 as an HCP
When I navigate to the Document Offive Visit Page
When I fill in information on the office visit with date: <date>, weight: <weight>, height: <height>, systolic blood pressure: <sys>, diastolic blood pressure: <dia>, household smoking status: <houseSmoking>, patient smoking status: <patientSmoking>, HDL cholesterol: <HDL>, LDL cholesterol: <LDL>, triglycerides: <triglycerides>, diagnosis: <diagnosis>, diagnosis note: <diagnosisNote>, and visit note: <note>
Then The office visit is documented sucessfully

	Examples:
	| first    | last     | dob      | date     | weight  | height  | sys  | dia  | houseSmoking | patientSmoking | HDL | LDL | triglycerides | diagnosis |diagnosisNote     |note                |
	| Nellie   | Sanderson|04/24/1993|10/17/2017|125      |62.3     |110   |75    |1             |3               |65   |102  |147            |Pneumonia  |Patient should avoid contact with others for first 24 hours and take prescribed antibiotics| Nellie has been experiencing symptoms of a cold or flu|

#View current and previous diagnoses
Scenario Outline: View current and previous diagnoses
Given The required diagnosis facilities exist
And A patient exists with the name: <first> <last> and DOB: <dob>
When I log into iTrust2 as a patient
When I navigate to my past diagnoses
Then I see the list of my diagnoses
And The <date>, <hcp>, <description>, and <note> are correct

	Examples:
	| first    | last     | dob      | hcp | date     |description|note|
	| Nellie   | Sanderson|04/24/1993| hcp |10/17/2017|Pneumonia  |Patient should avoid contact with others for first 24 hours and take prescribed antibiotics|

#admin add diagnoses	
Scenario Outline: Add a diagnosis to the system, then delete it
Given The required diagnosis facilities exist 
When I log into iTrust2 as an admin
When I navigate to the list of diagnoses
When I enter the info for a diagnosis with code: <code>, and description: <description>
Then The diagnosis is added sucessfully
And The diagnosis info is correct
When I delete the new code
Then The code is deleted

	Examples:
	| first    | last     | code   | description |
	|Al        | Minister |K35     |Acute Appendicitis|


#admin add invalid diagnoses	
Scenario Outline: Add an invalid diagnosis to the system
Given The required diagnosis facilities exist 
When I log into iTrust2 as an admin
When I navigate to the list of diagnoses
When I enter the info for a diagnosis with code: <code>, and description: <description>
Then The diagnosis is not added

	Examples:
	| first    | last     | code   | description |
	|Al        | Minister |K3      |Acute Appendicitis|
	|Al        | Minister |35A      |Acute Appendicitis|
	|Al        | Minister |K       |Acute Appendicitis|
	|Al        | Minister |K35.55555|Acute Appendicitis|
	|Al        | Minister |K35      |12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901|