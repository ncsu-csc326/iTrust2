#Author kpresle

Feature: Document office visit
	As an iTrust2 HCP
	I want to document an office visit
	So that a record exits of a Patient visiting the doctor

Scenario: Document an Office Visit
Given The required facilities exist
When I log in to iTrust2 as a HCP
When I navigate to the Document Office Visit page
When I fill in information on the office visit
Then The office visit is documented successfully

Scenario: Document an Office Visit
Given The required facilities exist
When I log in to iTrust2 as a HCP
When I navigate to the Document Office Visit page
When I fill in information on the office visit with leading zeroes
Then The office visit is documented successfully