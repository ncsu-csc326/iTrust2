@PatientBloodView
Feature: Patient Blood Sugar View
  As a patient
  I want to view my blood sugar entries in a table
  
  Scenario: HCP views patient's entries
	Given I am logged in as Frodo
	And Frodo has the following entries
	| Date       | Fasting | First | Second | Third |
	| 2019-10-13 | 102     | 78    | 134    | 101   |
	| 2019-10-14 | 92      | 130   | 98     | 89    |
	| 2019-10-15 | 78      | 123   | 120    | 123   |
	| 2019-10-16 | 111     | 100   | 103    | 112   |
	And I navigate to the Blood Sugar Journal page
	And I select the week view
	And I enter the given date
	"""
	10/13/2019 - 10/19/2019
	"""
	Then The table displays the following entries
	| Date       | Fasting | First | Second | Third |
	| 2019-10-13 | 102     | 78    | 134    | 101   |
	| 2019-10-14 | 92      | 130   | 98     | 89    |
	| 2019-10-15 | 78      | 123   | 120    | 123   |
	| 2019-10-16 | 111     | 100   | 103    | 112   |
	| 2019-10-17 |         |       |        |       |
	| 2019-10-18 |         |       |        |       |
	| 2019-10-19 |         |       |        |       |