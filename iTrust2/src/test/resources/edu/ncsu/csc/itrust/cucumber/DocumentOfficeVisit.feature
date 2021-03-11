#Author kpresle

Feature: Document office visit
	As an iTrust2 HCP
	I want to document an office visit
	So that a record exits of a Patient visiting the doctor

Scenario Outline: Document an Office Visit
Given A hospital exists in iTrust2
Given An HCP exists in iTrust2
Given A Patient exists in iTrust2
Given The patient has name: <name> and date of birth: <dob>
When I log in as hcp
When I navigate to the Document Office Visit page
When I fill in information on the office visit
Then The office visit is documented successfully
Examples:
| name | dob |
| Karl Liebknecht| 08/13/1871| 