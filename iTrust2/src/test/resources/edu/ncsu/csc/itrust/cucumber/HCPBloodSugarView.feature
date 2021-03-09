@HCPBloodView
Feature: HCP Blood Sugar View
  As a HCP
  I want to view patients blood sugar entries in a table and update
  their upper limits
  
  Scenario: HCP views patient's entries
	Given I am logged in as Saruman
	And Patient has the following entries
	| Date       | Fasting | First | Second | Third |
	| 2019-10-13 | 102     | 78    | 134    | 101   |
	| 2019-10-14 | 92      | 130   | 98     | 89    |
	| 2019-10-15 | 78      | 123   | 120    | 123   |
	| 2019-10-16 | 111     | 100   | 103    | 112   |
	And I navigate to the View Patient Blood Sugar Journal page
	When I search the patients for BillyBob
	And I select BillyBob from the list
	And I select the week view for patients
	And I enter the given date for patient
	"""
	10/13/2019 - 10/19/2019
	"""
	Then The patient's table displays the following entries
	| Date       | Fasting | First | Second | Third |
	| 2019-10-13 | 102     | 78    | 134    | 101   |
	| 2019-10-14 | 92      | 130   | 98     | 89    |
	| 2019-10-15 | 78      | 123   | 120    | 123   |
	| 2019-10-16 | 111     | 100   | 103    | 112   |
	| 2019-10-17 |         |       |        |       |
	| 2019-10-18 |         |       |        |       |
	| 2019-10-19 |         |       |        |       |
	
  Scenario Outline: HCP edits patient's limits successfully
    Given I am logged in as Saruman
	And I navigate to the View Patient Blood Sugar Journal page
	When I search the patients for BillyBob
	And I select BillyBob from the list
	And I enter <fasting> for fasting and <afterMeal> after meal
	Then I update the blood sugar limits
	
    Examples: 
      | fasting | afterMeal |
      | 80      | 120       |
      | 100     | 160       |
      | 130     | 180       |

  Scenario Outline: HCP edits patient's limits successfully
    Given I am logged in as Saruman
	And I navigate to the View Patient Blood Sugar Journal page
	When I search the patients for BillyBob
	And I select BillyBob from the list
	And I enter <fasting> for fasting and <afterMeal> after meal
	Then I fail to update the blood sugar limits
	
    Examples: 
      | fasting | afterMeal |
      | 79      | 120       |
      | 131     | 120       |
      | -1      | 120       |
      | gh      | 120       |     
      | 80      | 119       |
      | 80      | 181       |
      | 80      | rf        |
      | 80      | -1        |          
       